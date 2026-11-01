package nonprofit;

import nonprofit.communication.DonationReceiptObserver;
import nonprofit.communication.EventUpdateObserver;
import nonprofit.controller.*;
import nonprofit.service.*;
import nonprofit.view.ConsoleView;
import nonprofit.view.ControlPanelView;

/**
 * Entry point: creates the objects and wires them together.
 * Run with --gui for Swing UI, or no arguments for console mode.
 */
public class Main {
    public static void main(String[] args) {
        // --- Shared services ---
        DonationService donationService = new DonationService();
        VolunteerService volunteerService = new VolunteerService();
        EventService eventService = new EventService();
        BeneficiaryService beneficiaryService = new BeneficiaryService();
        LookupService lookupService = new LookupService();

        // --- Observer wiring ---
        donationService.attach(new DonationReceiptObserver());
        eventService.attach(new EventUpdateObserver());

        // --- Choose UI mode ---
        if (args.length > 0 && args[0].equalsIgnoreCase("--gui")) {
            // Swing UI
            ControlPanelView gui = new ControlPanelView(
                    donationService, volunteerService, eventService,
                    beneficiaryService, lookupService);
            gui.setVisible(true);
        } else {
            // Console UI
            ConsoleView view = new ConsoleView();
            MainController app = new MainController(view,
                    new DonationController(donationService, view),
                    new VolunteerController(volunteerService, view),
                    new EventController(eventService, view),
                    new BeneficiaryController(beneficiaryService, view),
                    new LookupController(lookupService, view));
            app.run();
        }
    }
}
