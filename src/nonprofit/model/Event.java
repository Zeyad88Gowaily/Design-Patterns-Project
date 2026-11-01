package nonprofit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An event with volunteers, required skills, and lifecycle status.
 *
 * TODO [PHASE 2 - Person B] STATE pattern: replace the String status with an EventState
 *      (Planned -> Open -> Ongoing -> Completed / Cancelled).
 */
public class Event extends Entity {
    private String name;
    private String date;
    private String location;
    private String eventType = "";              // from EVENT_TYPE lookup
    private String status = "PLANNED";
    private List<Volunteer> volunteers = new ArrayList<Volunteer>();
    private List<String> requiredSkills = new ArrayList<String>();

    public Event(String name, String date, String location) {
        this.name = name;
        this.date = date;
        this.location = location;
    }

    // --- Getters ---
    public String getName()                 { return name; }
    public String getDate()                 { return date; }
    public String getLocation()             { return location; }
    public String getEventType()            { return eventType; }
    public String getStatus()               { return status; }
    public List<Volunteer> getVolunteers()   { return volunteers; }
    public List<String> getRequiredSkills()  { return requiredSkills; }

    // --- Setters (for edit/update) ---
    public void setName(String name)         { this.name = name; }
    public void setDate(String date)         { this.date = date; }
    public void setLocation(String location) { this.location = location; }
    public void setEventType(String type)    { this.eventType = type; }
    public void setStatus(String status)     { this.status = status; }

    @Override
    public String toString() {
        return "#" + id + " " + name
                + (eventType.isEmpty() ? "" : " [" + eventType + "]")
                + " (" + date + ", " + location + ") " + status
                + " volunteers=" + volunteers.size()
                + " requiredSkills=" + requiredSkills;
    }
}
