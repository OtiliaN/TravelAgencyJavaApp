package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.domain.Agent;
import org.example.domain.Flight;
import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.ServiceException;

import java.io.IOException;
import java.net.URL;

public class LoginController  {
    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    private IService service;
    private Stage stage;

    public void setController(IService service, Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    @FXML
    private void handleLogIn(){
        String name = nameField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please complete all the fields!");
            return;
        }

        try {
            URL resource = getClass().getResource("/views/main.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            AnchorPane root = fxmlLoader.load();
            MainController mainController = fxmlLoader.getController();
            Agent agent = service.login(name, password, mainController);
            if (agent == null) {
                throw new ServiceException("Invalid username or password");
            }
            else {
                Stage dialogStage = new Stage();
                mainController.setController(service, dialogStage, agent);
                dialogStage.setTitle("Home");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                stage.close();
                dialogStage.show();
            }
        }catch (ServiceException | IOException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }


}