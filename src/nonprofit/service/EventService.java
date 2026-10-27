package nonprofit.service;

import java.util.List;

import nonprofit.model.Event;
import nonprofit.model.Volunteer;
import nonprofit.observer.Subject;
import nonprofit.repository.DataStore;

/**
 * Also a SUBJECT: notifies observers when an event is created or changed.
 *
 * TODO [PHASE 1 - Person B] OBSERVER: write EventUpdateObserver (communication package)
 *      that messages registered volunteers/attendees; attach it in Main.
 * TODO [PHASE 1 - Person B] registration, ticketing and attendance (new model classes).
 * TODO [PHASE 2 - Person B] COMMAND (undo register/assign) and STATE (event lifecycle).
 */
public class EventService extends Subject {
    private DataStore db = DataStore.getInstance();

    public Event create(String name, String date, String location) {
        Event e = db.events.add(new Event(name, date, location));
        notifyObservers("EVENT_CREATED", e);
        return e;
    }

    public boolean assignVolunteer(int eventId, int volunteerId) {
        Event e = db.events.getById(eventId);
        Volunteer v = db.volunteers.getById(volunteerId);
        if (e == null || v == null) {
            return false;
        }
        e.getVolunteers().add(v);
        notifyObservers("EVENT_UPDATED", e);
        return true;
    }

    public List<Event> getAll() { return db.events.getAll(); }
}
