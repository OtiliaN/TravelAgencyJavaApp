package org.example.network.protobufferprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Agent;
import org.example.domain.Booking;
import org.example.domain.Flight;
import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.ServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtobufProxy implements IService {
    private final String host;
    private final int port;

    private IObserver client;

    private InputStream input;
    private OutputStream output;
    private Socket connection;

    private BlockingQueue<Protobufs.Response> qresponses;
    private volatile boolean finished;

    private static final Logger logger = LogManager.getLogger(ProtobufProxy.class);

    public ProtobufProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.qresponses = new LinkedBlockingQueue<>();
    }

    private void initializeConnection() throws ServiceException {
        try {
            connection = new Socket(host, port);
            output = connection.getOutputStream();
            input = connection.getInputStream();
            finished = false;
            startReader();
            logger.info("Connection initialized successfully");
        } catch (IOException e) {
            logger.error("Error initializing connection: " + e.getMessage());
            throw new ServiceException("Error initializing connection: " + e.getMessage());
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (connection != null) connection.close();
            client = null;
            logger.info("Connection closed");
        } catch (IOException e) {
            logger.error("Error closing connection: " + e.getMessage());
        }
    }

    private void sendRequest(Protobufs.Request request) throws ServiceException {
        try {
            request.writeDelimitedTo(output);
            logger.info("Request sent successfully");
        } catch (IOException e) {
            logger.error("Failed to send request: " + e.getMessage());
            throw new ServiceException("Error sending request: " + e.getMessage());
        }
    }

    private Protobufs.Response readResponse() throws ServiceException {
        try {
            return qresponses.take();
        } catch (InterruptedException e) {
            throw new ServiceException("Error reading response: " + e.getMessage());
        }
    }

    private boolean isUpdate(Protobufs.ResponseType responseType) {
        return responseType == Protobufs.ResponseType.RESPONSE_MODIFIED_FLIGHT;
    }

    private void handleUpdate(Protobufs.Response response) {
        if (response.getResponseType() == Protobufs.ResponseType.RESPONSE_MODIFIED_FLIGHT) {
            Protobufs.Flight protoFlight = response.getFlight();
            logger.debug("Flight modified: " + protoFlight.getDestination());
            try {
                Flight flight = new Flight(protoFlight.getDestination(),
                        LocalDateTime.parse(protoFlight.getDepartureDateTime()),
                        protoFlight.getAirport(),
                        protoFlight.getAvailableSeats());
                flight.setId(protoFlight.getId());
                if (client != null) {
                    client.flightModified(flight);
                }
            } catch (ServiceException e) {
                logger.error("Error handling update: " + e.getMessage());
            }
        }
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Protobufs.Response response = Protobufs.Response.parseDelimitedFrom(input);
                    logger.debug("Response received: " + response);
                    if (isUpdate(response.getResponseType())) {
                        handleUpdate(response);
                    } else {
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            logger.error("Error adding response to queue: " + e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    logger.error("Error reading response: " + e.getMessage());
                    closeConnection();
                }
            }
        }
    }

    private void startReader() {
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    @Override
    public Agent login(String username, String password, IObserver client) throws ServiceException {
        initializeConnection();
        Protobufs.Request request = Protobufs.Request.newBuilder()
                .setRequestType(Protobufs.RequestType.REQUEST_LOGIN)
                .setUsername(username)
                .setPassword(password)
                .build();
        sendRequest(request);
        Protobufs.Response response = readResponse();

        if (response.getResponseType() == Protobufs.ResponseType.RESPONSE_OK) {
            this.client = client;
            Protobufs.Agent protoAgent = response.getAgent();
            return new Agent(protoAgent.getName(), protoAgent.getPassword());
        } else if (response.getResponseType() == Protobufs.ResponseType.RESPONSE_ERROR) {
            closeConnection();
            throw new ServiceException(response.getErrorMessage());
        }
        return null;
    }

    @Override
    public void logout(Agent agent, IObserver client) throws ServiceException {
        Protobufs.Request request = Protobufs.Request.newBuilder()
                .setRequestType(Protobufs.RequestType.REQUEST_LOGOUT)
                .setAgent(Protobufs.Agent.newBuilder()
                        .setName(agent.getName())
                        .setPassword(agent.getPassword())
                        .build())
                .build();
        sendRequest(request);
        Protobufs.Response response = readResponse();
        closeConnection();
        if (response.getResponseType() == Protobufs.ResponseType.RESPONSE_ERROR) {
            throw new ServiceException(response.getErrorMessage());
        }
    }

    @Override
    public List<Flight> findAllFlights() throws ServiceException {
        Protobufs.Request request = Protobufs.Request.newBuilder()
                .setRequestType(Protobufs.RequestType.REQUEST_GET_ALL_FLIGHTS)
                .build();
        sendRequest(request);
        Protobufs.Response response = readResponse();

        if (response.getResponseType() == Protobufs.ResponseType.RESPONSE_ERROR) {
            throw new ServiceException(response.getErrorMessage());
        }

        List<Flight> flights = new ArrayList<>();
        for (Protobufs.Flight protoFlight : response.getFlightsList()) {
            Flight flight = new Flight(
                    protoFlight.getDestination(),
                    LocalDateTime.parse(protoFlight.getDepartureDateTime()),
                    protoFlight.getAirport(),
                    protoFlight.getAvailableSeats()
            );
            flight.setId(protoFlight.getId());
            flights.add(flight);
        }
        return flights;
    }

    @Override
    public List<Flight> findFlightsByDestinationAndDate(String destination, LocalDate departureDate) throws ServiceException {
        Protobufs.Request request = Protobufs.Request.newBuilder()
                .setRequestType(Protobufs.RequestType.REQUEST_GET_FLIGHTS_BY_DESTINATION_AND_DATE)
                .setDestination(destination)
                .setDepartureDate(departureDate.toString())
                .build();
        sendRequest(request);
        Protobufs.Response response = readResponse();

        if (response.getResponseType() == Protobufs.ResponseType.RESPONSE_ERROR) {
            throw new ServiceException(response.getErrorMessage());
        }

        List<Flight> flights = new ArrayList<>();
        for (Protobufs.Flight protoFlight : response.getFlightsList()) {
            Flight flight = new Flight(
                    protoFlight.getDestination(),
                    LocalDateTime.parse(protoFlight.getDepartureDateTime()),
                    protoFlight.getAirport(),
                    protoFlight.getAvailableSeats()
            );
            flight.setId(protoFlight.getId());
            flights.add(flight);
        }
        return flights;
    }

    @Override
    public boolean buyTickets(Flight flight, List<String> passengers, int numberOfSeats) throws ServiceException {
        if (flight == null || passengers == null || passengers.isEmpty() || numberOfSeats <= 0) {
            throw new IllegalArgumentException("Invalid data for buying tickets.");
        }

        Protobufs.Flight protoFlight = Protobufs.Flight.newBuilder()
                .setId(flight.getId())
                .setDestination(flight.getDestination())
                .setDepartureDateTime(flight.getDepartureDateTime().toString())
                .setAirport(flight.getAirport())
                .setAvailableSeats(flight.getAvailableSeats())
                .build();

        Protobufs.Booking booking = Protobufs.Booking.newBuilder()
                .setFlight(protoFlight)
                .addAllPassengers(passengers)
                .setNumberOfSeats(numberOfSeats)
                .build();

        Protobufs.Request request = Protobufs.Request.newBuilder()
                .setRequestType(Protobufs.RequestType.REQUEST_BUY_TICKETS)
                .setBooking(booking)
                .build();

        logger.debug("Sending request: " + request);
        sendRequest(request);

        Protobufs.Response response = readResponse();
        if (response.getResponseType() == Protobufs.ResponseType.RESPONSE_OK) {
            return true;
        } else if (response.getResponseType() == Protobufs.ResponseType.RESPONSE_ERROR) {
            throw new ServiceException(response.getErrorMessage());
        }
        return false;
    }

}