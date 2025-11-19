package flightapp.presentation.general;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDialog extends JDialog {

    private final JTextField txtEmail = new JTextField(25);
    private String enteredEmail;

    // Hardcoded test users
    private static class TestUser {
        String name;
        String email;

        TestUser(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }

    private final List<TestUser> testUsers = new ArrayList<>();

    public LoginDialog(Frame owner) {
        super(owner, "Login", true);

        // Preload test users
        testUsers.add(new TestUser("Admin", "admin@example.com"));
        testUsers.add(new TestUser("Agent", "agent@example.com"));
        testUsers.add(new TestUser("Customer 1", "customer1@example.com"));
        testUsers.add(new TestUser("Customer 2", "customer2@example.com"));

        initUI();
    }

    private void initUI() {

        //
        // LEFT PANEL (Email Input)
        //
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        leftPanel.add(new JLabel("Enter Email:"), gbc);

        gbc.gridy = 1;
        leftPanel.add(txtEmail, gbc);

        // Buttons row
        JButton btnOk = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");

        btnOk.addActionListener(e -> {
            enteredEmail = txtEmail.getText().trim();
            dispose();
        });

        btnCancel.addActionListener(e -> {
            enteredEmail = null;
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        leftPanel.add(buttonPanel, gbc);


        //
        // RIGHT PANEL (Two-line Test User List)
        //
        DefaultListModel<TestUser> listModel = new DefaultListModel<>();
        testUsers.forEach(listModel::addElement);

        JList<TestUser> userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setVisibleRowCount(8);

        // 2-line cell renderer
        userList.setCellRenderer(new ListCellRenderer<TestUser>() {
            @Override
            public Component getListCellRendererComponent(
                    JList<? extends TestUser> list,
                    TestUser value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JPanel panel = new JPanel(new GridLayout(2, 1));
                JLabel lblName = new JLabel(value.name);
                JLabel lblEmail = new JLabel(value.email);

                lblName.setFont(lblName.getFont().deriveFont(Font.BOLD));

                if (isSelected) {
                    panel.setBackground(list.getSelectionBackground());
                    lblName.setForeground(list.getSelectionForeground());
                    lblEmail.setForeground(list.getSelectionForeground());
                } else {
                    panel.setBackground(list.getBackground());
                    lblName.setForeground(list.getForeground());
                    lblEmail.setForeground(Color.DARK_GRAY);
                }

                panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                panel.add(lblName);
                panel.add(lblEmail);

                return panel;
            }
        });

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setPreferredSize(new Dimension(250, 250));


        //
        // MAIN PANEL (Left Email Input + Right User List)
        //
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        //
        // FINAL WINDOW SETTINGS
        //
        setSize(650, 350);
        setLocationRelativeTo(getOwner());
    }

    public String showDialog() {
        setVisible(true);
        return enteredEmail;
    }
}
