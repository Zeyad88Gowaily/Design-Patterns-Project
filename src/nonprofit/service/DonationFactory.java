package nonprofit.service;


import nonprofit.model.*;
import nonprofit.repository.DataStore;

/** Factory: creates the Donation subclass that matches a type from the DONATION_TYPE lookup table. */
public class DonationFactory {
    public static Donation create(String type, Donor donor, double amount, String detail) {
        String key = normalize(type);
        if (!isConfiguredType(key)) {
            throw new IllegalArgumentException("Unknown donation type '" + type
                    + "'. Add it to the DONATION_TYPE lookup table first.");
        }
        if ("check".equals(key)) {
            return new CheckDonation(donor, amount);
        }
        if ("inkind".equals(key)) {
            return new InKindDonation(donor, amount, detail);
        }
        return new OnlineDonation(donor, amount);
    }

    /** Lower-cases and strips punctuation, so "In-Kind" and "inkind" match. */
    private static String normalize(String type) {
        return type == null ? "" : type.replaceAll("[^A-Za-z]", "").toLowerCase();
    }

    /** True if the DONATION_TYPE lookup table contains the type. */
    private static boolean isConfiguredType(String key) {
        for (LookupItem item : DataStore.getInstance().lookups.getAll()) {
            if ("DONATION_TYPE".equals(item.getCategory()) && normalize(item.getName()).equals(key)) {
                return true;
            }
        }
        return false;
    }
}
