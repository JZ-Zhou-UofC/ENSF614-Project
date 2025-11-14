package flightapp.presentation;

import flightapp.business.controller.BookingController;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class BookingDialog extends JDialog {

    private final Flight flight;
    private final BookingController bookingController;

    private final JSpinner seatSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));

    public BookingDialog(Window owner, Flight flight, BookingController bookingController) {
        super(owner, "Book Flight", ModalityType.APPLICATION_MODAL);
        this.flight = flight;
        this.bookingController = bookingController;
        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea txtDetails = new JTextArea();
        txtDetails.setEditable(false);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        txtDetails.setText(buildFlightText());
        main.add(new JScrollPane(txtDetails), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(new JLabel("Seats:"));
        south.add(seatSpinner);

        JButton btnOk = new JButton("Confirm Booking");
        JButton btnCancel = new JButton("Cancel");

        btnOk.addActionListener(e -> onConfirm());
        btnCancel.addActionListener(e -> dispose());

        south.add(btnOk);
        south.add(btnCancel);

        main.add(south, BorderLayout.SOUTH);

        setContentPane(main);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private String buildFlightText() {
        return "Flight ID: " + flight.getId() + "\n"
             + "From: " + flight.getOrigin() + "\n"
             + "To: " + flight.getDestination() + "\n"
             + "Departure: " + flight.getDepartureTime() + "\n"
             + "Arrival: " + flight.getArrivalTime() + "\n"
             + "Price: " + flight.getPrice() + "\n"
             + "Seats available: " + flight.getSeatsAvailable();
    }

    private void onConfirm() {
        int seats = (Integer) seatSpinner.getValue();
        try {
            Reservation r = bookingController.book(flight, seats);
            JOptionPane.showMessageDialog(this,
                    "Booking successful!\nReservation ID: " + r.getId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Cannot Book",
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
