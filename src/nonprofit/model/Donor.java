package nonprofit.model;

public class Donor extends Entity {
    private String name;
    private String email;
    private String phone;
    private String notificationChannel;

    public Donor(String name, String email, String phone) {
        this(name, email, phone, "email");
    }

    public Donor(String name, String email, String phone, String notificationChannel) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.notificationChannel = notificationChannel;
    }

    public String getName()  { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getNotificationChannel() { return notificationChannel; }

    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setNotificationChannel(String notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public String toString() { return "#" + id + " " + name + " <" + email + ">"; }
}
