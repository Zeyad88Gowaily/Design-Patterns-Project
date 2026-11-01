package nonprofit.model;

/**
 * DECORATOR: doubles the donation via a corporate matching programme.
 * The company pledges to match every dollar donated by an employee.
 */
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
