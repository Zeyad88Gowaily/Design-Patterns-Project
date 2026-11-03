package nonprofit.model;

/** Adds 25% Gift Aid to the amount. */
public class GiftAidDecorator extends DonationDecorator {

    public GiftAidDecorator(DonationInfo wrapped) {
        super(wrapped);
    }

    @Override
    public double getAmount() {
        return wrapped.getAmount() * 1.25;
    }

    @Override
    public String describe() {
        return wrapped.describe() + " + GiftAid(+25%)";
    }
}
