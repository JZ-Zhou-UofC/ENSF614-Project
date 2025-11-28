package flightapp.business.domain;

/**
 * Represents a payment method for a customer.
 */
public class PaymentMethod {

    private int id;
    private String type; // e.g., "CREDIT_CARD", "DEBIT_CARD", "PAYPAL", etc.
    private String cardNumber; // Should be masked in display
    private String cardholderName;
    private String expiryDate; // Format: MM/YY
    private String billingAddress;
    private String cvv; // Should be encrypted/hashed in production

    public PaymentMethod() {}

    public PaymentMethod(int id, String type, String cardNumber, String cardholderName, 
                         String expiryDate, String billingAddress, String cvv) {
        this.id = id;
        this.type = type;
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.billingAddress = billingAddress;
        this.cvv = cvv;
    }

    public PaymentMethod(String type, String cardNumber, String cardholderName, 
                         String expiryDate, String billingAddress, String cvv) {
        this(0, type, cardNumber, cardholderName, expiryDate, billingAddress, cvv);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Returns a masked version of the card number for display.
     * Example: "**** **** **** 1234"
     */
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        int length = cardNumber.length();
        String lastFour = cardNumber.substring(length - 4);
        return "**** **** **** " + lastFour;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
               "id=" + id +
               ", type='" + type + '\'' +
               ", cardNumber='" + getMaskedCardNumber() + '\'' +
               ", cardholderName='" + cardholderName + '\'' +
               ", expiryDate='" + expiryDate + '\'' +
               ", billingAddress='" + billingAddress + '\'' +
               '}';
    }
}

