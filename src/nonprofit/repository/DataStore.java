package nonprofit.repository;

import nonprofit.model.*;

/**
 * SINGLETON: one shared place that holds all repositories.
 *
 * TODO [PHASE 2 - Person C] swap the in-memory repositories for a file/DB version
 *      (only this class should need to change).
 */
public class DataStore {
    private static DataStore instance;

    public final Repository<Donor> donors = new Repository<Donor>();
    public final Repository<Donation> donations = new Repository<Donation>();
    public final Repository<Volunteer> volunteers = new Repository<Volunteer>();
    public final Repository<Event> events = new Repository<Event>();
    public final Repository<Beneficiary> beneficiaries = new Repository<Beneficiary>();

    private DataStore() { }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }
}
