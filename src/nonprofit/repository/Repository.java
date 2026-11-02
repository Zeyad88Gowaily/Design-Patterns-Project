package nonprofit.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nonprofit.model.Entity;

/** In-memory CRUD implementation used as the base for Supabase-backed repositories. */
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
    public boolean save(T item) {
        if (item == null || !items.containsKey(item.getId())) return false;
        items.put(item.getId(), item);
        return true;
    }
    // Update = edit the object you got from getById (objects are held by reference).
}
