package org.example.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MessageAlert {
    public static void showMessage(String owner, Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(owner);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showMessageConfirmation(String owner, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(owner);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait();
        return alert.getResult() == buttonTypeYes;
    }
}