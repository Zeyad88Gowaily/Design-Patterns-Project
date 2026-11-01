package nonprofit.controller;

import nonprofit.model.Donation;
import nonprofit.model.DonationInfo;
import nonprofit.model.Donor;
import nonprofit.service.DonationService;
import nonprofit.view.ConsoleView;

/**
 * MVC - Controller for donations (Person A).
 * Full CRUD for donors and donations + Decorator pattern add-ons.
 */
public class DonationController {
    private DonationService service;
    private ConsoleView view;

    public DonationController(DonationService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void menu() {
        while (true) {
            view.show("\n== Donations ==");
            view.show("1) Add donor     2) List donors    3) Edit donor    4) Delete donor");
            view.show("5) Make donation 6) List donations 7) Delete donation");
            view.show("0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;

            switch (choice) {
                case 1: doAddDonor(); break;
                case 2: view.showList("Donors", service.getDonors()); break;
                case 3: doEditDonor(); break;
                case 4: doDeleteDonor(); break;
                case 5: doMakeDonation(); break;
                case 6: view.showList("Donations", service.getDonations()); break;
                case 7: doDeleteDonation(); break;
                default: view.show("Invalid choice.");
            }
        }
    }

    private void doAddDonor() {
        Donor d = service.addDonor(view.ask("Name"), view.ask("Email"), view.ask("Phone"));
        view.show("Added " + d);
    }

    private void doEditDonor() {
        view.showList("Donors", service.getDonors());
        int id = view.askInt("Donor id to edit");
        Donor d = service.getDonorById(id);
        if (d == null) { view.show("Not found."); return; }
        view.show("Current: " + d);
        String name  = view.ask("New name (blank to keep)");
        String email = view.ask("New email (blank to keep)");
        String phone = view.ask("New phone (blank to keep)");
        service.updateDonor(id, name, email, phone);
        view.show("Updated: " + service.getDonorById(id));
    }

    private void doDeleteDonor() {
        view.showList("Donors", service.getDonors());
        int id = view.askInt("Donor id to delete");
        view.show(service.deleteDonor(id) ? "Deleted." : "Not found.");
    }

    private void doMakeDonation() {
        Donor d = null;
        int id = view.askInt("Donor id");
        d = service.getDonorById(id);
        if (d == null) { view.show("No such donor."); return; }

        String type = view.ask("Type (online / check / inkind)");
        String detail = type.equalsIgnoreCase("inkind") ? view.ask("Item description") : "";
        double amount = view.askDouble("Amount");

        Donation donation = service.donate(d, type, amount, detail);

        // DECORATOR: ask about add-ons
        view.show("Apply add-ons?");
        String giftAid = view.ask("Gift Aid +25%? (y/n)");
        String matching = view.ask("Matching Fund x2? (y/n)");
        String recurring = view.ask("Recurring? Enter months (0 = no)");
        int months = 0;
        try { months = Integer.parseInt(recurring); } catch (NumberFormatException ignored) {}

        boolean ga = giftAid.equalsIgnoreCase("y");
        boolean mf = matching.equalsIgnoreCase("y");

        if (ga || mf || months > 1) {
            DonationInfo decorated = service.applyDecorators(donation, ga, mf, months);
            view.show("Effective amount after add-ons: " + String.format("%.2f", decorated.getAmount()));
            view.show("Description: " + decorated.describe());
        }

        view.show("Saved " + donation);
    }

    private void doDeleteDonation() {
        view.showList("Donations", service.getDonations());
        int id = view.askInt("Donation id to delete");
        view.show(service.deleteDonation(id) ? "Deleted." : "Not found.");
    }
}
