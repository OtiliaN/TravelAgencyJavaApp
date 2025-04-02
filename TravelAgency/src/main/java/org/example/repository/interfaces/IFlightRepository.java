package org.example.repository.interfaces;

import org.example.domain.Flight;
import org.example.repository.IRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IFlightRepository extends IRepository<Long, Flight> {
    List<Flight> findByDestinationAndDate(String destination, LocalDate departureDate);
}
