package flightapp;

import javax.swing.SwingUtilities;
import flightapp.presentation.MainWindow;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}
