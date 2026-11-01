package nonprofit.service;

import java.util.List;

import nonprofit.model.*;
import nonprofit.repository.DataStore;

/**
 * FACTORY: builds the right Donation subclass from the type string.
 * Validates the type against the DONATION_TYPE lookup table in DataStore.
 */
public class DonationFactory {
    public static Donation create(String type, Donor donor, double amount, String detail) {
        // Validate type exists in the lookup table
        boolean valid = false;
        for (LookupItem item : DataStore.getInstance().lookups.getAll()) {
            if ("DONATION_TYPE".equals(item.getCategory())
                    && item.getName().equalsIgnoreCase(type)) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            System.out.println("[DonationFactory] Warning: '" + type
                    + "' not in lookup table, defaulting to Online.");
        }

        if ("check".equalsIgnoreCase(type)) {
            return new CheckDonation(donor, amount);
        }
        if ("inkind".equalsIgnoreCase(type) || "in-kind".equalsIgnoreCase(type)) {
            return new InKindDonation(donor, amount, detail);
        }
        return new OnlineDonation(donor, amount);   // default
    }
}
