package flightapp.presentation.agent;

import flightapp.business.domain.Customer;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class AgentCustomerTableModel extends AbstractTableModel {

    private final String[] columns = { "ID", "First Name", "Last Name", "Email", "Phone" };
    private final List<Customer> customers;

    public AgentCustomerTableModel(List<Customer> customers) {
        this.customers = customers;
    }

    @Override
    public int getRowCount() {
        return customers == null ? 0 : customers.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Customer c = customers.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> c.getId();
            case 1 -> c.getFirstName();
            case 2 -> c.getLastName();
            case 3 -> c.getEmail();
            case 4 -> c.getPhone();
            default -> null;
        };
    }

    public Customer getCustomerAt(int row) {
        return customers.get(row);
    }
}
