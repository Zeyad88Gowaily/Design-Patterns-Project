package nonprofit.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import nonprofit.controller.NotificationPanelController;

/** Notifications tab: send a message to an audience through a channel chosen at runtime. */
class NotificationsPanel extends TabPanel {
    private final NotificationPanelController controller;
    private final JComboBox<String> audience = new JComboBox<>(NotificationPanelController.AUDIENCES);
    private final JComboBox<String> channel = new JComboBox<>(NotificationPanelController.CHANNELS);
    private final JTextField recipient = new JTextField();
    private final JTextArea message = new JTextArea(4, 40);
    private final JTextArea log = new JTextArea();

    NotificationsPanel(NotificationPanelController controller) {
        this.controller = controller;
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        log.setEditable(false);
        log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JPanel form = form(
                "Send to:", audience,
                "Custom recipient (email / phone / handle):", recipient,
                "Channel (strategy):", channel,
                "Message:", new JScrollPane(message));

        JButton send = primaryButton("Send", this::send);
        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.add(form, BorderLayout.CENTER);
        top.add(buttonRow(send), BorderLayout.SOUTH);

        JScrollPane logPane = new JScrollPane(log);
        logPane.setBorder(BorderFactory.createTitledBorder("Sent messages"));
        add(top, BorderLayout.NORTH);
        add(logPane, BorderLayout.CENTER);
    }

    private void send() {
        List<String> sent = controller.send((String) audience.getSelectedItem(),
                recipient.getText().trim(), (String) channel.getSelectedItem(), message.getText());
        for (String line : sent) {
            log.append(line + "\n");
        }
    }
}
