package flightapp;

import flightapp.business.controller.BookingController;
import flightapp.business.controller.FlightController;
import flightapp.business.domain.*;
import flightapp.data.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ControllerTest {

    public static void main(String[] args) throws Exception {

        System.out.println("***** RUNNING CONTROLLER TESTS ****");

        FlightDAO flightDAO = new FlightDAO();
        AirplaneDAO airplaneDAO = new AirplaneDAO();
        FlightSeatDAO flightSeatDAO = new FlightSeatDAO();
        UserDAO userDAO = new UserDAO();
        ReservationDAO reservationDAO = new ReservationDAO();

        FlightController flightController =
                new FlightController(flightDAO, airplaneDAO, flightSeatDAO, reservationDAO);

        BookingController bookingController =
                new BookingController(reservationDAO, flightDAO, flightSeatDAO);

        Admin admin = new Admin(999, "Test", "Admin", "admin@test.com");

        System.out.println("\n--- STEP 1: FETCH EXISTING AIRPLANE FROM DB ---");

        Airplane plane = null;

        try {
            List<Airplane> planes = airplaneDAO.findAll();
            if (planes.isEmpty()) {
                System.out.println("FAIL: No airplanes found in DB. Add airplanes first.");
                return;
            }
            plane = planes.get(0);

            List<Seat> seats = new SeatDAO().findByAirplaneId(plane.getId());
            plane.setSeats(seats);

            System.out.println("PASS: Found airplane ID=" + plane.getId() +
                    ", Seats Loaded=" + seats.size());

        } catch (SQLException e) {
            System.out.println("FAIL: Could not fetch airplane: " + e.getMessage());
            return;
        }

        System.out.println("\n--- TEST 1: CREATE FLIGHT ---");

        Flight flight = new Flight();
        flight.setOrigin("Calgary");
        flight.setDestination("Vancouver");
        flight.setDepartureTime(LocalDateTime.now().plusDays(1));
        flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(1));
        flight.setPrice(199.99);

        flight.setAirplaneId(plane.getId());
        flight.setSeatsAvailable(0);

        Flight savedFlight = null;

        try {
            savedFlight = flightController.createFlight(admin, flight);
            System.out.println("PASS: Flight created with ID = " + savedFlight.getId());
            System.out.println("      Seats available = " + savedFlight.getSeatsAvailable());
        } catch (SQLException e) {
            System.out.println("FAIL: Could not create flight: " + e.getMessage());
            return;
        }

        System.out.println("\n--- TEST 2: BOOK SEAT ---");

        List<Customer> customers = userDAO.findAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("FAIL: No customers exist. Add at least one customer.");
            return;
        }
        Customer cust = customers.get(0);

        List<FlightSeat> flightSeats = flightSeatDAO.findByFlight(savedFlight.getId());
        FlightSeat freeSeat = null;

        for (FlightSeat fs : flightSeats) {
            if (!fs.isReserved()) {
                freeSeat = fs;
                break;
            }
        }

        if (freeSeat == null) {
            System.out.println("FAIL: No unreserved seats found.");
            return;
        }

        System.out.println("Attempting to book seat: " + freeSeat.getSeat().getSeatLabel());

        Reservation reservation = null;

        try {
            reservation = bookingController.bookForCustomer(cust, savedFlight, freeSeat.getSeat());
            System.out.println("PASS: Reservation created: ID = " + reservation.getId());
        } catch (SQLException e) {
            System.out.println("FAIL: Could not book seat: " + e.getMessage());
            return;
        }

        FlightSeat check = flightSeatDAO.findById(freeSeat.getId());
        if (check != null && check.isReserved()) {
            System.out.println("PASS: Seat was correctly marked as reserved.");
        } else {
            System.out.println("FAIL: Seat was NOT marked as reserved.");
        }

        System.out.println("\n--- TEST 3: CANCEL RESERVATION ---");

        try {
            bookingController.cancelReservation(reservation);
            System.out.println("PASS: Reservation cancelled.");
        } catch (SQLException e) {
            System.out.println("FAIL: Could not cancel reservation: " + e.getMessage());
            return;
        }

        FlightSeat check2 = flightSeatDAO.findById(freeSeat.getId());
        if (check2 != null && !check2.isReserved()) {
            System.out.println("PASS: Seat correctly unreserved after cancellation.");
        } else {
            System.out.println("FAIL: Seat still reserved after cancellation.");
        }

        Reservation deleted = reservationDAO.findById(reservation.getId());
        if (deleted == null) {
            System.out.println("PASS: Reservation removed from database.");
        } else {
            System.out.println("FAIL: Reservation still exists in database.");
        }


        System.out.println("\n***** TESTING COMPLETE *****");
    }
}
