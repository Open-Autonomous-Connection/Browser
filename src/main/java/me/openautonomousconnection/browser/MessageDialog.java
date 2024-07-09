/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.browser;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MessageDialog implements Initializable {
    private static MessageDialog instance;
    public Label txtServer;

    public static MessageDialog getInstance() {
        return instance;
    }

    public static void show(String text) {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(MessageDialog.class.getResource("MessageDialog.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Open Autonomous Connection - DNS Message Dialog");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.requestFocus();
                stage.show();

                MessageDialog.getInstance().txtServer.setText(text);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
    }
}
