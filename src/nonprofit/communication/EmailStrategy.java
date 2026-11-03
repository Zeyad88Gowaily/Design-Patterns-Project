package nonprofit.communication;

public class EmailStrategy implements NotificationStrategy {
    public void send(String to, String message) {
        // Simulated: prints instead of sending.
        System.out.println(format(to, message));
    }

    public String format(String to, String message) {
        return "[EMAIL to " + to + "] " + message;
    }
}
