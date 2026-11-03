package nonprofit.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import nonprofit.controller.LookupPanelController;
import nonprofit.model.LookupItem;
import nonprofit.repository.SupabaseException;

/** Base class for the tabs of the control panel. Holds the helpers every tab uses. */
abstract class TabPanel extends JPanel {

    protected TabPanel() {
        super(new BorderLayout(10, 10));
        setBorder(Ui.padding());
    }

    /** Reloads the data shown by this tab. Called when the tab is opened. */
    protected void refresh() {
    }

    final void reload() {
        run(this::refresh);
    }

    protected static JPanel form(Object... parts) {
        return Ui.form(parts);
    }

    /** A table model whose cells cannot be edited. */
    protected static DefaultTableModel newModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            /** Lets numbers sort as numbers instead of text. */
            @Override
            public Class<?> getColumnClass(int column) {
                Object value = getRowCount() > 0 ? getValueAt(0, column) : null;
                return value == null ? Object.class : value.getClass();
            }
        };
    }

    /** A button that runs the action and shows any error in a dialog. */
    protected JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> run(action));
        return button;
    }

    /** A button for the main action of a screen, filled with the accent colour. */
    protected JButton primaryButton(String text, Runnable action) {
        return Ui.primary(button(text, action));
    }

    protected JPanel buttonRow(JButton... buttons) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        for (JButton button : buttons) {
            row.add(button);
        }
        return row;
    }

    protected void run(Runnable action) {
        try {
            action.run();
        } catch (SupabaseException | IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    /** The id (first column) of the selected row, or -1 after telling the user to select one. */
    protected int selectedId(JTable table, String what) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Select " + what + " first.");
            return -1;
        }
        return (int) table.getValueAt(row, 0);
    }

    protected boolean confirmDelete(String what) {
        return JOptionPane.showConfirmDialog(this, "Delete " + what + "?", "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Editable combo box filled from a lookup category. */
    protected static JComboBox<String> lookupCombo(LookupPanelController lookups, String category,
                                                   String current) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setEditable(true);
        combo.addItem("");
        for (LookupItem item : lookups.getByCategory(category)) {
            combo.addItem(item.getName());
        }
        combo.setSelectedItem(current == null ? "" : current);
        return combo;
    }

    protected static String comboText(JComboBox<String> combo) {
        Object selected = combo.getEditor().getItem();
        return selected == null ? "" : selected.toString().trim();
    }

    /** Splits "a, b, c" into a list, dropping blank entries. */
    protected static List<String> splitList(String text) {
        List<String> items = new ArrayList<String>();
        for (String part : text.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                items.add(trimmed);
            }
        }
        return items;
    }
}
