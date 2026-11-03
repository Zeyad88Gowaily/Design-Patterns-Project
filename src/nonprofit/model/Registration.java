package nonprofit.model;

/** A volunteer's registration for an event. */
public class Registration extends Entity {
    private int volunteerId;
    private int eventId;
    private String registrationDate;

    public Registration(int volunteerId, int eventId, String registrationDate) {
        this.volunteerId = volunteerId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
    }

    public int getVolunteerId()       { return volunteerId; }
    public int getEventId()           { return eventId; }
    public String getRegistrationDate() { return registrationDate; }

    @Override
    public String toString() {
        return "#" + id + " Volunteer=" + volunteerId + " Event=" + eventId
                + " registered=" + registrationDate;
    }
}
