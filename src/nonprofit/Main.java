package nonprofit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import nonprofit.communication.DonationReceiptObserver;
import nonprofit.communication.EventUpdateObserver;
import nonprofit.controller.*;
import nonprofit.repository.DataStore;
import nonprofit.repository.SupabaseConfig;
import nonprofit.repository.SupabaseException;
import nonprofit.repository.SupabaseRestClient;
import nonprofit.service.*;
import nonprofit.view.AuthenticationDialog;
import nonprofit.view.ConsoleAuthenticationDialog;
import nonprofit.view.ConsoleView;
import nonprofit.view.ControlPanelView;

public class Main {
    public static void main(String[] args) {
        boolean headless = java.awt.GraphicsEnvironment.isHeadless();
        final SupabaseConfig config;
        final SupabaseRestClient client;
        try {
            config = SupabaseConfig.fromEnvironment();
            client = new SupabaseRestClient(config.getUrl(), config.getApiKey());
        } catch (IllegalStateException | IllegalArgumentException ex) {
            showStartupError("MissionOS configuration", ex.getMessage(), headless);
            return;
        }

        SupabaseAuthService authService = new SupabaseAuthService(client);
        boolean guiMode = args.length > 0 && args[0].equalsIgnoreCase("--gui");
        if (headless) {
            if (guiMode) {
                System.err.println("The Swing interface requires a graphical desktop.");
                return;
            }
            SupabaseAuthService.AuthSession session = ConsoleAuthenticationDialog.show(authService);
            if (session != null) {
                showAuthenticatedApplication(authService, client, session, false);
            }
            return;
        }
        startAuthenticatedSession(authService, client, guiMode);
    }

    private static void showStartupError(String title, String message, boolean headless) {
        if (headless) {
            System.err.println(title + ": " + message);
        } else {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void startAuthenticatedSession(SupabaseAuthService authService,
                                                  SupabaseRestClient client, boolean guiMode) {
        SupabaseAuthService.AuthSession session = AuthenticationDialog.show(authService);
        if (session == null) {
            return;
        }
        showAuthenticatedApplication(authService, client, session, guiMode);
    }

    private static void showAuthenticatedApplication(SupabaseAuthService authService,
                                                     SupabaseRestClient client,
                                                     SupabaseAuthService.AuthSession session,
                                                     boolean guiMode) {
        client.setSession(session.getAccessToken(), session.getRefreshToken());
        DataStore.configure(client);

        DonationService donationService = new DonationService();
        VolunteerService volunteerService = new VolunteerService();
        EventService eventService = new EventService();
        BeneficiaryService beneficiaryService = new BeneficiaryService();
        LookupService lookupService = new LookupService();
        donationService.attach(new DonationReceiptObserver());
        eventService.attach(new EventUpdateObserver());

        if (guiMode) {
            try {
                ControlPanelView gui = new ControlPanelView(
                        new DonationPanelController(donationService),
                        new VolunteerPanelController(volunteerService),
                        new EventPanelController(eventService),
                        new BeneficiaryPanelController(beneficiaryService),
                        new LookupPanelController(lookupService), () -> {
                            try {
                                authService.signOut();
                            } catch (SupabaseException ex) {
                                JOptionPane.showMessageDialog(null, ex.getMessage(), "Sign out failed",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            SwingUtilities.invokeLater(
                                    () -> startAuthenticatedSession(authService, client, true));
                        });
                gui.setVisible(true);
            } catch (SupabaseException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Could not load application data",
                        JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        ConsoleView view = new ConsoleView();
        MainController app = new MainController(view,
                new DonationController(donationService, view),
                new VolunteerController(volunteerService, view),
                new EventController(eventService, view),
                new BeneficiaryController(beneficiaryService, view),
                new LookupController(lookupService, view));
        app.run();
    }
}
