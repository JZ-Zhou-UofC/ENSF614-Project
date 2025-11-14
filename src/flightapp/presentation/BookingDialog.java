package flightapp.presentation;

import flightapp.AppContext;
import flightapp.business.controllers.BookingController;
import flightapp.business.domain.Reservation;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookingDialog extends JDialog {

    private final BookingController bookingController = new BookingController();
    private final DefaultListModel<Reservation> reservationListModel = new DefaultListModel<>();

    public BookingDialog(JFrame parent) {
        super(parent, "My Reservations", true);

        if (!AppContext.isLoggedIn()) {
            JOptionPane.showMessageDialog(parent, "Please login first.");
            dispose();
            return;
        }

        JList<Reservation> reservationList = new JList<>(reservationListModel);
        reservationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton refreshButton = new JButton("Refresh");
        JButton cancelButton  = new JButton("Cancel Selected");

        refreshButton.addActionListener(e -> loadReservations());
        cancelButton.addActionListener(e -> {
            Reservation selected = reservationList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
                return;
            }
            bookingController.cancelReservation(selected);
            JOptionPane.showMessageDialog(this, "Reservation cancelled.");
            loadReservations();
        });

        JPanel topPanel = new JPanel();
        topPanel.add(refreshButton);
        topPanel.add(cancelButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(reservationList), BorderLayout.CENTER);

        setSize(700, 400);
        setLocationRelativeTo(parent);

        loadReservations();
        setVisible(true);
    }

    private void loadReservations() {
        reservationListModel.clear();
        List<Reservation> reservations = bookingController
                .getReservationsForCustomer(AppContext.getCurrentCustomer());
        for (Reservation r : reservations) {
            reservationListModel.addElement(r);
        }
    }
}
