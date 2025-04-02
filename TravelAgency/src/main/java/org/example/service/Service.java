package org.example.service;

import org.example.domain.Agent;
import org.example.domain.Booking;
import org.example.domain.Flight;
import org.example.repository.interfaces.IAgentRepository;
import org.example.repository.interfaces.IBookingRepository;
import org.example.repository.interfaces.IFlightRepository;
import org.example.utils.PasswordUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Service {
    private final IAgentRepository agentRepository;
    private final IBookingRepository bookingRepository;
    private final IFlightRepository flightRepository;

    public Service(IAgentRepository agentRepository, IBookingRepository bookingRepository, IFlightRepository flightRepository) {
        this.agentRepository = agentRepository;
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
    }

    public Optional<Agent> login(String name, String password) {
        Optional<Agent> agent = agentRepository.findByName(name);
        if (agent.isPresent() &&  PasswordUtils.checkPassword(password, agent.get().getPassword())) {
            return agent;
        }
        return Optional.empty();
    }

    public List<Flight> findAllFlights() {
        return (List<Flight>) flightRepository.findAll();
    }

    public List<Flight> findFlightsByDestinationAndDate(String destination, LocalDate departureDate) {
        return flightRepository.findByDestinationAndDate(destination, departureDate);
    }

    public boolean buyTickets(Flight flight, List<String> passengers, int numberOfSeats) {
        if (flight.getAvailableSeats() < numberOfSeats) {
            return false;
        }

        Booking booking = new Booking(flight, passengers, numberOfSeats);
        Optional<Booking> savedBooking = bookingRepository.save(booking);
        return savedBooking.isPresent();
    }


}
