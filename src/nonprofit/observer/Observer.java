package nonprofit.observer;

/** Receives updates from a Subject. */
public interface Observer {
    void update(String eventType, Object data);
}
