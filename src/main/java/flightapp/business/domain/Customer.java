package flightapp.business.domain;

public class Customer extends User {

    private String phone;

    public Customer() {}

    public Customer(int id, String firstName, String lastName, String email, String phone) {
        super(id, firstName, lastName, email);
        this.phone = phone;
    }

    public Customer(String firstName, String lastName, String email, String phone) {
        super(0, firstName, lastName, email);
        this.phone = phone;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               '}';
    }
}
