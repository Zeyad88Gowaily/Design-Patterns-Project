package nonprofit.repository;

import nonprofit.model.*;

/**
 * SINGLETON: one shared place that holds all repositories.
 * Every service accesses data exclusively through DataStore.getInstance().
 * The private constructor ensures only one instance exists at runtime.
 *
 * TODO [PHASE 2 - Person C] swap the in-memory repositories for a file/DB version
 *      (only this class should need to change).
 */
public class DataStore {
    private static DataStore instance;

    // --- Core entity repositories ---
    public final Repository<Donor> donors = new Repository<Donor>();
    public final Repository<Donation> donations = new Repository<Donation>();
    public final Repository<Volunteer> volunteers = new Repository<Volunteer>();
    public final Repository<Event> events = new Repository<Event>();
    public final Repository<Beneficiary> beneficiaries = new Repository<Beneficiary>();

    // --- Lookup tables (Person B - Phase 1) ---
    public final Repository<LookupItem> lookups = new Repository<LookupItem>();

    // --- Event registration & ticketing (Person B - Phase 1) ---
    public final Repository<Registration> registrations = new Repository<Registration>();
    public final Repository<Ticket> tickets = new Repository<Ticket>();

    private DataStore() {
        seedLookups();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /** Pre-populate lookup tables with sensible defaults. */
    private void seedLookups() {
        // Skills
        lookups.add(new LookupItem("First Aid",      "SKILL"));
        lookups.add(new LookupItem("Teaching",        "SKILL"));
        lookups.add(new LookupItem("Cooking",         "SKILL"));
        lookups.add(new LookupItem("Driving",         "SKILL"));
        lookups.add(new LookupItem("IT Support",      "SKILL"));

        // Event types
        lookups.add(new LookupItem("Fundraiser",      "EVENT_TYPE"));
        lookups.add(new LookupItem("Workshop",        "EVENT_TYPE"));
        lookups.add(new LookupItem("Community Clean", "EVENT_TYPE"));

        // Donation types
        lookups.add(new LookupItem("Online",          "DONATION_TYPE"));
        lookups.add(new LookupItem("Check",           "DONATION_TYPE"));
        lookups.add(new LookupItem("In-Kind",         "DONATION_TYPE"));

        // Need types
        lookups.add(new LookupItem("Food",            "NEED_TYPE"));
        lookups.add(new LookupItem("Shelter",         "NEED_TYPE"));
        lookups.add(new LookupItem("Education",       "NEED_TYPE"));
        lookups.add(new LookupItem("Healthcare",      "NEED_TYPE"));
    }
}
