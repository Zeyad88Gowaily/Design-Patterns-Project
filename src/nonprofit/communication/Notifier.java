package nonprofit.communication;

/** Strategy context: sends messages with whichever strategy is currently set. */
public class Notifier {
    private NotificationStrategy strategy;

    public Notifier(NotificationStrategy strategy) { this.strategy = strategy; }

    public void setStrategy(NotificationStrategy strategy) { this.strategy = strategy; }

    public void notify(String to, String message) { strategy.send(to, message); }
}
