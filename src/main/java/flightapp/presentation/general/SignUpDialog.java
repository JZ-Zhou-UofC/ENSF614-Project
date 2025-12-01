package flightapp.presentation.general;

import flightapp.business.controller.PaymentController;
import flightapp.business.controller.UserController;
import flightapp.business.domain.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class SignUpDialog extends JDialog {

    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JTextField txtRole;
    private JCheckBox chkSubscribe;
    private JCheckBox chkCreditCard;
    private JCheckBox chkPaypal;

    private boolean success = false;

    private final UserController userController; 
    private final PaymentController paymentController; 


    public SignUpDialog(JFrame parent, UserController userController) {
        super(parent, "Sign Up", true);
        this.userController = userController;
        this.paymentController = new PaymentController(); 
        initUI();
    }

    private void initUI() {
        setSize(350, 350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        form.add(new JLabel("First Name:"));
        txtFirstName = new JTextField();
        form.add(txtFirstName);

        form.add(new JLabel("Last Name:"));
        txtLastName = new JTextField();
        form.add(txtLastName);

        form.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        form.add(txtEmail);

        form.add(new JLabel("Role:"));
        txtRole = new JTextField("Customer");
        txtRole.setEditable(false);              
        txtRole.setBackground(Color.LIGHT_GRAY);
        form.add(txtRole);

        form.add(new JLabel("Add Credit Card")); 
        chkCreditCard = new JCheckBox("Credit Card");
        form.add(chkCreditCard);

        form.add(new JLabel("Add Paypal")); 
        chkPaypal = new JCheckBox("Paypal");
        form.add(chkPaypal);

        form.add(new JLabel("Subscribe:"));
        chkSubscribe = new JCheckBox("Monthly Promotions");
        form.add(chkSubscribe);

        JButton btnSignUp = new JButton("Create Account");
        JButton btnCancel = new JButton("Cancel");


        btnSignUp.addActionListener(e -> doRealSignUp());

        btnCancel.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.add(btnSignUp);
        bottom.add(btnCancel);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);


        getRootPane().setDefaultButton(btnSignUp);
        SwingUtilities.invokeLater(() -> txtFirstName.requestFocusInWindow());
    }


    private void doRealSignUp() {

        String firstName = txtFirstName.getText().trim();
        String lastName  = txtLastName.getText().trim();
        String email     = txtEmail.getText().trim();
        boolean creditCardSelected = chkCreditCard.isSelected();
        boolean paypalSelected = chkPaypal.isSelected();

        if (firstName.isBlank() || lastName.isBlank() || email.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return;
        }

        if(!creditCardSelected && !paypalSelected){
            JOptionPane.showMessageDialog(this, "Must add atleast one payment method.");
            return;
        }



        try {
            boolean subscribed = chkSubscribe.isSelected();
            User currentUser = userController.register(firstName, lastName, email, subscribed);
            if(creditCardSelected){
                paymentController.savePaymentMethod(currentUser, "Credit Card");
            }
            if(paypalSelected){
                paymentController.savePaymentMethod(currentUser, "PayPal");
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Account created successfully!\nPlease log in as Customer."
            );

            success = true;
            dispose();

        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Sign up failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFirstName() {
        return txtFirstName.getText().trim();
    }

    public String getLastName() {
        return txtLastName.getText().trim();
    }

    public String getEmail() {
        return txtEmail.getText().trim();
    }

    public String getRole() {
        return "Customer";
    }
}
