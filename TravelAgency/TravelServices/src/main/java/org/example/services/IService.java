package org.example.services;

import org.example.domain.Agent;
import org.example.domain.Flight;

import java.time.LocalDate;
import java.util.List;


public interface IService {
    Agent login(String name, String password, IObserver client) throws ServiceException;

    List<Flight> findAllFlights() throws ServiceException;

    List<Flight> findFlightsByDestinationAndDate(String destination, LocalDate departureDate) throws ServiceException;

    boolean buyTickets(Flight flight, List<String> passengers, int numberOfSeats) throws ServiceException;

    void logout(Agent agent, IObserver client) throws ServiceException;
}
