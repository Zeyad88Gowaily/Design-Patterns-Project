package nonprofit.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nonprofit.model.Entity;

/**
 * Simple in-memory CRUD storage, one instance per entity type.
 *
 * TODO [PHASE 1 - Person B] "CRUD look up tables": reuse this class for lookup tables
 *      (skills, event types, donation types, need types) and give them an admin screen.
 * TODO [PHASE 2 - Person B] ITERATOR: add an iterator/filter (e.g. volunteers by skill).
 */
public class Repository<T extends Entity> {
    private Map<Integer, T> items = new LinkedHashMap<Integer, T>();
    private int nextId = 1;

    public T add(T item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return item;
    }

    public T getById(int id) { return items.get(id); }
    public List<T> getAll() { return new ArrayList<T>(items.values()); }
    public boolean remove(int id) { return items.remove(id) != null; }
    // Update = edit the object you got from getById (objects are held by reference).
}
