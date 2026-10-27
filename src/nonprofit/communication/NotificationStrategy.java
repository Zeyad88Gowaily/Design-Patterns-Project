package nonprofit.communication;

/** STRATEGY: how a message is delivered. Swap at runtime in Notifier. */
public interface NotificationStrategy {
    void send(String to, String message);
}
