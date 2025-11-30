package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.FlightSeat;
import flightapp.business.domain.Reservation;
import flightapp.business.domain.Seat;
import flightapp.data.FlightSeatDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ModifyReservationDialog extends JDialog {

    private final Agent agentUser;
    private final Customer targetCustomer;
    private final Reservation reservation;
    private final BookingController bookingController;
    private final FlightSeatDAO flightSeatDAO = new FlightSeatDAO();

    private JComboBox<String> seatDropdown;

    public ModifyReservationDialog(
            Window parent,
            Reservation reservation,
            Agent agentUser,
            Customer targetCustomer,
            BookingController bookingController
    ) {
        super(parent, "Modify Reservation", ModalityType.APPLICATION_MODAL);

        this.reservation = reservation;
        this.agentUser = agentUser;
        this.targetCustomer = targetCustomer;
        this.bookingController = bookingController;

        setSize(450, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        Flight flight = reservation.getFlight();

        // Flight is NOT editable
        panel.add(new JLabel("Flight:"));
        panel.add(new JLabel(
                flight.getOrigin() + " → " + flight.getDestination()
        ));

        // Current seat
        panel.add(new JLabel("Current Seat:"));
        panel.add(new JLabel(reservation.getSeatLabel()));

        // Dropdown for new seats
        panel.add(new JLabel("Select New Seat:"));

        seatDropdown = new JComboBox<>();
        loadAvailableSeats();

        panel.add(seatDropdown);

        return panel;
    }

    private void loadAvailableSeats() {
        try {
            List<FlightSeat> allSeats = flightSeatDAO.findByFlight(reservation.getFlight().getId());

            List<String> freeSeats = allSeats.stream()
                    .filter(fs -> !fs.isReserved())              // only free seats
                    .map(fs -> fs.getSeat().getSeatLabel())
                    .collect(Collectors.toList());

            // Remove current seat (it can't be selected)
            freeSeats.remove(reservation.getSeatLabel());

            for (String seat : freeSeats) {
                seatDropdown.addItem(seat);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading seats:\n" + e.getMessage());
        }
    }

    private JPanel buildButtons() {
        JButton btnSave = new JButton("Save Changes");
        JButton btnDelete = new JButton("Cancel Reservation");
        JButton btnClose = new JButton("Close");

        btnSave.addActionListener(e -> saveChanges());
        btnDelete.addActionListener(e -> deleteReservation());
        btnClose.addActionListener(e -> dispose());

        JPanel panel = new JPanel();
        panel.add(btnSave);
        panel.add(btnDelete);
        panel.add(btnClose);

        return panel;
    }

    private void saveChanges() {
        String chosenSeatLabel = (String) seatDropdown.getSelectedItem();

        if (chosenSeatLabel == null) {
            JOptionPane.showMessageDialog(this, "No seat selected.");
            return;
        }

        try {
            // Convert seat label to Seat object
            List<FlightSeat> allSeats = flightSeatDAO.findByFlight(reservation.getFlight().getId());
            FlightSeat newFs = allSeats.stream()
                    .filter(fs -> fs.getSeat().getSeatLabel().equals(chosenSeatLabel))
                    .findFirst()
                    .orElseThrow();

            bookingController.changeSeat(agentUser, reservation, newFs.getSeat());

            JOptionPane.showMessageDialog(this,
                    "Seat changed successfully!");
            dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to change seat:\n" + e.getMessage());
        }
    }

    private void deleteReservation() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this reservation?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION)
            return;

        try {
            bookingController.cancelReservation(reservation);
            JOptionPane.showMessageDialog(this,
                    "Reservation cancelled.");
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to cancel reservation:\n" + e.getMessage());
        }
    }
}

