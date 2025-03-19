package org.example.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Flight;
import org.example.repository.interfaces.IFlightRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class FlightRepositoryImpl implements IFlightRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger();

    public FlightRepositoryImpl(Properties properties) {
        logger.info("Initialising Flight Repository with properties: {}", properties);
        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<Flight> findOne(Long id) {
        logger.traceEntry();
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("select * from flights where id=?")) {
            preStmt.setLong(1, id);
            try (ResultSet result = preStmt.executeQuery()) {
                if (result.next()) {
                    String destination = result.getString("destination");
                    Timestamp timestamp = result.getTimestamp("departure_date_time");
                    LocalDateTime departureTime = timestamp.toLocalDateTime();
                    String airport = result.getString("airport");
                    int availableSeats = result.getInt("available_seats");
                    Flight flight = new Flight(destination, departureTime, airport, availableSeats);
                    flight.setId(id);
                    return Optional.of(flight);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Iterable<Flight> findAll() {
        logger.traceEntry();
        List<Flight> flights = new ArrayList<>();

        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("select * from flights")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String destination = result.getString("destination");
                    Timestamp timestamp = result.getTimestamp("departure_date_time");
                    LocalDateTime departureTime = timestamp.toLocalDateTime();
                    String airport = result.getString("airport");
                    int availableSeats = result.getInt("available_seats");
                    Flight flight = new Flight(destination, departureTime, airport, availableSeats);
                    flight.setId(id);
                    flights.add(flight);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error DB " + e);
        }
        logger.traceExit();
        return flights;
    }

    @Override
    public Optional<Flight> save(Flight entity) {
        logger.traceEntry("saving flight {} ", entity);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("insert into flights(destination, departure_date_time, airport, available_seats) values (?,?,?,?)")) {
            preStmt.setString(1, entity.getDestination());
            preStmt.setTimestamp(2, Timestamp.valueOf(entity.getDepartureDateTime()));
            preStmt.setString(3, entity.getAirport());
            preStmt.setInt(4, entity.getAvailableSeats());
            int result = preStmt.executeUpdate();

            logger.trace("Saved {} instances", result);
            return Optional.of(entity);
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<Flight> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Flight> update(Flight entity) {
        return Optional.empty();
    }
}
