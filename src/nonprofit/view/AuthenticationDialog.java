package nonprofit.view;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;

import nonprofit.repository.SupabaseException;
import nonprofit.service.SupabaseAuthService;

public final class AuthenticationDialog {
    private AuthenticationDialog() {
    }

    public static SupabaseAuthService.AuthSession show(SupabaseAuthService authService) {
        while (true) {
            Object[] choices = {"Sign in", "Create account", "Exit"};
            int choice = JOptionPane.showOptionDialog(null, "Sign in or create a MissionOS account.",
                    "MissionOS authentication", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
            if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
                return null;
            }

            boolean signup = choice == 1;
            JPanel fields = createFields(signup);
            int result = JOptionPane.showConfirmDialog(null, fields,
                    signup ? "Create account" : "Sign in", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                continue;
            }

            JTextField email = (JTextField) fields.getClientProperty("email");
            JPasswordField password = (JPasswordField) fields.getClientProperty("password");
            String emailValue = email.getText().trim();
            char[] passwordValue = password.getPassword();
            try {
                if (emailValue.isEmpty() || passwordValue.length == 0) {
                    showError("Email and password are required.");
                    continue;
                }
                if (signup) {
                    JPasswordField confirmation =
                            (JPasswordField) fields.getClientProperty("confirmation");
                    char[] confirmationValue = confirmation.getPassword();
                    try {
                        if (!Arrays.equals(passwordValue, confirmationValue)) {
                            showError("Passwords do not match.");
                            continue;
                        }
                        SupabaseAuthService.AuthSession session =
                                authService.signUp(emailValue, new String(passwordValue));
                        if (session != null) {
                            return session;
                        }
                        JOptionPane.showMessageDialog(null,
                                "Account created. Confirm your email, then sign in.",
                                "Email confirmation required", JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        Arrays.fill(confirmationValue, '\0');
                    }
                } else {
                    return authService.signIn(emailValue, new String(passwordValue));
                }
            } catch (SupabaseException ex) {
                showError(ex.getMessage());
            } finally {
                Arrays.fill(passwordValue, '\0');
                password.setText("");
            }
        }
    }

    private static JPanel createFields(boolean signup) {
        JPanel panel = new JPanel(new GridLayout(signup ? 3 : 2, 2, 8, 8));
        JTextField email = new JTextField();
        JPasswordField password = new JPasswordField();
        panel.add(new JLabel("Email:"));
        panel.add(email);
        panel.add(new JLabel("Password:"));
        panel.add(password);
        panel.putClientProperty("email", email);
        panel.putClientProperty("password", password);
        if (signup) {
            JPasswordField confirmation = new JPasswordField();
            panel.add(new JLabel("Confirm password:"));
            panel.add(confirmation);
            panel.putClientProperty("confirmation", confirmation);
        }
        return panel;
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog((Component) null, message,
                "Authentication failed", JOptionPane.ERROR_MESSAGE);
    }
}
