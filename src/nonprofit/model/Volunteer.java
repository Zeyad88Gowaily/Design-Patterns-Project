package nonprofit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO [PHASE 1 - Person B] add availability (days/times) and a lookup table of skills.
 * TODO [PHASE 2 - Person B] track hours per task/event + volunteer certificate generation.
 */
public class Volunteer extends Entity {
    private String name;
    private String email;
    private List<String> skills = new ArrayList<String>();
    private double hours;

    public Volunteer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getSkills() { return skills; }
    public double getHours() { return hours; }
    public void addHours(double h) { hours += h; }

    public String toString() { return "#" + id + " " + name + " skills=" + skills + " hours=" + hours; }
}
