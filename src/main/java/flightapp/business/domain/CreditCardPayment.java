package flightapp.business.domain;

public class CreditCardPayment implements PaymentStrategy{
    private String CreditCardNumber; 
    private String type; 
    public CreditCardPayment(String CreditCardNumber, String type){
        this.CreditCardNumber = CreditCardNumber; 
        this.type = type; 
    }

    public String pay(){
        return "Paid with credit card ending in: "+this.CreditCardNumber; 
    }

    public String getType(){
        return this.type; 
    }
}
