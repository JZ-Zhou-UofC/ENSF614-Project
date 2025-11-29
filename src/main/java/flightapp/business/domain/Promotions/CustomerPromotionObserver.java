package flightapp.business.domain.Promotions;
import flightapp.business.domain.Customer;

public class CustomerPromotionObserver implements PromotionObserver {

    private final Customer customer;

    public CustomerPromotionObserver(Customer customer) {
        this.customer = customer;
    }

    @Override
    public void update(String promotionMessage) {

        System.out.println("=== PROMOTION EMAIL ===");
        System.out.println("To: " + customer.getEmail());
        System.out.println("Subject: Monthly Promotion");
        System.out.println("Dear " + customer.getFirstName() + " " + customer.getLastName() + "got a promotion message");

    }

    @Override
    public String getEmail() {
        return customer.getEmail();
    }

    @Override
    public String getName() {
        return customer.getFirstName() + " " + customer.getLastName();
    }

    public Customer getCustomer() {
        return this.customer;
    }
}