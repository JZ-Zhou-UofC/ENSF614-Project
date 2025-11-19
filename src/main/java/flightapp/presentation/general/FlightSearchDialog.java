package flightapp.presentation.general;

import flightapp.business.controller.FlightController;
import flightapp.business.domain.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FlightSearchDialog extends JDialog {

    private final FlightController flightController;

    private final JTextField txtOrigin = new JTextField(10);
    private final JTextField txtDest   = new JTextField(10);
    private final JTextField txtDate   = new JTextField(10); // Optional date filter

    private final DefaultTableModel tableModel;
    private final JTable tblFlights;

    public FlightSearchDialog(JFrame parent, FlightController flightController) {
        super(parent, "Search Flights", true);
        this.flightController = flightController;

        setSize(800, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        //
        // --- TOP SEARCH PANEL ---
        //
        JPanel top = new JPanel();
        top.add(new JLabel("From:"));
        top.add(txtOrigin);
        top.add(new JLabel("To:"));
        top.add(txtDest);
        top.add(new JLabel("Date (YYYY-MM-DD):"));
        top.add(txtDate);

        JButton btnSearch = new JButton("Search");
        top.add(btnSearch);

        add(top, BorderLayout.NORTH);

        //
        // --- TABLE ---
        //
        tableModel = new DefaultTableModel(
                new Object[]{"Origin", "Destination", "Departure", "Arrival", "Price", "Seats"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblFlights = new JTable(tableModel);
        tblFlights.setFillsViewportHeight(true);

        add(new JScrollPane(tblFlights), BorderLayout.CENTER);

        //
        // --- BOTTOM CLOSE BUTTON ---
        //
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        //
        // EVENT LISTENERS
        //
        btnSearch.addActionListener(e -> doSearch());
    }

    private void doSearch() {
        String origin = txtOrigin.getText().trim();
        String dest   = txtDest.getText().trim();
        String dateStr = txtDate.getText().trim();

        LocalDate date = null;
        if (!dateStr.isBlank()) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
        }

        try {
            // ⭐ Use controller, not DAO
            List<Flight> flights = flightController.searchFlights(origin, dest, date);

            tableModel.setRowCount(0);

            for (Flight f : flights) {
                tableModel.addRow(new Object[]{
                        f.getOrigin(),
                        f.getDestination(),
                        f.getDepartureTime(),
                        f.getArrivalTime(),
                        f.getPrice(),
                        f.getSeatsAvailable()
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error searching flights:\n" + ex.getMessage());
        }
    }
}
