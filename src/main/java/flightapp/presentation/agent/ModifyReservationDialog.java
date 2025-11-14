package flightapp.presentation.agent;

import flightapp.business.AppSession;
import flightapp.business.controller.BookingController;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.data.FlightDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ModifyReservationDialog extends JDialog {

    private final Reservation reservation;
    private final AppSession session;
    private final BookingController bookingController;

    private final FlightDAO flightDAO = new FlightDAO();

    private JComboBox<Flight> flightCombo;
    private JSpinner seatSpinner;

    public ModifyReservationDialog(Window parent,
                                   Reservation reservation,
                                   AppSession session,
                                   BookingController bookingController) {
        super(parent, "Modify Reservation", ModalityType.APPLICATION_MODAL);

        this.reservation = reservation;
        this.session = session;
        this.bookingController = bookingController;

        setSize(500, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        add(buildForm(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        try {
            List<Flight> flights = flightDAO.findAll();
            flightCombo = new JComboBox<>(flights.toArray(new Flight[0]));
        } catch (SQLException e) {
            flightCombo = new JComboBox<>();
            JOptionPane.showMessageDialog(this, "Failed to load flights: " + e.getMessage());
        }

        // Pre-select current flight
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

    private JPanel buildButtons() {
        JButton btnSave = new JButton("Save Changes");
        JButton btnDelete = new JButton("Delete Reservation");  // ⭐ NEW BUTTON
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> saveChanges());
        btnDelete.addActionListener(e -> deleteReservation());  // ⭐ NEW ACTION
        btnCancel.addActionListener(e -> dispose());

        JPanel panel = new JPanel();
        panel.add(btnSave);
        panel.add(btnDelete);  // ⭐ ADD BUTTON
        panel.add(btnCancel);

        return panel;
    }

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

            if (session.getCurrentUser().isAgent()) {
                reservation.setModifiedByUserId(session.getCurrentUser().getId());
            }

            reservation.setModifiedAt(java.time.LocalDateTime.now());

            session.getReservationService().modifyReservation(reservation);

            JOptionPane.showMessageDialog(this, "Reservation updated successfully!");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update reservation: " + ex.getMessage());
        }
    }

    private void deleteReservation() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this reservation?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            session.getReservationService().deleteReservation(reservation);
            JOptionPane.showMessageDialog(this, "Reservation deleted successfully!");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete reservation: " + ex.getMessage());
        }
    }
}
