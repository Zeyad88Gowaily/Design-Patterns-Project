package nonprofit.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import nonprofit.model.Event;
import nonprofit.model.Registration;
import nonprofit.model.Ticket;
import nonprofit.model.Volunteer;
import nonprofit.observer.Subject;
import nonprofit.repository.DataStore;

/**
 * Event, registration, ticket and attendance operations.
 * Subject in the Observer pattern: observers are told when an event is created or changed.
 */
public class EventService extends Subject {
    private DataStore db = DataStore.getInstance();
    private static final int TICKET_START = 1000;   // first ticket is TKT-1001

    public Event create(String name, String date, String location) {
        Event e = db.events.add(new Event(name, date, location));
        notifyObservers("EVENT_CREATED", e);
        return e;
    }

    public Event create(String name, String date, String location, String eventType,
                        List<String> requiredSkills) {
        Event event = new Event(name, date, location);
        event.setEventType(eventType == null ? "" : eventType);
        if (requiredSkills != null) {
            event.getRequiredSkills().addAll(requiredSkills);
        }
        Event saved = db.events.add(event);
        notifyObservers("EVENT_CREATED", saved);
        return saved;
    }

    public List<Event> getAll()     { return db.events.getAll(); }
    public Event getById(int id)    { return db.events.getById(id); }

    public boolean update(int id, String name, String date, String location) {
        Event e = db.events.getById(id);
        if (e == null) return false;
        if (name != null && name.length() > 0)     e.setName(name);
        if (date != null && date.length() > 0)     e.setDate(date);
        if (location != null && location.length() > 0) e.setLocation(location);
        if (!db.events.save(e)) return false;
        notifyObservers("EVENT_UPDATED", e);
        return true;
    }

    public boolean delete(int id) { return db.events.remove(id); }

    public boolean assignVolunteer(int eventId, int volunteerId) {
        Event e = db.events.getById(eventId);
        Volunteer v = db.volunteers.getById(volunteerId);
        if (e == null || v == null) return false;
        for (Volunteer assigned : e.getVolunteers()) {
            if (assigned.getId() == volunteerId) return false;   // already assigned
        }
        e.getVolunteers().add(v);
        if (!db.events.save(e)) return false;
        notifyObservers("EVENT_UPDATED", e);
        return true;
    }

    /** Volunteers who have at least one of the event's required skills. */
    public List<Volunteer> suggestVolunteers(int eventId) {
        Event e = db.events.getById(eventId);
        if (e == null) return new ArrayList<Volunteer>();

        List<Volunteer> suggestions = new ArrayList<Volunteer>();
        for (Volunteer v : db.volunteers.getAll()) {
            for (String required : e.getRequiredSkills()) {
                for (String skill : v.getSkills()) {
                    if (skill.equalsIgnoreCase(required) && !suggestions.contains(v)) {
                        suggestions.add(v);
                    }
                }
            }
        }
        return suggestions;
    }

    /** Returns null if the event or volunteer is missing, or the volunteer is already registered. */
    public Registration register(int eventId, int volunteerId) {
        Event e = db.events.getById(eventId);
        Volunteer v = db.volunteers.getById(volunteerId);
        if (e == null || v == null) return null;

        for (Registration r : db.registrations.getAll()) {
            if (r.getEventId() == eventId && r.getVolunteerId() == volunteerId) {
                return null;
            }
        }

        Registration reg = new Registration(volunteerId, eventId, LocalDate.now().toString());
        return db.registrations.add(reg);
    }

    /** Registrations for one event. */
    public List<Registration> getRegistrations(int eventId) {
        List<Registration> result = new ArrayList<Registration>();
        for (Registration r : db.registrations.getAll()) {
            if (r.getEventId() == eventId) result.add(r);
        }
        return result;
    }

    /** Returns null if the volunteer is not registered or already has a ticket. */
    public Ticket issueTicket(int eventId, int volunteerId) {
        boolean registered = false;
        for (Registration r : db.registrations.getAll()) {
            if (r.getEventId() == eventId && r.getVolunteerId() == volunteerId) {
                registered = true;
                break;
            }
        }
        if (!registered) return null;

        // Continue from the highest stored ticket number so numbers stay unique across restarts.
        int highest = TICKET_START;
        for (Ticket t : db.tickets.getAll()) {
            if (t.getEventId() == eventId && t.getVolunteerId() == volunteerId) {
                return null;
            }
            highest = Math.max(highest, numberOf(t.getTicketNumber()));
        }

        String ticketNumber = "TKT-" + (highest + 1);
        Ticket ticket = new Ticket(ticketNumber, eventId, volunteerId);
        return db.tickets.add(ticket);
    }

    private static int numberOf(String ticketNumber) {
        try {
            return Integer.parseInt(ticketNumber.substring(ticketNumber.lastIndexOf('-') + 1));
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /** Tickets for one event. */
    public List<Ticket> getTickets(int eventId) {
        List<Ticket> result = new ArrayList<Ticket>();
        for (Ticket t : db.tickets.getAll()) {
            if (t.getEventId() == eventId) result.add(t);
        }
        return result;
    }

    /** Checks the ticket in. Returns false if it is missing or already used. */
    public boolean markAttendance(int ticketId) {
        Ticket t = db.tickets.getById(ticketId);
        if (t == null || t.isCheckedIn()) return false;
        t.checkIn();
        return db.tickets.save(t);
    }
}
