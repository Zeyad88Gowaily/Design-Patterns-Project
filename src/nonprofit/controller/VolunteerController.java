package nonprofit.controller;

import nonprofit.service.VolunteerService;
import nonprofit.view.ConsoleView;

/** MVC - Controller for volunteers (Person B). */
public class VolunteerController {
    private VolunteerService service;
    private ConsoleView view;

    public VolunteerController(VolunteerService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void menu() {
        while (true) {
            view.show("\n== Volunteers ==\n1) Add volunteer  2) List volunteers  0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;
            if (choice == 1) {
                view.show("Added " + service.add(view.ask("Name"), view.ask("Email"), view.ask("Skill")));
            } else if (choice == 2) {
                view.showList("Volunteers", service.getAll());
            }
            // TODO [PHASE 1 - Person B] edit/delete volunteer, set availability
        }
    }
}
