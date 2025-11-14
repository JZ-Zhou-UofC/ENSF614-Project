package flightapp.presentation;

import flightapp.business.domain.Flight;
import flightapp.data.FlightDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class FlightSearchDialog extends JDialog {

    private final FlightDAO flightDAO;

    private final JTextField txtOrigin = new JTextField(10);
    private final JTextField txtDest   = new JTextField(10);

    private final DefaultTableModel tableModel;
    private final JTable tblFlights;

    public FlightSearchDialog(JFrame parent, FlightDAO flightDAO) {
        super(parent, "Search Flights", true);
        this.flightDAO = flightDAO;

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

        JButton btnSearch = new JButton("Search");
        top.add(btnSearch);

        add(top, BorderLayout.NORTH);

        //
        // --- CENTER TABLE (VIEW ONLY) ---
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
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);

        //
        // EVENT LISTENERS
        //
        btnSearch.addActionListener(e -> doSearch());
        btnClose.addActionListener(e -> dispose());
    }

    private void doSearch() {
        String origin = txtOrigin.getText().trim();
        String dest   = txtDest.getText().trim();

        try {
            List<Flight> flights = flightDAO.searchFlights(origin, dest);

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
