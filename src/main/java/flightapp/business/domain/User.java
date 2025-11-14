package flightapp.business.domain;

/**
 * Base type for all users in the system.
 */
public abstract class User {

    protected int id;
    protected String name;
    protected String email;

    protected User() {}

    protected User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public boolean isCustomer() { return this instanceof Customer; }
    public boolean isAgent()    { return this instanceof Agent; }
    public boolean isAdmin()    { return this instanceof Admin; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
