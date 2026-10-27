package nonprofit.model;

public class CheckDonation extends Donation {
    public CheckDonation(Donor donor, double amount) { super(donor, amount); }
    public String getType() { return "CHECK"; }
}
