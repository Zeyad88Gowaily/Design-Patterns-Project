package nonprofit.model;

public class InKindDonation extends Donation {
    private String item;

    /** amount = estimated value of the item. */
    public InKindDonation(Donor donor, double amount, String item) {
        super(donor, amount);
        this.item = item;
    }

    public String getItem() { return item; }
    public String getType() { return "IN-KIND (" + item + ")"; }
}
