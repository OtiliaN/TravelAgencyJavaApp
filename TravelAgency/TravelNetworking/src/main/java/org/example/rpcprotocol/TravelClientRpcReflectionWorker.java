// Îmbunătățiri pentru TravelClientRpcReflectionWorker.java
package org.example.rpcprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Flight;
import org.example.domain.Agent;
import org.example.domain.Booking;
import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TravelClientRpcReflectionWorker implements Runnable, IObserver {
    private final IService server;
    private final Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    private static final Logger logger = LogManager.getLogger(TravelClientRpcReflectionWorker.class);

    public TravelClientRpcReflectionWorker(IService server, Socket connection) {
        this.server = server;
        this.connection = connection;

        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
            logger.info("Client connected");
        } catch (IOException e) {
            logger.error("Error initializing streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();


    private Response handleRequest(Request request) {
        Response response = null;
        String handlerName = "handle" + (request).type();
        logger.debug("Handler name: " + handlerName);
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            logger.debug("Method invoked " + handlerName);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("Error handling request: " + e.getMessage(), e);
            return new Response.Builder()
                    .type(ResponseType.ERROR)
                    .data("Server error: " + e.getMessage())
                    .build();
        }

        return response;
    }

    private void sendResponse(Response response) throws IOException {
        logger.debug("Sending response: " + response);
        if (response != null) {
            output.writeObject(response);
            output.flush();
        }
    }

    // Handlers for specific RequestTypes
    private Response handleLOGIN(Request request) {
        logger.info("Handling login request");
        Agent agent = (Agent) request.data();
        try {
            Agent loggedInAgent = server.login(agent.getName(), agent.getPassword(), this);
            return new Response.Builder().type(ResponseType.OK).data(loggedInAgent).build();
        } catch (ServiceException e) {
            logger.info("Login failed, sending ERROR response: " + e.getMessage());
            connected= false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleLOGOUT(Request request) {
        logger.info("Handling logout request");
        Agent agent = (Agent) request.data();
        try {
            server.logout(agent, this);
            connected= false;
            return okResponse;
        } catch (ServiceException e) {
            logger.error("Error logging out: " + e.getMessage());
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_ALL_FLIGHTS(Request request) {
        logger.info("Handling get all flights request");
        try {
            List<Flight> flights = server.findAllFlights();
            return new Response.Builder().type(ResponseType.GET_ALL_FLIGHTS).data(flights).build();
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_FLIGHTS_BY_DESTINATION_AND_DATE(Request request) {
        logger.info("Handling get flights by destination and date request");
        List<Object> data = (List<Object>) request.data();
        String destination = (String) data.get(0);
        LocalDate date = (LocalDate) data.get(1);
        try {
            List<Flight> flights = server.findFlightsByDestinationAndDate(destination, date);
            return new Response.Builder().type(ResponseType.GET_FLIGHTS_BY_DESTINATION_AND_DATE).data(flights).build();
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleBUY_TICKETS(Request request) {
        logger.info("Handling buy tickets request");
        Booking booking = (Booking) request.data();
        try {
            boolean success = server.buyTickets(booking.getFlight(), booking.getPassengers(), booking.getNumberOfSeats());
            if (success) {
                return new Response.Builder().type(ResponseType.OK).build();
            } else {
                return new Response.Builder().type(ResponseType.ERROR).data("Not enough seats available").build();
            }
        } catch (ServiceException e) {
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    @Override
    public void flightModified(Flight flight) throws ServiceException {
        logger.info("Notifying client about flight update: " + flight);
        Response response = new Response.Builder().type(ResponseType.FLIGHT_MODIFIED).data(flight).build();
        try {
            sendResponse(response);
        } catch (Exception e) {
            logger.error("Error sending flight modified notification: " + e.getMessage());
            throw new ServiceException("Error sending flight notification: " + e.getMessage());
        }
    }
}