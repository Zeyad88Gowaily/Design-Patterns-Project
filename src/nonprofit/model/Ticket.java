package nonprofit.model;

/** A ticket for an event. checkedIn records attendance. */
public class Ticket extends Entity {
    private String ticketNumber;
    private int eventId;
    private int volunteerId;
    private boolean checkedIn;

    public Ticket(String ticketNumber, int eventId, int volunteerId) {
        this.ticketNumber = ticketNumber;
        this.eventId = eventId;
        this.volunteerId = volunteerId;
        this.checkedIn = false;
    }

    public String getTicketNumber() { return ticketNumber; }
    public int getEventId()         { return eventId; }
    public int getVolunteerId()     { return volunteerId; }
    public boolean isCheckedIn()    { return checkedIn; }
    public void checkIn()           { this.checkedIn = true; }

    @Override
    public String toString() {
        return "#" + id + " Ticket=" + ticketNumber + " Event=" + eventId
                + " Volunteer=" + volunteerId + " checkedIn=" + checkedIn;
    }
}
