package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.Agent;

import javax.swing.*;
import java.awt.*;

public class AgentMainDialog extends JDialog {

    private final Agent agentUser;
    private final FlightController flightController;
    private final BookingController bookingController;

    // 👇 Signature now matches how you call it:
    // new AgentMainDialog(this, flightController, bookingController, (Agent) currentUser)
    public AgentMainDialog(
            Window parent,
            FlightController flightController,
            BookingController bookingController,
            Agent currentUser
    ) {
        super(parent, "Agent Panel", ModalityType.APPLICATION_MODAL);

        this.agentUser = currentUser;
        this.flightController = flightController;
        this.bookingController = bookingController;

        setSize(420, 200);
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
        new AgentSelectCustomerDialog(
                this,
                agentUser,
                flightController,
                bookingController
        ).setVisible(true);
    }
}
