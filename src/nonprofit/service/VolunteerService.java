package nonprofit.service;

import java.util.ArrayList;
import java.util.List;

import nonprofit.model.Volunteer;
import nonprofit.repository.DataStore;

/** Volunteer operations: skills, availability and matching. */
public class VolunteerService {
    private DataStore db = DataStore.getInstance();

    public Volunteer add(String name, String email, String skill) {
        Volunteer v = new Volunteer(name, email);
        if (skill != null && skill.length() > 0) {
            v.getSkills().add(skill);
        }
        return db.volunteers.add(v);
    }

    public List<Volunteer> getAll()       { return db.volunteers.getAll(); }
    public Volunteer getById(int id)      { return db.volunteers.getById(id); }

    public boolean update(int id, String name, String email) {
        Volunteer v = db.volunteers.getById(id);
        if (v == null) return false;
        if (name != null && name.length() > 0) v.setName(name);
        if (email != null && email.length() > 0) v.setEmail(email);
        return db.volunteers.save(v);
    }

    public boolean delete(int id) { return db.volunteers.remove(id); }

    public boolean addSkill(int volunteerId, String skill) {
        Volunteer v = db.volunteers.getById(volunteerId);
        if (v == null || skill == null || skill.isEmpty()) return false;
        if (!v.getSkills().contains(skill)) {
            v.getSkills().add(skill);
        }
        return db.volunteers.save(v);
    }

    public boolean setAvailability(int volunteerId, List<String> days) {
        Volunteer v = db.volunteers.getById(volunteerId);
        if (v == null) return false;
        v.getAvailability().clear();
        v.getAvailability().addAll(days);
        return db.volunteers.save(v);
    }

    /** Volunteers who have the skill. */
    public List<Volunteer> matchBySkill(String requiredSkill) {
        List<Volunteer> matched = new ArrayList<Volunteer>();
        for (Volunteer v : db.volunteers.getAll()) {
            for (String s : v.getSkills()) {
                if (s.equalsIgnoreCase(requiredSkill)) {
                    matched.add(v);
                    break;
                }
            }
        }
        return matched;
    }

    /** Volunteers available on the day. */
    public List<Volunteer> matchByAvailability(String day) {
        List<Volunteer> matched = new ArrayList<Volunteer>();
        for (Volunteer v : db.volunteers.getAll()) {
            for (String d : v.getAvailability()) {
                if (d.equalsIgnoreCase(day)) {
                    matched.add(v);
                    break;
                }
            }
        }
        return matched;
    }
}
