package nonprofit.model;

/** Base class for everything stored in the database. Holds the id. */
public abstract class Entity {
    protected int id;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
