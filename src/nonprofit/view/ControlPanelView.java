package nonprofit.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import nonprofit.model.*;
import nonprofit.service.*;

/**
 * MVC - Swing-based View for the Non-Profit System.
 * Provides a tabbed interface for all modules:
 * Donations, Volunteers, Events, Beneficiaries, and Lookup Tables.
 */
public class ControlPanelView extends JFrame {

    private DonationService donationService;
    private VolunteerService volunteerService;
    private EventService eventService;
    private BeneficiaryService beneficiaryService;
    private LookupService lookupService;

    // Volunteer tab components
    private DefaultTableModel volunteerTableModel;
    private JTable volunteerTable;

    // Event tab components
    private DefaultTableModel eventTableModel;
    private JTable eventTable;

    // Registration tab components
    private DefaultTableModel registrationTableModel;

    // Ticket tab components
    private DefaultTableModel ticketTableModel;

    // Donor tab components
    private DefaultTableModel donorTableModel;
    private JTable donorTable;

    // Donation tab components
    private DefaultTableModel donationTableModel;
    private JTable donationTable;

    // Beneficiary tab components
    private DefaultTableModel beneficiaryTableModel;
    private JTable beneficiaryTable;

    // Lookup tab components
    private DefaultTableModel lookupTableModel;
    private JTable lookupTable;
    private JComboBox<String> categoryCombo;

    public ControlPanelView(DonationService donationService, VolunteerService volunteerService,
                            EventService eventService, BeneficiaryService beneficiaryService,
                            LookupService lookupService) {
        this.donationService = donationService;
        this.volunteerService = volunteerService;
        this.eventService = eventService;
        this.beneficiaryService = beneficiaryService;
        this.lookupService = lookupService;

        setTitle("MissionOS - Non-Profit Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        // Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Donations", buildDonationPanel());
        tabs.addTab("Volunteers", buildVolunteerPanel());
        tabs.addTab("Events", buildEventPanel());
        tabs.addTab("Beneficiaries", buildBeneficiaryPanel());
        tabs.addTab("Lookup Tables", buildLookupPanel());

        add(tabs);
    }

    // ========================= DONATIONS TAB =========================

