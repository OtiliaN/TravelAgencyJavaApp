package org.example.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@jakarta.persistence.Entity
@Table(name = "flights_hibernate")
public class Flight extends Entity<Long> {
    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "departure_date_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime departureDateTime;

    @Column(name = "airport", nullable = false)
    private String airport;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    public Flight() {}

    public Flight(String destination, LocalDateTime departureDateTime, String airport, int seats) {
        this.destination = destination;
        this.departureDateTime = departureDateTime;
        this.airport = airport;
        this.availableSeats = seats;
    }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureDateTime() { return departureDateTime; }
    public void setDepartureDateTime(LocalDateTime departureDateTime) { this.departureDateTime = departureDateTime; }

    public String getAirport() { return airport; }
    public void setAirport(String airport) { this.airport = airport; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    @Override
    public String toString() {
        return "Flight{" +
                "destination='" + destination + '\'' +
                ", departureDateTime=" + departureDateTime +
                ", airport='" + airport + '\'' +
                ", availableSeats=" + availableSeats +
                '}';
    }
}