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

    public void makePayment(Reservation res, String paymentMethod, User customer)throws SQLException{
        try{
            ArrayList<PaymentMethod> potentialOptions = getPaymentMethods(customer); 
            for (int i = 0; i < potentialOptions.size();i++){
                if(paymentMethod.equals(potentialOptions.get(i).getStrType())){
                    makePayment(res,potentialOptions.get(i)); 
                }
            }  
        }catch(SQLException e){
            System.out.println("SQL Error! Could not access payment table in database");
        }
    }

    public ArrayList<PaymentMethod> getPaymentMethods(User customer) throws SQLException{
        // Get available payment methods from DAO 
        return paymentDAO.retrievePaymentMethod(customer); 
        // Put in string list 
    }

    public void savePaymentMethod(User customer,String paymentMethod) throws SQLException{
        paymentDAO.savePaymentMethod(customer,paymentMethod); 
    }

}
