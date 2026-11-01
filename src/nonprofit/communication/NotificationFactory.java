package nonprofit.communication;

/**
 * FACTORY: creates the notification strategy from a name.
 * STRATEGY pattern: the user or system can pick email, sms, or social media at runtime.
 */
public class NotificationFactory {
    public static NotificationStrategy create(String type) {
        if ("sms".equalsIgnoreCase(type)) {
            return new SmsStrategy();
        }
        if ("social".equalsIgnoreCase(type) || "socialmedia".equalsIgnoreCase(type)) {
            return new SocialMediaStrategy();
        }
        return new EmailStrategy();   // default
    }
}
