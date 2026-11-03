package nonprofit.model;

/** Decorator: common interface of Donation and its add-ons. */
public interface DonationInfo {
    double getAmount();
    String describe();
}
