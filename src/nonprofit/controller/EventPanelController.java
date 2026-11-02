package nonprofit.controller;

import java.util.List;

import nonprofit.model.Event;
import nonprofit.model.Registration;
import nonprofit.model.Ticket;
import nonprofit.model.Volunteer;
import nonprofit.service.EventService;

public class EventPanelController {
    private final EventService service;

    public EventPanelController(EventService service) {
        this.service = service;
    }

    public Event create(String name, String date, String location, String type,
                        List<String> requiredSkills) {
        return service.create(name, date, location, type, requiredSkills);
    }

    public Event getById(int id) {
        return service.getById(id);
    }

    public boolean update(int id, String name, String date, String location) {
        return service.update(id, name, date, location);
    }

    public boolean delete(int id) {
        return service.delete(id);
    }

    public boolean assignVolunteer(int eventId, int volunteerId) {
        return service.assignVolunteer(eventId, volunteerId);
    }

    public List<Volunteer> suggestVolunteers(int eventId) {
        return service.suggestVolunteers(eventId);
    }

    public Registration register(int eventId, int volunteerId) {
        return service.register(eventId, volunteerId);
    }

    public Ticket issueTicket(int eventId, int volunteerId) {
        return service.issueTicket(eventId, volunteerId);
    }

    public boolean markAttendance(int ticketId) {
        return service.markAttendance(ticketId);
    }

    public List<Registration> getRegistrations(int eventId) {
        return service.getRegistrations(eventId);
    }

    public List<Ticket> getTickets(int eventId) {
        return service.getTickets(eventId);
    }

    public List<Event> getAll() {
        return service.getAll();
    }
}
