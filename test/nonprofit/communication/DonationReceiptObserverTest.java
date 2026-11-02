package nonprofit.communication;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import nonprofit.model.Donor;
import nonprofit.model.OnlineDonation;

class DonationReceiptObserverTest {
    @Test
    void sendsDonationReceiptUsingDonorSmsPreference() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Donor donor = new Donor("Amina", "amina@example.org", "555-0100", "sms");
            new DonationReceiptObserver().update(
                    "DONATION_RECEIVED", new OnlineDonation(donor, 10.0));
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("[SMS to 555-0100]"));
    }
}
