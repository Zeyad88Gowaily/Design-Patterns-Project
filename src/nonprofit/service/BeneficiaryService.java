package nonprofit.service;

import java.util.List;

import nonprofit.model.Beneficiary;
import nonprofit.repository.DataStore;

/**
 * Business logic for beneficiaries.
 *
 * TODO [PHASE 2 - Person C] resource distribution tracking + impact reports.
 */
public class BeneficiaryService {
    private DataStore db = DataStore.getInstance();

    public Beneficiary add(String name, String contact, String need) {
        return db.beneficiaries.add(new Beneficiary(name, contact, need));
    }

    public List<Beneficiary> getAll() { return db.beneficiaries.getAll(); }

    public Beneficiary getById(int id) { return db.beneficiaries.getById(id); }

    public boolean update(int id, String name, String contact, String need) {
        Beneficiary b = db.beneficiaries.getById(id);
        if (b == null) return false;
        if (name != null && !name.isEmpty())    b.setName(name);
        if (contact != null && !contact.isEmpty()) b.setContact(contact);
        if (need != null && !need.isEmpty())    b.setNeed(need);
        return true;
    }

    public boolean delete(int id) { return db.beneficiaries.remove(id); }
}
