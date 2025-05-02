package org.example.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.domain.Agent;
import org.example.domain.Flight;
import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.ServiceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainController implements IObserver {

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
    private DatePicker departureDatePicker;

    @FXML
    private TableView<Flight> filteredFlightTable;

    @FXML
    private TableColumn<Flight, String> filteredDepartureTimeColumn;

    @FXML
    private TableColumn<Flight, Integer> filteredAvailableSeatsColumn;

    @FXML
    private TextField destinationField;

    @FXML
    private TextField numberOfSeatsField;

    @FXML
    private TextField passengersField;

    @FXML
    private Label searchResultLabel;

    private IService service;
    private Agent currentAgent;
    private Stage stage;

    public void setController(IService service, Stage stage, Agent agent) {
        this.service = service;
        this.stage = stage;
        this.currentAgent = agent;
        loadFlights();
    }

    @FXML
    private void initialize() {
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        departureTimeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getDepartureDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        airportColumn.setCellValueFactory(new PropertyValueFactory<>("airport"));
        availableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

        filteredDepartureTimeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
                cellData.getValue().getDepartureDateTime().format(DateTimeFormatter.ofPattern("HH:mm"))));
        filteredAvailableSeatsColumn.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
    }

    private void loadFlights() {
        try {
            List<Flight> flights = service.findAllFlights();
            flightTable.getItems().setAll(flights);
        } catch (ServiceException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Could not load flights: " + e.getMessage());
        }
    }

    @FXML
    private void searchClicked() {
        LocalDate departureDate = departureDatePicker.getValue();
        String destination = destinationField.getText();

        if (departureDate == null || destination == null || destination.isEmpty()) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please enter a valid destination and departure date.");
            return;
        }

        try {
            List<Flight> flights = service.findFlightsByDestinationAndDate(destination, departureDate);
            filteredFlightTable.getItems().setAll(flights);
            searchResultLabel.setText("Search Result for: " + destination + " on " + departureDate);
        } catch (ServiceException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Search Error", e.getMessage());
        }
    }

    @FXML
    private void handleBuyTickets() {
        Flight selectedFlight = filteredFlightTable.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please select a flight to buy tickets for.");
            return;
        }

        int numberOfSeats;
        try {
            numberOfSeats = Integer.parseInt(numberOfSeatsField.getText());
        } catch (NumberFormatException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Please enter a valid number of seats.");
            return;
        }

        String passengersText = passengersField.getText();
        List<String> passengers = List.of(passengersText.split(","));
        if (numberOfSeats != passengers.size()) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "The number of seats must match the number of passengers.");
            return;
        }

        try {
            boolean success = service.buyTickets(selectedFlight, passengers, numberOfSeats);
            if (success) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "The tickets have been successfully purchased.");
                loadFlights();
            } else {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Not enough available seats for the selected flight.");
            }
        } catch (ServiceException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Purchase Error", e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            service.logout(currentAgent, this);
            stage.close();
        } catch (ServiceException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @Override
    public void flightModified(Flight flight) {
        Platform.runLater(() -> {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,
                    "Zbor actualizat", "Zborul spre " + flight.getDestination() + " a fost actualizat!\nLocuri disponibile: " + flight.getAvailableSeats());
            loadFlights();
        });
    }

}