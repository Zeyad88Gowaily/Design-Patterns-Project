package nonprofit.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import nonprofit.controller.DonationPanelController;
import nonprofit.model.Donation;
import nonprofit.model.Donor;

/** Donations tab: donors on top, donations below. */
class DonationsPanel extends TabPanel {
    /** Receipt channels a donor can choose (see NotificationFactory). */
    private static final String[] RECEIPT_CHANNELS = {"email", "sms", "social"};

    private final DonationPanelController controller;
    private final DefaultTableModel donorModel = newModel("ID", "Name", "Email", "Phone", "Notification");
    private final DefaultTableModel donationModel = newModel("ID", "Type", "Amount", "Donor", "Date");
    private final JTable donorTable = Ui.table(donorModel);
    private final JTable donationTable = Ui.table(donationModel);

    DonationsPanel(DonationPanelController controller) {
        this.controller = controller;

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                section("Donors", donorTable, buttonRow(
                        primaryButton("Add Donor", this::addDonor),
                        button("Edit Donor", this::editDonor),
                        button("Delete Donor", this::deleteDonor),
                        button("Refresh", this::refreshDonors))),
                section("Donations", donationTable, buttonRow(
                        primaryButton("Make Donation", this::makeDonation),
                        button("Delete Donation", this::deleteDonation),
                        button("Refresh", this::refreshDonations))));
        split.setResizeWeight(0.4);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    private JPanel section(String title, JTable table, JPanel buttons) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(Ui.searchable(table), BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void addDonor() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        JComboBox<String> channel = new JComboBox<>(RECEIPT_CHANNELS);
        JPanel fields = form("Name:", name, "Email:", email, "Phone:", phone, "Donation receipt via:", channel);
        if (JOptionPane.showConfirmDialog(this, fields, "Add Donor", JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) {
            return;
        }
        if (name.getText().trim().isEmpty()) {
            showError("Name is required.");
            return;
        }
        String selected = (String) channel.getSelectedItem();
        if ("sms".equals(selected) && phone.getText().trim().isEmpty()) {
            showError("A phone number is required for SMS receipts.");
            return;
        }
        controller.addDonor(name.getText().trim(), email.getText().trim(), phone.getText().trim(), selected);
        refreshDonors();
    }

    private void editDonor() {
        int id = selectedId(donorTable, "a donor");
        if (id < 0) return;
        Donor donor = controller.getDonorById(id);
        if (donor == null) {
            showError("Not found.");
            return;
        }
        JTextField name = new JTextField(donor.getName());
        JTextField email = new JTextField(donor.getEmail());
        JTextField phone = new JTextField(donor.getPhone());
        JComboBox<String> channel = new JComboBox<>(RECEIPT_CHANNELS);
        channel.setSelectedItem(donor.getNotificationChannel());
        JPanel fields = form("Name:", name, "Email:", email, "Phone:", phone, "Donation receipt via:", channel);
        if (JOptionPane.showConfirmDialog(this, fields, "Edit Donor #" + id, JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            controller.updateDonor(id, name.getText().trim(), email.getText().trim(),
                    phone.getText().trim(), (String) channel.getSelectedItem());
            refreshDonors();
        }
    }

    private void deleteDonor() {
        int id = selectedId(donorTable, "a donor");
        if (id >= 0 && confirmDelete("donor #" + id)) {
            controller.deleteDonor(id);
            refreshDonors();
        }
    }

    private void makeDonation() {
        List<Donor> donors = controller.getDonors();
        if (donors.isEmpty()) {
            showError("Add a donor first.");
            return;
        }
        List<String> types = controller.getDonationTypes();
        if (types.isEmpty()) {
            showError("No donation types configured. Add one in Lookup Tables.");
            return;
        }
        String[] donorNames = new String[donors.size()];
        for (int i = 0; i < donorNames.length; i++) {
            donorNames[i] = "#" + donors.get(i).getId() + " " + donors.get(i).getName();
        }
        JComboBox<String> donorCombo = new JComboBox<>(donorNames);
        JComboBox<String> typeCombo = new JComboBox<>(types.toArray(new String[0]));
        JTextField amount = new JTextField();
        JTextField detail = new JTextField();
        JCheckBox giftAid = new JCheckBox("Gift Aid (+25%)");
        JCheckBox matching = new JCheckBox("Corporate matching fund (x2)");
        JTextField months = new JTextField("0");
        JPanel fields = form("Donor:", donorCombo, "Type:", typeCombo, "Amount:", amount,
                "Item description (in-kind only):", detail,
                "Add-ons:", giftAid, matching, "Recurring months (0 = one-off):", months);
        if (JOptionPane.showConfirmDialog(this, fields, "Make Donation", JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            Donation saved = controller.donate(donors.get(donorCombo.getSelectedIndex()),
                    (String) typeCombo.getSelectedItem(), Double.parseDouble(amount.getText().trim()),
                    detail.getText().trim(), giftAid.isSelected(), matching.isSelected(),
                    Integer.parseInt(months.getText().trim()));
            refreshDonations();
            showInfo("Saved: " + saved);
        } catch (NumberFormatException ex) {
            showError("Amount must be a number and months a whole number.");
        }
    }

    private void deleteDonation() {
        int id = selectedId(donationTable, "a donation");
        if (id >= 0 && confirmDelete("donation #" + id)) {
            controller.deleteDonation(id);
            refreshDonations();
        }
    }

    @Override
    protected void refresh() {
        refreshDonors();
        refreshDonations();
    }

    private void refreshDonors() {
        donorModel.setRowCount(0);
        for (Donor d : controller.getDonors()) {
            donorModel.addRow(new Object[]{d.getId(), d.getName(), d.getEmail(), d.getPhone(),
                    d.getNotificationChannel()});
        }
    }

    private void refreshDonations() {
        donationModel.setRowCount(0);
        for (Donation d : controller.getDonations()) {
            donationModel.addRow(new Object[]{d.getId(), d.getType(), d.getAmount(),
                    d.getDonor().getName(), d.getDate()});
        }
    }
}
