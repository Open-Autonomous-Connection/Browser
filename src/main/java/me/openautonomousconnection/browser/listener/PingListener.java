package me.openautonomousconnection.browser.listener;

import me.finn.libraries.eventsystem.EventPriority;
import me.finn.libraries.eventsystem.Listener;
import me.openautonomousconnection.browser.controller.Browser;
import me.openautonomousconnection.protocol.packets.PingPacket;

import java.io.File;
import java.net.URISyntaxException;

public class PingListener {

    @Listener(priority = EventPriority.HIGH)
    public void onPing(PingPacket.PingPacketReceiveEvent event) {
        if (event.isRequest || !Browser.getInstance().pingRequest) return;

        Browser.getInstance().pingRequest = false;

        if (event.isReachable) {
            Browser.getInstance().loadDomain(event.responseDomain);
        }
        else {
            try {
                Browser.getInstance().loadFile(new File(getClass().getResource("sites/site_not_reached.html").toURI()));
            } catch (URISyntaxException exception) {
                Browser.getInstance().pingRequest = false;
                Browser.getInstance().domainExistingRequest = false;

                exception.printStackTrace();
            }
        }
    }

}
