package flightapp.business.domain;

public class Guest extends User {

    public Guest() {}

    public Guest(String name, String email) {
        super(0, name, email);
    }

    @Override
    public String toString() {
        return "Guest{" +
               "name='" + name + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
