package nonprofit.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import nonprofit.controller.EventPanelController;
import nonprofit.controller.LookupPanelController;
import nonprofit.model.Event;
import nonprofit.model.Registration;
import nonprofit.model.Ticket;
import nonprofit.model.Volunteer;

/** Events tab: events, volunteer assignment, registration, tickets and attendance. */
class EventsPanel extends TabPanel {
    private final EventPanelController controller;
    private final LookupPanelController lookups;
    private final DefaultTableModel model = newModel(
            "ID", "Name", "Type", "Date", "Location", "Status", "Volunteers", "Required Skills");
    private final JTable table = Ui.table(model);

    EventsPanel(EventPanelController controller, LookupPanelController lookups) {
        this.controller = controller;
        this.lookups = lookups;
        add(Ui.searchable(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 5, 5));
        buttons.add(buttonRow(
                primaryButton("Create", this::createEvent),
                button("Edit", this::editEvent),
                button("Delete", this::deleteEvent),
                button("Assign Volunteer", this::assignVolunteer),
                button("Suggest Volunteers", this::suggestVolunteers),
                button("Refresh", this::refresh)));
        buttons.add(buttonRow(
                button("Register", this::register),
                button("Issue Ticket", this::issueTicket),
                button("Mark Attendance", this::markAttendance),
                button("List Registrations", this::listRegistrations),
                button("List Tickets", this::listTickets)));
        add(buttons, BorderLayout.SOUTH);
        refresh();
    }

    private void createEvent() {
        JTextField name = new JTextField();
        JTextField date = new JTextField();
        JTextField location = new JTextField();
        JComboBox<String> type = lookupCombo(lookups, "EVENT_TYPE", "");
        JTextField skills = new JTextField();
        JPanel fields = form("Name:", name, "Date:", date, "Location:", location,
                "Event Type (optional):", type, "Required Skills (comma-sep, optional):", skills);
        if (JOptionPane.showConfirmDialog(this, fields, "Create Event", JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) {
            return;
        }
        if (name.getText().trim().isEmpty()) {
            showError("Name is required.");
            return;
        }
        controller.create(name.getText().trim(), date.getText().trim(), location.getText().trim(),
                comboText(type), splitList(skills.getText()));
        refresh();
    }

    private void editEvent() {
        int id = selectedId(table, "an event");
        if (id < 0) return;
        Event event = controller.getById(id);
        if (event == null) {
            showError("Not found.");
            return;
        }
        JTextField name = new JTextField(event.getName());
        JTextField date = new JTextField(event.getDate());
        JTextField location = new JTextField(event.getLocation());
        JPanel fields = form("Name:", name, "Date:", date, "Location:", location);
        if (JOptionPane.showConfirmDialog(this, fields, "Edit Event #" + id, JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            controller.update(id, name.getText().trim(), date.getText().trim(), location.getText().trim());
            refresh();
        }
    }

    private void deleteEvent() {
        int id = selectedId(table, "an event");
        if (id >= 0 && confirmDelete("event #" + id)) {
            controller.delete(id);
            refresh();
        }
    }

    private void assignVolunteer() {
        int eventId = selectedId(table, "an event");
        Integer volunteerId = eventId < 0 ? null : askId("Volunteer ID to assign:");
        if (volunteerId != null) {
            showInfo(controller.assignVolunteer(eventId, volunteerId)
                    ? "Assigned." : "Event or volunteer not found, or already assigned.");
            refresh();
        }
    }

    private void suggestVolunteers() {
        int eventId = selectedId(table, "an event");
        if (eventId < 0) return;
        List<Volunteer> suggestions = controller.suggestVolunteers(eventId);
        if (suggestions.isEmpty()) {
            showInfo("No matching volunteers found.");
            return;
        }
        StringBuilder text = new StringBuilder("Suggested volunteers:\n");
        for (Volunteer v : suggestions) {
            text.append("  #").append(v.getId()).append(" ").append(v.getName())
                    .append(" skills=").append(v.getSkills()).append("\n");
        }
        showInfo(text.toString());
    }

    private void register() {
        int eventId = selectedId(table, "an event");
        Integer volunteerId = eventId < 0 ? null : askId("Volunteer ID to register:");
        if (volunteerId != null) {
            Registration registration = controller.register(eventId, volunteerId);
            showInfo(registration != null ? "Registered: " + registration
                    : "Failed (not found or already registered).");
        }
    }

    private void issueTicket() {
        int eventId = selectedId(table, "an event");
        Integer volunteerId = eventId < 0 ? null : askId("Volunteer ID to issue ticket for:");
        if (volunteerId != null) {
            Ticket ticket = controller.issueTicket(eventId, volunteerId);
            showInfo(ticket != null ? "Ticket issued: " + ticket
                    : "Failed (not registered or already issued).");
        }
    }

    private void markAttendance() {
        Integer ticketId = askId("Ticket ID to check in:");
        if (ticketId != null) {
            showInfo(controller.markAttendance(ticketId)
                    ? "Checked in." : "Ticket not found or already checked in.");
        }
    }

    private void listRegistrations() {
        int eventId = selectedId(table, "an event");
        if (eventId < 0) return;
        List<Registration> registrations = controller.getRegistrations(eventId);
        if (registrations.isEmpty()) {
            showInfo("No registrations for event #" + eventId + ".");
            return;
        }
        StringBuilder text = new StringBuilder("Registrations for event #" + eventId + ":\n");
        for (Registration r : registrations) {
            text.append("  ").append(r).append("\n");
        }
        showInfo(text.toString());
    }

    private void listTickets() {
        int eventId = selectedId(table, "an event");
        if (eventId < 0) return;
        List<Ticket> tickets = controller.getTickets(eventId);
        if (tickets.isEmpty()) {
            showInfo("No tickets for event #" + eventId + ".");
            return;
        }
        StringBuilder text = new StringBuilder("Tickets for event #" + eventId + ":\n");
        for (Ticket t : tickets) {
            text.append("  ").append(t).append("\n");
        }
        showInfo(text.toString());
    }

    /** Asks for an id; returns null if the dialog was cancelled or the input is not a number. */
    private Integer askId(String prompt) {
        String input = JOptionPane.showInputDialog(this, prompt);
        if (input == null) return null;
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException ex) {
            showError("Invalid ID.");
            return null;
        }
    }

    @Override
    protected void refresh() {
        model.setRowCount(0);
        for (Event e : controller.getAll()) {
            model.addRow(new Object[]{e.getId(), e.getName(), e.getEventType(), e.getDate(),
                    e.getLocation(), e.getStatus(), e.getVolunteers().size(),
                    String.join(", ", e.getRequiredSkills())});
        }
    }
}
