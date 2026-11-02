package nonprofit.model;

/**
 * A simple lookup-table entry (e.g. a skill name, event type, donation type, need type).
 * Reuses the Repository<T extends Entity> infrastructure for full CRUD.
 *
 * CRUD LOOKUP TABLES: one generic entity covers all four lookup categories;
 * the category field tells us which table the item belongs to.
 */
public class LookupItem extends Entity {
    private String name;
    private String category;   // "SKILL", "EVENT_TYPE", "DONATION_TYPE", "NEED_TYPE"

    public LookupItem(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName()     { return name; }
    public String getCategory() { return category; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return "#" + id + " " + name + " [" + category + "]"; }
}
