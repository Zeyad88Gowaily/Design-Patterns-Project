package nonprofit.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import nonprofit.controller.BeneficiaryPanelController;
import nonprofit.controller.DashboardPanelController;
import nonprofit.controller.DonationPanelController;
import nonprofit.controller.EventPanelController;
import nonprofit.controller.LookupPanelController;
import nonprofit.controller.NotificationPanelController;
import nonprofit.controller.VolunteerPanelController;

/** Main Swing window: a header and one tab per module. Each tab is its own class. */
public class ControlPanelView extends JFrame {

    public ControlPanelView(DashboardPanelController dashboard,
                            DonationPanelController donations,
                            VolunteerPanelController volunteers,
                            EventPanelController events,
                            BeneficiaryPanelController beneficiaries,
                            LookupPanelController lookups,
                            NotificationPanelController notifications,
                            Runnable logoutAction) {
        setTitle("MissionOS - Non-Profit Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new java.awt.Dimension(900, 560));
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Dashboard", new DashboardPanel(dashboard));
        tabs.addTab("Donations", new DonationsPanel(donations));
        tabs.addTab("Volunteers", new VolunteersPanel(volunteers));
        tabs.addTab("Events", new EventsPanel(events, lookups));
        tabs.addTab("Beneficiaries", new BeneficiariesPanel(beneficiaries, lookups));
        tabs.addTab("Notifications", new NotificationsPanel(notifications));
        tabs.addTab("Lookup Tables", new LookupPanel(lookups));
        // Show current data every time a tab is opened.
        tabs.addChangeListener(e -> {
            Component selected = tabs.getSelectedComponent();
            if (selected instanceof TabPanel) {
                ((TabPanel) selected).reload();
            }
        });

        JPanel content = new JPanel(new BorderLayout());
        content.add(header(logoutAction), BorderLayout.NORTH);
        content.add(tabs, BorderLayout.CENTER);
        add(content);
    }

    private JPanel header(Runnable logoutAction) {
        JLabel title = new JLabel("MissionOS");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(javax.swing.UIManager.getColor("Component.accentColor"));
        JLabel subtitle = new JLabel("Non-profit management");

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        left.add(title);
        left.add(subtitle);

        JButton theme = new JButton("Dark mode");
        theme.addActionListener(e -> {
            Theme.toggle();
            theme.setText(Theme.isDark() ? "Light mode" : "Dark mode");
        });
        JButton signOut = new JButton("Sign out");
        signOut.addActionListener(e -> {
            setVisible(false);
            dispose();
            logoutAction.run();
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(theme);
        right.add(signOut);

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 8, 16));
        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }
}
