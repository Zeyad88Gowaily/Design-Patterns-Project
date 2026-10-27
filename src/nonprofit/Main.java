package nonprofit;

import nonprofit.communication.DonationReceiptObserver;
import nonprofit.controller.*;
import nonprofit.service.*;
import nonprofit.view.ConsoleView;

/** Entry point: creates the objects and wires them together. */
public class Main {
    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();

        DonationService donationService = new DonationService();
        VolunteerService volunteerService = new VolunteerService();
        EventService eventService = new EventService();
        BeneficiaryService beneficiaryService = new BeneficiaryService();

        // OBSERVER wiring: donor gets a message after each donation.
        donationService.attach(new DonationReceiptObserver());
        // TODO [PHASE 1 - Person B] eventService.attach(new EventUpdateObserver());

        MainController app = new MainController(view,
                new DonationController(donationService, view),
                new VolunteerController(volunteerService, view),
                new EventController(eventService, view),
                new BeneficiaryController(beneficiaryService, view));
        app.run();
    }
}
