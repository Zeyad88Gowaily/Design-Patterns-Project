package nonprofit.controller;

import java.util.ArrayList;
import java.util.List;

import nonprofit.model.Volunteer;
import nonprofit.service.VolunteerService;
import nonprofit.view.ConsoleView;

/** MVC - Controller for volunteers (Person B). */
public class VolunteerController {
    private VolunteerService service;
    private ConsoleView view;

    public VolunteerController(VolunteerService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void menu() {
        while (true) {
            view.show("\n== Volunteers ==");
            view.show("1) Add volunteer  2) List volunteers  3) Edit volunteer  4) Delete volunteer");
            view.show("5) Add skill      6) Set availability 7) Match by skill  8) Match by availability");
            view.show("0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;

            switch (choice) {
                case 1: doAdd(); break;
                case 2: view.showList("Volunteers", service.getAll()); break;
                case 3: doEdit(); break;
                case 4: doDelete(); break;
                case 5: doAddSkill(); break;
                case 6: doSetAvailability(); break;
                case 7: doMatchBySkill(); break;
                case 8: doMatchByAvailability(); break;
                default: view.show("Invalid choice.");
            }
        }
    }

    private void doAdd() {
        String name  = view.ask("Name");
        String email = view.ask("Email");
        String skill = view.ask("Skill (or blank)");
        view.show("Added " + service.add(name, email, skill));
    }

    private void doEdit() {
        view.showList("Volunteers", service.getAll());
        int id = view.askInt("Volunteer id to edit");
        Volunteer v = service.getById(id);
        if (v == null) { view.show("Not found."); return; }
        view.show("Current: " + v);
        String name  = view.ask("New name (blank to keep)");
        String email = view.ask("New email (blank to keep)");
        service.update(id, name, email);
        view.show("Updated: " + service.getById(id));
    }

    private void doDelete() {
        view.showList("Volunteers", service.getAll());
        int id = view.askInt("Volunteer id to delete");
        view.show(service.delete(id) ? "Deleted." : "Not found.");
    }

    private void doAddSkill() {
        int id = view.askInt("Volunteer id");
        String skill = view.ask("Skill to add");
        view.show(service.addSkill(id, skill) ? "Skill added." : "Volunteer not found.");
    }

    private void doSetAvailability() {
        int id = view.askInt("Volunteer id");
        String input = view.ask("Available days (comma-separated, e.g. Monday,Wednesday,Weekend)");
        List<String> days = new ArrayList<String>();
        for (String d : input.split(",")) {
            String trimmed = d.trim();
            if (trimmed.length() > 0) days.add(trimmed);
        }
        view.show(service.setAvailability(id, days) ? "Availability set." : "Volunteer not found.");
    }

    private void doMatchBySkill() {
        String skill = view.ask("Skill to search for");
        List<Volunteer> matches = service.matchBySkill(skill);
        view.showList("Volunteers with skill '" + skill + "'", matches);
    }

    private void doMatchByAvailability() {
        String day = view.ask("Day to search for (e.g. Monday)");
        List<Volunteer> matches = service.matchByAvailability(day);
        view.showList("Volunteers available on '" + day + "'", matches);
    }
}
