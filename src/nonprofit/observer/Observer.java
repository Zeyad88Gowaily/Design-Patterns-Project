package nonprofit.observer;

/** OBSERVER: anything that wants to hear about changes (new donation, event updated...). */
public interface Observer {
    void update(String eventType, Object data);
}
