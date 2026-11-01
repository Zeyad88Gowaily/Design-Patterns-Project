package nonprofit.service;

import java.util.ArrayList;
import java.util.List;

import nonprofit.model.LookupItem;
import nonprofit.repository.DataStore;

/**
 * CRUD LOOKUP TABLES: provides add / list / delete for any lookup category
 * (skills, event types, donation types, need types).
 * All categories share the same repository — filtered by the category field.
 */
public class LookupService {
    private DataStore db = DataStore.getInstance();

    /** Add a new entry to the given category. */
    public LookupItem add(String name, String category) {
        return db.lookups.add(new LookupItem(name, category.toUpperCase()));
    }

    /** List all entries in a category. */
    public List<LookupItem> getByCategory(String category) {
        List<LookupItem> result = new ArrayList<LookupItem>();
        for (LookupItem item : db.lookups.getAll()) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                result.add(item);
            }
        }
        return result;
    }

    /** Get all entries regardless of category. */
    public List<LookupItem> getAll() { return db.lookups.getAll(); }

    /** Delete a lookup entry by id. */
    public boolean delete(int id) { return db.lookups.remove(id); }

    /** Get a single entry by id. */
    public LookupItem getById(int id) { return db.lookups.getById(id); }

    /** Update the name of a lookup entry. */
    public boolean update(int id, String newName) {
        LookupItem item = db.lookups.getById(id);
        if (item == null) return false;
        item.setName(newName);
        return true;
    }

    /** Check whether a value exists in a category (e.g. is "First Aid" a valid skill?). */
    public boolean exists(String name, String category) {
        for (LookupItem item : getByCategory(category)) {
            if (item.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
