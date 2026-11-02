package nonprofit.controller;

import java.util.List;

import nonprofit.model.Volunteer;
import nonprofit.service.VolunteerService;

public class VolunteerPanelController {
    private final VolunteerService service;

    public VolunteerPanelController(VolunteerService service) {
        this.service = service;
    }

    public Volunteer add(String name, String email, String skill) {
        return service.add(name, email, skill);
    }

    public Volunteer getById(int id) {
        return service.getById(id);
    }

    public boolean update(int id, String name, String email) {
        return service.update(id, name, email);
    }

    public boolean delete(int id) {
        return service.delete(id);
    }

    public boolean addSkill(int volunteerId, String skill) {
        return service.addSkill(volunteerId, skill);
    }

    public boolean setAvailability(int volunteerId, List<String> days) {
        return service.setAvailability(volunteerId, days);
    }

    public List<Volunteer> matchBySkill(String skill) {
        return service.matchBySkill(skill);
    }

    public List<Volunteer> getAll() {
        return service.getAll();
    }
}
