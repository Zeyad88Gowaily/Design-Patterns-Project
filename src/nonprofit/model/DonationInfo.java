package nonprofit.model;

/**
 * DECORATOR pattern: interface extracted from Donation.
 * Both Donation and all decorators implement this.
 * Allows wrapping a donation with add-ons (GiftAid, MatchingFund, Recurring)
 * that modify the effective amount and description.
 */
public interface DonationInfo {
    double getAmount();
    String describe();
}
