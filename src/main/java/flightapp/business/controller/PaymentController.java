package flightapp.business.controller;

import flightapp.business.domain.*;
import flightapp.data.PaymentDAO; 
import java.util.ArrayList;
import java.sql.SQLException;

public class PaymentController {
    private final PaymentDAO paymentDAO; 

    public PaymentController(){
        this.paymentDAO = new PaymentDAO(); 
    }

    public void makePayment(Reservation res, PaymentMethod pm)throws SQLException{
        pm.makePayment(); 
        // Save payment in DB 
        paymentDAO.savePayment(res, pm); 
    }

    public ArrayList<PaymentMethod> getPaymentMethods(User customer) throws SQLException{
        // Get available payment methods from DAO 
        return paymentDAO.retrievePaymentMethod(customer); 
        // Put in string list 
    }

}
