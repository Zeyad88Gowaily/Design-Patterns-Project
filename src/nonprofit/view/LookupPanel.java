package nonprofit.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import nonprofit.controller.LookupPanelController;
import nonprofit.model.LookupItem;

/** Lookup tables tab: add, rename and delete entries of any category. */
class LookupPanel extends TabPanel {
    private final LookupPanelController controller;
    private final JComboBox<String> category =
            new JComboBox<>(new String[]{"SKILL", "EVENT_TYPE", "DONATION_TYPE", "NEED_TYPE"});
    private final DefaultTableModel model = newModel("ID", "Name", "Category");
    private final JTable table = Ui.table(model);

    LookupPanel(LookupPanelController controller) {
        this.controller = controller;

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Category:"));
        category.addActionListener(e -> run(this::refresh));
        top.add(category);
        add(top, BorderLayout.NORTH);
        add(Ui.searchable(table), BorderLayout.CENTER);
        add(buttonRow(
                primaryButton("Add", this::addEntry),
                button("Edit", this::editEntry),
                button("Delete", this::deleteEntry),
                button("Refresh", this::refresh)), BorderLayout.SOUTH);
        refresh();
    }

    private String selectedCategory() {
        return (String) category.getSelectedItem();
    }

    private void addEntry() {
        String name = JOptionPane.showInputDialog(this, "New " + selectedCategory() + " entry name:");
        if (name != null && !name.trim().isEmpty()) {
            controller.add(name.trim(), selectedCategory());
            refresh();
        }
    }

    private void editEntry() {
        int id = selectedId(table, "an entry");
        if (id < 0) return;
        String current = (String) table.getValueAt(table.getSelectedRow(), 1);
        String name = JOptionPane.showInputDialog(this, "New name:", current);
        if (name != null && !name.trim().isEmpty()) {
            controller.update(id, name.trim());
            refresh();
        }
    }

    private void deleteEntry() {
        int id = selectedId(table, "an entry");
        if (id >= 0 && confirmDelete("entry #" + id)) {
            controller.delete(id);
            refresh();
        }
    }

    @Override
    protected void refresh() {
        model.setRowCount(0);
        for (LookupItem item : controller.getByCategory(selectedCategory())) {
            model.addRow(new Object[]{item.getId(), item.getName(), item.getCategory()});
        }
    }
}
