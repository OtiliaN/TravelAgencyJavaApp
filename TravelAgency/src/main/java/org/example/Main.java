package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.domain.Agent;
import org.example.repository.impl.AgentRepositoryImpl;
import org.example.repository.impl.BookingRepositoryImpl;
import org.example.repository.impl.FlightRepositoryImpl;
import org.example.repository.interfaces.IAgentRepository;
import org.example.repository.interfaces.IBookingRepository;
import org.example.repository.interfaces.IFlightRepository;
import org.example.service.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config " + e);
            return;
        }

        AgentRepositoryImpl agentRepository = new AgentRepositoryImpl(properties);
        FlightRepositoryImpl flightRepository = new FlightRepositoryImpl(properties);
        BookingRepositoryImpl bookingRepository = new BookingRepositoryImpl(properties, flightRepository);

        Service service = new Service(agentRepository, bookingRepository, flightRepository);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.setService(service);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}