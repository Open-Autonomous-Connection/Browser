package me.openautonomousconnection.browser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.finn.libraries.eventsystem.EventManager;
import me.finn.libraries.networksystem.client.NetworkClient;
import me.finn.libraries.networksystem.packets.PacketHandler;
import me.openautonomousconnection.browser.controller.Browser;
import me.openautonomousconnection.browser.history.HistoryManager;
import me.openautonomousconnection.browser.listener.DomainListener;
import me.openautonomousconnection.browser.listener.MessageListener;
import me.openautonomousconnection.browser.listener.PingListener;
import me.openautonomousconnection.protocol.packets.DomainPacket;
import me.openautonomousconnection.protocol.packets.MessagePacket;
import me.openautonomousconnection.protocol.packets.PingPacket;
import me.openautonomousconnection.protocol.packets.TopLevelDomainPacket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;

public class Main extends Application {
    public static NetworkClient client;
    private static final Thread shutdownThread = new Thread(shutdown());
    private static Runnable shutdown() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    client.disconnect();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };
    }

    public static void main(String[] args) {

        try {
            Config.init();
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }

        HistoryManager.loadHistory();

        String host = Config.getDNSHost();
        int port = Config.getDNSPort();

        if (args.length > 1) {
            if (args[0].equals("-host")) host = args[1];
            if (args[0].equals("-port")) port = Integer.parseInt(args[1]);
        }

        final PacketHandler packetHandler = new PacketHandler();

        try {
            packetHandler.registerPacket(DomainPacket.class);
            packetHandler.registerPacket(TopLevelDomainPacket.class);
            packetHandler.registerPacket(PingPacket.class);
            packetHandler.registerPacket(MessagePacket.class);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException exception) {
            exception.printStackTrace();
            return;
        }

        EventManager.registerListener(new DomainListener());
        EventManager.registerListener(new PingListener());
        EventManager.registerListener(new MessageListener());

        client = new NetworkClient.ClientBuilder().
                setPort(port).setHost(host).
                setPacketHandler(packetHandler).
                enableDebugLog().
                build();

        Runtime.getRuntime().addShutdownHook(shutdownThread);

        try {
            client.connect();
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

            return;
        }

        launch(args);
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