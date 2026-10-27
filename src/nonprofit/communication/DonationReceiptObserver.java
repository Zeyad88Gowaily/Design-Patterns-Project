package nonprofit.communication;

import nonprofit.model.Donation;
import nonprofit.observer.Observer;

/**
 * OBSERVER example: when a donation is received, thank the donor.
 *
 * TODO [PHASE 1 - Person C] choose strategy per recipient (donor prefers SMS or email).
 * TODO [PHASE 2 - Person A] replace the thank-you text with a real receipt (TEMPLATE METHOD).
 */
public class DonationReceiptObserver implements Observer {
    private Notifier notifier = new Notifier(NotificationFactory.create("email"));

    public void update(String eventType, Object data) {
        if ("DONATION_RECEIVED".equals(eventType)) {
            Donation d = (Donation) data;
            notifier.notify(d.getDonor().getEmail(),
                    "Thank you " + d.getDonor().getName() + " for your donation of " + d.getAmount());
        }
    }
}
