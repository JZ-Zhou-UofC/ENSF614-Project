package flightapp.business.domain;

public class Customer extends User {

    private String phone;

    public Customer() {}

    public Customer(int id, String name, String email, String phone) {
        super(id, name, email);
        this.phone = phone;
    }

    public Customer(String name, String email, String phone) {
        super(0, name, email);
        this.phone = phone;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               '}';
    }
}
