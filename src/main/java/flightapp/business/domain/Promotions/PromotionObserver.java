package flightapp.business.domain.Promotions;

/**
 * Observer interface for customers who want to receive monthly promotions.
 */
public interface PromotionObserver {
    
    /**
     * Called when a promotion message is sent.
     * @param promotionMessage The promotion message content
     */
    void update(String promotionMessage);
    
    /**
     * Returns the email address of the observer for sending promotions.
     * @return The email address
     */
    String getEmail();
    
    /**
     * Returns the name of the observer for personalization.
     * @return The full name
     */
    String getName();
}

