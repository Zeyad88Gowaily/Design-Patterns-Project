package nonprofit.model;

/**
 * One lookup table entry. The category says which table it belongs to
 * (SKILL, EVENT_TYPE, DONATION_TYPE or NEED_TYPE).
 */
public class LookupItem extends Entity {
    private String name;
    private String category;

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
