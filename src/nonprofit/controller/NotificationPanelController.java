package nonprofit.controller;

import java.util.ArrayList;
import java.util.List;

import nonprofit.communication.NotificationFactory;
import nonprofit.communication.NotificationStrategy;
import nonprofit.communication.Notifier;
import nonprofit.model.Beneficiary;
import nonprofit.model.Donor;
import nonprofit.model.Volunteer;
import nonprofit.service.BeneficiaryService;
import nonprofit.service.DonationService;
import nonprofit.service.VolunteerService;

/**
 * Controller for the Notifications tab: sends a message to the chosen audience
 * with the chosen strategy.
 */
public class NotificationPanelController {
    public static final String[] CHANNELS = {"email", "sms", "social"};
    public static final String[] AUDIENCES = {"Custom recipient", "All donors", "All volunteers", "All beneficiaries"};

    private final DonationService donations;
    private final VolunteerService volunteers;
    private final BeneficiaryService beneficiaries;
    private final Notifier notifier = new Notifier(NotificationFactory.create("email"));

    public NotificationPanelController(DonationService donations, VolunteerService volunteers,
                                       BeneficiaryService beneficiaries) {
        this.donations = donations;
        this.volunteers = volunteers;
        this.beneficiaries = beneficiaries;
    }

    /** Sends the message and returns one log line per recipient. */
    public List<String> send(String audience, String customRecipient, String channel, String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message is required.");
        }
        NotificationStrategy strategy = NotificationFactory.create(channel);
        notifier.setStrategy(strategy);

        List<String> recipients = new ArrayList<String>();
        boolean sms = "sms".equalsIgnoreCase(channel);
        if (audience.equals(AUDIENCES[1])) {
            for (Donor d : donations.getDonors()) recipients.add(sms ? d.getPhone() : d.getEmail());
        } else if (audience.equals(AUDIENCES[2])) {
            for (Volunteer v : volunteers.getAll()) recipients.add(v.getEmail());
        } else if (audience.equals(AUDIENCES[3])) {
            for (Beneficiary b : beneficiaries.getAll()) recipients.add(b.getContact());
        } else {
            recipients.add(customRecipient);
        }

        List<String> log = new ArrayList<String>();
        for (String to : recipients) {
            if (to == null || to.trim().isEmpty()) continue;   // recipient has no contact detail for this channel
            notifier.notify(to, message);
            log.add(strategy.format(to, message));
        }
        if (log.isEmpty()) {
            throw new IllegalArgumentException("No recipient with a contact detail for the '" + channel + "' channel.");
        }
        return log;
    }
}
