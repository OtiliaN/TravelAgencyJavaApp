package org.example.persistance.interfaces;

import org.example.domain.Flight;
import org.example.persistance.IRepository;

import java.time.LocalDate;
import java.util.List;

public interface IFlightRepository extends IRepository<Long, Flight> {
    List<Flight> findByDestinationAndDate(String destination, LocalDate departureDate);
}
