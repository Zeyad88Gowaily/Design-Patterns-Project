package nonprofit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DonationDecoratorTest {
    private final Donor donor = new Donor("Amina", "amina@example.org", "555-0100");

    @Test
    void giftAidAddsTwentyFivePercent() {
        DonationInfo info = new GiftAidDecorator(new OnlineDonation(donor, 100.0));
        assertEquals(125.0, info.getAmount(), 0.001);
    }

    @Test
    void decoratorsStackAndDescribeEveryAddOn() {
        DonationInfo info = new RecurringDecorator(
                new MatchingFundDecorator(new GiftAidDecorator(new CheckDonation(donor, 10.0))), 3);
        assertEquals(10.0 * 1.25 * 2.0 * 3, info.getAmount(), 0.001);
        String description = info.describe();
        assertTrue(description.contains("GiftAid") && description.contains("MatchingFund")
                && description.contains("Recurring(3 months)"));
    }
}
