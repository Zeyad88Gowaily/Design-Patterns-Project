package nonprofit.controller;

import nonprofit.service.BeneficiaryService;
import nonprofit.view.ConsoleView;

/** MVC - Controller for beneficiaries (Person C). */
public class BeneficiaryController {
    private BeneficiaryService service;
    private ConsoleView view;

    public BeneficiaryController(BeneficiaryService service, ConsoleView view) {
        this.service = service;
        this.view = view;
    }

    public void menu() {
        while (true) {
            view.show("\n== Beneficiaries ==\n1) Add beneficiary  2) List beneficiaries  0) Back");
            int choice = view.askInt("Choice");
            if (choice == 0) return;
            if (choice == 1) {
                view.show("Added " + service.add(view.ask("Name"), view.ask("Contact"), view.ask("Need")));
            } else if (choice == 2) {
                view.showList("Beneficiaries", service.getAll());
            }
            // TODO [PHASE 1 - Person C] edit/delete beneficiary
        }
    }
}
