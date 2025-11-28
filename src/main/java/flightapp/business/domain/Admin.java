package flightapp.business.domain;

public class Admin extends User {

    public Admin() {}

    public Admin(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    public Admin(String firstName, String lastName, String email) {
        super(0, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return "Admin{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
    

}
