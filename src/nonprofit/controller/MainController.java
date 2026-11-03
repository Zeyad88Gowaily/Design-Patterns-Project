package nonprofit.controller;

import nonprofit.repository.SupabaseException;
import nonprofit.view.ConsoleView;

/** Console main menu. */
public class MainController {
    private ConsoleView view;
    private DonationController donations;
    private VolunteerController volunteers;
    private EventController events;
    private BeneficiaryController beneficiaries;
    private LookupController lookups;

    public MainController(ConsoleView view, DonationController donations, VolunteerController volunteers,
            EventController events, BeneficiaryController beneficiaries,
            LookupController lookups) {
        this.view = view;
        this.donations = donations;
        this.volunteers = volunteers;
        this.events = events;
        this.beneficiaries = beneficiaries;
        this.lookups = lookups;
    }

    public void run() {
        while (true) {
            view.show("\n=== MissionOS ===");
            view.show("1) Donations  2) Volunteers  3) Events  4) Beneficiaries  5) Lookup Tables  0) Exit");
            int choice = view.askInt("Choice");
            if (choice == 0) {
                view.show("Bye.");
                return;
            }
            try {
                if (choice == 1)
                    donations.menu();
                else if (choice == 2)
                    volunteers.menu();
                else if (choice == 3)
                    events.menu();
                else if (choice == 4)
                    beneficiaries.menu();
                else if (choice == 5)
                    lookups.menu();
            } catch (SupabaseException | IllegalArgumentException | IllegalStateException ex) {
                // Show the error and return to the menu instead of ending the session.
                view.show("Error: " + ex.getMessage());
            }
        }
    }
}
