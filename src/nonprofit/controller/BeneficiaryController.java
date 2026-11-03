package nonprofit.controller;

import nonprofit.model.Beneficiary;
import nonprofit.service.BeneficiaryService;
import nonprofit.view.ConsoleView;

/** Console menu for beneficiaries. */
public class BeneficiaryController {
    private BeneficiaryService service;
    private ConsoleView view;

    public BeneficiaryController(BeneficiaryService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void menu() {
        while (true) {
            view.show("\n== Beneficiaries ==");
            view.show("1) Add  2) List  3) Edit  4) Delete  0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;

            switch (choice) {
                case 1: doAdd(); break;
                case 2: view.showList("Beneficiaries", service.getAll()); break;
                case 3: doEdit(); break;
                case 4: doDelete(); break;
                default: view.show("Invalid choice.");
            }
        }
    }

    private void doAdd() {
        view.show("Added " + service.add(view.ask("Name"), view.ask("Contact"), view.ask("Need")));
    }

    private void doEdit() {
        view.showList("Beneficiaries", service.getAll());
        int id = view.askInt("Beneficiary id to edit");
        Beneficiary b = service.getById(id);
        if (b == null) { view.show("Not found."); return; }
        view.show("Current: " + b);
        String name    = view.ask("New name (blank to keep)");
        String contact = view.ask("New contact (blank to keep)");
        String need    = view.ask("New need (blank to keep)");
        service.update(id, name, contact, need);
        view.show("Updated: " + service.getById(id));
    }

    private void doDelete() {
        view.showList("Beneficiaries", service.getAll());
        int id = view.askInt("Beneficiary id to delete");
        view.show(service.delete(id) ? "Deleted." : "Not found.");
    }
}
