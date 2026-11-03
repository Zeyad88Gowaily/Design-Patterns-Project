package nonprofit.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nonprofit.model.Donation;
import nonprofit.model.Event;
import nonprofit.service.BeneficiaryService;
import nonprofit.service.DonationService;
import nonprofit.service.EventService;
import nonprofit.service.VolunteerService;

/** Controller for the Dashboard tab: totals and the latest records across all modules. */
public class DashboardPanelController {
    private final DonationService donations;
    private final VolunteerService volunteers;
    private final EventService events;
    private final BeneficiaryService beneficiaries;

    public DashboardPanelController(DonationService donations, VolunteerService volunteers,
                                    EventService events, BeneficiaryService beneficiaries) {
        this.donations = donations;
        this.volunteers = volunteers;
        this.events = events;
        this.beneficiaries = beneficiaries;
    }

    /** Label and value of each summary card, in display order. */
    public Map<String, String> getStats() {
        double total = 0;
        for (Donation d : donations.getDonations()) {
            total += d.getAmount();
        }
        Map<String, String> stats = new LinkedHashMap<String, String>();
        stats.put("Donors", String.valueOf(donations.getDonors().size()));
        stats.put("Total donated", String.format("%,.2f", total));
        stats.put("Volunteers", String.valueOf(volunteers.getAll().size()));
        stats.put("Events", String.valueOf(events.getAll().size()));
        stats.put("Beneficiaries", String.valueOf(beneficiaries.getAll().size()));
        return stats;
    }

    /** The most recent donations, newest first. */
    public List<Donation> getRecentDonations(int limit) {
        List<Donation> all = donations.getDonations();
        List<Donation> recent = new ArrayList<Donation>();
        for (int i = all.size() - 1; i >= 0 && recent.size() < limit; i--) {
            recent.add(all.get(i));
        }
        return recent;
    }

    public List<Event> getEvents() {
        return events.getAll();
    }
}
