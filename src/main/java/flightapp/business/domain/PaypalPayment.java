package flightapp.business.domain;

public class PaypalPayment implements PaymentStrategy{
    private String credential; 
    private String type; 

    public PaypalPayment(String credential, String type){
        this.credential = credential; 
        this.type = type; 
    }

    public String pay(){
        return "Paid with paypal account: "+this.credential;
    }
    public String getType(){
        return this.type; 
    }
}
