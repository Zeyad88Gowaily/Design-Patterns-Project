package nonprofit.controller;

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
            view.show("\n== Events ==\n1) Create event  2) List events  3) Assign volunteer  0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;
            if (choice == 1) {
                view.show("Created " + service.create(view.ask("Name"), view.ask("Date"), view.ask("Location")));
            } else if (choice == 2) {
                view.showList("Events", service.getAll());
            } else if (choice == 3) {
                boolean ok = service.assignVolunteer(view.askInt("Event id"), view.askInt("Volunteer id"));
                view.show(ok ? "Assigned." : "Event or volunteer not found.");
            }
            // TODO [PHASE 1 - Person B] edit/delete event, registration + attendance
        }
    }
}
