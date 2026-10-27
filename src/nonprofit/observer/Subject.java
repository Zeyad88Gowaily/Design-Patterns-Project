package nonprofit.observer;

import java.util.ArrayList;
import java.util.List;

/** OBSERVER: services extend this and call notifyObservers() when something changes. */
public class Subject {
    private List<Observer> observers = new ArrayList<Observer>();

    public void attach(Observer o) { observers.add(o); }
    public void detach(Observer o) { observers.remove(o); }

    protected void notifyObservers(String eventType, Object data) {
        for (Observer o : observers) {
            o.update(eventType, data);
        }
    }
}
