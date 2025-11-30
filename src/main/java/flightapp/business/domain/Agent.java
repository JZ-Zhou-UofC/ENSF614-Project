package flightapp.business.domain;

import flightapp.business.domain.*;
import flightapp.data.UserDAO;

public class Agent extends User {
	
    public Agent() {}

    public Agent(int id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);
    }

    public Agent(String firstName, String lastName, String email) {
        super(0, firstName, lastName, email);
    }

    @Override
    public String toString() {
        return "Agent{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
