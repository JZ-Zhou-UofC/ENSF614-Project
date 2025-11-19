package flightapp.business.domain;

public class Admin extends User {

    public Admin() {}

    public Admin(int id, String name, String email) {
        super(id, name, email);
    }

    public Admin(String name, String email) {
        super(0, name, email);
    }

    @Override
    public String toString() {
        return "Admin{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
    

}
