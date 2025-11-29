package flightapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized logging utility for system status monitoring.
 * Provides 6 status levels: TRACE, DEBUG, INFO, WARN, ERROR, FATAL
 */
public class SystemLogger {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemLogger.class);
    
    // Status enumeration
    public enum SystemStatus {
        TRACE,   // Very detailed debugging information
        DEBUG,   // Detailed information for debugging
        INFO,    // General informational messages
        WARN,    // Warning messages (potential issues)
        ERROR,   // Error messages (exceptions, failures)
        FATAL    // Critical errors that may cause system shutdown
    }
    
    /**
     * Log system status with custom message
     */
    public static void logStatus(SystemStatus status, String component, String message) {
        String logMessage = String.format("[%s] %s", component, message);
        
        switch (status) {
            case TRACE:
                logger.trace(logMessage);
                break;
            case DEBUG:
                logger.debug(logMessage);
                break;
            case INFO:
                logger.info(logMessage);
                break;
            case WARN:
                logger.warn(logMessage);
                break;
            case ERROR:
                logger.error(logMessage);
                break;
            case FATAL:
                logger.error("FATAL: " + logMessage);
                break;
        }
    }
    
    /**
     * Log system status with exception
     */
    public static void logStatus(SystemStatus status, String component, String message, Throwable throwable) {
        String logMessage = String.format("[%s] %s", component, message);
        
        switch (status) {
            case TRACE:
                logger.trace(logMessage, throwable);
                break;
            case DEBUG:
                logger.debug(logMessage, throwable);
                break;
            case INFO:
                logger.info(logMessage, throwable);
                break;
            case WARN:
                logger.warn(logMessage, throwable);
                break;
            case ERROR:
                logger.error(logMessage, throwable);
                break;
            case FATAL:
                logger.error("FATAL: " + logMessage, throwable);
                break;
        }
    }
    
    /**
     * Log database connection status
     */
    public static void logDatabaseStatus(SystemStatus status, String operation, String details) {
        logStatus(status, "DATABASE", String.format("%s - %s", operation, details));
    }
    
    /**
     * Log database connection status with exception
     */
    public static void logDatabaseStatus(SystemStatus status, String operation, String details, Throwable throwable) {
        logStatus(status, "DATABASE", String.format("%s - %s", operation, details), throwable);
    }
    
    /**
     * Log user action status
     */
    public static void logUserAction(SystemStatus status, String userType, String action, String details) {
        logStatus(status, "USER_ACTION", String.format("[%s] %s - %s", userType, action, details));
    }
    
    /**
     * Log user action status with exception
     */
    public static void logUserAction(SystemStatus status, String userType, String action, String details, Throwable throwable) {
        logStatus(status, "USER_ACTION", String.format("[%s] %s - %s", userType, action, details), throwable);
    }
    
    /**
     * Log business operation status
     */
    public static void logBusinessOperation(SystemStatus status, String operation, String details) {
        logStatus(status, "BUSINESS", String.format("%s - %s", operation, details));
    }
    
    /**
     * Log business operation status with exception
     */
    public static void logBusinessOperation(SystemStatus status, String operation, String details, Throwable throwable) {
        logStatus(status, "BUSINESS", String.format("%s - %s", operation, details), throwable);
    }
    
    /**
     * Log system startup/shutdown
     */
    public static void logSystemLifecycle(SystemStatus status, String event, String details) {
        logStatus(status, "SYSTEM", String.format("%s - %s", event, details));
    }
    
    /**
     * Log system startup/shutdown with exception
     */
    public static void logSystemLifecycle(SystemStatus status, String event, String details, Throwable throwable) {
        logStatus(status, "SYSTEM", String.format("%s - %s", event, details), throwable);
    }
}