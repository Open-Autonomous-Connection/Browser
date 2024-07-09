/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.browser.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import me.openautonomousconnection.browser.Config;
import me.openautonomousconnection.browser.MessageDialog;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Settings implements Initializable {
    public TextField hostInput;
    public TextField portInput;
    public Button btnSave;
    public TextField usernameInput;
    public TextField applicationInput;
    public TextField apiKeyInput;

    public void onSaveClick(ActionEvent actionEvent) {

        try {
            Integer.parseInt(portInput.getText());
        } catch (NumberFormatException ignored) {
            MessageDialog.show("Please enter a valid port number");
            return;
        }

        try {
            Config.setDNSHost(hostInput.getText());
            Config.setDNSPort(Integer.parseInt(portInput.getText()));
            Config.setAPIApplication(applicationInput.getText());
            Config.setAPIUsername(usernameInput.getText());
            Config.setAPIKey(apiKeyInput.getText());
        } catch (IOException exception) {
            MessageDialog.show("Failed to save settings:\n" + exception.getMessage());
            return;
        }

        MessageDialog.show("Browser need a restart");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hostInput.setText(Config.getDNSHost());
        portInput.setText(String.valueOf(Config.getDNSPort()));
        usernameInput.setText(Config.getAPIUsername());
        applicationInput.setText(Config.getAPIApplication());
        apiKeyInput.setText(Config.getAPIKey());
    }
}
