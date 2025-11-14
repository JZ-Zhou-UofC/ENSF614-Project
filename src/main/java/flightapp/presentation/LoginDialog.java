package flightapp.presentation;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private final JTextField txtEmail = new JTextField(25);
    private String enteredEmail;

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtEmail, gbc);

        JButton btnOk = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");

        btnOk.addActionListener(e -> {
            enteredEmail = txtEmail.getText();
            dispose();
        });

        btnCancel.addActionListener(e -> {
            enteredEmail = null;
            dispose();
        });

        JPanel buttons = new JPanel();
        buttons.add(btnOk);
        buttons.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getOwner());
    }

    public String showDialog() {
        setVisible(true);
        return enteredEmail;
    }
}
