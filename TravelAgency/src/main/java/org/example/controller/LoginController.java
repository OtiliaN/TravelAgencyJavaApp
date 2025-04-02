package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.Agent;
import org.example.service.Service;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    private Service service;

    public void setService(Service service) {
        this.service = service;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String name = nameField.getText();
        String password = passwordField.getText();

        Optional<Agent> agent = service.login(name, password);
        if (agent.isPresent()) {
            // Logare reușită
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome " + agent.get().getName());
            openMainWindow(event);
        } else {
            // Logare eșuată
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid name or password");
        }
    }

    private void openMainWindow(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Scene scene = new Scene(loader.load());

            MainController controller = loader.getController();
            controller.setService(service);

            // Închide fereastra curentă de login
            Stage loginStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loginStage.close();

            // Creează o nouă scenă și fereastră pentru fereastra principală
            Stage mainStage = new Stage();
            mainStage.setScene(scene);
            mainStage.setTitle("Flights");
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
