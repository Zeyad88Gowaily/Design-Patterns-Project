package nonprofit.communication;

/** Strategy: one way of delivering a message. */
public interface NotificationStrategy {
    void send(String to, String message);

    /** The message as one line of text; the Notifications tab shows this in its log. */
    String format(String to, String message);
}
