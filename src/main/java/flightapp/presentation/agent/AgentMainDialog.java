package flightapp.presentation.agent;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.controller.PromotionController;
import flightapp.business.domain.Agent;

import javax.swing.*;
import java.awt.*;

public class AgentMainDialog extends JDialog {

    private final Agent agentUser;
    private final FlightController flightController;
    private final BookingController bookingController;
    private final PromotionController promotionController;

public AgentMainDialog(
            Window parent,
            FlightController flightController,
            BookingController bookingController,
            PromotionController promotionController,
            Agent currentUser
    ) {
        super(parent, "Agent Panel", ModalityType.APPLICATION_MODAL);

        this.agentUser = currentUser;
        this.flightController = flightController;
        this.bookingController = bookingController;
        this.promotionController = promotionController;

        setSize(420, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JButton btnSelectCustomer = new JButton("Select Customer...");
        JButton btnSendPromotion = new JButton("Send Monthly Promotion");
        JButton btnClose = new JButton("Close");

        btnSelectCustomer.addActionListener(e -> openCustomerSelector());
        btnSendPromotion.addActionListener(e -> openPromotionDialog());
        btnClose.addActionListener(e -> dispose());

        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.add(btnSelectCustomer);
        center.add(btnSendPromotion);

        JPanel bottom = new JPanel();
        bottom.add(btnClose);

        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
    
    private void openPromotionDialog() {
        new AgentSendPromotionDialog(this,agentUser, promotionController).setVisible(true);
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
