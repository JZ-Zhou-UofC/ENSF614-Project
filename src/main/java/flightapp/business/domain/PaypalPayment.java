package flightapp.business.domain;

public class PaypalPayment implements PaymentStrategy{
    public String pay(){
        return "Paid with paypal";
    }
}
