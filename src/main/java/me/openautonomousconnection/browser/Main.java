/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.browser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.openautonomousconnection.browser.controller.Browser;
import me.openautonomousconnection.browser.history.HistoryManager;
import me.openautonomousconnection.protocol.ProtocolBridge;
import me.openautonomousconnection.protocol.ProtocolSettings;
import me.openautonomousconnection.protocol.ProtocolVersion;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main extends Application {
    public static ProtocolBridge protocolBridge;
    private static final Thread shutdownThread = new Thread(shutdown());
    private static Runnable shutdown() {
        return () -> {
            try {
                if (protocolBridge == null) return;
                protocolBridge.getProtocolClient().disconnectClient();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        };
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        try {
            Config.init();
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

        HistoryManager.loadHistory();

        String host = Config.getDNSHost();
        int port = Config.getDNSPort();

        final ProtocolSettings protocolSettings = new ProtocolSettings();
        protocolSettings.host = host;
        protocolSettings.port = port;

        protocolBridge = new ProtocolBridge(ProtocolVersion.PV_1_0_0, protocolSettings, new Client());
        protocolBridge.getProtocolClient().setProtocolBridge(protocolBridge);

        Runtime.getRuntime().addShutdownHook(shutdownThread);

        try {
            protocolBridge.getProtocolClient().startClient();
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();

            Platform.runLater(() -> {
                try {
                    Parent root2 = FXMLLoader.load(MessageDialog.class.getResource("MessageDialog.fxml"));
                    Stage stage2 = new Stage();
                    stage2.setTitle("Open Autonomous Connection - DNS Message Dialog");
                    stage2.setScene(new Scene(root2));
                    stage2.setResizable(false);
                    stage2.requestFocus();
                    stage2.show();
                    MessageDialog.getInstance().txtServer.setText("Failed to connect to DNS-Server! Please try again later.\n" + exception.getMessage());
                } catch (IOException exception2) {
                    exception2.printStackTrace();
                    return;
                }
            });

            launch(args);
            return;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Browser.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Open Autonomous Connection");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> shutdownThread.start());
        Browser.getInstance().setStage(stage);

        Platform.runLater(() -> {
            try {
                Parent root2 = FXMLLoader.load(MessageDialog.class.getResource("MessageDialog.fxml"));
                Stage stage2 = new Stage();
                stage2.setTitle("Open Autonomous Connection - DNS Message Dialog");
                stage2.setScene(new Scene(root2));
                stage2.setResizable(false);
                stage2.requestFocus();
                stage2.show();
                MessageDialog.getInstance().txtServer.setText("Please close this window now!");
                stage2.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }
}