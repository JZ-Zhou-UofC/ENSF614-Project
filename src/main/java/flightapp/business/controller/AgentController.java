package flightapp.business.controller;

import flightapp.business.domain.*;
import flightapp.data.*;

import java.sql.SQLException;

import flightapp.business.controller.*;
import flightapp.data.*;
import flightapp.util.SystemLogger;
import flightapp.business.domain.User;

public class AgentController extends Agent{
	
    private final UserDAO userDAO;
    
    public AgentController(){
    	userDAO = new UserDAO();
    }
    
    
    public Customer updateCustomer(Agent agent, Customer customer) throws SQLException {
        if (agent == null) {
            throw new IllegalArgumentException("Agent cannot be null");
        }
        
        if (!(agent instanceof Agent)) {
            throw new IllegalArgumentException("Only agents can update customer information");
        }
        
        SystemLogger.logUserAction(
            SystemLogger.SystemStatus.INFO,
            "AGENT",
            "UPDATE_CUSTOMER_ATTEMPT",
            String.format("Agent ID %d attempting to update customer ID %d", agent.getId(), customer.getId())
        );
        
        try {
            // Validate that customer exists and is actually a customer
            User existingUser = userDAO.findById(customer.getId());
            if (existingUser == null || !(existingUser instanceof Customer)) {
                SystemLogger.logUserAction(
                    SystemLogger.SystemStatus.WARN,
                    "AGENT",
                    "UPDATE_CUSTOMER_FAILED",
                    String.format("Customer ID %d not found", customer.getId())
                );
                throw new SQLException("Customer not found");
            }
            
            // Check if email is being changed and if new email already exists
            if (!existingUser.getEmail().equals(customer.getEmail())) {
                User emailCheck = userDAO.findByEmail(customer.getEmail());
                if (emailCheck != null && emailCheck.getId() != customer.getId()) {
                    SystemLogger.logUserAction(
                        SystemLogger.SystemStatus.WARN,
                        "AGENT",
                        "UPDATE_CUSTOMER_FAILED",
                        String.format("Email %s already exists", customer.getEmail())
                    );
                    throw new SQLException("Email already exists");
                }
            }
            
            Customer updatedCustomer = userDAO.updateCustomer(customer);
            
            SystemLogger.logUserAction(
                SystemLogger.SystemStatus.INFO,
                "AGENT",
                "UPDATE_CUSTOMER_SUCCESS",
                String.format("Agent ID %d successfully updated customer ID %d", agent.getId(), customer.getId())
            );
            
            return updatedCustomer;
        } catch (SQLException e) {
            SystemLogger.logUserAction(
                SystemLogger.SystemStatus.ERROR,
                "AGENT",
                "UPDATE_CUSTOMER_ERROR",
                String.format("Error updating customer ID %d: %s", customer.getId(), e.getMessage()),
                e
            );
            throw e;
        }
    }
;}
