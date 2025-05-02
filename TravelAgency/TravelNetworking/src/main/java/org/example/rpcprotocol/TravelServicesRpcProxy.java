
// Îmbunătățiri pentru TravelServicesRpcProxy.java
package org.example.rpcprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Agent;
import org.example.domain.Booking;
import org.example.domain.Flight;
import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TravelServicesRpcProxy implements IService {
    private final String host;
    private final int port;

    private IObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    private static final Logger logger = LogManager.getLogger(TravelServicesRpcProxy.class);

    public TravelServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.qresponses = new LinkedBlockingQueue<>();

    }

    private void initializeConnection() throws ServiceException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader(); // Start a thread to read responses asynchronously
            logger.info("Connection initialized successfully");
        } catch (IOException e) {
            logger.error("Error reading response in initializeConnection: " + e.getMessage());
            logger.error(e.getStackTrace());
        }
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
            logger.info("Connection closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws ServiceException {
        try {
            output.writeObject(request);
            output.flush();
            logger.info("Request sent successfully");
        } catch (IOException e) {
            logger.error("Failed to send request: " + e.getMessage());
            throw new ServiceException("Error sending request: " + e.getMessage());
        }
    }

    private Response readResponse() throws ServiceException {
        Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            logger.error("Error reading response: " + e.getMessage());
            logger.error(e.getStackTrace());
        }
        return response;
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    logger.debug("Response received: " + response);
                    if (isUpdate((Response)response)) {
                        handleUpdate((Response)response);
                    } else {
                        try{
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            logger.error("Error reading response im ReaderThread: " + e.getMessage());
                            logger.error(e.getStackTrace());
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    logger.error("Error reading response in ReaderThread: " + e.getMessage());
                    logger.error(e.getStackTrace());
                    closeConnection();
                }
            }
        }
    }


    private void startReader() {
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.FLIGHT_MODIFIED;
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.FLIGHT_MODIFIED) {
            Flight flight = (Flight) response.data();
            try {
                if (client != null) {
                    client.flightModified(flight);
                }
            } catch (ServiceException e) {
                logger.error("Error notifying client about flight modification: " + e.getMessage());
            }
        }
    }

    // IService methods implementation
    @Override
    public Agent login(String username, String password, IObserver client) throws ServiceException {
        initializeConnection();
        Agent agent = new Agent(username, password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(agent).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            return (Agent) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            closeConnection();
            throw new ServiceException(response.data().toString());
        }
        return null;
    }

    @Override
    public void logout(Agent agent, IObserver client) throws ServiceException {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(agent).build();
        try {
            sendRequest(request);
            Response response = readResponse();
            closeConnection();
            if (response.type() == ResponseType.ERROR) {
                throw new ServiceException((String) response.data());
            }
        } catch (ServiceException e) {
            closeConnection(); // Asigurăm închiderea conexiunii chiar și în caz de eroare
            throw e;
        }
    }

    @Override
    public List<Flight> findAllFlights() throws ServiceException {
        Request request = new Request.Builder().type(RequestType.GET_ALL_FLIGHTS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ServiceException((String) response.data());
        }
        logger.info("Response received: " + response);
        return (List<Flight>) response.data();
    }

    @Override
    public List<Flight> findFlightsByDestinationAndDate(String destination, LocalDate departureDate) throws ServiceException {
        List<Object> params = List.of(destination, departureDate);  // ✅ Trimitem parametrii
        Request req = new Request.Builder()
                .type(RequestType.GET_FLIGHTS_BY_DESTINATION_AND_DATE)
                .data(params)
                .build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR) {
            throw new ServiceException(response.data().toString());
        }
        logger.info("Response received: " + response);
        return (List<Flight>) response.data();
    }


    @Override
    public boolean buyTickets(Flight flight, List<String> passengers, int numberOfSeats) throws ServiceException {
        Booking booking = new Booking(flight, passengers, numberOfSeats);
        Request request = new Request.Builder().type(RequestType.BUY_TICKETS).data(booking).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            return true;
        } else if (response.type() == ResponseType.ERROR) {
            throw new ServiceException((String) response.data());
        }
        return false;
    }
}