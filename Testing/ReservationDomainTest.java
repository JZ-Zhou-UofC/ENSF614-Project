package flightapp.business.domain;

import java.time.LocalDateTime;



public class ReservationDomainTest {
    public static void main(String[] args) {

        Customer c = new Customer(1, "William", "Watson",
                "william.watson@ucalgary.ca", "123-4567");
        System.out.println(c);

                // Create airplane
        Airplane airplane = new Airplane(10,30, new char[] {'A','B','C','D','E','F'});
        System.out.println("Airplane: " + airplane);

        // Pick seat
        Seat seat = airplane.getSeatByLabel("12C");
        System.out.println("Picked seat: " + seat);

        // Create flight
        Flight flight = new Flight();
        flight.setId(100);
        flight.setOrigin("Calgary");
        flight.setDestination("Toronto");
        flight.setDepartureTime(LocalDateTime.of(2025, 5, 10, 8, 30));
        flight.setArrivalTime(LocalDateTime.of(2025, 5, 10, 14, 10));
        flight.setPrice(399.99);
        flight.setSeatsAvailable(180);
        System.out.println("Flight: " + flight);

        // 5. Create FlightSeat
        FlightSeat fs = new FlightSeat(flight.getId(), seat, false);
        System.out.println("FlightSeat: " + fs);

        // 6. Create reservation
        Reservation r = new Reservation(c, flight, fs, c.getId());
        System.out.println("Reservation: " + r);

        // 7. Show seat label
        System.out.println("Seat label: " + r.getSeatLabel());
    }
    
}