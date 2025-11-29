package flightapp.business.controller;

import flightapp.business.domain.Customer;
import flightapp.business.domain.Promotions.CustomerPromotionObserver;
import flightapp.business.domain.Promotions.PromotionObserver;
import flightapp.data.UserDAO;
import flightapp.data.PromotionDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Unified Controller + Subject for handling promotion operations.
 * Manages subscribed customers and sends monthly promotions.
 */
public class PromotionController {
    private final PromotionDAO promotionDAO;
    private final List<PromotionObserver> observers;
    private final UserDAO userDAO;

    // ✅ Default Constructor
    public PromotionController() {
        this.observers = new ArrayList<>();
        this.userDAO = new UserDAO();
        this.promotionDAO= new PromotionDAO();
    }

   
    // ========================
    // ✅ OBSERVER MANAGEMENT
    // ========================

    /**
     * Registers a customer as an observer (subscriber).
     */
    public void subscribe(PromotionObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * Returns number of active subscribers.
     */
    public int getSubscriberCount() {
        return observers.size();
    }

    // ========================
    // ✅ DATABASE LOADING
    // ========================

    /**
     * Loads all subscribed customers from the database and registers them.
     */
    public void loadSubscribedCustomers() throws SQLException {
        List<Customer> customers = userDAO.findAllCustomers();

        for (Customer customer : customers) {
            if (customer.isSubscribed()) {
                subscribe(new CustomerPromotionObserver(customer));
            }
        }
    }

    // ========================
    // ✅ PROMOTION SENDING
    // ========================

    /**
     * Sends a monthly promotion message to all subscribed customers.
     * 
     * @return List of email addresses that received the promotion
     */
    public List<String> sendMonthlyPromotion(int agentId, String promotionMessage) throws SQLException {

        loadSubscribedCustomers();

        List<String> notifiedEmails = new ArrayList<>();

        for (PromotionObserver observer : observers) {

            try {
                // ✅ Send notification
                observer.update(promotionMessage);
                notifiedEmails.add(observer.getEmail());

                // ✅ Save to database
                int customerId = ((CustomerPromotionObserver) observer)
                        .getCustomer()
                        .getId();

                promotionDAO.createPromotion(agentId, customerId, promotionMessage);

            } catch (Exception e) {
                System.err.println("Error notifying " + observer.getEmail() + ": " + e.getMessage());
            }
        }

        return notifiedEmails;
    }

    /**
     * Returns all subscribed customers as a list.
     */
    public List<Customer> getSubscribedCustomers() throws SQLException {

        List<Customer> customers = userDAO.findAllCustomers();
        List<Customer> subscribed = new ArrayList<>();

        for (Customer customer : customers) {
            if (customer.isSubscribed()) {
                subscribed.add(customer);
            }
        }

        return subscribed;
    }

}
