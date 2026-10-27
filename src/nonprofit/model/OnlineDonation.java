package nonprofit.model;

public class OnlineDonation extends Donation {
    public OnlineDonation(Donor donor, double amount) { super(donor, amount); }
    public String getType() { return "ONLINE"; }
}
