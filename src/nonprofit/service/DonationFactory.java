package nonprofit.service;

import nonprofit.model.*;

/**
 * FACTORY: builds the right Donation subclass from the type typed by the user.
 *
 * TODO [PHASE 1 - Person A] load the allowed types from a lookup table instead of if/else.
 */
public class DonationFactory {
    public static Donation create(String type, Donor donor, double amount, String detail) {
        if ("check".equalsIgnoreCase(type)) {
            return new CheckDonation(donor, amount);
        }
        if ("inkind".equalsIgnoreCase(type)) {
            return new InKindDonation(donor, amount, detail);
        }
        return new OnlineDonation(donor, amount);   // default
    }
}
