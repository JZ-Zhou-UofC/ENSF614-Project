package flightapp.business.domain;

/**
 * Represents a payment method for a customer.
 */
public class PaymentMethod {

    private int id;

    private PaymentStrategy str; 
    
    public PaymentMethod(int id) {
        this.id = id; 
    }

    public void setPaymentStrategy(PaymentStrategy strategy){
        str = strategy; 
    }

    public String makePayment(){
        return str.pay(); 
    }

    // Getters no setters 
    public int getId() {
        return id;
    }

    public String getStrType() {
        if (str != null){
            return str.getType();
        }else{
            return "Error, no strategy selected!";
        }
        
    }
}

