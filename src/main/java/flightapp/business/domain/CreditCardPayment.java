package flightapp.business.domain;

public class CreditCardPayment implements PaymentStrategy{
    public String pay(){
        return "Paid with credit card"; 
    }
}
