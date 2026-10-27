package nonprofit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO [PHASE 2 - Person B] STATE pattern: replace the String status with an EventState
 *      (Planned -> Open -> Ongoing -> Completed / Cancelled).
 */
public class Event extends Entity {
    private String name;
    private String date;
    private String location;
    private String status = "PLANNED";
    private List<Volunteer> volunteers = new ArrayList<Volunteer>();

    public Event(String name, String date, String location) {
        this.name = name;
        this.date = date;
        this.location = location;
    }

    public String getName() { return name; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<Volunteer> getVolunteers() { return volunteers; }

    public String toString() {
        return "#" + id + " " + name + " (" + date + ", " + location + ") " + status
                + " volunteers=" + volunteers.size();
    }
}
