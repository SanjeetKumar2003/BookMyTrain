public class User {
    private int id;
    private String username;
    private String password;
    private String fullname;
    private String contact;

    public User() {}

    public User(int id, String username, String password, String fullname, String contact) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.contact = contact;
    }

    public User(String username, String password, String fullname, String contact) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.contact = contact;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullname() { return fullname; }
    public String getContact() { return contact; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public void setContact(String contact) { this.contact = contact; }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + ", fullname='" + fullname + '\'' + ", contact='" + contact + '\'' + '}';
    }
}
