package nonprofit.controller;

import java.util.List;

import nonprofit.model.Beneficiary;
import nonprofit.service.BeneficiaryService;

public class BeneficiaryPanelController {
    private final BeneficiaryService service;

    public BeneficiaryPanelController(BeneficiaryService service) {
        this.service = service;
    }

    public Beneficiary add(String name, String contact, String need) {
        return service.add(name, contact, need);
    }

    public List<Beneficiary> getAll() {
        return service.getAll();
    }
}
