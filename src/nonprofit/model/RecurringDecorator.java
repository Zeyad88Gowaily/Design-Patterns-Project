package nonprofit.model;

/**
 * DECORATOR: multiplies the donation for recurring monthly pledges.
 * If the donor pledges for N months, the effective total = base * N.
 */
public class RecurringDecorator extends DonationDecorator {
    private int months;

    public RecurringDecorator(DonationInfo wrapped, int months) {
        super(wrapped);
        this.months = months;
    }

    @Override
    public double getAmount() {
        return wrapped.getAmount() * months;
    }

    @Override
    public String describe() {
        return wrapped.describe() + " + Recurring(" + months + " months)";
    }

    public int getMonths() { return months; }
}
