package nonprofit.model;

/** Doubles the amount (corporate matching). */
public class MatchingFundDecorator extends DonationDecorator {

    public MatchingFundDecorator(DonationInfo wrapped) {
        super(wrapped);
    }

    @Override
    public double getAmount() {
        return wrapped.getAmount() * 2.0;
    }

    @Override
    public String describe() {
        return wrapped.describe() + " + MatchingFund(x2)";
    }
}
