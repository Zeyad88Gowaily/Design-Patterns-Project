package nonprofit.service;

import com.google.gson.JsonObject;

import nonprofit.repository.SupabaseException;
import nonprofit.repository.SupabaseRestClient;

public class SupabaseAuthService {
    private final SupabaseRestClient client;

    public SupabaseAuthService(SupabaseRestClient client) {
        this.client = client;
    }

    public AuthSession signIn(String email, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);
        return sessionFrom(client.authPost("token?grant_type=password", body));
    }

    public AuthSession signUp(String email, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);
        JsonObject response = client.authPost("signup", body);
        if (!response.has("access_token") || response.get("access_token").isJsonNull()) {
            return null;
        }
        return sessionFrom(response);
    }

    public void signOut() {
        try {
            if (client.hasAccessToken()) {
                client.authLogout();
            }
        } finally {
            client.clearAccessToken();
        }
    }

    private AuthSession sessionFrom(JsonObject response) {
        if (!response.has("access_token") || response.get("access_token").isJsonNull()) {
            throw new SupabaseException("Supabase authentication response did not contain an access token.");
        }
        String token = response.get("access_token").getAsString();
        String refreshToken = response.has("refresh_token") && !response.get("refresh_token").isJsonNull()
                ? response.get("refresh_token").getAsString() : null;
        client.setSession(token, refreshToken);
        return new AuthSession(token, refreshToken);
    }

    public static class AuthSession {
        private final String accessToken;
        private final String refreshToken;

        private AuthSession(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}
