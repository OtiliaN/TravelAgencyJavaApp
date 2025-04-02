package org.example.repository.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Flight;
import org.example.repository.interfaces.IFlightRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
                    String dateTimeStr = result.getString("departure_date_time");
                    LocalDateTime departureTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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
        try (PreparedStatement preStmt = connection.prepareStatement("select id, destination, strftime('%Y-%m-%d %H:%M:%S', departure_date_time) as departure_date_time, airport, available_seats from Flights")) {
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    Long id = result.getLong("id");
                    String destination = result.getString("destination");
                    String dateTimeStr = result.getString("departure_date_time");
                    LocalDateTime departureTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
            preStmt.setString(2, entity.getDepartureDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
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
       logger.traceEntry("updating flight {} ", entity);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = connection.prepareStatement("update flights set destination=?, departure_date_time=?, airport=?, available_seats=? where id=?")) {
            preStmt.setString(1, entity.getDestination());
            preStmt.setString(2, entity.getDepartureDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            preStmt.setString(3, entity.getAirport());
            preStmt.setInt(4, entity.getAvailableSeats());
            preStmt.setLong(5, entity.getId());
            int result = preStmt.executeUpdate();

            if (result == 0) {
                logger.trace("No flight found with id {}", entity.getId());
                return Optional.empty();
            }

            logger.trace("Updated {} instances", result);
            return Optional.of(entity);
        } catch (SQLException exception) {
            logger.error(exception);
            System.out.println("Error DB " + exception);
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public List<Flight> findByDestinationAndDate(String destination, LocalDate departureDate) {
        logger.traceEntry("findFlightByDestinationAndDate: destination = {}, departureDate = {}", destination, departureDate);

        String sql = "SELECT * FROM flights WHERE destination = ? AND strftime('%Y-%m-%d', departure_date_time) = ?";

        try (
                Connection connection = jdbcUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {
            preparedStatement.setString(1, destination);
            preparedStatement.setString(2, departureDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Flight> flights = new ArrayList<>();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String destination1 = resultSet.getString("destination");
                String dateTimeStr = resultSet.getString("departure_date_time");
                LocalDateTime departureDateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                String airport = resultSet.getString("airport");
                int availableSeats = resultSet.getInt("available_seats");

                Flight flight = new Flight(destination1, departureDateTime, airport, availableSeats);
                flight.setId(id);
                flights.add(flight);
            }
            logger.traceExit();
            return flights;
        } catch (SQLException e) {
            logger.error(e);
            e.printStackTrace();
        }
        logger.traceExit();
        return null;
    }

}
