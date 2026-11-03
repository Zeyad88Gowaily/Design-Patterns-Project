package nonprofit.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import nonprofit.controller.VolunteerPanelController;
import nonprofit.model.Volunteer;

/** Volunteers tab. */
class VolunteersPanel extends TabPanel {
    private final VolunteerPanelController controller;
    private final DefaultTableModel model = newModel("ID", "Name", "Email", "Skills", "Availability", "Hours");
    private final JTable table = Ui.table(model);

    VolunteersPanel(VolunteerPanelController controller) {
        this.controller = controller;
        add(Ui.searchable(table), BorderLayout.CENTER);
        add(buttonRow(
                primaryButton("Add", this::addVolunteer),
                button("Edit", this::editVolunteer),
                button("Delete", this::deleteVolunteer),
                button("Add Skill", this::addSkill),
                button("Set Availability", this::setAvailability),
                button("Match by Skill", this::matchBySkill),
                button("Refresh", this::refresh)), BorderLayout.SOUTH);
        refresh();
    }

    private void addVolunteer() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField skill = new JTextField();
        JPanel fields = form("Name:", name, "Email:", email, "Skill (optional):", skill);
        if (JOptionPane.showConfirmDialog(this, fields, "Add Volunteer", JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) {
            return;
        }
        if (name.getText().trim().isEmpty()) {
            showError("Name is required.");
            return;
        }
        controller.add(name.getText().trim(), email.getText().trim(), skill.getText().trim());
        refresh();
    }

    private void editVolunteer() {
        int id = selectedId(table, "a volunteer");
        if (id < 0) return;
        Volunteer volunteer = controller.getById(id);
        if (volunteer == null) {
            showError("Not found.");
            return;
        }
        JTextField name = new JTextField(volunteer.getName());
        JTextField email = new JTextField(volunteer.getEmail());
        JPanel fields = form("Name:", name, "Email:", email);
        if (JOptionPane.showConfirmDialog(this, fields, "Edit Volunteer #" + id, JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            controller.update(id, name.getText().trim(), email.getText().trim());
            refresh();
        }
    }

    private void deleteVolunteer() {
        int id = selectedId(table, "a volunteer");
        if (id >= 0 && confirmDelete("volunteer #" + id)) {
            controller.delete(id);
            refresh();
        }
    }

    private void addSkill() {
        int id = selectedId(table, "a volunteer");
        if (id < 0) return;
        String skill = JOptionPane.showInputDialog(this, "Skill to add:");
        if (skill != null && !skill.trim().isEmpty()) {
            controller.addSkill(id, skill.trim());
            refresh();
        }
    }

    private void setAvailability() {
        int id = selectedId(table, "a volunteer");
        if (id < 0) return;
        String input = JOptionPane.showInputDialog(this,
                "Available days (comma-separated, e.g. Monday,Wednesday,Weekend):");
        if (input != null) {
            controller.setAvailability(id, splitList(input));
            refresh();
        }
    }

    private void matchBySkill() {
        String skill = JOptionPane.showInputDialog(this, "Skill to search for:");
        if (skill == null || skill.trim().isEmpty()) return;
        List<Volunteer> matches = controller.matchBySkill(skill.trim());
        if (matches.isEmpty()) {
            showInfo("No volunteers with skill '" + skill.trim() + "'.");
            return;
        }
        StringBuilder text = new StringBuilder("Volunteers with skill '" + skill.trim() + "':\n");
        for (Volunteer v : matches) {
            text.append("  #").append(v.getId()).append(" ").append(v.getName()).append("\n");
        }
        showInfo(text.toString());
    }

    @Override
    protected void refresh() {
        model.setRowCount(0);
        for (Volunteer v : controller.getAll()) {
            model.addRow(new Object[]{v.getId(), v.getName(), v.getEmail(),
                    String.join(", ", v.getSkills()), String.join(", ", v.getAvailability()), v.getHours()});
        }
    }
}
