package nonprofit.model;

public class Donor extends Entity {
    private String name;
    private String email;
    private String phone;

    public Donor(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public String toString() { return "#" + id + " " + name + " <" + email + ">"; }
}
