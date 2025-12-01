package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Agent;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Flight;
import flightapp.presentation.general.FlightFilterDialog;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AgentBookFlightDialog extends JDialog {

    private final Agent agentUser;
    private final Customer targetCustomer;
    private final FlightController flightController;
    private final BookingController bookingController;

    public AgentBookFlightDialog(
            Window parent,
            Agent agentUser,
            Customer targetCustomer,
            FlightController flightController,
            BookingController bookingController
    ) {
        super(parent, "Book Flight for Customer", ModalityType.APPLICATION_MODAL);

        this.agentUser = agentUser;
        this.targetCustomer = targetCustomer;
        this.flightController = flightController;
        this.bookingController = bookingController;

        setSize(800, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // =========================
        // ✅ CENTER PANEL (BUTTON)
        // =========================
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnBrowse = new JButton("Browse & Select Flight");
        center.add(btnBrowse);
        add(center, BorderLayout.CENTER);

        // =========================
        // ✅ BOTTOM BUTTONS
        // =========================
        JButton btnClose = new JButton("Close");

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        // =========================
        // ✅ ACTIONS
        // =========================
        btnBrowse.addActionListener(e -> openBookingFlow());
        btnClose.addActionListener(e -> dispose());
    }

    // =========================
    // ✅ FULL AGENT BOOKING FLOW
    // =========================
    private void openBookingFlow() {
        try {
            List<Flight> list = flightController.getAllFlights();

            // ✅ OPEN FILTER DIALOG
            FlightFilterDialog dialog = new FlightFilterDialog(this, list);
            dialog.setVisible(true);

            // ✅ GET SELECTED FLIGHT
            Flight selected = dialog.getSelectedFlight();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "No flight selected.");
                return;
            }

            // ✅ SEAT SELECTION & BOOKING
            new SeatSelectionDialog(
                    this,
                    agentUser,
                    targetCustomer,
                    selected,
                    bookingController
            ).setVisible(true);

            dispose(); // ✅ Close after booking step

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading flights:\n" + ex.getMessage());
        }
    }
}
