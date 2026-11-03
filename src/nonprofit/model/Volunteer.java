package nonprofit.model;

import java.util.ArrayList;
import java.util.List;

/** A volunteer with skills, availability and hours. */
public class Volunteer extends Entity {
    private String name;
    private String email;
    private List<String> skills = new ArrayList<String>();
    private List<String> availability = new ArrayList<String>(); // e.g. "Monday", "Weekend"
    private double hours;

    public Volunteer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName()             { return name; }
    public String getEmail()            { return email; }
    public List<String> getSkills()     { return skills; }
    public List<String> getAvailability() { return availability; }
    public double getHours()            { return hours; }

    public void setName(String name)    { this.name = name; }
    public void setEmail(String email)  { this.email = email; }

    public void addHours(double h) { hours += h; }

    @Override
    public String toString() {
        return "#" + id + " " + name + " <" + email + ">"
                + " skills=" + skills
                + " available=" + availability
                + " hours=" + hours;
    }
}
