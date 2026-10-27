package nonprofit.service;

import java.util.List;

import nonprofit.model.Volunteer;
import nonprofit.repository.DataStore;

/**
 * TODO [PHASE 1 - Person B] match volunteers to events by skills/availability.
 * TODO [PHASE 2 - Person B] hours tracking + certificate; ITERATOR for searching volunteers.
 */
public class VolunteerService {
    private DataStore db = DataStore.getInstance();

    public Volunteer add(String name, String email, String skill) {
        Volunteer v = new Volunteer(name, email);
        if (skill != null && skill.length() > 0) {
            v.getSkills().add(skill);
        }
        return db.volunteers.add(v);
    }

    public List<Volunteer> getAll() { return db.volunteers.getAll(); }
}
