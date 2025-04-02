package org.example.controller;

import com.sun.javafx.binding.LongConstant;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.domain.Flight;
import org.example.service.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class MainController {

    public Button logoutButton;

    public DatePicker departureDatePicker;

    @FXML
    private TableView<Flight> flightTable;

    @FXML
    private TableColumn<Flight, String> destinationColumn;

    @FXML
    private TableColumn<Flight, String> departureTimeColumn;

    @FXML
    private TableColumn<Flight, String> airportColumn;

    @FXML
    private TableColumn<Flight, Integer> availableSeatsColumn;

    @FXML
    private TextField destinationField;

    @FXML
    private TextField departureDateField;

    @FXML
    private TableView<Flight> filteredFlightTable;

    @FXML
    private Label searchResultLabel;

    @FXML
    private TableColumn<Flight, String> filteredDepartureTimeColumn;

    @FXML
    private TableColumn<Flight, Integer> filteredAvailableSeatsColumn;

    @FXML
    private TextField numberOfSeatsField;

    @FXML
    private TextField passengersField;

    @FXML
    private Label buyTicketsResultLabel;

    private Service service;

    public void setService(Service service) {
        this.service = service;
        loadFlights();
    }

    @FXML
    private void initialize() {
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime departureDateTime = cellData.getValue().getDepartureDateTime();
            String departureTime = departureDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return new ReadOnlyStringWrapper(departureTime);
        });
        airportColumn.setCellValueFactory(new PropertyValueFactory<>("airport"));
        availableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        filteredDepartureTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime departureDateTime = cellData.getValue().getDepartureDateTime();
            String departureTime = departureDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            return new ReadOnlyStringWrapper(departureTime);
        });  filteredAvailableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
    }

    private void loadFlights() {
        List<Flight> flights = service.findAllFlights();
        flightTable.getItems().setAll(flights);
    }

    public void searchClicked() {
        LocalDate departureDate = departureDatePicker.getValue();
        String destination = destinationField.getText();

        if (departureDate != null && destination != null && !destination.isEmpty()) {
            List<Flight> flights = service.findFlightsByDestinationAndDate(destination, departureDate);
            filteredFlightTable.getItems().setAll(flights);
            searchResultLabel.setText("Search Result for: " + destination + " on " + departureDate);
        } else {
            showAlert("Invalid Input", "Please enter a valid destination and departure date.");
        }
    }
    @FXML
    private void handleBuyTickets() {
        Flight selectedFlight = filteredFlightTable.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("No Flight Selected", "Please select a flight to buy tickets for.");
            return;
        }

        int numberOfSeats;
        try {
            numberOfSeats = Integer.parseInt(numberOfSeatsField.getText());
        } catch (NumberFormatException e) {
            showAlert("Invalid Number of Seats", "Please enter a valid number of seats.");
            return;
        }

        String passengersText = passengersField.getText();
        List<String> passengers = Arrays.asList(passengersText.split(","));
        if (numberOfSeats != passengers.size()) {
            showAlert("Mismatch", "The number of seats must match the number of passengers.");
            return;
        }
        boolean success = service.buyTickets(selectedFlight, passengers, numberOfSeats);
        if (success) {
            showAlert("Purchase Successful", "The tickets have been successfully purchased.");
            loadFlights();
        } else {
            showAlert("Purchase Failed", "Not enough available seats for the selected flight.");
        }
    }


    private void showAlert (String title, String message){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
    }

        public void handleLogout (ActionEvent event){
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.close();
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent root = loader.load();

                Stage loginStage = new Stage();
                loginStage.setScene(new Scene(root));
                loginStage.setTitle("Login");

                LoginController loginController = loader.getController();
                loginController.setService(service);

                loginStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
