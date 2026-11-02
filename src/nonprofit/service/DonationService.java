package nonprofit.service;

import java.util.List;

import nonprofit.model.*;
import nonprofit.observer.Subject;
import nonprofit.repository.DataStore;

/**
 * Business logic for donations. It is the SUBJECT: observers are told about each new donation.
 *
 * DECORATOR pattern: applyDecorators() wraps a DonationInfo with selected add-ons
 * and updates the stored donation's effective amount.
 *
 * TODO [PHASE 2 - Person A] PAYMENT MODULE: take payment before saving the donation.
 * TODO [PHASE 2 - Person A] PROXY: only admins may see donor details / tax documents.
 */
public class DonationService extends Subject {
    private DataStore db = DataStore.getInstance();

    // ==================== Donor CRUD ====================

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

    public boolean deleteDonor(int id) { return db.donors.remove(id); }

    // ==================== Donation CRUD ====================

    public Donation donate(Donor donor, String type, double amount, String detail) {
        Donation d = db.donations.add(DonationFactory.create(type, donor, amount, detail));
        notifyObservers("DONATION_RECEIVED", d);
        return d;
    }

    public List<Donation> getDonations() { return db.donations.getAll(); }

    public Donation getDonationById(int id) { return db.donations.getById(id); }

    public boolean deleteDonation(int id) { return db.donations.remove(id); }

    // ==================== DECORATOR ====================

    /**
     * Apply selected decorators to a donation and update its stored amount.
     * @param donation the base donation
     * @param giftAid   true to add GiftAid (+25%)
     * @param matching  true to add MatchingFund (x2)
     * @param recurringMonths  number of months (0 = not recurring)
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

        // Update the stored donation with the effective amount
        donation.setAmount(info.getAmount());
        donation.setDecoratorDescription(desc.toString());
        if (!db.donations.save(donation)) {
            throw new IllegalStateException("Could not save the decorated donation.");
        }

        return info;
    }
}
