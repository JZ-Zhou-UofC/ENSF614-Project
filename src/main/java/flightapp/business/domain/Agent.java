package flightapp.business.domain;

public class Agent extends User {

    public Agent() {}

    public Agent(int id, String name, String email) {
        super(id, name, email);
    }

    public Agent(String name, String email) {
        super(0, name, email);
    }

    @Override
    public String toString() {
        return "Agent{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
