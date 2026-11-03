package nonprofit.communication;

public class SmsStrategy implements NotificationStrategy {
    public void send(String to, String message) {
        System.out.println(format(to, message));
    }

    public String format(String to, String message) {
        return "[SMS to " + to + "] " + message;
    }
}
