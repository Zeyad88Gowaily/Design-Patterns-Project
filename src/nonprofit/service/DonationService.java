package nonprofit.service;

import java.util.List;

import nonprofit.model.*;
import nonprofit.observer.Subject;
import nonprofit.repository.DataStore;

/**
 * Business logic for donations. It is the SUBJECT: observers are told about each new donation.
 *
 * TODO [PHASE 1 - Person A] DECORATOR: apply add-ons to a donation (gift aid, matching fund...).
 * TODO [PHASE 2 - Person A] PAYMENT MODULE: take payment before saving the donation.
 * TODO [PHASE 2 - Person A] PROXY: only admins may see donor details / tax documents.
 */
public class DonationService extends Subject {
    private DataStore db = DataStore.getInstance();

    public Donor addDonor(String name, String email, String phone) {
        return db.donors.add(new Donor(name, email, phone));
    }

    public Donation donate(Donor donor, String type, double amount, String detail) {
        Donation d = DonationFactory.create(type, donor, amount, detail);
        db.donations.add(d);
        notifyObservers("DONATION_RECEIVED", d);
        return d;
    }

    public List<Donor> getDonors() { return db.donors.getAll(); }
    public List<Donation> getDonations() { return db.donations.getAll(); }
}
