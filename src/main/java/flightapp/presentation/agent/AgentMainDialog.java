package flightapp.presentation.agent;

import flightapp.business.AppSession;
import flightapp.business.controller.BookingController;

import javax.swing.*;
import java.awt.*;

public class AgentMainDialog extends JDialog {

    private final AppSession session;
    private final BookingController bookingController;

    public AgentMainDialog(Window parent, AppSession session, BookingController bookingController) {
        super(parent, "Agent Panel", ModalityType.APPLICATION_MODAL);

        this.session = session;
        this.bookingController = bookingController;

        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JButton btnSelectCustomer = new JButton("Select Customer...");
        JButton btnClose = new JButton("Close");

        btnSelectCustomer.addActionListener(e -> openCustomerSelector());
        btnClose.addActionListener(e -> dispose());

        JPanel center = new JPanel();
        center.add(btnSelectCustomer);

        JPanel bottom = new JPanel();
        bottom.add(btnClose);

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void openCustomerSelector() {
        new AgentSelectCustomerDialog(this, session, bookingController).setVisible(true);
    }
}
