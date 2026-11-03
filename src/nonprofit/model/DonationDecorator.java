package nonprofit.model;

/** Decorator: base class for add-ons that wrap a DonationInfo. */
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
