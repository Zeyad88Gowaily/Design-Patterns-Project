package nonprofit.controller;

import java.util.List;

import nonprofit.model.Donation;
import nonprofit.model.DonationInfo;
import nonprofit.model.Donor;
import nonprofit.service.DonationService;

public class DonationPanelController {
    private final DonationService service;

    public DonationPanelController(DonationService service) {
        this.service = service;
    }

    public Donor addDonor(String name, String email, String phone, String channel) {
        return service.addDonor(name, email, phone, channel);
    }

    public List<Donor> getDonors() {
        return service.getDonors();
    }

    public Donor getDonorById(int id) {
        return service.getDonorById(id);
    }

    public boolean updateDonor(int id, String name, String email, String phone, String channel) {
        return service.updateDonor(id, name, email, phone, channel);
    }

    public boolean deleteDonor(int id) {
        return service.deleteDonor(id);
    }

    public Donation donate(Donor donor, String type, double amount, String detail) {
        return service.donate(donor, type, amount, detail);
    }

    public Donation donate(Donor donor, String type, double amount, String detail,
                           boolean giftAid, boolean matching, int recurringMonths) {
        return service.donate(donor, type, amount, detail, giftAid, matching, recurringMonths);
    }

    public boolean deleteDonation(int id) {
        return service.deleteDonation(id);
    }

    public List<String> getDonationTypes() {
        return service.getDonationTypes();
    }

    public List<Donation> getDonations() {
        return service.getDonations();
    }

    public DonationInfo applyDecorators(Donation donation, boolean giftAid,
                                        boolean matching, int recurringMonths) {
        return service.applyDecorators(donation, giftAid, matching, recurringMonths);
    }
}
