package flightapp.presentation;

import flightapp.business.controller.BookingController;
import flightapp.business.domain.Flight;
import flightapp.data.FlightDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class FlightSearchDialog extends JDialog {

    private final FlightDAO flightDAO;
    private final BookingController bookingController;

    private final JTextField txtOrigin = new JTextField(10);
    private final JTextField txtDestination = new JTextField(10);
    private final FlightTableModel tableModel = new FlightTableModel();
    private final JTable table = new JTable(tableModel);

    private List<Flight> allFlights;

    public FlightSearchDialog(Frame owner,
                              FlightDAO flightDAO,
                              BookingController bookingController) {
        super(owner, "Search Flights", true);
        this.flightDAO = flightDAO;
        this.bookingController = bookingController;
        initUI();
        loadFlights();
    }

    private void initUI() {
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("From:"));
        filters.add(txtOrigin);
        filters.add(new JLabel("To:"));
        filters.add(txtDestination);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> applyFilter());
        filters.add(btnSearch);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);

        JButton btnBook = new JButton("Book Selected Flight");
        btnBook.addActionListener(e -> openBooking());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnBook);

        JPanel main = new JPanel(new BorderLayout(5, 5));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(filters, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);

        setContentPane(main);
        setSize(800, 400);
        setLocationRelativeTo(getOwner());
    }

    private void loadFlights() {
        try {
            allFlights = flightDAO.findAll();
            tableModel.setFlights(allFlights);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load flights: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilter() {
        if (allFlights == null) return;

        String origin = txtOrigin.getText().trim().toLowerCase();
        String dest = txtDestination.getText().trim().toLowerCase();

        List<Flight> filtered = allFlights.stream()
                .filter(f -> origin.isEmpty() ||
                        (f.getOrigin() != null && f.getOrigin().toLowerCase().contains(origin)))
                .filter(f -> dest.isEmpty() ||
                        (f.getDestination() != null && f.getDestination().toLowerCase().contains(dest)))
                .collect(Collectors.toList());

        tableModel.setFlights(filtered);
    }

    private void openBooking() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a flight first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Flight selected = tableModel.getFlightAt(row);
        BookingDialog dialog = new BookingDialog(this, selected, bookingController);
        dialog.setVisible(true);
    }
}