    private JPanel buildDonationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Split into Donors (top) and Donations (bottom)
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.4);

        // --- Donor section ---
        JPanel donorPanel = new JPanel(new BorderLayout(5, 5));
        donorPanel.setBorder(BorderFactory.createTitledBorder("Donors"));

        donorTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        donorTable = new JTable(donorTableModel);
        donorPanel.add(new JScrollPane(donorTable), BorderLayout.CENTER);

        JPanel donorButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addDonor = new JButton("Add Donor");
        JButton refreshDonors = new JButton("Refresh");
        addDonor.addActionListener(e -> doAddDonor());
        refreshDonors.addActionListener(e -> refreshDonorTable());
        donorButtons.add(addDonor);
        donorButtons.add(refreshDonors);
        donorPanel.add(donorButtons, BorderLayout.SOUTH);

        // --- Donation section ---
        JPanel donationPanel = new JPanel(new BorderLayout(5, 5));
        donationPanel.setBorder(BorderFactory.createTitledBorder("Donations"));

        donationTableModel = new DefaultTableModel(new String[]{"ID", "Type", "Amount", "Donor", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        donationTable = new JTable(donationTableModel);
        donationPanel.add(new JScrollPane(donationTable), BorderLayout.CENTER);

        JPanel donationButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton makeDonation = new JButton("Make Donation");
        JButton refreshDonations = new JButton("Refresh");
        makeDonation.addActionListener(e -> doMakeDonation());
        refreshDonations.addActionListener(e -> refreshDonationTable());
        donationButtons.add(makeDonation);
        donationButtons.add(refreshDonations);
        donationPanel.add(donationButtons, BorderLayout.SOUTH);

        split.setTopComponent(donorPanel);
        split.setBottomComponent(donationPanel);
        panel.add(split, BorderLayout.CENTER);

        refreshDonorTable();
        refreshDonationTable();
        return panel;
    }

    private void doAddDonor() {
        JTextField nameF = new JTextField();
        JTextField emailF = new JTextField();
        JTextField phoneF = new JTextField();
        Object[] fields = {"Name:", nameF, "Email:", emailF, "Phone:", phoneF};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Add Donor", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            String phone = phoneF.getText().trim();
            if (name.isEmpty()) { showError("Name is required."); return; }
            donationService.addDonor(name, email, phone);
            refreshDonorTable();
        }
    }

    private void doMakeDonation() {
        List<Donor> donors = donationService.getDonors();
        if (donors.isEmpty()) { showError("Add a donor first."); return; }
        String[] donorNames = new String[donors.size()];
        for (int i = 0; i < donors.size(); i++) {
            donorNames[i] = "#" + donors.get(i).getId() + " " + donors.get(i).getName();
        }
        JComboBox<String> donorCombo = new JComboBox<>(donorNames);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"online", "check", "inkind"});
        JTextField amountF = new JTextField();
        JTextField detailF = new JTextField();
        Object[] fields = {"Donor:", donorCombo, "Type:", typeCombo, "Amount:", amountF, "Detail (inkind):", detailF};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Make Donation", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                int idx = donorCombo.getSelectedIndex();
                Donor donor = donors.get(idx);
                String type = (String) typeCombo.getSelectedItem();
                double amount = Double.parseDouble(amountF.getText().trim());
                String detail = detailF.getText().trim();
                donationService.donate(donor, type, amount, detail);
                refreshDonationTable();
            } catch (NumberFormatException ex) {
                showError("Invalid amount.");
            }
        }
    }

    private void refreshDonorTable() {
        donorTableModel.setRowCount(0);
        for (Donor d : donationService.getDonors()) {
            donorTableModel.addRow(new Object[]{d.getId(), d.getName(), d.getEmail(), d.getPhone()});
        }
    }

    private void refreshDonationTable() {
        donationTableModel.setRowCount(0);
        for (Donation d : donationService.getDonations()) {
            donationTableModel.addRow(new Object[]{d.getId(), d.getType(), d.getAmount(),
                    d.getDonor().getName(), d.getDate()});
        }
    }

    // ========================= VOLUNTEERS TAB =========================

    private JPanel buildVolunteerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        volunteerTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Email", "Skills", "Availability", "Hours"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        volunteerTable = new JTable(volunteerTableModel);
        panel.add(new JScrollPane(volunteerTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton addSkill = new JButton("Add Skill");
        JButton setAvail = new JButton("Set Availability");
        JButton matchSkill = new JButton("Match by Skill");
        JButton refresh = new JButton("Refresh");

        add.addActionListener(e -> doAddVolunteer());
        edit.addActionListener(e -> doEditVolunteer());
        delete.addActionListener(e -> doDeleteVolunteer());
        addSkill.addActionListener(e -> doAddSkillVolunteer());
        setAvail.addActionListener(e -> doSetAvailability());
        matchSkill.addActionListener(e -> doMatchBySkill());
        refresh.addActionListener(e -> refreshVolunteerTable());

        buttons.add(add);
        buttons.add(edit);
        buttons.add(delete);
        buttons.add(addSkill);
        buttons.add(setAvail);
        buttons.add(matchSkill);
        buttons.add(refresh);
        panel.add(buttons, BorderLayout.SOUTH);

        refreshVolunteerTable();
        return panel;
    }

    private void doAddVolunteer() {
        JTextField nameF = new JTextField();
        JTextField emailF = new JTextField();
        JTextField skillF = new JTextField();
        Object[] fields = {"Name:", nameF, "Email:", emailF, "Skill (optional):", skillF};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Add Volunteer", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim();
            String email = emailF.getText().trim();
            if (name.isEmpty()) { showError("Name is required."); return; }
            volunteerService.add(name, email, skillF.getText().trim());
            refreshVolunteerTable();
        }
    }

    private void doEditVolunteer() {
        int row = volunteerTable.getSelectedRow();
        if (row < 0) { showError("Select a volunteer first."); return; }
        int id = (int) volunteerTableModel.getValueAt(row, 0);
        Volunteer v = volunteerService.getById(id);
        if (v == null) { showError("Not found."); return; }

        JTextField nameF = new JTextField(v.getName());
        JTextField emailF = new JTextField(v.getEmail());
        Object[] fields = {"Name:", nameF, "Email:", emailF};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Edit Volunteer #" + id, JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            volunteerService.update(id, nameF.getText().trim(), emailF.getText().trim());
            refreshVolunteerTable();
        }
    }

    private void doDeleteVolunteer() {
        int row = volunteerTable.getSelectedRow();
        if (row < 0) { showError("Select a volunteer first."); return; }
        int id = (int) volunteerTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete volunteer #" + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            volunteerService.delete(id);
            refreshVolunteerTable();
        }
    }

    private void doAddSkillVolunteer() {
        int row = volunteerTable.getSelectedRow();
        if (row < 0) { showError("Select a volunteer first."); return; }
        int id = (int) volunteerTableModel.getValueAt(row, 0);
        String skill = JOptionPane.showInputDialog(this, "Skill to add:");
        if (skill != null && !skill.trim().isEmpty()) {
            volunteerService.addSkill(id, skill.trim());
            refreshVolunteerTable();
        }
    }

    private void doSetAvailability() {
        int row = volunteerTable.getSelectedRow();
        if (row < 0) { showError("Select a volunteer first."); return; }
        int id = (int) volunteerTableModel.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this,
                "Available days (comma-separated, e.g. Monday,Wednesday,Weekend):");
        if (input != null) {
            java.util.ArrayList<String> days = new java.util.ArrayList<>();
            for (String d : input.split(",")) {
                String trimmed = d.trim();
                if (!trimmed.isEmpty()) days.add(trimmed);
            }
            volunteerService.setAvailability(id, days);
            refreshVolunteerTable();
        }
    }

    private void doMatchBySkill() {
        String skill = JOptionPane.showInputDialog(this, "Skill to search for:");
        if (skill != null && !skill.trim().isEmpty()) {
            List<Volunteer> matches = volunteerService.matchBySkill(skill.trim());
            if (matches.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No volunteers with skill '" + skill + "'.");
            } else {
                StringBuilder sb = new StringBuilder("Volunteers with skill '" + skill + "':\n");
                for (Volunteer v : matches) {
                    sb.append("  #").append(v.getId()).append(" ").append(v.getName()).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString());
            }
        }
    }

    private void refreshVolunteerTable() {
        volunteerTableModel.setRowCount(0);
        for (Volunteer v : volunteerService.getAll()) {
            volunteerTableModel.addRow(new Object[]{
                    v.getId(), v.getName(), v.getEmail(),
                    String.join(", ", v.getSkills()),
                    String.join(", ", v.getAvailability()),
                    v.getHours()
            });
        }
    }

    // ========================= EVENTS TAB =========================

    private JPanel buildEventPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top: event table
        eventTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Type", "Date", "Location", "Status", "Volunteers", "Required Skills"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        eventTable = new JTable(eventTableModel);
        panel.add(new JScrollPane(eventTable), BorderLayout.CENTER);

        // Bottom: two rows of buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton create = new JButton("Create");
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton assign = new JButton("Assign Volunteer");
        JButton suggest = new JButton("Suggest Volunteers");
        JButton refresh = new JButton("Refresh");
        create.addActionListener(e -> doCreateEvent());
        edit.addActionListener(e -> doEditEvent());
        delete.addActionListener(e -> doDeleteEvent());
        assign.addActionListener(e -> doAssignVolunteer());
        suggest.addActionListener(e -> doSuggestVolunteers());
        refresh.addActionListener(e -> refreshEventTable());
        row1.add(create); row1.add(edit); row1.add(delete);
        row1.add(assign); row1.add(suggest); row1.add(refresh);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton register = new JButton("Register");
        JButton ticket = new JButton("Issue Ticket");
        JButton attend = new JButton("Mark Attendance");
        JButton listReg = new JButton("List Registrations");
        JButton listTix = new JButton("List Tickets");
        register.addActionListener(e -> doRegister());
        ticket.addActionListener(e -> doIssueTicket());
        attend.addActionListener(e -> doMarkAttendance());
        listReg.addActionListener(e -> doListRegistrations());
        listTix.addActionListener(e -> doListTickets());
        row2.add(register); row2.add(ticket); row2.add(attend);
        row2.add(listReg); row2.add(listTix);

        buttonPanel.add(row1);
        buttonPanel.add(row2);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        refreshEventTable();
        return panel;
    }

    private void doCreateEvent() {
        JTextField nameF = new JTextField();
        JTextField dateF = new JTextField();
        JTextField locF = new JTextField();
        JTextField typeF = new JTextField();
        JTextField skillsF = new JTextField();
        Object[] fields = {"Name:", nameF, "Date:", dateF, "Location:", locF,
                "Event Type (optional):", typeF, "Required Skills (comma-sep, optional):", skillsF};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Create Event", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim();
            if (name.isEmpty()) { showError("Name is required."); return; }
            Event e = eventService.create(name, dateF.getText().trim(), locF.getText().trim());
            String type = typeF.getText().trim();
            if (!type.isEmpty()) e.setEventType(type);
            String skills = skillsF.getText().trim();
            if (!skills.isEmpty()) {
                for (String s : skills.split(",")) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) e.getRequiredSkills().add(trimmed);
                }
            }
            refreshEventTable();
        }
    }

    private void doEditEvent() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { showError("Select an event first."); return; }
        int id = (int) eventTableModel.getValueAt(row, 0);
        Event e = eventService.getById(id);
        if (e == null) { showError("Not found."); return; }

        JTextField nameF = new JTextField(e.getName());
        JTextField dateF = new JTextField(e.getDate());
        JTextField locF = new JTextField(e.getLocation());
        Object[] fields = {"Name:", nameF, "Date:", dateF, "Location:", locF};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Edit Event #" + id, JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            eventService.update(id, nameF.getText().trim(), dateF.getText().trim(), locF.getText().trim());
            refreshEventTable();
        }
    }

    private void doDeleteEvent() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { showError("Select an event first."); return; }
        int id = (int) eventTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete event #" + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            eventService.delete(id);
            refreshEventTable();
        }
    }

    private void doAssignVolunteer() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { showError("Select an event first."); return; }
        int eventId = (int) eventTableModel.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Volunteer ID to assign:");
        if (input != null) {
            try {
                int vId = Integer.parseInt(input.trim());
                boolean ok = eventService.assignVolunteer(eventId, vId);
                showInfo(ok ? "Assigned." : "Event or volunteer not found.");
                refreshEventTable();
            } catch (NumberFormatException ex) { showError("Invalid ID."); }
        }
    }

    private void doSuggestVolunteers() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { showError("Select an event first."); return; }
        int eventId = (int) eventTableModel.getValueAt(row, 0);
        List<Volunteer> suggestions = eventService.suggestVolunteers(eventId);
        if (suggestions.isEmpty()) {
            showInfo("No matching volunteers found.");
        } else {
            StringBuilder sb = new StringBuilder("Suggested volunteers:\n");
            for (Volunteer v : suggestions) {
                sb.append("  #").append(v.getId()).append(" ").append(v.getName())
                        .append(" skills=").append(v.getSkills()).append("\n");
            }
            showInfo(sb.toString());
        }
    }

    private void doRegister() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { showError("Select an event first."); return; }
        int eventId = (int) eventTableModel.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Volunteer ID to register:");
        if (input != null) {
            try {
                int vId = Integer.parseInt(input.trim());
                Registration reg = eventService.register(eventId, vId);
                showInfo(reg != null ? "Registered: " + reg : "Failed (not found or already registered).");
            } catch (NumberFormatException ex) { showError("Invalid ID."); }
        }
    }

    private void doIssueTicket() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { showError("Select an event first."); return; }
        int eventId = (int) eventTableModel.getValueAt(row, 0);
        String input = JOptionPane.showInputDialog(this, "Volunteer ID to issue ticket for:");
        if (input != null) {
            try {
                int vId = Integer.parseInt(input.trim());
                Ticket t = eventService.issueTicket(eventId, vId);
                showInfo(t != null ? "Ticket issued: " + t : "Failed (not registered or already issued).");
            } catch (NumberFormatException ex) { showError("Invalid ID."); }
        }
    }

    private void doMarkAttendance() {
        String input = JOptionPane.showInputDialog(this, "Ticket ID to check in:");
        if (input != null) {
            try {
                int tId = Integer.parseInt(input.trim());
                boolean ok = eventService.markAttendance(tId);
                showInfo(ok ? "Checked in!" : "Ticket not found or already checked in.");
            } catch (NumberFormatException ex) { showError("Invalid ID."); }
        }
    }

    private void doListRegistrations() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { showError("Select an event first."); return; }
        int eventId = (int) eventTableModel.getValueAt(row, 0);
        List<Registration> regs = eventService.getRegistrations(eventId);
        if (regs.isEmpty()) {
            showInfo("No registrations for event #" + eventId + ".");
        } else {
            StringBuilder sb = new StringBuilder("Registrations for event #" + eventId + ":\n");
            for (Registration r : regs) {
                sb.append("  ").append(r).append("\n");
            }
            showInfo(sb.toString());
        }
    }

    private void doListTickets() {
        int row = eventTable.getSelectedRow();
        if (row < 0) { showError("Select an event first."); return; }
        int eventId = (int) eventTableModel.getValueAt(row, 0);
        List<Ticket> tickets = eventService.getTickets(eventId);
        if (tickets.isEmpty()) {
            showInfo("No tickets for event #" + eventId + ".");
        } else {
            StringBuilder sb = new StringBuilder("Tickets for event #" + eventId + ":\n");
            for (Ticket t : tickets) {
                sb.append("  ").append(t).append("\n");
            }
            showInfo(sb.toString());
        }
    }

    private void refreshEventTable() {
        eventTableModel.setRowCount(0);
        for (Event e : eventService.getAll()) {
            eventTableModel.addRow(new Object[]{
                    e.getId(), e.getName(), e.getEventType(), e.getDate(),
                    e.getLocation(), e.getStatus(), e.getVolunteers().size(),
                    String.join(", ", e.getRequiredSkills())
            });
        }
    }

    // ========================= BENEFICIARIES TAB =========================

    private JPanel buildBeneficiaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        beneficiaryTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Contact", "Need"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        beneficiaryTable = new JTable(beneficiaryTableModel);
        panel.add(new JScrollPane(beneficiaryTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton refresh = new JButton("Refresh");
        add.addActionListener(e -> doAddBeneficiary());
        refresh.addActionListener(e -> refreshBeneficiaryTable());
        buttons.add(add);
        buttons.add(refresh);
        panel.add(buttons, BorderLayout.SOUTH);

        refreshBeneficiaryTable();
        return panel;
    }

    private void doAddBeneficiary() {
        JTextField nameF = new JTextField();
        JTextField contactF = new JTextField();
        JTextField needF = new JTextField();
        Object[] fields = {"Name:", nameF, "Contact:", contactF, "Need:", needF};
        int ok = JOptionPane.showConfirmDialog(this, fields, "Add Beneficiary", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim();
            if (name.isEmpty()) { showError("Name is required."); return; }
            beneficiaryService.add(name, contactF.getText().trim(), needF.getText().trim());
            refreshBeneficiaryTable();
        }
    }

    private void refreshBeneficiaryTable() {
        beneficiaryTableModel.setRowCount(0);
        for (Beneficiary b : beneficiaryService.getAll()) {
            beneficiaryTableModel.addRow(new Object[]{b.getId(), b.getName(), b.getContact(), b.getNeed()});
        }
    }

    // ========================= LOOKUP TABLES TAB =========================

    private JPanel buildLookupPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Category selector at top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"SKILL", "EVENT_TYPE", "DONATION_TYPE", "NEED_TYPE"});
        categoryCombo.addActionListener(e -> refreshLookupTable());
        topPanel.add(categoryCombo);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        lookupTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Category"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        lookupTable = new JTable(lookupTableModel);
        panel.add(new JScrollPane(lookupTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add");
        JButton editBtn = new JButton("Edit");
        JButton delete = new JButton("Delete");
        JButton refresh = new JButton("Refresh");
        add.addActionListener(e -> doAddLookup());
        editBtn.addActionListener(e -> doEditLookup());
        delete.addActionListener(e -> doDeleteLookup());
        refresh.addActionListener(e -> refreshLookupTable());
        buttons.add(add);
        buttons.add(editBtn);
        buttons.add(delete);
        buttons.add(refresh);
        panel.add(buttons, BorderLayout.SOUTH);

        refreshLookupTable();
        return panel;
    }

    private String getSelectedCategory() {
        return (String) categoryCombo.getSelectedItem();
    }

    private void doAddLookup() {
        String name = JOptionPane.showInputDialog(this, "New " + getSelectedCategory() + " entry name:");
        if (name != null && !name.trim().isEmpty()) {
            lookupService.add(name.trim(), getSelectedCategory());
            refreshLookupTable();
        }
    }

    private void doEditLookup() {
        int row = lookupTable.getSelectedRow();
        if (row < 0) { showError("Select an entry first."); return; }
        int id = (int) lookupTableModel.getValueAt(row, 0);
        String current = (String) lookupTableModel.getValueAt(row, 1);
        String newName = JOptionPane.showInputDialog(this, "New name:", current);
        if (newName != null && !newName.trim().isEmpty()) {
            lookupService.update(id, newName.trim());
            refreshLookupTable();
        }
    }

    private void doDeleteLookup() {
        int row = lookupTable.getSelectedRow();
        if (row < 0) { showError("Select an entry first."); return; }
        int id = (int) lookupTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete entry #" + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            lookupService.delete(id);
            refreshLookupTable();
        }
    }

    private void refreshLookupTable() {
        lookupTableModel.setRowCount(0);
        for (LookupItem item : lookupService.getByCategory(getSelectedCategory())) {
            lookupTableModel.addRow(new Object[]{item.getId(), item.getName(), item.getCategory()});
        }
    }

    // ========================= UTILITIES =========================

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
