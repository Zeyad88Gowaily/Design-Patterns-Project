package nonprofit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nonprofit.model.Donation;
import nonprofit.model.Event;
import nonprofit.model.InKindDonation;

class EntityJsonCodecTest {
    @Test
    void roundTripsPolymorphicDonationAndItsDonor() {
        nonprofit.model.Donor donor =
                new nonprofit.model.Donor("Amina", "amina@example.org", "555-0100", "sms");
        donor.setId(7);
        Donation donation = new InKindDonation(donor, 50.0, "blankets");
        donation.setDecoratorDescription("GiftAid +25%");

        EntityJsonCodec<Donation> codec = new EntityJsonCodec<Donation>(Donation.class);
        Donation restored = codec.fromJson(codec.toJson(donation), 23);

        assertInstanceOf(InKindDonation.class, restored);
        assertEquals(23, restored.getId());
        assertEquals("blankets", ((InKindDonation) restored).getItem());
        assertEquals(donation.getDate(), restored.getDate());
        assertEquals("GiftAid +25%", restored.getDecoratorDescription());
    }

    @Test
    void roundTripsEventAttributes() {
        Event event = new Event("Community day", "2026-11-02", "Town hall");
        event.setId(5);
        event.setEventType("Workshop");
        event.getRequiredSkills().add("First Aid");

        EntityJsonCodec<Event> codec = new EntityJsonCodec<Event>(Event.class);
        Event restored = codec.fromJson(codec.toJson(event), 17);

        assertEquals(17, restored.getId());
        assertEquals("Community day", restored.getName());
        assertTrue(restored.getVolunteers().isEmpty());
        assertEquals("Workshop", restored.getEventType());
        assertEquals("First Aid", restored.getRequiredSkills().get(0));
    }
}
