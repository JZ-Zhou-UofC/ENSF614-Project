package flightapp.presentation.customer;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.PaymentController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.FlightSeat;
import flightapp.business.domain.Reservation;
import flightapp.data.FlightSeatDAO;

import flightapp.business.domain.PaymentMethod;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList; 
import java.util.List;
import java.util.stream.Collectors;

public class BookingDialog extends JDialog {

    private final Customer customer;
    private final Flight flight;
    private final BookingController bookingController;
    private final PaymentController paymentController; 

    private final JSpinner seatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));
    JComboBox<String> paymentSelector; 
    private JComboBox<String> seatDropdown;
    private List<FlightSeat> availableSeats;
    private final FlightSeatDAO flightSeatDAO = new FlightSeatDAO();

    public BookingDialog(Window owner, Customer customer, Flight flight, BookingController bookingController) {
        super(owner, "Select Seat", ModalityType.APPLICATION_MODAL);
        this.customer = customer;
        this.flight = flight;
        this.bookingController = bookingController;
        paymentController = new PaymentController(); 

        initUI();
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
        return "Flight ID: " + flight.getId() + "\n" +
                "From: " + flight.getOrigin() + "\n" +
                "To: " + flight.getDestination() + "\n" +
                "Departure: " + flight.getDepartureTime() + "\n" +
                "Arrival: " + flight.getArrivalTime() + "\n" +
                "Price: $" + flight.getPrice() + "\n" +
                "Seats available: " + flight.getSeatsAvailable();
    }

    private void onConfirm() {
        if (availableSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No seats available!");
            return;
        }

        String label = (String) seatDropdown.getSelectedItem();
        FlightSeat chosen = availableSeats.stream()
                .filter(fs -> fs.getSeat().getSeatLabel().equals(label))
                .findFirst()
                .orElse(null);

        if (chosen == null) {
            JOptionPane.showMessageDialog(this, "Invalid seat selection.");
            return;
        }

        try {
            Reservation r = bookingController.bookForCustomer(customer, flight, chosen.getSeat());
            String selectedPaymentMethod = (String) paymentSelector.getSelectedItem(); 
            System.out.println(selectedPaymentMethod);
            paymentController.makePayment(r, selectedPaymentMethod, customer);
            JOptionPane.showMessageDialog(this,
                    "Seat booked! Reservation ID: " + r.getId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Booking failed:\n" + ex.getMessage());
        }
    }
}

