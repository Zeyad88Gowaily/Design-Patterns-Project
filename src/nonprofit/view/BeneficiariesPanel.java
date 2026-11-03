package nonprofit.view;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import nonprofit.controller.BeneficiaryPanelController;
import nonprofit.controller.LookupPanelController;
import nonprofit.model.Beneficiary;

/** Beneficiaries tab. The need is chosen from the NEED_TYPE lookup table. */
class BeneficiariesPanel extends TabPanel {
    private final BeneficiaryPanelController controller;
    private final LookupPanelController lookups;
    private final DefaultTableModel model = newModel("ID", "Name", "Contact", "Need");
    private final JTable table = Ui.table(model);

    BeneficiariesPanel(BeneficiaryPanelController controller, LookupPanelController lookups) {
        this.controller = controller;
        this.lookups = lookups;
        add(Ui.searchable(table), BorderLayout.CENTER);
        add(buttonRow(
                primaryButton("Add", this::addBeneficiary),
                button("Edit", this::editBeneficiary),
                button("Delete", this::deleteBeneficiary),
                button("Refresh", this::refresh)), BorderLayout.SOUTH);
        refresh();
    }

    private void addBeneficiary() {
        JTextField name = new JTextField();
        JTextField contact = new JTextField();
        JComboBox<String> need = lookupCombo(lookups, "NEED_TYPE", "");
        JPanel fields = form("Name:", name, "Contact:", contact, "Need:", need);
        if (JOptionPane.showConfirmDialog(this, fields, "Add Beneficiary", JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) {
            return;
        }
        if (name.getText().trim().isEmpty()) {
            showError("Name is required.");
            return;
        }
        controller.add(name.getText().trim(), contact.getText().trim(), comboText(need));
        refresh();
    }

    private void editBeneficiary() {
        int id = selectedId(table, "a beneficiary");
        if (id < 0) return;
        Beneficiary beneficiary = controller.getById(id);
        if (beneficiary == null) {
            showError("Not found.");
            return;
        }
        JTextField name = new JTextField(beneficiary.getName());
        JTextField contact = new JTextField(beneficiary.getContact());
        JComboBox<String> need = lookupCombo(lookups, "NEED_TYPE", beneficiary.getNeed());
        JPanel fields = form("Name:", name, "Contact:", contact, "Need:", need);
        if (JOptionPane.showConfirmDialog(this, fields, "Edit Beneficiary #" + id, JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {
            controller.update(id, name.getText().trim(), contact.getText().trim(), comboText(need));
            refresh();
        }
    }

    private void deleteBeneficiary() {
        int id = selectedId(table, "a beneficiary");
        if (id >= 0 && confirmDelete("beneficiary #" + id)) {
            controller.delete(id);
            refresh();
        }
    }

    @Override
    protected void refresh() {
        model.setRowCount(0);
        for (Beneficiary b : controller.getAll()) {
            model.addRow(new Object[]{b.getId(), b.getName(), b.getContact(), b.getNeed()});
        }
    }
}
