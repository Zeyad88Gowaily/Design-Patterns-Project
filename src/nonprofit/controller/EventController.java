package nonprofit.controller;

import nonprofit.model.Event;
import nonprofit.model.Registration;
import nonprofit.model.Ticket;
import nonprofit.service.EventService;
import nonprofit.view.ConsoleView;

/** MVC - Controller for events (Person B). */
public class EventController {
    private EventService service;
    private ConsoleView view;

    public EventController(EventService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void menu() {
        while (true) {
            view.show("\n== Events ==");
            view.show("1) Create event     2) List events       3) Edit event        4) Delete event");
            view.show("5) Assign volunteer 6) Suggest volunteers 7) Register volunteer 8) Issue ticket");
            view.show("9) Mark attendance 10) List registrations 11) List tickets");
            view.show("0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;

            switch (choice) {
                case  1: doCreate(); break;
                case  2: view.showList("Events", service.getAll()); break;
                case  3: doEdit(); break;
                case  4: doDelete(); break;
                case  5: doAssign(); break;
                case  6: doSuggest(); break;
                case  7: doRegister(); break;
                case  8: doIssueTicket(); break;
                case  9: doMarkAttendance(); break;
                case 10: doListRegistrations(); break;
                case 11: doListTickets(); break;
                default: view.show("Invalid choice.");
            }
        }
    }

    private void doCreate() {
        String name     = view.ask("Name");
        String date     = view.ask("Date (e.g. 2026-12-01)");
        String location = view.ask("Location");
        String type = view.ask("Event type (or blank)");
        String skills = view.ask("Required skills (comma-separated, or blank)");
        java.util.List<String> requiredSkills = new java.util.ArrayList<String>();
        if (skills.length() > 0) {
            for (String s : skills.split(",")) {
                String trimmed = s.trim();
                if (trimmed.length() > 0) requiredSkills.add(trimmed);
            }
        }
        Event e = service.create(name, date, location, type, requiredSkills);
        view.show("Created " + e);
    }

    private void doEdit() {
        view.showList("Events", service.getAll());
        int id = view.askInt("Event id to edit");
        Event e = service.getById(id);
        if (e == null) { view.show("Not found."); return; }
        view.show("Current: " + e);
        String name     = view.ask("New name (blank to keep)");
        String date     = view.ask("New date (blank to keep)");
        String location = view.ask("New location (blank to keep)");
        service.update(id, name, date, location);
        view.show("Updated: " + service.getById(id));
    }

    private void doDelete() {
        view.showList("Events", service.getAll());
        int id = view.askInt("Event id to delete");
        view.show(service.delete(id) ? "Deleted." : "Not found.");
    }

    private void doAssign() {
        int eventId = view.askInt("Event id");
        int volId   = view.askInt("Volunteer id");
        boolean ok = service.assignVolunteer(eventId, volId);
        view.show(ok ? "Assigned." : "Event or volunteer not found.");
    }

    private void doSuggest() {
        int eventId = view.askInt("Event id");
        view.showList("Suggested volunteers (matching skills)", service.suggestVolunteers(eventId));
    }

    private void doRegister() {
        int eventId = view.askInt("Event id");
        int volId   = view.askInt("Volunteer id");
        Registration reg = service.register(eventId, volId);
        view.show(reg != null ? "Registered: " + reg : "Registration failed (not found or already registered).");
    }

    private void doIssueTicket() {
        int eventId = view.askInt("Event id");
        int volId   = view.askInt("Volunteer id");
        Ticket t = service.issueTicket(eventId, volId);
        view.show(t != null ? "Ticket issued: " + t : "Failed (not registered or ticket already issued).");
    }

    private void doMarkAttendance() {
        int ticketId = view.askInt("Ticket id");
        view.show(service.markAttendance(ticketId) ? "Checked in!" : "Ticket not found or already checked in.");
    }

    private void doListRegistrations() {
        int eventId = view.askInt("Event id");
        view.showList("Registrations for event #" + eventId, service.getRegistrations(eventId));
    }

    private void doListTickets() {
        int eventId = view.askInt("Event id");
        view.showList("Tickets for event #" + eventId, service.getTickets(eventId));
    }
}
