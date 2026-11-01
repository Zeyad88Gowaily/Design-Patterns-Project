package nonprofit.communication;

import nonprofit.model.Event;
import nonprofit.model.Volunteer;
import nonprofit.observer.Observer;

/**
 * OBSERVER pattern: notifies volunteers when an event is created or updated.
 *
 * Why Observer? — Event changes (new event, volunteer assigned, details changed)
 * need to reach the right people automatically without the EventService knowing
 * who those people are or how to contact them. The Observer pattern decouples
 * the "what happened" (EventService) from the "who cares" (this class).
 */
public class EventUpdateObserver implements Observer {
    private Notifier notifier = new Notifier(NotificationFactory.create("email"));

    @Override
    public void update(String eventType, Object data) {
        if (!(data instanceof Event)) return;
        Event event = (Event) data;

        if ("EVENT_CREATED".equals(eventType)) {
            // Announce the new event (logged to console as a general broadcast).
            notifier.notify("all-volunteers@nonprofit.org",
                    "New event created: \"" + event.getName()
                            + "\" on " + event.getDate()
                            + " at " + event.getLocation() + ". Sign up now!");
        } else if ("EVENT_UPDATED".equals(eventType)) {
            // Notify every volunteer already assigned to this event.
            for (Volunteer v : event.getVolunteers()) {
                notifier.notify(v.getEmail(),
                        "Event update: \"" + event.getName()
                                + "\" (" + event.getDate() + ", " + event.getLocation()
                                + ") has been updated. Please check the latest details.");
            }
        }
    }
}
