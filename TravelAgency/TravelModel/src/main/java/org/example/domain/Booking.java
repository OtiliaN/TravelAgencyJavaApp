package org.example.domain;

import java.util.List;

public class Booking extends Entity<Long>{
    private Flight flight;
    private List<String> passengers;
    private int numberOfSeats;

    public Booking(Flight flight, List<String> passengers, int numberOfSeats) {
        this.flight = flight;
        this.passengers = passengers;
        this.numberOfSeats = numberOfSeats;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "flight=" + flight +
                ", passengers=" + passengers +
                ", numberOfSeats=" + numberOfSeats +
                '}';
    }
}
