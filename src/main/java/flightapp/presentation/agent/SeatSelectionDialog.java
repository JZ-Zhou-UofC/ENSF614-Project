package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.domain.*;
import flightapp.data.FlightSeatDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SeatSelectionDialog extends JDialog {

    private final User performer;
    private final Customer customer;
    private final Flight flight;
    private final BookingController bookingController;

    private final FlightSeatDAO flightSeatDAO = new FlightSeatDAO();
    private JComboBox<String> seatDropdown;

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

        initUI();
        loadSeats();
    }

    private void initUI() {
        setLayout(new BorderLayout(10,10));
        setSize(300, 150);
        setLocationRelativeTo(getOwner());

        seatDropdown = new JComboBox<>();
        add(seatDropdown, BorderLayout.CENTER);

        JButton btnBook = new JButton("Book Seat");
        btnBook.addActionListener(e -> doBook());
        add(btnBook, BorderLayout.SOUTH);
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

    private void doBook() {
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

            JOptionPane.showMessageDialog(this,
                    "Seat booked! Reservation ID: " + r.getId());

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error booking seat:\n" + e.getMessage());
        }
    }
}

