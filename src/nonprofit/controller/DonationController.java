package nonprofit.controller;

import nonprofit.model.Donor;
import nonprofit.service.DonationService;
import nonprofit.view.ConsoleView;

/** MVC - Controller for donations (Person A). */
public class DonationController {
    private DonationService service;
    private ConsoleView view;

    public DonationController(DonationService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void menu() {
        while (true) {
            view.show("\n== Donations ==\n1) Add donor  2) Make donation  3) List donors  4) List donations  0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;
            if (choice == 1) {
                Donor d = service.addDonor(view.ask("Name"), view.ask("Email"), view.ask("Phone"));
                view.show("Added " + d);
            } else if (choice == 2) {
                Donor d = null;
                int id = view.askInt("Donor id");
                for (Donor x : service.getDonors()) {
                    if (x.getId() == id) d = x;
                }
                if (d == null) { view.show("No such donor."); continue; }
                String type = view.ask("Type (online / check / inkind)");
                String detail = type.equalsIgnoreCase("inkind") ? view.ask("Item description") : "";
                double amount = view.askDouble("Amount");
                view.show("Saved " + service.donate(d, type, amount, detail));
            } else if (choice == 3) {
                view.showList("Donors", service.getDonors());
            } else if (choice == 4) {
                view.showList("Donations", service.getDonations());
            }
            // TODO [PHASE 1 - Person A] edit/delete donor, edit/delete donation (full CRUD)
        }
    }
}
