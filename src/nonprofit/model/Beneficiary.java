package nonprofit.model;

/**
 * TODO [PHASE 1 - Person C] add a lookup table of need types (food, shelter, education...).
 * TODO [PHASE 2 - Person C] track distribution of resources + impact evaluation.
 */
public class Beneficiary extends Entity {
    private String name;
    private String contact;
    private String need;

    public Beneficiary(String name, String contact, String need) {
        this.name = name;
        this.contact = contact;
        this.need = need;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getNeed() { return need; }

    public String toString() { return "#" + id + " " + name + " needs: " + need; }
}
