package nonprofit.view;

import java.io.Console;
import java.util.Arrays;

import nonprofit.repository.SupabaseException;
import nonprofit.service.SupabaseAuthService;

public final class ConsoleAuthenticationDialog {
    private ConsoleAuthenticationDialog() {
    }

    public static SupabaseAuthService.AuthSession show(SupabaseAuthService authService) {
        Console console = System.console();
        if (console == null) {
            System.err.println("Console authentication requires a terminal. Run with --gui in a desktop session.");
            return null;
        }

        while (true) {
            String choice = console.readLine("%n1) Sign in  2) Create account  0) Exit%nChoice: ");
            if (choice == null || "0".equals(choice.trim())) {
                return null;
            }
            boolean signup = "2".equals(choice.trim());
            if (!signup && !"1".equals(choice.trim())) {
                console.printf("Choose 1, 2, or 0.%n");
                continue;
            }

            String email = console.readLine("Email: ");
            char[] password = console.readPassword("Password: ");
            char[] confirmation = null;
            try {
                if (password == null) {
                    return null;
                }
                if (email == null || email.trim().isEmpty() || password.length == 0) {
                    console.printf("Email and password are required.%n");
                    continue;
                }
                if (signup) {
                    confirmation = console.readPassword("Confirm password: ");
                    if (!Arrays.equals(password, confirmation)) {
                        console.printf("Passwords do not match.%n");
                        continue;
                    }
                    SupabaseAuthService.AuthSession session =
                            authService.signUp(email.trim(), new String(password));
                    if (session != null) {
                        return session;
                    }
                    console.printf("Account created. Confirm your email, then sign in.%n");
                } else {
                    return authService.signIn(email.trim(), new String(password));
                }
            } catch (SupabaseException ex) {
                console.printf("Authentication failed: %s%n", ex.getMessage());
            } finally {
                if (password != null) {
                    Arrays.fill(password, '\0');
                }
                if (confirmation != null) {
                    Arrays.fill(confirmation, '\0');
                }
            }
        }
    }
}
