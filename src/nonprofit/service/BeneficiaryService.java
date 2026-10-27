package nonprofit.service;

import java.util.List;

import nonprofit.model.Beneficiary;
import nonprofit.repository.DataStore;

/**
 * TODO [PHASE 2 - Person C] resource distribution tracking + impact reports.
 */
public class BeneficiaryService {
    private DataStore db = DataStore.getInstance();

    public Beneficiary add(String name, String contact, String need) {
        return db.beneficiaries.add(new Beneficiary(name, contact, need));
    }

    public List<Beneficiary> getAll() { return db.beneficiaries.getAll(); }
}
