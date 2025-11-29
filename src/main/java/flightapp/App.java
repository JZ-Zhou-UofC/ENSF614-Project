package flightapp;

import flightapp.util.SystemLogger;

import javax.swing.SwingUtilities;
import flightapp.presentation.MainWindow;

public class App {
	public static void main(String[] args) {
	    // Add shutdown hook for graceful logging
	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	        SystemLogger.logSystemLifecycle(
	            SystemLogger.SystemStatus.INFO,
	            "SHUTDOWN",
	            "Application shutting down gracefully"
	        );
	    }));
	    
	    // Log system startup
	    SystemLogger.logSystemLifecycle(
	        SystemLogger.SystemStatus.INFO,
	        "STARTUP",
	        "Flight Booking Application starting..."
	    );
	    
	    try {
	        SwingUtilities.invokeLater(() -> {
	            SystemLogger.logSystemLifecycle(
	                SystemLogger.SystemStatus.INFO,
	                "UI_INITIALIZATION",
	                "Initializing Swing UI components"
	            );
	            
	            new MainWindow().setVisible(true);
	            
	            SystemLogger.logSystemLifecycle(
	                SystemLogger.SystemStatus.INFO,
	                "UI_READY",
	                "Main window displayed successfully"
	            );
	        });
	    } catch (Exception e) {
	        SystemLogger.logSystemLifecycle(
	            SystemLogger.SystemStatus.FATAL,
	            "STARTUP_FAILED",
	            "Critical error during application startup: " + e.getMessage(),
	            e
	        );
	        System.exit(1);
	    }
	}
}
