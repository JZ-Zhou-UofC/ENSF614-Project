package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.PaymentController;
import flightapp.business.domain.*;
import flightapp.data.FlightSeatDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeatSelectionDialog extends JDialog {

    private final User performer;
    private final Customer customer;
    private final Flight flight;
    private final BookingController bookingController;
    private final PaymentController paymentController; 
    private List<FlightSeat> availableSeats;
    private final FlightSeatDAO flightSeatDAO = new FlightSeatDAO();
    private JComboBox<String> seatDropdown;
    JComboBox<String> paymentSelector; 
    public SeatSelectionDialog(Window parent,
                               User performer,
                               Customer customer,
                               Flight flight,
                               BookingController bookingController) {
        super(parent, "Select Seat", ModalityType.APPLICATION_MODAL);

        this.performer = performer;
        this.customer = customer;
        this.flight = flight;
        this.bookingController = bookingController;
        paymentController = new PaymentController(); 

        initUI();
        loadSeats();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea txtDetails = new JTextArea(buildFlightText());
        txtDetails.setEditable(false);

        main.add(new JScrollPane(txtDetails), BorderLayout.CENTER);

        try {
            availableSeats = flightSeatDAO.findByFlight(flight.getId())
                    .stream().filter(fs -> !fs.isReserved())
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            availableSeats = List.of();
        }

        seatDropdown = new JComboBox<>();
        for (FlightSeat fs : availableSeats) {
            seatDropdown.addItem(fs.getSeat().getSeatLabel());
        }

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(new JLabel("Select Seat:"));
        south.add(seatDropdown);

        JButton btnOk = new JButton("Confirm");
        JButton btnCancel = new JButton("Cancel");

        paymentSelector = new JComboBox<>();
        this.initPaymentComboBox();
        btnOk.addActionListener(e -> onConfirm());
        btnCancel.addActionListener(e -> dispose());

        south.add(btnOk);
        south.add(btnCancel);
        south.add(paymentSelector); 
        main.add(south, BorderLayout.SOUTH);

        setContentPane(main);
        pack();
        setLocationRelativeTo(getOwner());
        
    }

    private String buildFlightText() {
        return  "For Customer: " + customer.getFirstName() + "\n" +
                "Flight ID: " + flight.getId() + "\n" +
                "From: " + flight.getOrigin() + "\n" +
                "To: " + flight.getDestination() + "\n" +
                "Departure: " + flight.getDepartureTime() + "\n" +
                "Arrival: " + flight.getArrivalTime() + "\n" +
                "Price: $" + flight.getPrice() + "\n" +
                "Seats available: " + flight.getSeatsAvailable();
    }

    private void initPaymentComboBox(){
        try{
            ArrayList<PaymentMethod> payMethds = paymentController.getPaymentMethods(customer); 
            this.paymentSelector.removeAllItems(); 
    	    for (int i = 0; i < payMethds.size();i++) {
    		this.paymentSelector.addItem(payMethds.get(i).getStrType()); 
    	    }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Warning, no payment options saved to account\n");
        }
    }

    private void loadSeats() {
        try {
            List<FlightSeat> seats = flightSeatDAO.findByFlight(flight.getId());

            List<String> freeSeats = seats.stream()
                    .filter(fs -> !fs.isReserved())
                    .map(fs -> fs.getSeat().getSeatLabel())
                    .collect(Collectors.toList());

            if (freeSeats.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No available seats.");
                dispose();
                return;
            }

            freeSeats.forEach(seatDropdown::addItem);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading seats:\n" + e.getMessage());
            dispose();
        }
    }

    private void onConfirm() {
        String seatLabel = (String) seatDropdown.getSelectedItem();

        if (seatLabel == null) {
            JOptionPane.showMessageDialog(this, "No seat selected.");
            return;
        }

        try {
            // convert to Seat object
            FlightSeatDAO fsDao = new FlightSeatDAO();
            List<FlightSeat> allSeats = fsDao.findByFlight(flight.getId());
            FlightSeat chosen = allSeats.stream()
                    .filter(fs -> fs.getSeat().getSeatLabel().equals(seatLabel))
                    .findFirst()
                    .orElseThrow();

            Reservation r = bookingController.bookSeat(
                    performer,
                    customer,
                    flight,
                    chosen.getSeat()
            );
            String selectedPaymentMethod = (String) paymentSelector.getSelectedItem(); 
            System.out.println(selectedPaymentMethod);
            paymentController.makePayment(r, selectedPaymentMethod, customer);
            JOptionPane.showMessageDialog(this,
                    "Seat booked! Reservation ID: " + r.getId());

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error booking seat:\n" + e.getMessage());
        }
    }
}

