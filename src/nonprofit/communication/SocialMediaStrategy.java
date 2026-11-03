package nonprofit.communication;

/** Strategy: posts the message to social media (simulated). */
public class SocialMediaStrategy implements NotificationStrategy {
    @Override
    public void send(String to, String message) {
        System.out.println(format(to, message));
    }

    @Override
    public String format(String to, String message) {
        return "[SOCIAL MEDIA post for " + to + "] " + message;
    }
}
