package org.example;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.rpcprotocol.TravelServicesRpcProxy;
import org.example.services.IService;

import java.io.IOException;
import java.util.Properties;

public class StartRpcClient extends Application {
    private Stage primaryStage;

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Starting Travel Agency Client");
        Properties clientProps = new Properties();
        try{
            clientProps.load(StartRpcClient.class.getResourceAsStream("/travelclient.properties"));
            System.out.println("Properties loaded.");
            clientProps.list(System.out);
        } catch (IOException e){
            System.err.println("Cannot find travelclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("travel.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try{
            serverPort = Integer.parseInt(clientProps.getProperty("travel.server.port"));
        } catch (NumberFormatException e){
            System.err.println("Wrong port number " + e);
        }
        System.out.println("Connecting to server " + serverIP + ":" + serverPort);
        System.out.println("Starting client...");

        IService travelServices = new TravelServicesRpcProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        AnchorPane setLayout = loader.load();
        primaryStage.setTitle("Travel Agency");
        primaryStage.setScene(new Scene(setLayout, Color.POWDERBLUE));

        LoginController logInController = loader.getController();
        logInController.setController(travelServices, primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
