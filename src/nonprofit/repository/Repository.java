package nonprofit.repository;

import java.util.List;

import nonprofit.model.Entity;

/** CRUD operations for one kind of entity. */
public interface Repository<T extends Entity> {
    T add(T item);

    T getById(int id);

    List<T> getAll();

    boolean remove(int id);

    /** Writes the changes made to an entity that was loaded earlier. */
    boolean save(T item);
}
