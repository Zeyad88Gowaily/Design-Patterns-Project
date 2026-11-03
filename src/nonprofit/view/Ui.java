package nonprofit.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/** Small Swing helpers shared by the windows, so every screen looks the same. */
final class Ui {
    private Ui() {
    }

    /** A sortable table with comfortable rows. */
    static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(String.format("%,.2f", (Double) value));
                setHorizontalAlignment(RIGHT);
            }
        });
        if (table.getColumnCount() > 0 && "ID".equals(model.getColumnName(0))) {
            table.getColumnModel().getColumn(0).setMaxWidth(70);
        }
        return table;
    }

    /** The table in a scroll pane with a search box above it that filters every column. */
    static JPanel searchable(JTable table) {
        JTextField search = new JTextField();
        search.putClientProperty("JTextField.placeholderText", "Search...");
        search.putClientProperty("JTextField.showClearButton", true);
        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { apply(); }
            public void removeUpdate(DocumentEvent e) { apply(); }
            public void changedUpdate(DocumentEvent e) { apply(); }

            @SuppressWarnings("unchecked")
            private void apply() {
                TableRowSorter<DefaultTableModel> sorter =
                        (TableRowSorter<DefaultTableModel>) table.getRowSorter();
                String text = search.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null
                        : RowFilter.<DefaultTableModel, Integer>regexFilter(
                                "(?i)" + Pattern.quote(text)));
            }
        });
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(search, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    /**
     * A two-column form. A String is the label of the component that follows it;
     * a component without a label spans both columns.
     */
    static JPanel form(Object... parts) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 4, 5, 4);
        c.fill = GridBagConstraints.HORIZONTAL;
        String label = null;
        int row = 0;
        for (Object part : parts) {
            if (part instanceof String) {
                label = (String) part;
                continue;
            }
            c.gridy = row++;
            if (label != null) {
                c.gridx = 0;
                c.gridwidth = 1;
                c.weightx = 0;
                panel.add(new JLabel(label), c);
                c.gridx = 1;
                c.weightx = 1;
                panel.add((Component) part, c);
                label = null;
            } else {
                c.gridx = 0;
                c.gridwidth = 2;
                c.weightx = 1;
                panel.add((Component) part, c);
            }
        }
        Dimension size = panel.getPreferredSize();
        panel.setPreferredSize(new Dimension(Math.max(size.width, 380), size.height));
        return panel;
    }

    /** Fills the button with the accent colour to mark the main action. */
    static JButton primary(JButton button) {
        button.putClientProperty("FlatLaf.style",
                "background: @accentColor; foreground: #ffffff; focusedBackground: lighten(@accentColor, 8%)");
        return button;
    }

    /** Empty border used around the content of every tab. */
    static javax.swing.border.Border padding() {
        return BorderFactory.createEmptyBorder(14, 14, 14, 14);
    }
}
