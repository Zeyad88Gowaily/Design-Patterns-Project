package nonprofit.repository;

import java.io.IOException;
import java.util.Base64;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class SupabaseRestClient {
    private static final Pattern TABLE_NAME = Pattern.compile("[a-z_]+");
    private static final int TIMEOUT_MILLIS = 15000;

    private final String baseUrl;
    private final String apiKey;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(TIMEOUT_MILLIS))
            .build();
    private final Gson gson = new Gson();
    private volatile String accessToken;
    private volatile String refreshToken;

    public SupabaseRestClient(String baseUrl, String apiKey) {
        if (baseUrl == null || apiKey == null || baseUrl.trim().isEmpty() || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Supabase URL and publishable/anon key are required.");
        }
        String normalizedKey = apiKey.trim();
        if (isPrivilegedKey(normalizedKey)) {
            throw new IllegalArgumentException(
                    "A Supabase service-role/secret key cannot be used by the desktop application.");
        }
        String normalizedUrl = baseUrl.trim().replaceAll("/+$", "");
        URI uri;
        try {
            uri = URI.create(normalizedUrl);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("SUPABASE_URL must be a valid HTTP(S) URL.", ex);
        }
        String scheme = uri.getScheme();
        String host = uri.getHost();
        boolean localHttp = "http".equalsIgnoreCase(scheme)
                && ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host)
                        || "::1".equals(host) || "[::1]".equals(host));
        if (host == null || uri.getUserInfo() != null || uri.getRawQuery() != null
                || uri.getRawFragment() != null || !(("https".equalsIgnoreCase(scheme)) || localHttp)
                || (uri.getRawPath() != null && !uri.getRawPath().isEmpty()
                        && !"/".equals(uri.getRawPath()))) {
            throw new IllegalArgumentException(
                    "SUPABASE_URL must be an HTTPS project URL (HTTP is allowed only on localhost).");
        }
        this.baseUrl = normalizedUrl;
        this.apiKey = normalizedKey;
    }

    public void setAccessToken(String accessToken) {
        setSession(accessToken, null);
    }

    public void setSession(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void clearAccessToken() {
        this.accessToken = null;
        this.refreshToken = null;
    }

    public JsonObject authPost(String path, JsonObject body) {
        return request("POST", baseUrl + "/auth/v1/" + path, body, null, false).getAsJsonObject();
    }

    public void authLogout() {
        request("POST", baseUrl + "/auth/v1/logout", new JsonObject(), null, true);
    }

    public boolean hasAccessToken() {
        return accessToken != null && !accessToken.trim().isEmpty();
    }

    public JsonArray selectAll(String table) {
        validateTable(table);
        return request("GET", baseUrl + "/rest/v1/" + table + "?select=*", null, null, true).getAsJsonArray();
    }

    public JsonArray selectById(String table, int id) {
        validateTable(table);
        return request("GET", baseUrl + "/rest/v1/" + table + "?id=eq." + id + "&select=*",
                null, null, true).getAsJsonArray();
    }

    public JsonArray insert(String table, JsonObject row) {
        validateTable(table);
        return request("POST", baseUrl + "/rest/v1/" + table, row, "return=representation", true)
                .getAsJsonArray();
    }

    public JsonArray insertMany(String table, JsonArray rows) {
        validateTable(table);
        return request("POST", baseUrl + "/rest/v1/" + table, rows, "return=minimal", true)
                .getAsJsonArray();
    }

    public JsonArray update(String table, int id, JsonObject row) {
        validateTable(table);
        return request("PATCH", baseUrl + "/rest/v1/" + table + "?id=eq." + id,
                row, "return=representation", true).getAsJsonArray();
    }

    public JsonArray delete(String table, String filter) {
        validateTable(table);
        if (filter == null || !filter.matches("[a-z_]+=(eq|in)\\.[0-9(),]+")) {
            throw new IllegalArgumentException("Invalid Supabase delete filter.");
        }
        return request("DELETE", baseUrl + "/rest/v1/" + table + "?" + filter,
                null, "return=representation", true).getAsJsonArray();
    }

    public void replaceEventVolunteers(int eventId, JsonArray volunteerIds) {
        validateTable("event_volunteers");
        delete("event_volunteers", "event_id=eq." + eventId);
        if (volunteerIds.size() == 0) {
            return;
        }
        JsonArray rows = new JsonArray();
        for (JsonElement id : volunteerIds) {
            JsonObject row = new JsonObject();
            row.addProperty("event_id", eventId);
            row.addProperty("volunteer_id", id.getAsInt());
            rows.add(row);
        }
        insertMany("event_volunteers", rows);
    }

    private JsonElement request(String method, String address, JsonElement body, String prefer,
                                boolean authenticated) {
        return request(method, address, body, prefer, authenticated, true);
    }

    private JsonElement request(String method, String address, JsonElement body, String prefer,
                                boolean authenticated, boolean allowRefresh) {
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder()
                    .uri(URI.create(address))
                    .timeout(Duration.ofMillis(TIMEOUT_MILLIS))
                    .header("apikey", apiKey)
                    .header("Accept", "application/json");
            if (authenticated) {
                String token = accessToken;
                if (token == null || token.trim().isEmpty()) {
                    throw new SupabaseException("Sign in is required before accessing application data.");
                }
                request.header("Authorization", "Bearer " + token);
            }
            if (prefer != null) {
                request.header("Prefer", prefer);
            }
            if (body != null) {
                request.header("Content-Type", "application/json")
                        .method(method, HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
            } else {
                request.method(method, HttpRequest.BodyPublishers.noBody());
            }
            HttpResponse<String> httpResponse =
                    httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int status = httpResponse.statusCode();
            String response = httpResponse.body();
            if (status == 401 && authenticated && allowRefresh
                    && hasRefreshToken()) {
                refreshSession();
                return request(method, address, body, prefer, true, false);
            }
            if (status < 200 || status >= 300) {
                throw new SupabaseException(errorMessage(response, status));
            }
            if (response.trim().isEmpty()) {
                return new JsonArray();
            }
            return JsonParser.parseString(response);
        } catch (IOException | IllegalArgumentException ex) {
            throw new SupabaseException("Could not communicate with Supabase: " + ex.getMessage(), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new SupabaseException("Supabase request was interrupted.", ex);
        }
    }

    private synchronized void refreshSession() {
        String currentRefreshToken = refreshToken;
        if (currentRefreshToken == null || currentRefreshToken.trim().isEmpty()) {
            throw new SupabaseException("Your session expired. Sign in again to continue.");
        }
        JsonObject body = new JsonObject();
        body.addProperty("refresh_token", currentRefreshToken);
        JsonObject response = request("POST",
                baseUrl + "/auth/v1/token?grant_type=refresh_token", body, null, false, false)
                .getAsJsonObject();
        if (!response.has("access_token") || !response.has("refresh_token")) {
            clearAccessToken();
            throw new SupabaseException("Supabase could not refresh the session. Sign in again.");
        }
        setSession(response.get("access_token").getAsString(),
                response.get("refresh_token").getAsString());
    }

    private boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.trim().isEmpty();
    }

    private boolean isPrivilegedKey(String key) {
        if (key.startsWith("sb_secret_")) {
            return true;
        }
        String[] parts = key.split("\\.");
        if (parts.length != 3) {
            return false;
        }
        try {
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonObject claims = JsonParser.parseString(payload).getAsJsonObject();
            return claims.has("role") && "service_role".equals(claims.get("role").getAsString());
        } catch (IllegalArgumentException | IllegalStateException | JsonParseException ex) {
            return false;
        }
    }

    private String errorMessage(String response, int status) {
        try {
            JsonObject error = JsonParser.parseString(response).getAsJsonObject();
            for (String key : new String[]{"msg", "message", "error_description", "error"}) {
                if (error.has(key) && !error.get(key).isJsonNull()) {
                    return "Supabase request failed (" + status + "): " + error.get(key).getAsString();
                }
            }
        } catch (RuntimeException ignored) {
            // Return the HTTP status when Supabase does not return a JSON error.
        }
        return "Supabase request failed with HTTP status " + status + ".";
    }

    private void validateTable(String table) {
        if (table == null || !TABLE_NAME.matcher(table).matches()) {
            throw new IllegalArgumentException("Invalid Supabase table name.");
        }
    }
}
