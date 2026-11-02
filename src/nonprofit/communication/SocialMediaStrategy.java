package nonprofit.communication;

/**
 * STRATEGY pattern: sends a notification via social media (simulated).
 */
public class SocialMediaStrategy implements NotificationStrategy {
    @Override
    public void send(String to, String message) {
        System.out.println("[SOCIAL MEDIA post for " + to + "] " + message);
    }
}
