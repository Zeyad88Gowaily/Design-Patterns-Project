package nonprofit.model;

/**
 * DECORATOR: adds 25% Gift Aid on top of the donation amount.
 * In the UK, Gift Aid lets charities reclaim 25% extra from HMRC.
 */
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
