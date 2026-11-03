package nonprofit.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import nonprofit.controller.DashboardPanelController;
import nonprofit.model.Donation;
import nonprofit.model.Event;

/** Dashboard tab: summary cards, latest donations and events. */
class DashboardPanel extends TabPanel {
    private final DashboardPanelController controller;
    private final JPanel cards = new JPanel(new GridLayout(1, 0, 12, 0));
    private final DefaultTableModel donationModel = newModel("Type", "Amount", "Donor", "Date");
    private final DefaultTableModel eventModel = newModel("Name", "Date", "Location", "Status");

    DashboardPanel(DashboardPanelController controller) {
        this.controller = controller;

        JPanel lists = new JPanel(new GridLayout(1, 2, 12, 0));
        lists.add(titled("Latest donations", Ui.table(donationModel)));
        lists.add(titled("Events", Ui.table(eventModel)));

        add(cards, BorderLayout.NORTH);
        add(lists, BorderLayout.CENTER);
        refresh();
    }

    private static JPanel titled(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static java.awt.Color borderColor() {
        java.awt.Color color = javax.swing.UIManager.getColor("Component.borderColor");
        return color != null ? color : java.awt.Color.GRAY;
    }

    private static JPanel card(String label, String value) {
        JLabel number = new JLabel(value);
        number.setFont(number.getFont().deriveFont(Font.BOLD, 26f));
        JLabel caption = new JLabel(label);

        JPanel card = new JPanel(new GridLayout(2, 1, 0, 2));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor(), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        card.add(number);
        card.add(caption);
        return card;
    }

    @Override
    protected void refresh() {
        cards.removeAll();
        for (Map.Entry<String, String> stat : controller.getStats().entrySet()) {
            cards.add(card(stat.getKey(), stat.getValue()));
        }
        cards.revalidate();
        cards.repaint();

        donationModel.setRowCount(0);
        for (Donation d : controller.getRecentDonations(8)) {
            donationModel.addRow(new Object[]{d.getType(), d.getAmount(), d.getDonor().getName(), d.getDate()});
        }
        eventModel.setRowCount(0);
        for (Event e : controller.getEvents()) {
            eventModel.addRow(new Object[]{e.getName(), e.getDate(), e.getLocation(), e.getStatus()});
        }
    }
}
