package flightapp.presentation.general;

import flightapp.business.domain.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FlightFilterDialog extends JDialog {

    private final JTextField txtOrigin = new JTextField(10);
    private final JTextField txtDest   = new JTextField(10);
    private final JTextField txtDate   = new JTextField(10); // YYYY-MM-DD

    private final JButton btnFilter = new JButton("Filter");
    private final JButton btnReset  = new JButton("Reset");
    private final JButton btnClose  = new JButton("Close");

    private final DefaultTableModel tableModel;
    private final JTable tblFlights;

    private final List<Flight> allFlights;
    private final List<Flight> filteredFlights = new ArrayList<>();

    // ✅ Selected flight to return
    private Flight selectedFlight;

    public FlightFilterDialog(Window parent, List<Flight> flights) {
        super(parent, "Browse & Filter Flights", ModalityType.APPLICATION_MODAL);

        this.allFlights = new ArrayList<>(flights);
        this.filteredFlights.addAll(flights);

        setSize(860, 460);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // =========================
        // ✅ TOP FILTER PANEL
        // =========================
        JPanel filterPanel = new JPanel();

        filterPanel.add(new JLabel("From:"));
        filterPanel.add(txtOrigin);

        filterPanel.add(new JLabel("To:"));
        filterPanel.add(txtDest);

        filterPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        filterPanel.add(txtDate);

        filterPanel.add(btnFilter);
        filterPanel.add(btnReset);

        add(filterPanel, BorderLayout.NORTH);

        // =========================
        // ✅ TABLE
        // =========================
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

        // =========================
        // ✅ BOTTOM PANEL
        // =========================
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        refreshTable();

        // =========================
        // ✅ ACTIONS
        // =========================
        btnFilter.addActionListener(e -> applyFilter());
        btnReset.addActionListener(e -> resetFilter());
        btnClose.addActionListener(e -> dispose());

        // ✅ DOUBLE-CLICK TO SELECT FLIGHT
        tblFlights.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tblFlights.getSelectedRow();
                    if (row >= 0) {
                        selectedFlight = filteredFlights.get(row);
                        dispose();
                    }
                }
            }
        });
    }

    // =========================
    // ✅ FILTER LOGIC
    // =========================
    private void applyFilter() {
        String origin  = txtOrigin.getText().trim();
        String dest    = txtDest.getText().trim();
        String dateStr = txtDate.getText().trim();

        LocalDate date = null;

        if (!dateStr.isBlank()) {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid date format.\nUse YYYY-MM-DD.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        filteredFlights.clear();

        for (Flight f : allFlights) {
            boolean match = true;

            if (!origin.isBlank())
                match &= f.getOrigin().equalsIgnoreCase(origin);

            if (!dest.isBlank())
                match &= f.getDestination().equalsIgnoreCase(dest);

            if (date != null)
                match &= f.getDepartureTime().toLocalDate().equals(date);

            if (match)
                filteredFlights.add(f);
        }

        refreshTable();
    }

    private void resetFilter() {
        txtOrigin.setText("");
        txtDest.setText("");
        txtDate.setText("");

        filteredFlights.clear();
        filteredFlights.addAll(allFlights);
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Flight f : filteredFlights) {
            tableModel.addRow(new Object[]{
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    f.getPrice(),
                    f.getSeatsAvailable()
            });
        }
    }

    // ✅ Getter used by parent dialog
    public Flight getSelectedFlight() {
        return selectedFlight;
    }
}
