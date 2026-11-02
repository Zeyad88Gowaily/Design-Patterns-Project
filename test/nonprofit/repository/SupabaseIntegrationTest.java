package nonprofit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import nonprofit.model.Donor;
import nonprofit.repository.DonationRepository;
import nonprofit.repository.EventRepository;
import nonprofit.service.EventService;
import nonprofit.service.SupabaseAuthService;
import nonprofit.controller.EventPanelController;

class SupabaseIntegrationTest {
    private HttpServer server;

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void rejectsNonLocalHttpSupabaseUrls() {
        assertThrows(IllegalArgumentException.class,
                () -> new SupabaseRestClient("http://example.org", "test-api-key"));
    }

    @Test
    void rejectsSupabaseServiceRoleKeys() {
        assertThrows(IllegalArgumentException.class,
                () -> new SupabaseRestClient("https://project.supabase.co", "sb_secret_test"));
        assertThrows(IllegalArgumentException.class,
                () -> new SupabaseRestClient("https://project.supabase.co",
                        "header.eyJyb2xlIjoic2VydmljZV9yb2xlIn0.signature"));
    }

    @Test
    void refreshesExpiredAccessTokenAndRetriesTheDataRequest() throws Exception {
        List<String> dataAuthorization = new ArrayList<String>();
        List<String> refreshBodies = new ArrayList<String>();
        startServer(exchange -> {
            String path = exchange.getRequestURI().getPath();
            if ("/auth/v1/token".equals(path)) {
                String body = readBody(exchange);
                if (body.contains("\"email\"")) {
                    respond(exchange, 200,
                            "{\"access_token\":\"expired-token\",\"refresh_token\":\"refresh-one\"}");
                } else {
                    refreshBodies.add(body);
                    respond(exchange, 200,
                            "{\"access_token\":\"fresh-token\",\"refresh_token\":\"refresh-two\"}");
                }
                return;
            }
            if ("/rest/v1/donors".equals(path)) {
                String authorization = exchange.getRequestHeaders().getFirst("Authorization");
                dataAuthorization.add(authorization);
                if ("Bearer expired-token".equals(authorization)) {
                    respond(exchange, 401, "{\"message\":\"JWT expired\"}");
                } else {
                    respond(exchange, 200, "[]");
                }
                return;
            }
            respond(exchange, 404, "{}");
        });

        SupabaseRestClient client = newClient();
        new SupabaseAuthService(client).signIn("person@example.org", "safe-test-password");
        JsonArray records = client.selectAll("donors");

        assertEquals(0, records.size());
        assertEquals(List.of("Bearer expired-token", "Bearer fresh-token"), dataAuthorization);
        assertEquals(1, refreshBodies.size());
        assertTrue(refreshBodies.get(0).contains("\"refresh_token\":\"refresh-one\""));
    }

    @Test
    void signsUpAndRetainsTheReturnedSessionTokens() throws Exception {
        startServer(exchange -> {
            assertEquals("test-api-key", exchange.getRequestHeaders().getFirst("apikey"));
            assertTrue(readBody(exchange).contains("\"email\":\"new@example.org\""));
            respond(exchange, 200,
                    "{\"access_token\":\"signup-access\",\"refresh_token\":\"signup-refresh\"}");
        });

        SupabaseRestClient client = newClient();
        SupabaseAuthService.AuthSession session =
                new SupabaseAuthService(client).signUp("new@example.org", "safe-test-password");

        assertEquals("signup-access", session.getAccessToken());
        assertEquals("signup-refresh", session.getRefreshToken());
        assertTrue(client.hasAccessToken());
    }

    @Test
    void repositoryPersistsAndReloadsEntitiesThroughPostgrest() throws Exception {
        AtomicReference<JsonObject> storedRow = new AtomicReference<JsonObject>();
        startServer(exchange -> {
            String method = exchange.getRequestMethod();
            if ("POST".equals(method)) {
                JsonObject body = JsonParser.parseString(readBody(exchange)).getAsJsonObject();
                JsonObject row = new JsonObject();
                row.addProperty("id", 12);
                row.add("data", body.getAsJsonObject("data"));
                storedRow.set(row);
                respond(exchange, 201, "[" + row + "]");
            } else if ("GET".equals(method)) {
                JsonObject row = storedRow.get();
                respond(exchange, 200, row == null ? "[]" : "[" + row + "]");
            } else if ("PATCH".equals(method)) {
                JsonObject body = JsonParser.parseString(readBody(exchange)).getAsJsonObject();
                JsonObject row = storedRow.get();
                row.add("data", body.getAsJsonObject("data"));
                respond(exchange, 200, "[" + row + "]");
            } else if ("DELETE".equals(method)) {
                JsonObject row = storedRow.getAndSet(null);
                respond(exchange, 200, row == null ? "[]" : "[" + row + "]");
            } else {
                respond(exchange, 405, "{}");
            }
        });

        SupabaseRestClient client = newClient();
        client.setAccessToken("test-access-token");
        SupabaseRepository<Donor> donors =
                new SupabaseRepository<Donor>(client, "donors", Donor.class);
        Donor added = donors.add(new Donor("Rana", "rana@example.org", "555-0101", "sms"));

        assertEquals(12, added.getId());
        assertEquals("sms", donors.getById(12).getNotificationChannel());
        Donor donor = donors.getById(12);
        donor.setPhone("555-0102");
        assertTrue(donors.save(donor));
        assertEquals("555-0102", donors.getById(12).getPhone());
        assertTrue(donors.remove(12));
        assertFalse(donors.remove(12));
    }

