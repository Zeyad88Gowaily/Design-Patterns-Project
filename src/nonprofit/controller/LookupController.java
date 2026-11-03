package nonprofit.controller;

import nonprofit.service.LookupService;
import nonprofit.view.ConsoleView;

/** Console menu for the lookup tables. */
public class LookupController {
    private LookupService service;
    private ConsoleView view;

    private static final String[] CATEGORIES = {
            "SKILL", "EVENT_TYPE", "DONATION_TYPE", "NEED_TYPE"
    };

    public LookupController(LookupService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void menu() {
        while (true) {
            view.show("\n== Lookup Tables ==");
            view.show("1) Skills  2) Event Types  3) Donation Types  4) Need Types  0) Back");
            int cat = view.askInt("Choice");
            if (cat == 0) return;
            if (cat < 1 || cat > 4) { view.show("Invalid choice."); continue; }
            categoryMenu(CATEGORIES[cat - 1]);
        }
    }

    private void categoryMenu(String category) {
        while (true) {
            view.show("\n-- " + category + " --");
            view.show("1) Add  2) List  3) Edit  4) Delete  0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;
            if (choice == 1) {
                view.show("Added " + service.add(view.ask("Name"), category));
            } else if (choice == 2) {
                view.showList(category + " entries", service.getByCategory(category));
            } else if (choice == 3) {
                int id = view.askInt("Entry id to edit");
                String newName = view.ask("New name");
                view.show(service.update(id, newName) ? "Updated." : "Not found.");
            } else if (choice == 4) {
                int id = view.askInt("Entry id to delete");
                view.show(service.delete(id) ? "Deleted." : "Not found.");
            }
        }
    }
}
