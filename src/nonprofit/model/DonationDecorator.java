package nonprofit.model;

/**
 * DECORATOR pattern: abstract decorator that wraps a DonationInfo.
 * Concrete decorators (GiftAid, MatchingFund, Recurring) extend this
 * and override getAmount() / describe() to add behaviour.
 */
public abstract class DonationDecorator implements DonationInfo {
    protected DonationInfo wrapped;

    public DonationDecorator(DonationInfo wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public double getAmount() { return wrapped.getAmount(); }

    @Override
    public String describe() { return wrapped.describe(); }
}
