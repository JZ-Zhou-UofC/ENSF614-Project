package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ModifyReservationDialog extends JDialog {

    private final Agent agentUser;
    private final Customer targetCustomer;
    private final Reservation reservation;
    private final FlightController flightController;
    private final BookingController bookingController;

    private JComboBox<Flight> flightCombo;
    private JSpinner seatSpinner;

    public ModifyReservationDialog(
            Window parent,
            Reservation reservation,
            Agent agentUser,
            Customer targetCustomer,
            FlightController flightController,
            BookingController bookingController
    ) {
        super(parent, "Modify Reservation", ModalityType.APPLICATION_MODAL);

        this.reservation = reservation;
        this.agentUser = agentUser;
        this.targetCustomer = targetCustomer;
        this.flightController = flightController;
        this.bookingController = bookingController;

        setSize(520, 360);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    // ============================================================
    // FORM
    // ============================================================
    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        try {
            List<Flight> flights = flightController.getAllFlights();
            flightCombo = new JComboBox<>(flights.toArray(new Flight[0]));
        } catch (SQLException e) {
            flightCombo = new JComboBox<>();
            JOptionPane.showMessageDialog(this, "Failed to load flights:\n" + e.getMessage());
        }

        // pre-select the current flight
        flightCombo.setSelectedItem(reservation.getFlight());

        seatSpinner = new JSpinner(new SpinnerNumberModel(
                reservation.getSeatCount(), 1, 10, 1
        ));

        panel.add(new JLabel("Flight:"));
        panel.add(flightCombo);
        panel.add(new JLabel("Seat Count:"));
        panel.add(seatSpinner);

        return panel;
    }

    // ============================================================
    // BUTTONS
    // ============================================================
    private JPanel buildButtons() {
        JButton btnSave = new JButton("Save Changes");
        JButton btnDelete = new JButton("Delete Reservation");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> saveChanges());
        btnDelete.addActionListener(e -> deleteReservation());
        btnCancel.addActionListener(e -> dispose());

        JPanel panel = new JPanel();
        panel.add(btnSave);
        panel.add(btnDelete);
        panel.add(btnCancel);

        return panel;
    }

    // ============================================================
    // SAVE CHANGES
    // ============================================================
    private void saveChanges() {
        Flight selectedFlight = (Flight) flightCombo.getSelectedItem();
        int seats = (int) seatSpinner.getValue();

        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(this, "Please select a flight.");
            return;
        }

        try {
            reservation.setFlight(selectedFlight);
            reservation.setSeatCount(seats);
            reservation.setModifiedAt(LocalDateTime.now());
            reservation.setModifiedByUserId(agentUser.getId());

            bookingController.modifyReservation(reservation);

            JOptionPane.showMessageDialog(this, "Reservation updated successfully!");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to update reservation:\n" + ex.getMessage());
        }
    }

    // ============================================================
    // DELETE RESERVATION
    // ============================================================
    private void deleteReservation() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this reservation?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            bookingController.deleteReservation(reservation);

            JOptionPane.showMessageDialog(this,
                    "Reservation deleted successfully!");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to delete reservation:\n" + ex.getMessage());
        }
    }
}
