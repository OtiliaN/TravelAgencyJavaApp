package org.example.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Booking;
import org.example.domain.Flight;
import org.example.repository.interfaces.IBookingRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BookingRepositoryImpl implements IBookingRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();
    private FlightRepositoryImpl flightRepository;

    public BookingRepositoryImpl(Properties properties, FlightRepositoryImpl flightRepository) {
        logger.info("Initialising Booking Repository with properties: {}", properties);
        jdbcUtils = new JdbcUtils(properties);
        this.flightRepository = flightRepository;
    }

    @Override
    public Optional<Booking> findOne(Long id) {
        logger.traceEntry();
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("select * from bookings where id=?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    Long id_flight = result.getLong("id_flight");
                    Flight flight = flightRepository.findOne(id_flight).orElse(null);
                    if (flight == null) {
                        return Optional.empty();
                    }
                    String passengersString = result.getString("passengers");
                    List<String> passengers = Arrays.asList(passengersString.split(","));
                    int numberOfSeats = result.getInt("numberOfSeats");
                    Booking booking = new Booking(flight, passengers, numberOfSeats);
                    booking.setId(id);
                    return Optional.of(booking);
                }
            }
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Booking> findAll() {
        logger.traceEntry();
        List<Booking> bookings = new ArrayList<>();

        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("select * from bookings")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    findOne(id).ifPresent(bookings::add);
                }
            }
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
       return bookings;
    }

    @Override
    public Optional<Booking> save(Booking entity) {
        logger.traceEntry("saving booking {} ", entity);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("insert into bookings(id_flight, passengers, numberOfSeats) values (?,?,?)")) {
            preStmt.setLong(1, entity.getFlight().getId());

            String clientsList = String.join(",", entity.getPassengers());
            preStmt.setString(2, clientsList);

            preStmt.setInt(3, entity.getNumberOfSeats());

            int result = preStmt.executeUpdate();
            logger.trace("Saved {} instances", result);

            // Update available seats in Flight
            Flight flight = entity.getFlight();
            flight.setAvailableSeats(flight.getAvailableSeats() - entity.getNumberOfSeats());
            flightRepository.update(flight);

            return Optional.of(entity);
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Booking> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Booking> update(Booking entity) {
        return Optional.empty();
    }
}
