package nonprofit.model;

/** Base class for everything stored in a repository (gives it an id). */
public abstract class Entity {
    protected int id;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
