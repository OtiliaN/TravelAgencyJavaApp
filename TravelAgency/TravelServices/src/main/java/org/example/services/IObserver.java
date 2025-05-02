package org.example.services;
import org.example.domain.Flight;

public interface IObserver {
    void flightModified(Flight flight) throws ServiceException;
}
