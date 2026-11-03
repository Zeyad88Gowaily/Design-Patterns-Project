package nonprofit.service;

import java.util.List;

import nonprofit.model.*;
import nonprofit.observer.Subject;
import nonprofit.repository.DataStore;

/**
 * Donor and donation operations.
 * Subject in the Observer pattern: observers are told about each new donation.
 */
public class DonationService extends Subject {
    private DataStore db = DataStore.getInstance();

    public Donor addDonor(String name, String email, String phone) {
        return addDonor(name, email, phone, "email");
    }

    public Donor addDonor(String name, String email, String phone, String notificationChannel) {
        return db.donors.add(new Donor(name, email, phone, notificationChannel));
    }

    public List<Donor> getDonors() { return db.donors.getAll(); }

    public Donor getDonorById(int id) { return db.donors.getById(id); }

    public boolean updateDonor(int id, String name, String email, String phone) {
        Donor d = db.donors.getById(id);
        if (d == null) return false;
        if (name != null && !name.isEmpty())  d.setName(name);
        if (email != null && !email.isEmpty()) d.setEmail(email);
        if (phone != null && !phone.isEmpty()) d.setPhone(phone);
        return db.donors.save(d);
    }

    public boolean updateDonor(int id, String name, String email, String phone, String notificationChannel) {
        Donor d = db.donors.getById(id);
        if (d == null) return false;
        if (name != null && !name.isEmpty())  d.setName(name);
        if (email != null && !email.isEmpty()) d.setEmail(email);
        if (phone != null && !phone.isEmpty()) d.setPhone(phone);
        if (notificationChannel != null && !notificationChannel.isEmpty()) d.setNotificationChannel(notificationChannel);
        return db.donors.save(d);
    }

    public boolean deleteDonor(int id) { return db.donors.remove(id); }

    public Donation donate(Donor donor, String type, double amount, String detail) {
        return donate(donor, type, amount, detail, false, false, 0);
    }

    /** Saves the donation and applies the add-ons before notifying, so the receipt shows the final amount. */
    public Donation donate(Donor donor, String type, double amount, String detail,
                           boolean giftAid, boolean matching, int recurringMonths) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Donation amount must be greater than zero.");
        }
        Donation d = db.donations.add(DonationFactory.create(type, donor, amount, detail));
        if (giftAid || matching || recurringMonths > 1) {
            applyDecorators(d, giftAid, matching, recurringMonths);
        }
        notifyObservers("DONATION_RECEIVED", d);
        return d;
    }

    /** Donation type names from the DONATION_TYPE lookup table. */
    public List<String> getDonationTypes() {
        List<String> types = new java.util.ArrayList<String>();
        for (LookupItem item : db.lookups.getAll()) {
            if ("DONATION_TYPE".equals(item.getCategory())) types.add(item.getName());
        }
        return types;
    }

    public List<Donation> getDonations() { return db.donations.getAll(); }

    public Donation getDonationById(int id) { return db.donations.getById(id); }

    public boolean deleteDonation(int id) { return db.donations.remove(id); }

    /**
     * Wraps the donation in the selected decorators (Gift Aid +25%, matching x2, recurring
     * over the given months) and stores the resulting amount. Months of 0 or 1 mean one-off.
     */
    public DonationInfo applyDecorators(Donation donation, boolean giftAid,
                                        boolean matching, int recurringMonths) {
        DonationInfo info = donation;
        StringBuilder desc = new StringBuilder();

        if (giftAid) {
            info = new GiftAidDecorator(info);
            desc.append("GiftAid +25%");
        }
        if (matching) {
            info = new MatchingFundDecorator(info);
            if (desc.length() > 0) desc.append(", ");
            desc.append("MatchingFund x2");
        }
        if (recurringMonths > 1) {
            info = new RecurringDecorator(info, recurringMonths);
            if (desc.length() > 0) desc.append(", ");
            desc.append("Recurring " + recurringMonths + " months");
        }

        // The stored amount becomes the amount after all add-ons.
        donation.setAmount(info.getAmount());
        donation.setDecoratorDescription(desc.toString());
        if (!db.donations.save(donation)) {
            throw new IllegalStateException("Could not save the decorated donation.");
        }

        return info;
    }
}
