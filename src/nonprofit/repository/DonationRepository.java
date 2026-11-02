package nonprofit.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import nonprofit.model.Donation;
import nonprofit.model.Donor;

public class DonationRepository extends SupabaseRepository<Donation> {
    private final EntityJsonCodec<Donor> donorCodec = new EntityJsonCodec<Donor>(Donor.class);

    public DonationRepository(SupabaseRestClient client) {
        super(client, "donations", Donation.class);
    }

    @Override
    public Donation add(Donation donation) {
        Donation saved = super.add(donation);
        saved.setDonor(donation.getDonor());
        return saved;
    }

    @Override
    public Donation getById(int id) {
        JsonArray rows = getClient().selectById("donations", id);
        if (rows.size() == 0) {
            return null;
        }
        return withDonor(rows.get(0));
    }

    @Override
    public List<Donation> getAll() {
        JsonArray rows = getClient().selectAll("donations");
        if (rows.size() == 0) {
            return new ArrayList<Donation>();
        }
        Map<Integer, Donor> donorsById = loadDonors();
        List<Donation> donations = new ArrayList<Donation>();
        for (JsonElement rowElement : rows) {
            JsonObject row = rowElement.getAsJsonObject();
            Donation donation = decode(row);
            int donorId = row.get("donor_id").getAsInt();
            Donor donor = donorsById.get(donorId);
            if (donor == null) {
                throw new SupabaseException("Donation references missing donor " + donorId + ".");
            }
            donation.setDonor(donor);
            donations.add(donation);
        }
        return donations;
    }

    private Donation withDonor(JsonElement rowElement) {
        JsonObject row = rowElement.getAsJsonObject();
        Donation donation = decode(row);
        int donorId = row.get("donor_id").getAsInt();
        JsonArray donorRows = getClient().selectById("donors", donorId);
        if (donorRows.size() == 0) {
            throw new SupabaseException("Donation references missing donor " + donorId + ".");
        }
        JsonObject donorRow = donorRows.get(0).getAsJsonObject();
        donation.setDonor(donorCodec.fromJson(
                donorRow.getAsJsonObject("data"), donorRow.get("id").getAsInt()));
        return donation;
    }

    private Map<Integer, Donor> loadDonors() {
        Map<Integer, Donor> donorsById = new HashMap<Integer, Donor>();
        for (JsonElement rowElement : getClient().selectAll("donors")) {
            JsonObject row = rowElement.getAsJsonObject();
            Donor donor = donorCodec.fromJson(
                    row.getAsJsonObject("data"), row.get("id").getAsInt());
            donorsById.put(donor.getId(), donor);
        }
        return donorsById;
    }
}
