package org.example.network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.services.ServiceException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    private int port;
    private ServerSocket server = null;
    private static Logger logger = LogManager.getLogger(AbstractServer.class);

    public AbstractServer(int port) {
        this.port = port;
    }

    public void start() throws ServiceException {
        try {
            server = new ServerSocket(port);
            while (true) {
                logger.info("Waiting for client connection...");
                System.out.println("Waiting for client connection...");
                Socket client = server.accept();
                logger.info("Client connected...");
                System.out.println("Client connected...");
                processRequest(client);
            }
        } catch (IOException e) {
            throw new ServiceException("Error starting server: " + e.getMessage(), e);
        } finally {
            stop();
        }
    }

    protected abstract void processRequest(Socket client);

    public void stop() throws ServiceException{
        try{
            server.close();
        } catch (IOException e) {
            throw new ServiceException("Error stopping server: " + e.getMessage(), e);
        }
    }
}
