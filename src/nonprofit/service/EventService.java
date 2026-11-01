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
 * Business logic for events. Also a SUBJECT: notifies observers when an
 * event is created or changed.
 *
 * TODO [PHASE 2 - Person B] COMMAND (undo register/assign) and STATE (event lifecycle).
 */
public class EventService extends Subject {
    private DataStore db = DataStore.getInstance();
    private int ticketSeq = 1000;   // auto-incrementing ticket numbers

    // ==================== Event CRUD ====================

    public Event create(String name, String date, String location) {
        Event e = db.events.add(new Event(name, date, location));
        notifyObservers("EVENT_CREATED", e);
        return e;
    }

    public List<Event> getAll()     { return db.events.getAll(); }
    public Event getById(int id)    { return db.events.getById(id); }

    public boolean update(int id, String name, String date, String location) {
        Event e = db.events.getById(id);
        if (e == null) return false;
        if (name != null && name.length() > 0)     e.setName(name);
        if (date != null && date.length() > 0)     e.setDate(date);
        if (location != null && location.length() > 0) e.setLocation(location);
        notifyObservers("EVENT_UPDATED", e);
        return true;
    }

    public boolean delete(int id) { return db.events.remove(id); }

    // ==================== Volunteer assignment (with skill matching) ====================

    public boolean assignVolunteer(int eventId, int volunteerId) {
        Event e = db.events.getById(eventId);
        Volunteer v = db.volunteers.getById(volunteerId);
        if (e == null || v == null) return false;
        e.getVolunteers().add(v);
        notifyObservers("EVENT_UPDATED", e);
        return true;
    }

    /** Suggest volunteers whose skills match the event's required skills. */
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

    // ==================== Registration ====================

    /** Register a volunteer for an event. */
    public Registration register(int eventId, int volunteerId) {
        Event e = db.events.getById(eventId);
        Volunteer v = db.volunteers.getById(volunteerId);
        if (e == null || v == null) return null;

        // Prevent duplicate registration
        for (Registration r : db.registrations.getAll()) {
            if (r.getEventId() == eventId && r.getVolunteerId() == volunteerId) {
                return null;
            }
        }

        Registration reg = new Registration(volunteerId, eventId, LocalDate.now().toString());
        db.registrations.add(reg);
        return reg;
    }

    /** Get all registrations for an event. */
    public List<Registration> getRegistrations(int eventId) {
        List<Registration> result = new ArrayList<Registration>();
        for (Registration r : db.registrations.getAll()) {
            if (r.getEventId() == eventId) result.add(r);
        }
        return result;
    }

    // ==================== Ticketing ====================

    /** Issue a ticket for a registered volunteer. */
    public Ticket issueTicket(int eventId, int volunteerId) {
        // Must be registered first
        boolean registered = false;
        for (Registration r : db.registrations.getAll()) {
            if (r.getEventId() == eventId && r.getVolunteerId() == volunteerId) {
                registered = true;
                break;
            }
        }
        if (!registered) return null;

        // Prevent duplicate ticket
        for (Ticket t : db.tickets.getAll()) {
            if (t.getEventId() == eventId && t.getVolunteerId() == volunteerId) {
                return null;
            }
        }

        String ticketNumber = "TKT-" + (ticketSeq++);
        Ticket ticket = new Ticket(ticketNumber, eventId, volunteerId);
        db.tickets.add(ticket);
        return ticket;
    }

    /** List tickets for an event. */
    public List<Ticket> getTickets(int eventId) {
        List<Ticket> result = new ArrayList<Ticket>();
        for (Ticket t : db.tickets.getAll()) {
            if (t.getEventId() == eventId) result.add(t);
        }
        return result;
    }

    // ==================== Attendance ====================

    /** Mark a ticket as checked-in (attendance). */
    public boolean markAttendance(int ticketId) {
        Ticket t = db.tickets.getById(ticketId);
        if (t == null || t.isCheckedIn()) return false;
        t.checkIn();
        return true;
    }
}
