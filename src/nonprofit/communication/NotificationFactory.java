package nonprofit.communication;

/**
 * FACTORY: creates the strategy from a name (config / user input).
 *
 * TODO [PHASE 1 - Person C] add SocialMediaStrategy ("social") and any strategy the client asks for.
 */
public class NotificationFactory {
    public static NotificationStrategy create(String type) {
        if ("sms".equalsIgnoreCase(type)) {
            return new SmsStrategy();
        }
        return new EmailStrategy();   // default
    }
}
