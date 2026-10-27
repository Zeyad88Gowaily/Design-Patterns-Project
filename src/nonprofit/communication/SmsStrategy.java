package nonprofit.communication;

public class SmsStrategy implements NotificationStrategy {
    public void send(String to, String message) {
        System.out.println("[SMS to " + to + "] " + message);
    }
}
