package nonprofit.service;

import java.util.ArrayList;
import java.util.List;

import nonprofit.model.LookupItem;
import nonprofit.repository.DataStore;

/**
 * Lookup tables: skills, event types, donation types and need types.
 * All categories share one repository and are told apart by their category.
 */
public class LookupService {
    private DataStore db = DataStore.getInstance();

    /** Adds an entry to a category. */
    public LookupItem add(String name, String category) {
        return db.lookups.add(new LookupItem(name, category.toUpperCase()));
    }

    /** Entries of one category. */
    public List<LookupItem> getByCategory(String category) {
        List<LookupItem> result = new ArrayList<LookupItem>();
        for (LookupItem item : db.lookups.getAll()) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    public List<LookupItem> getAll() { return db.lookups.getAll(); }

    public boolean delete(int id) { return db.lookups.remove(id); }

    public LookupItem getById(int id) { return db.lookups.getById(id); }

    /** Renames an entry. */
    public boolean update(int id, String newName) {
        LookupItem item = db.lookups.getById(id);
        if (item == null) return false;
        item.setName(newName);
        return db.lookups.save(item);
    }

    /** True if the category already has an entry with this name (case-insensitive). */
    public boolean exists(String name, String category) {
        for (LookupItem item : getByCategory(category)) {
            if (item.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
