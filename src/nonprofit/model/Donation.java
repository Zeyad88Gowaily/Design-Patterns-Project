package nonprofit.model;

import java.time.LocalDate;

/**
 * A donation. Concrete types (online, check, in-kind) are created by DonationFactory.
 *
 * DECORATOR pattern: implements DonationInfo so it can be wrapped by decorators
 * (GiftAidDecorator, MatchingFundDecorator, RecurringDecorator) that modify the
 * effective amount and description.
 *
 * TODO [PHASE 2 - Person A] TEMPLATE METHOD: receipt / tax document generation.
 */
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
