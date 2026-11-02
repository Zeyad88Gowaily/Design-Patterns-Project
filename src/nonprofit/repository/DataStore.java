package nonprofit.repository;

import nonprofit.model.*;

/** SINGLETON: one shared set of Supabase-backed repositories for application services. */
public class DataStore {
    private static DataStore instance;

    // --- Core entity repositories ---
    public final Repository<Donor> donors;
    public final Repository<Donation> donations;
    public final Repository<Volunteer> volunteers;
    public final Repository<Event> events;
    public final Repository<Beneficiary> beneficiaries;

    public final Repository<LookupItem> lookups;

    public final Repository<Registration> registrations;
    public final Repository<Ticket> tickets;

    private DataStore(SupabaseRestClient supabaseClient) {
        donors = new SupabaseRepository<Donor>(supabaseClient, "donors", Donor.class);
        donations = new DonationRepository(supabaseClient);
        volunteers = new SupabaseRepository<Volunteer>(supabaseClient, "volunteers", Volunteer.class);
        events = new EventRepository(supabaseClient);
        beneficiaries = new SupabaseRepository<Beneficiary>(supabaseClient, "beneficiaries", Beneficiary.class);
        lookups = new SupabaseRepository<LookupItem>(supabaseClient, "lookups", LookupItem.class);
        registrations = new SupabaseRepository<Registration>(supabaseClient, "registrations", Registration.class);
        tickets = new SupabaseRepository<Ticket>(supabaseClient, "tickets", Ticket.class);
    }

    public static synchronized void configure(SupabaseRestClient supabaseClient) {
        if (supabaseClient == null) {
            throw new IllegalArgumentException("A Supabase client is required.");
        }
        instance = new DataStore(supabaseClient);
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DataStore must be configured with an authenticated Supabase client.");
        }
        return instance;
    }
}
