package nonprofit.controller;

import nonprofit.model.Donation;
import nonprofit.model.Donor;
import nonprofit.service.DonationService;
import nonprofit.view.ConsoleView;

/** Console menu for donors and donations. */
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
        String name = view.ask("Name");
        String email = view.ask("Email");
        String phone = view.ask("Phone");
        String channel = view.ask("Receipt channel (email / sms / social)");
        Donor d = service.addDonor(name, email, phone, channel.isEmpty() ? "email" : channel);
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

        view.show("Types: " + service.getDonationTypes());
        String type = view.ask("Type");
        String detail = type.replaceAll("[^A-Za-z]", "").equalsIgnoreCase("inkind")
                ? view.ask("Item description") : "";
        double amount = view.askDouble("Amount");

        view.show("Apply add-ons?");
        boolean ga = view.ask("Gift Aid +25%? (y/n)").equalsIgnoreCase("y");
        boolean mf = view.ask("Matching Fund x2? (y/n)").equalsIgnoreCase("y");
        int months = 0;
        try { months = Integer.parseInt(view.ask("Recurring? Enter months (0 = no)")); }
        catch (NumberFormatException ignored) {}

        Donation donation = service.donate(d, type, amount, detail, ga, mf, months);
        if (ga || mf || months > 1) {
            view.show("Effective amount after add-ons: " + String.format("%.2f", donation.getAmount()));
        }
        view.show("Saved " + donation);
    }

    private void doDeleteDonation() {
        view.showList("Donations", service.getDonations());
        int id = view.askInt("Donation id to delete");
        view.show(service.deleteDonation(id) ? "Deleted." : "Not found.");
    }
}
