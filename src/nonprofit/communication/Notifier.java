package nonprofit.communication;

/** Context class of the Strategy pattern. */
public class Notifier {
    private NotificationStrategy strategy;

    public Notifier(NotificationStrategy strategy) { this.strategy = strategy; }

    public void setStrategy(NotificationStrategy strategy) { this.strategy = strategy; }

    public void notify(String to, String message) { strategy.send(to, message); }
}
