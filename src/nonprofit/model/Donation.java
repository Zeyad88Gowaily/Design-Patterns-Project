package nonprofit.model;

import java.time.LocalDate;

/** Base class for donations. Implements DonationInfo so decorators can wrap it. */
public abstract class Donation extends Entity implements DonationInfo {
    protected Donor donor;
    protected double amount;
    protected LocalDate date = LocalDate.now();
    protected String decoratorDescription = "";

    public Donation(Donor donor, double amount) {
        this.donor = donor;
        this.amount = amount;
    }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }
    @Override
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public String getDecoratorDescription() { return decoratorDescription; }
    public void setDecoratorDescription(String desc) { this.decoratorDescription = desc; }

    public abstract String getType();

    @Override
    public String describe() {
        return getType() + " $" + String.format("%.2f", amount);
    }

    @Override
    public String toString() {
        String base = "#" + id + " [" + getType() + "] " + amount + " from " + donor.getName() + " on " + date;
        if (decoratorDescription != null && !decoratorDescription.isEmpty()) {
            base += " (" + decoratorDescription + ")";
        }
        return base;
    }
}
