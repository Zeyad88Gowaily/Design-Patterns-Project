package nonprofit.model;

/** A person or group that receives help from the organisation. */
public class Beneficiary extends Entity {
    private String name;
    private String contact;
    private String need;

    public Beneficiary(String name, String contact, String need) {
        this.name = name;
        this.contact = contact;
        this.need = need;
    }

    public String getName()    { return name; }
    public String getContact() { return contact; }
    public String getNeed()    { return need; }

    public void setName(String name)       { this.name = name; }
    public void setContact(String contact) { this.contact = contact; }
    public void setNeed(String need)       { this.need = need; }

    public String toString() { return "#" + id + " " + name + " needs: " + need; }
}
