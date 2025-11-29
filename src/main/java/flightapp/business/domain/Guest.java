package flightapp.business.domain;

public class Guest extends User {

    public Guest() {}

    public Guest(String firstName, String lastName, String email) {
        super(0, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return "Guest{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
