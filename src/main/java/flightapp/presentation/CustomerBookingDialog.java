package flightapp.presentation;

import flightapp.business.controller.BookingController;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CustomerBookingDialog extends JDialog {

    public CustomerBookingDialog(Window parent, Flight flight, BookingController bookingController) {
        super(parent, "Confirm Booking", ModalityType.APPLICATION_MODAL);

        setSize(380, 260);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JLabel lbl = new JLabel("<html><body style='width:300px'>"
                + "<h3>Confirm Booking</h3>"
                + "Origin: " + flight.getOrigin() + "<br>"
                + "Destination: " + flight.getDestination() + "<br>"
                + "Departure: " + flight.getDepartureTime() + "<br>"
                + "Arrival: " + flight.getArrivalTime() + "<br>"
                + "Price: $" + flight.getPrice() + "<br>"
                + "</body></html>");

        add(lbl, BorderLayout.CENTER);

        JButton btnConfirm = new JButton("Confirm");
        JButton btnCancel = new JButton("Cancel");

        JPanel bottom = new JPanel();
        bottom.add(btnConfirm);
        bottom.add(btnCancel);
        add(bottom, BorderLayout.SOUTH);

        btnConfirm.addActionListener(e -> {
            try {
                Reservation r = bookingController.bookForCurrentUser(flight);
                JOptionPane.showMessageDialog(this,
                        "Booking successful!\nReservation ID: " + r.getId());
                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dispose());
    }
}
