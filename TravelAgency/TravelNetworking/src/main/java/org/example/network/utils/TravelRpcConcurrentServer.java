// Îmbunătățiri pentru clasa TravelRpcConcurrentServer
package org.example.network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.network.rpcprotocol.TravelClientRpcReflectionWorker;
import org.example.services.IService;

import java.net.Socket;

public class TravelRpcConcurrentServer extends AbsConcurrentServer {
    private IService travelServices;
    private static final Logger logger = LogManager.getLogger(TravelRpcConcurrentServer.class);

    public TravelRpcConcurrentServer(int port, IService travelServices) {
        super(port);
        this.travelServices = travelServices;
        logger.info("Creating RpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        logger.info("Creating worker for client: " + client);
        TravelClientRpcReflectionWorker worker = new TravelClientRpcReflectionWorker(travelServices, client);
        Thread thread = new Thread(worker);
        return thread;
    }

    @Override
    public void stop() {
        logger.info("Stopping RpcConcurrentServer");

    }
}