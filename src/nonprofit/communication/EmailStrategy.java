package nonprofit.communication;

public class EmailStrategy implements NotificationStrategy {
    public void send(String to, String message) {
        // Fake sending: just print. TODO [PHASE 2 - Person C] real/mocked gateway via ADAPTER.
        System.out.println("[EMAIL to " + to + "] " + message);
    }
}
