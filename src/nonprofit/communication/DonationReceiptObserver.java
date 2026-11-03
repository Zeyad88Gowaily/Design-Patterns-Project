package nonprofit.communication;

import nonprofit.model.Donation;
import nonprofit.observer.Observer;

/** Observer: sends the donor a receipt through their chosen channel when a donation is received. */
public class DonationReceiptObserver implements Observer {
    private Notifier notifier = new Notifier(NotificationFactory.create("email"));

    public void update(String eventType, Object data) {
        if ("DONATION_RECEIVED".equals(eventType)) {
            Donation d = (Donation) data;
            String channel = d.getDonor().getNotificationChannel();
            if (channel == null || channel.trim().isEmpty()) {
                channel = "email";
            }
            String recipient = "sms".equalsIgnoreCase(channel)
                    ? d.getDonor().getPhone() : d.getDonor().getEmail();
            notifier.setStrategy(NotificationFactory.create(channel));
            notifier.notify(recipient,
                    "Thank you " + d.getDonor().getName() + " for your donation of " + d.getAmount());
        }
    }
}
