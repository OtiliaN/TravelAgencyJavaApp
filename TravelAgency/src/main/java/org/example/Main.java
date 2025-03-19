package org.example;

import org.example.domain.Agent;
import org.example.repository.impl.AgentRepositoryImpl;
import org.example.repository.impl.BookingRepositoryImpl;
import org.example.repository.impl.FlightRepositoryImpl;
import org.example.repository.interfaces.IAgentRepository;
import org.example.repository.interfaces.IBookingRepository;
import org.example.repository.interfaces.IFlightRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
        }

        IAgentRepository agentRepository = new AgentRepositoryImpl(properties);
        IBookingRepository bookingRepository = new BookingRepositoryImpl(properties);
        IFlightRepository flightRepository = new FlightRepositoryImpl(properties);

        Iterable<Agent> agents = agentRepository.findAll();
        for (Agent agent : agents) {
            System.out.println(agent);
        }
    }
}