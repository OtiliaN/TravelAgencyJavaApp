package org.example.services;

import org.example.domain.Agent;
import org.example.domain.Flight;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IService {
    public Agent login(String name, String password, IObserver client) throws ServiceException;

    public List<Flight> findAllFlights() throws ServiceException;

    public List<Flight> findFlightsByDestinationAndDate(String destination, LocalDate departureDate) throws ServiceException;

    public boolean buyTickets(Flight flight, List<String> passengers, int numberOfSeats) throws ServiceException;

    public void logout(Agent agent, IObserver client) throws ServiceException;
}
