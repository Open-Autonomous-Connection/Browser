package me.openautonomousconnection.browser.listener;

import javafx.application.Platform;
import me.finn.libraries.eventsystem.EventPriority;
import me.finn.libraries.eventsystem.Listener;
import me.openautonomousconnection.browser.MessageDialog;
import me.openautonomousconnection.protocol.packets.MessagePacket;

public class MessageListener {

    @Listener(priority = EventPriority.HIGH)
    public void onMessage(MessagePacket.MessagePacketReceiveEvent event) {
        Platform.runLater(() -> {
            MessageDialog.show(event.message);
        });
    }

}
