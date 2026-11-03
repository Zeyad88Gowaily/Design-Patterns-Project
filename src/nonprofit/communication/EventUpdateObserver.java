package nonprofit.communication;

import nonprofit.model.Event;
import nonprofit.model.Volunteer;
import nonprofit.observer.Observer;
import nonprofit.repository.DataStore;

/** Observer: tells volunteers when an event is created or updated. */
public class EventUpdateObserver implements Observer {
    private Notifier notifier = new Notifier(NotificationFactory.create("email"));

    @Override
    public void update(String eventType, Object data) {
        if (!(data instanceof Event)) return;
        Event event = (Event) data;

        if ("EVENT_CREATED".equals(eventType)) {
            // Announce the new event to every volunteer.
            for (Volunteer v : DataStore.getInstance().volunteers.getAll()) {
                notifier.notify(v.getEmail(),
                        "New event created: \"" + event.getName()
                                + "\" on " + event.getDate()
                                + " at " + event.getLocation() + ". Sign up now!");
            }
        } else if ("EVENT_UPDATED".equals(eventType)) {
            // Tell the volunteers already assigned.
            for (Volunteer v : event.getVolunteers()) {
                notifier.notify(v.getEmail(),
                        "Event update: \"" + event.getName()
                                + "\" (" + event.getDate() + ", " + event.getLocation()
                                + ") has been updated. Please check the latest details.");
            }
        }
    }
}
