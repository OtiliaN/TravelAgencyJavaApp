package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.persistance.impl.AgentRepositoryImpl;
import org.example.persistance.impl.BookingRepositoryImpl;
import org.example.persistance.impl.FlightRepositoryImpl;
import org.example.persistance.interfaces.IAgentRepository;
import org.example.persistance.interfaces.IBookingRepository;
import org.example.persistance.interfaces.IFlightRepository;
import org.example.server.ServiceImpl;
import org.example.utils.AbstractServer;
import org.example.utils.TravelRpcConcurrentServer;


import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;
    private static Logger logger = LogManager.getLogger(StartRpcServer.class.getName());

    public static void main(String[] args){
        Properties serverProps = new Properties();
        try{
            serverProps.load(StartRpcServer.class.getResourceAsStream("/travelserver.properties"));
            logger.info("Properties loaded.");
            serverProps.list(System.out);
        } catch (IOException e){
            logger.error("Cannot find travelserver.properties " + e);
            return;
        }

        AgentRepositoryImpl agentRepository = new AgentRepositoryImpl(serverProps);
        FlightRepositoryImpl flightRepository = new FlightRepositoryImpl(serverProps);
        BookingRepositoryImpl bookingRepository = new BookingRepositoryImpl(serverProps, flightRepository);

        ServiceImpl travelService = new ServiceImpl(agentRepository, bookingRepository, flightRepository);

        int serverPort = defaultPort;
        try{
            serverPort = Integer.parseInt(serverProps.getProperty("travel.server.port"));
        } catch (NumberFormatException e){
            logger.error("Wrong port number " + e);
        }

        // Pornim serverul RPC
        logger.info("Starting RPC server on port {}", serverPort);
        AbstractServer server = new TravelRpcConcurrentServer(serverPort, travelService);

        try {
            server.start();
        } catch (Exception e) {
            logger.error("Error starting server", e);
        } finally {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error("Error stopping server", e);
            }
        }
    }
}
