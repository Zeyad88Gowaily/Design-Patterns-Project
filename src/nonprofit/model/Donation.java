package nonprofit.model;

import java.time.LocalDate;

/**
 * A donation. Concrete types (online, check, in-kind) are created by DonationFactory.
 *
 * TODO [PHASE 1 - Person A] DECORATOR: make the amount something that can be wrapped,
 *      e.g. extract an interface "DonationInfo" (getAmount, describe) and write decorators
 *      such as GiftAidDecorator, MatchingFundDecorator, RecurringDecorator.
 * TODO [PHASE 2 - Person A] TEMPLATE METHOD: receipt / tax document generation.
 */
public abstract class Donation extends Entity {
    protected Donor donor;
    protected double amount;
    protected LocalDate date = LocalDate.now();

    public Donation(Donor donor, double amount) {
        this.donor = donor;
        this.amount = amount;
    }

    public Donor getDonor() { return donor; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }

    public abstract String getType();

    public String toString() {
        return "#" + id + " [" + getType() + "] " + amount + " from " + donor.getName() + " on " + date;
    }
}
