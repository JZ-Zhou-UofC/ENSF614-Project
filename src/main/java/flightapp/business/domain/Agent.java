package flightapp.business.domain;

public class Agent extends User {

    private Customer currentCustomer;

    public Agent() {}

    public Agent(int id, String name, String email) {
        super(id, name, email);
    }

    public Agent(String name, String email) {
        super(0, name, email);
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public void clearCurrentCustomer() {
        this.currentCustomer = null;
    }

    public boolean hasCurrentCustomer() {
        return currentCustomer != null;
    }

    @Override
    public String toString() {
        return "Agent{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               (currentCustomer != null ? ", currentCustomer=" + currentCustomer.getName() : "") +
               '}';
    }
}
