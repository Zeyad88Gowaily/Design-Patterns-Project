package nonprofit.model;

import java.util.ArrayList;
import java.util.List;

/** An event with its assigned volunteers, required skills and status. */
public class Event extends Entity {
    private String name;
    private String date;
    private String location;
    private String eventType = "";              // value from the EVENT_TYPE lookup
    private String status = "PLANNED";
    private List<Volunteer> volunteers = new ArrayList<Volunteer>();
    private List<String> requiredSkills = new ArrayList<String>();

    public Event(String name, String date, String location) {
        this.name = name;
        this.date = date;
        this.location = location;
    }

    public String getName()                 { return name; }
    public String getDate()                 { return date; }
    public String getLocation()             { return location; }
    public String getEventType()            { return eventType; }
    public String getStatus()               { return status; }
    public List<Volunteer> getVolunteers()   { return volunteers; }
    public List<String> getRequiredSkills()  { return requiredSkills; }

    public void setName(String name)         { this.name = name; }
    public void setDate(String date)         { this.date = date; }
    public void setLocation(String location) { this.location = location; }
    public void setEventType(String type)    { this.eventType = type; }
    public void setStatus(String status)     { this.status = status; }
    public void setVolunteers(List<Volunteer> volunteers) { this.volunteers = volunteers; }

    @Override
    public String toString() {
        return "#" + id + " " + name
                + (eventType.isEmpty() ? "" : " [" + eventType + "]")
                + " (" + date + ", " + location + ") " + status
                + " volunteers=" + volunteers.size()
                + " requiredSkills=" + requiredSkills;
    }
}
