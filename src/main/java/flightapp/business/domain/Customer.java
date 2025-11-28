package flightapp.business.domain;

public class Customer extends User {

    private String phone;
    private boolean subscribed;
    private PaymentMethod paymentMethod;

    public Customer() {}

    public Customer(int id, String firstName, String lastName, String email, String phone) {
        super(id, firstName, lastName, email);
        this.phone = phone;
        this.subscribed = false;
    }

    public Customer(int id, String firstName, String lastName, String email, String phone, boolean subscribed) {
        super(id, firstName, lastName, email);
        this.phone = phone;
        this.subscribed = subscribed;
    }

    public Customer(String firstName, String lastName, String email, String phone) {
        super(0, firstName, lastName, email);
        this.phone = phone;
        this.subscribed = false;
    }

    public Customer(String firstName, String lastName, String email, String phone, boolean subscribed) {
        super(0, firstName, lastName, email);
        this.phone = phone;
        this.subscribed = subscribed;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public boolean isSubscribed() { return subscribed; }

    public void setSubscribed(boolean subscribed) { this.subscribed = subscribed; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }

    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    @Override
    public String toString() {
        return "Customer{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", subscribed=" + subscribed +
               ", paymentMethod=" + (paymentMethod != null ? paymentMethod.toString() : "null") +
               '}';
    }
}
