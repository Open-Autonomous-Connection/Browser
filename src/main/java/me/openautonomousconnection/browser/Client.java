package me.openautonomousconnection.browser;

import me.openautonomousconnection.browser.controller.Browser;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.side.ProtocolClient;
import me.openautonomousconnection.protocol.utils.SiteType;

public class Client extends ProtocolClient {
    @Override
    public void handleHTMLContent(SiteType siteType, Domain domain, String htmlContent) {
        Browser.getInstance().loadHtml(siteType, domain, htmlContent);
    }

    @Override
    public void handleMessage(String message) {
        MessageDialog.show(message);
    }
}