    @Test
    void donationRepositoryResolvesTheCurrentDonorByForeignKey() throws Exception {
        startServer(exchange -> {
            if (exchange.getRequestURI().getPath().endsWith("/donations")) {
                respond(exchange, 200, "[{\"id\":22,\"donor_id\":7,\"data\":"
                        + "{\"entityKind\":\"inKind\",\"amount\":25.0,\"date\":\"2026-11-02\","
                        + "\"decoratorDescription\":\"\",\"item\":\"blankets\"}}]");
            } else if (exchange.getRequestURI().getPath().endsWith("/donors")) {
                respond(exchange, 200, "[{\"id\":7,\"data\":"
                        + "{\"name\":\"Updated Donor\",\"email\":\"updated@example.org\","
                        + "\"phone\":\"555-0100\",\"notificationChannel\":\"sms\"}}]");
            } else {
                respond(exchange, 404, "{}");
            }
        });

        SupabaseRestClient client = newClient();
        client.setAccessToken("test-access-token");
        DonationRepository repository = new DonationRepository(client);
        nonprofit.model.Donation donation = repository.getAll().get(0);

        assertEquals("Updated Donor", donation.getDonor().getName());
        assertEquals("sms", donation.getDonor().getNotificationChannel());
        assertEquals("blankets", ((nonprofit.model.InKindDonation) donation).getItem());
    }

    @Test
    void eventRepositoryHydratesVolunteerAssignmentsFromJoinRows() throws Exception {
        startServer(exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.endsWith("/events")) {
                respond(exchange, 200, "[{\"id\":5,\"data\":"
                        + "{\"name\":\"Community day\",\"date\":\"2026-11-02\","
                        + "\"location\":\"Town hall\",\"eventType\":\"Workshop\","
                        + "\"status\":\"PLANNED\",\"requiredSkills\":[\"First Aid\"]}}]");
            } else if (path.endsWith("/event_volunteers")) {
                respond(exchange, 200, "[{\"id\":3,\"event_id\":5,\"volunteer_id\":8}]");
            } else if (path.endsWith("/volunteers")) {
                respond(exchange, 200, "[{\"id\":8,\"data\":"
                        + "{\"name\":\"Sam Updated\",\"email\":\"sam@example.org\","
                        + "\"skills\":[\"First Aid\"],\"availability\":[],\"hours\":0.0}}]");
            } else {
                respond(exchange, 404, "{}");
            }
        });

        SupabaseRestClient client = newClient();
        client.setAccessToken("test-access-token");
        EventRepository repository = new EventRepository(client);
        nonprofit.model.Event event = repository.getAll().get(0);

        assertEquals(1, event.getVolunteers().size());
        assertEquals("Sam Updated", event.getVolunteers().get(0).getName());
        assertEquals(8, event.getVolunteers().get(0).getId());
    }

    @Test
    void swingEventControllerPersistsTypeAndRequiredSkillsAtCreation() throws Exception {
        AtomicReference<JsonObject> insertedEvent = new AtomicReference<JsonObject>();
        startServer(exchange -> {
            if ("/rest/v1/events".equals(exchange.getRequestURI().getPath())
                    && "POST".equals(exchange.getRequestMethod())) {
                JsonObject request = JsonParser.parseString(readBody(exchange)).getAsJsonObject();
                insertedEvent.set(request.getAsJsonObject("data"));
                JsonObject row = new JsonObject();
                row.addProperty("id", 31);
                row.add("data", insertedEvent.get());
                respond(exchange, 201, "[" + row + "]");
            } else if ("/rest/v1/event_volunteers".equals(exchange.getRequestURI().getPath())) {
                respond(exchange, 200, "[]");
            } else {
                respond(exchange, 404, "{}");
            }
        });

        SupabaseRestClient client = newClient();
        client.setAccessToken("test-access-token");
        DataStore.configure(client);
        EventPanelController controller = new EventPanelController(new EventService());

        nonprofit.model.Event event = controller.create("Food drive", "2026-11-08",
                "Community center", "Fundraiser", List.of("Driving", "Cooking"));

        assertEquals(31, event.getId());
        assertEquals("Fundraiser", insertedEvent.get().get("eventType").getAsString());
        assertEquals("Driving", insertedEvent.get().getAsJsonArray("requiredSkills").get(0).getAsString());
        assertEquals("Cooking", insertedEvent.get().getAsJsonArray("requiredSkills").get(1).getAsString());
    }

    private SupabaseRestClient newClient() {
        return new SupabaseRestClient("http://127.0.0.1:" + server.getAddress().getPort(), "test-api-key");
    }

    private void startServer(com.sun.net.httpserver.HttpHandler handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", handler);
        server.start();
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] response = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }
}
