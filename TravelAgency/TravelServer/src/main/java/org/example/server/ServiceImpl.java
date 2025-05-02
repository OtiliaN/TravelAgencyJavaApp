package org.example.server;

import org.example.domain.Agent;
import org.example.domain.Booking;
import org.example.domain.Flight;
import org.example.persistance.interfaces.IAgentRepository;
import org.example.persistance.interfaces.IBookingRepository;
import org.example.persistance.interfaces.IFlightRepository;

import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.ServiceException;
import org.example.utils.PasswordUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceImpl implements IService {
    private final IAgentRepository agentRepository;
    private final IBookingRepository bookingRepository;
    private final IFlightRepository flightRepository;

    private final Map<String, IObserver> loggedAgents = new ConcurrentHashMap<>();

    public ServiceImpl(IAgentRepository agentRepository, IBookingRepository bookingRepository, IFlightRepository flightRepository) {
        this.agentRepository = agentRepository;
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public synchronized Agent login(String username, String password, IObserver client) throws ServiceException {
        Optional<Agent> agent = agentRepository.findByName(username);
        if (agent.isPresent() && PasswordUtils.checkPassword(password, agent.get().getPassword())) {
            if (loggedAgents.containsKey(username)) {
                throw new IllegalStateException("Agent already logged in!");
            }
            loggedAgents.put(username, client);
            return agent.get();
        }else{
            throw new ServiceException("Authentication failed!");
        }
    }


    @Override
    public synchronized List<Flight> findAllFlights() {
        return (List<Flight>) flightRepository.findAll();
    }

    @Override
    public synchronized List<Flight> findFlightsByDestinationAndDate(String destination, LocalDate departureDate) throws ServiceException {
        try {
            // Logica pentru căutare în baza de date
            return flightRepository.findByDestinationAndDate(destination, departureDate);
        } catch (Exception e) {
            System.err.println("Error finding flights: " + e.getMessage());
            throw new ServiceException("Could not find flights", e);
        }
    }

    @Override
    public synchronized boolean buyTickets(Flight flight, List<String> passengers, int numberOfSeats) {
        if (flight.getAvailableSeats() < numberOfSeats) {
            return false;
        }

        Booking booking = new Booking(flight, passengers, numberOfSeats);
        Optional<Booking> savedBooking = bookingRepository.save(booking);

        if (savedBooking.isPresent()) {
            // Actualizăm numărul de locuri disponibile
            flight.setAvailableSeats(flight.getAvailableSeats() - numberOfSeats);
            flightRepository.update(flight);

            // Notificăm agenții conectați
            notifyAgents(flight);
            return true;
        }
        return false;
    }

    private void notifyAgents(Flight flight) {
        for (IObserver agent : loggedAgents.values()) {
            try {
                agent.flightModified(flight); // Notificăm despre modificarea excursiei
            } catch (Exception e) {
                System.err.println("Error notifying agent: " + e.getMessage());
            }
        }
    }

    @Override
    public synchronized void logout(Agent agent, IObserver client) throws ServiceException {
        IObserver localClient = loggedAgents.remove(agent.getName());
        if (localClient == null) {
            throw new ServiceException("Agent " + agent.getName() + " is not logged in.");
        }
        System.out.println("Agent " + agent.getName() + " logged out.");
    }
}