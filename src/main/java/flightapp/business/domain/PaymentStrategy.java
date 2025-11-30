package flightapp.business.domain;

public interface PaymentStrategy {
    public String pay(); 
    public String getType(); 
}
