package org.example;

import org.example.domain.Flight;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

public class StartFlightTest {
    private static final FlightClient flightClient = new FlightClient();

    public static void main(String[] args) {
        //Addding a new flight (Post)
        Flight flight = new Flight("Bucharest", LocalDateTime.parse("2025-06-15T09:00:00"), "OTP", 150);
        try {
            System.out.println("Adding a new flight: " + flight);
            Flight addedFlight = flightClient.create(flight);
            flight.setId(addedFlight.getId());

            show(() -> System.out.println("Added: " + addedFlight));

            // Get all flights (Get)
            System.out.println("\nAll flights:");
            show(() -> {
                Flight[] flights = flightClient.getAll();
                for (Flight f : flights) {
                    System.out.println(f.getId() + ": " + f.getDestination() + ", " + f.getDepartureDateTime() + ", " + f.getAirport() + ", " + f.getAvailableSeats());
                }
            });
        } catch (RestClientException ex) {
            System.out.println("Exception... " + ex.getMessage());
        }

        // Get după ID
        System.out.println("\nInfo for flight with id = 1");
        show(() -> System.out.println(flightClient.getById(1L)));

        // Update
        System.out.println("\nUpdating flight with id = " + flight.getId());
        Flight updatedFlight = new Flight("Paris", LocalDateTime.parse("2025-06-16T12:00"), "CDG", 120);
        show(() -> System.out.println("Updated: " + flightClient.update(flight.getId(), updatedFlight)));

        // Delete
        System.out.println("\nDeleting flight with id = " + flight.getId());
        show(() -> flightClient.delete(flight.getId()));
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (ServiceException e) {
            System.out.println("Service exception: " + e.getMessage());
        }
    }
}
