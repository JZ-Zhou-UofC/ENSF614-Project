package flightapp.presentation.agent;

import flightapp.business.controller.PromotionController;
import flightapp.business.domain.Customer;
import flightapp.business.domain.Agent;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Dialog for agents to compose and send monthly promotion messages to
 * subscribed customers.
 */
public class AgentSendPromotionDialog extends JDialog {

    private final PromotionController promotionController;
    private final Agent agentUser;
    private JTextArea txtMessage;
    private JList<String> lstSubscribers;
    private DefaultListModel<String> subscriberListModel;

    public AgentSendPromotionDialog(Window parent, Agent agentUser, PromotionController promotionController) {
        super(parent, "Send Monthly Promotion", ModalityType.APPLICATION_MODAL);
        this.agentUser = agentUser;
        this.promotionController = promotionController;

        setSize(650, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        initUI();
        updateSubscriberList();
    }

    private void initUI() {

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("<html><h3>Compose Monthly Promotion Message</h3></html>");
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // Subscriber List
        subscriberListModel = new DefaultListModel<>();
        lstSubscribers = new JList<>(subscriberListModel);
        lstSubscribers.setBorder(BorderFactory.createTitledBorder("Subscribed Customers"));

        JScrollPane subscriberScroll = new JScrollPane(lstSubscribers);
        subscriberScroll.setPreferredSize(new Dimension(250, 120));

        topPanel.add(subscriberScroll, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);



        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblMessage = new JLabel("Promotion Message:");
        centerPanel.add(lblMessage, BorderLayout.NORTH);

        txtMessage = new JTextArea(15, 40);
        txtMessage.setLineWrap(true);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Default message template
        txtMessage.setText(
                "Dear Valued Customer,\n\n" +
                        "We are excited to share our monthly promotions with you!\n\n" +
                        "This month, enjoy special discounts on select flights.\n\n" +
                        "Best regards,\n" +
                        "614 Group 1");

        JScrollPane scrollPane = new JScrollPane(txtMessage);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnSend = new JButton("Send Promotion");
        JButton btnCancel = new JButton("Cancel");

        btnSend.addActionListener(e -> sendPromotion());
        btnCancel.addActionListener(e -> dispose());

        bottomPanel.add(btnSend);
        bottomPanel.add(btnCancel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Default button
        getRootPane().setDefaultButton(btnSend);
    }


    private void updateSubscriberList() {
        try {
            List<Customer> customers = promotionController.getSubscribedCustomers();
            subscriberListModel.clear();

            for (Customer customer : customers) {
                subscriberListModel.addElement(
                        customer.getFirstName() + " " + customer.getLastName());
            }

            if (customers.isEmpty()) {
                subscriberListModel.addElement("No subscribed customers.");
            }

        } catch (SQLException ex) {
            subscriberListModel.clear();
            subscriberListModel.addElement("Error loading subscribers: " + ex.getMessage());
        }
    }


    private void sendPromotion() {
        String message = txtMessage.getText().trim();

        if (message.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a promotion message.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<String> notifiedEmails = promotionController.sendMonthlyPromotion(agentUser.getId(), message);

            if (notifiedEmails.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No subscribed customers found. No promotions were sent.",
                        "No Subscribers",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Promotion sent successfully to " + notifiedEmails.size() + " customer(s)!\n\n" +
                                "Emails sent to:\n" + String.join("\n", notifiedEmails),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error sending promotion: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
