package nonprofit.communication;

/** Factory: returns the notification strategy for a channel name (email, sms or social). */
public class NotificationFactory {
    public static NotificationStrategy create(String type) {
        if ("sms".equalsIgnoreCase(type)) {
            return new SmsStrategy();
        }
        if ("social".equalsIgnoreCase(type) || "socialmedia".equalsIgnoreCase(type)) {
            return new SocialMediaStrategy();
        }
        return new EmailStrategy();   // email is the default
    }
}
