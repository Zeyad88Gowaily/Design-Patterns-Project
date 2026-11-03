package nonprofit.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nonprofit.repository.SupabaseException;
import nonprofit.service.SupabaseAuthService;

/** Sign-in and create-account window. Returns the session once the user is signed in. */
public final class AuthenticationDialog {
    private AuthenticationDialog() {
    }

    /** Shows the window and blocks until the user signs in or closes it (then returns null). */
    public static SupabaseAuthService.AuthSession show(SupabaseAuthService authService) {
        final SupabaseAuthService.AuthSession[] result = new SupabaseAuthService.AuthSession[1];
        final boolean[] signup = {false};

        JDialog dialog = new JDialog((java.awt.Frame) null, "MissionOS", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("MissionOS");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setForeground(javax.swing.UIManager.getColor("Component.accentColor"));
        JLabel heading = new JLabel("Sign in to continue");

        JTextField email = new JTextField();
        email.putClientProperty("JTextField.placeholderText", "name@example.com");
        JPasswordField password = new JPasswordField();
        password.putClientProperty("JTextField.showRevealButton", true);
        JPasswordField confirmation = new JPasswordField();
        confirmation.putClientProperty("JTextField.showRevealButton", true);
        JLabel confirmLabel = new JLabel("Confirm password");
        JLabel message = new JLabel(" ");
        message.setForeground(new Color(0xC62828));

        JButton submit = Ui.primary(new JButton("Sign in"));
        JButton switchMode = new JButton("Create an account");
        switchMode.putClientProperty("JButton.buttonType", "borderless");
        JButton exit = new JButton("Exit");

        JPanel form = new JPanel();
        form.setLayout(new javax.swing.BoxLayout(form, javax.swing.BoxLayout.Y_AXIS));
        addField(form, new JLabel("Email"), email);
        addField(form, new JLabel("Password"), password);
        addField(form, confirmLabel, confirmation);
        confirmLabel.setVisible(false);
        confirmation.setVisible(false);

        Runnable refreshMode = () -> {
            heading.setText(signup[0] ? "Create your account" : "Sign in to continue");
            submit.setText(signup[0] ? "Create account" : "Sign in");
            switchMode.setText(signup[0] ? "I already have an account" : "Create an account");
            confirmLabel.setVisible(signup[0]);
            confirmation.setVisible(signup[0]);
            message.setText(" ");
            dialog.pack();
        };
        switchMode.addActionListener(e -> {
            signup[0] = !signup[0];
            refreshMode.run();
        });
        exit.addActionListener(e -> dialog.dispose());
        submit.addActionListener(e -> {
            String emailValue = email.getText().trim();
            char[] passwordValue = password.getPassword();
            char[] confirmValue = confirmation.getPassword();
            try {
                if (emailValue.isEmpty() || passwordValue.length == 0) {
                    message.setText("Email and password are required.");
                } else if (signup[0] && !Arrays.equals(passwordValue, confirmValue)) {
                    message.setText("Passwords do not match.");
                } else if (signup[0]) {
                    SupabaseAuthService.AuthSession session =
                            authService.signUp(emailValue, new String(passwordValue));
                    if (session != null) {
                        result[0] = session;
                        dialog.dispose();
                    } else {
                        signup[0] = false;
                        refreshMode.run();
                        message.setText("Account created. Confirm your email, then sign in.");
                    }
                } else {
                    result[0] = authService.signIn(emailValue, new String(passwordValue));
                    dialog.dispose();
                }
            } catch (SupabaseException ex) {
                message.setText("<html>" + ex.getMessage().replace("<", "&lt;") + "</html>");
                dialog.pack();
            } finally {
                Arrays.fill(passwordValue, '\0');
                Arrays.fill(confirmValue, '\0');
            }
        });
        dialog.getRootPane().setDefaultButton(submit);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(exit);
        buttons.add(submit);
        JPanel south = new JPanel(new BorderLayout(0, 8));
        south.add(message, BorderLayout.NORTH);
        south.add(switchMode, BorderLayout.WEST);
        south.add(buttons, BorderLayout.EAST);

        JPanel head = new JPanel(new java.awt.GridLayout(0, 1));
        head.add(title);
        head.add(heading);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        content.add(head, BorderLayout.NORTH);
        content.add(form, BorderLayout.CENTER);
        content.add(south, BorderLayout.SOUTH);
        content.setPreferredSize(new Dimension(420, content.getPreferredSize().height));

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return result[0];
    }

    private static void addField(JPanel form, JLabel label, java.awt.Component field) {
        label.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        ((javax.swing.JComponent) field).setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        form.add(label);
        form.add(javax.swing.Box.createVerticalStrut(4));
        form.add(field);
        form.add(javax.swing.Box.createVerticalStrut(10));
    }
}
