package me.openautonomousconnection.browser.listener;

import me.finn.libraries.eventsystem.EventPriority;
import me.finn.libraries.eventsystem.Listener;
import me.openautonomousconnection.browser.Config;
import me.openautonomousconnection.browser.Main;
import me.openautonomousconnection.browser.controller.Browser;
import me.openautonomousconnection.protocol.RequestType;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import me.openautonomousconnection.protocol.packets.DomainPacket;
import me.openautonomousconnection.protocol.packets.PingPacket;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class DomainListener {


    @Listener(priority = EventPriority.HIGH)
    public void onDomain(DomainPacket.DomainPacketReceiveEvent event) {
        if (event.requestType != RequestType.EXISTS || !Browser.getInstance().domainExistingRequest) return;

        Browser.getInstance().domainExistingRequest = false;
        Browser.getInstance().pingRequest = false;

        if (event.domain == null) {
            try {
                Browser.getInstance().loadFile(new File(getClass().getResource("sites/domain_not_found.html").toURI()));
            } catch (URISyntaxException exception) {
                exception.printStackTrace();
            }
        } else {
            try {
                Browser.getInstance().pingRequest = true;
                Main.client.sendPacket(new PingPacket(Main.client.getClientID(), true, false, new RequestDomain(event.domain.name, event.domain.topLevelDomain), null, Config.getApiInformation()));
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
            }
        }
    }
}