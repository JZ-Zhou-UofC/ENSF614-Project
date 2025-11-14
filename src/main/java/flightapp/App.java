package flightapp;

import javax.swing.*;
import flightapp.presentation.MainWindow;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}
