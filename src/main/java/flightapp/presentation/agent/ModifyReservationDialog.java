package flightapp.presentation.agent;

import flightapp.business.AppSession;
import flightapp.business.controller.BookingController;
import flightapp.business.domain.Flight;
import flightapp.business.domain.Reservation;
import flightapp.data.FlightDAO;
import flightapp.presentation.FlightTableModel;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ModifyReservationDialog extends JDialog {

    private final Reservation reservation;
    private final AppSession session;
    private final BookingController bookingController;

    private final FlightDAO flightDAO = new FlightDAO();

    private JTable tableFlights;
    private JSpinner seatSpinner;

    public ModifyReservationDialog(Window parent,
                                   Reservation reservation,
                                   AppSession session,
                                   BookingController bookingController) {
        super(parent, "Modify / Cancel Reservation", ModalityType.APPLICATION_MODAL);

        this.reservation = reservation;
        this.session = session;
        this.bookingController = bookingController;

        setSize(850, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JLabel lbl = new JLabel("<html><h2>Reservation #" + reservation.getId() + "</h2>"
                + "<b>Current Flight:</b> " + reservation.getFlight().getOrigin()
                + " → " + reservation.getFlight().getDestination()
                + " (" + reservation.getFlight().getDepartureTime() + ")"
                + "<br><b>Current Seats:</b> " + reservation.getSeatCount()
                + "</html>");

        add(lbl, BorderLayout.NORTH);

        // --- Flight list table ---
        loadFlights();

        // --- Seat count spinner ---
        JPanel seatPanel = new JPanel();
        seatPanel.add(new JLabel("New Seat Count:"));

        seatSpinner = new JSpinner(new SpinnerNumberModel(
                reservation.getSeatCount(), 1, 10, 1));

        seatPanel.add(seatSpinner);

        // --- Buttons ---
        JButton btnSave = new JButton("Save Changes");
        JButton btnCancel = new JButton("Cancel Reservation");
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel();
        bottom.add(seatPanel);
        bottom.add(btnSave);
        bottom.add(btnCancel);
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        // listeners
        btnSave.addActionListener(e -> saveChanges());
        btnCancel.addActionListener(e -> cancelReservation());
        btnClose.addActionListener(e -> dispose());
    }

    private void loadFlights() {
        try {
            List<Flight> flights = flightDAO.findAll();
            tableFlights = new JTable(new FlightTableModel(flights));
            add(new JScrollPane(tableFlights), BorderLayout.CENTER);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading flights: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // SAVE CHANGES (modify reservation)
    // ---------------------------------------------------------
    private void saveChanges() {
        int row = tableFlights.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a flight.");
            return;
        }

        Flight newFlight = ((FlightTableModel) tableFlights.getModel()).getFlightAt(row);
        int newSeatCount = (int) seatSpinner.getValue();

        try {
            session.getReservationService().modifyReservation(
                    reservation,
                    newFlight,
                    newSeatCount,
                    session.getCurrentUser()
            );

            JOptionPane.showMessageDialog(this, "Reservation updated successfully.");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ---------------------------------------------------------
    // CANCEL RESERVATION
    // ---------------------------------------------------------
    private void cancelReservation() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this reservation?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            session.getReservationService().cancelReservation(
                    reservation,
                    session.getCurrentUser()
            );

            JOptionPane.showMessageDialog(this, "Reservation cancelled.");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
