/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.browser.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import me.openautonomousconnection.browser.Main;
import me.openautonomousconnection.browser.MessageDialog;
import me.openautonomousconnection.browser.history.HistoryManager;
import me.openautonomousconnection.protocol.domain.Domain;
import me.openautonomousconnection.protocol.domain.LocalDomain;
import me.openautonomousconnection.protocol.domain.RequestDomain;
import me.openautonomousconnection.protocol.utils.DomainUtils;
import me.openautonomousconnection.protocol.utils.SiteType;
import me.openautonomousconnection.protocol.utils.WebsitesContent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class Browser implements Initializable {
    public static Browser instance;
    public Button btnHome;
    public Button btnBackward;
    public Button btnReload;
    public Button btnGo;
    public Button btnForward;
    public Button btnSettings;
    public TextField domainInput;
    public Button btnHistory;
    public WebView webView;

    public static Browser getInstance() {
        return instance;
    }

    public void onHistoryClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("History.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Open Autonomous Connection - Settings");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.requestFocus();
                stage.show();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void onForwardClick(ActionEvent actionEvent) {
        try {
            navigate(HistoryManager.navigateForward());
        } catch (URISyntaxException | IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void onBackwardClick(ActionEvent actionEvent) {
        try {
            navigate(HistoryManager.navigateBack());
        } catch (URISyntaxException | IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void onHomeClick(ActionEvent actionEvent) {
        try {
            navigate(SiteType.PUBLIC.name + "://browser-start.root/");
        } catch (IOException | URISyntaxException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void onReloadClick(ActionEvent actionEvent) {
        try {
            navigate(domainInput.getText());
        } catch (URISyntaxException | IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void onGoClick(ActionEvent actionEvent) {
        try {
            navigate(domainInput.getText());
            HistoryManager.addHistoryItem(domainInput.getText());
        } catch (URISyntaxException | IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public void navigate(String url) throws IOException, URISyntaxException, ClassNotFoundException {
        if (url == null) return;
        if (url.isEmpty()) return;
        if (url.isBlank()) return;

        if (url.startsWith(SiteType.LOCAL.name)) {
            loadLocalDomain(url);
            return;
        }

        if (url.startsWith("http://")) url = url.substring("http://".length());
        if (url.startsWith("https://")) url = url.substring("https://".length());
        if (url.startsWith("www.")) url = url.substring("www.".length());
        if (!url.startsWith(SiteType.PUBLIC.name)) url = SiteType.PUBLIC.name + "://" + url;

        String tld = DomainUtils.getTopLevelDomain(url);
        String name = DomainUtils.getDomainName(url);
        String path = DomainUtils.getPath(url);

        // TODO: Navigate
        Main.protocolBridge.getProtocolClient().resolveSite(new RequestDomain(name, tld, path));
    }

    private void loadLocalDomain(String url) throws IOException, URISyntaxException, ClassNotFoundException {
        if (url == null) return;
        if (url.isEmpty()) return;
        if (url.isBlank()) return;

        if (url.startsWith(SiteType.PUBLIC.name) || url.startsWith("http://") || url.startsWith("Https://") || url.startsWith("www.")) {
            navigate(url);
            return;
        }

        if (!url.startsWith(SiteType.LOCAL.name)) url = SiteType.LOCAL.name + "://" + url;
        File file = new File(url.substring((SiteType.LOCAL.name + "://").length()));

        if (!file.exists()) {
            loadHtml(SiteType.PROTOCOL, new LocalDomain("file-not-found", "html", ""), WebsitesContent.FILE_NOT_FOUND);
            return;
        }

        loadFile(file);
    }

    public void onSettingsClick(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("Settings.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Open Autonomous Connection - Settings");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.requestFocus();
                stage.show();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    private Stage stage;

    public final void setStage(Stage stage) {
        this.stage = stage;
        stage.setTitle("Open Autonomous Connection - " + getTitle(webView.getEngine()));
    }

    private String getTitle(WebEngine webEngine) {
        Document doc = webEngine.getDocument();
        if (doc == null) return domainInput.getText();
        NodeList heads = doc.getElementsByTagName("head");
        String titleText = webEngine.getLocation() ; // use location if page does not define a title
        if (heads.getLength() > 0) {
            Element head = (Element)heads.item(0);
            NodeList titles = head.getElementsByTagName("title");
            if (titles.getLength() > 0) {
                Node title = titles.item(0);
                titleText = title.getTextContent();
            }
        }
        return titleText ;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        btnGo.fire();

        try {
            URL oracle = new URL("https://raw.githubusercontent.com/Open-Autonomous-Connection/browser/master/src/resources/version.txt");

            BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
            String version = "";
            String inputLine;

            while ((inputLine = in.readLine()) != null) version += inputLine;
            if (!version.equalsIgnoreCase(Files.readString(Path.of(Main.class.getResource("../../../version.txt").toURI())))) {
                System.out.println();
                System.out.println("===============================================");
                System.out.println("IMPORTANT: A NEW VERSION IS PUBLISHED ON GITHUB");
                System.out.println("===============================================");
                System.out.println();

                MessageDialog.show("A new version is published on GitHub:\nhttps://github.com/Open-Autonomous-Connection/");
            }
        } catch (IOException | URISyntaxException exception) {
            System.out.println();
            System.out.println("===============================================");
            System.out.println("IMPORTANT: VERSION CHECK COULD NOT COMPLETED! VISIT OUR GITHUB");
            System.out.println("https://github.com/Open-Autonomous-Connection");
            System.out.println("===============================================");
            System.out.println();

            MessageDialog.show("Version check could not completed! Visit our GitHub:\nhttps://github.com/Open-Autonomous-Connection/");
        }

        webView.getEngine().getLoadWorker().stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                EventListener eventListener = evt -> {
                    String type = evt.getType();

                    if (type.equalsIgnoreCase("click")) {
                        String href = ((Element) evt.getTarget()).getAttribute("href");

                        try {
                            if (href.startsWith("http") || href.startsWith("https://")) {
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) Desktop.getDesktop().browse(new URI(href));
                                else MessageDialog.show("Failed to load Website " + href);

                                return;
                            }

                            if (href.startsWith(SiteType.PUBLIC.name + "://")) navigate(href);
                            else {
                                String base = "oac://" + DomainUtils.getDomainName(domainInput.getText()) + "." + DomainUtils.getTopLevelDomain(domainInput.getText()) + "/";
                                navigate(base + (href.startsWith("/") ? href.substring("/".length()) : href));
                            }
                        } catch (IOException | URISyntaxException | ClassNotFoundException exception) {
                            loadHtml(SiteType.PROTOCOL, new LocalDomain("error-occurred", "html", ""), WebsitesContent.ERROR_OCCURRED(exception.getMessage()));
                        }
                    }
                };

                Document doc = webView.getEngine().getDocument();
                NodeList nodeList = doc.getElementsByTagName("a");

                for (int i = 0; i < nodeList.getLength(); i++) {
                    ((EventTarget) nodeList.item(i)).addEventListener("click", eventListener, false);
                    //((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_MOUSEOVER, listener, false);
                    //((EventTarget) nodeList.item(i)).addEventListener(EVENT_TYPE_MOUSEOVER, listener, false);
                }
            }
        });

        // TODO: Crash on header redirect. Fixing later
//        webView.getEngine().locationProperty().addListener((obs, oldLocation, newLocation) -> {
//            try {
//                if (newLocation.startsWith("http") || newLocation.startsWith("https://")) {
//                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) Desktop.getDesktop().browse(new URI(newLocation));
//                    else MessageDialog.show("Failed to load Website " + newLocation);
//
//                    return;
//                }
//
//                if (newLocation.startsWith(SiteType.PUBLIC.name + "://")) navigate(newLocation);
//                else {
//                    String base = "oac://" + DomainUtils.getDomainName(domainInput.getText()) + "." + DomainUtils.getTopLevelDomain(domainInput.getText()) + "/";
//                    navigate(base + (newLocation.startsWith("/") ? newLocation.substring("/".length()) : newLocation));
//                }
//            } catch (IOException | URISyntaxException | ClassNotFoundException exception) {
//                loadHtml(SiteType.PROTOCOL, new LocalDomain("error-occurred", "html", ""), WebsitesContent.ERROR_OCCURRED(exception.getMessage()));
//            }
//        });
    }

    public void loadFile(File file) {
        domainInput.setText(SiteType.LOCAL.name + "://" + file.getAbsolutePath());

        Platform.runLater(() -> {
            webView.getEngine().load(file.toURI().toString());
            if (stage != null) stage.setTitle("Open Autonomous Connection - " + getTitle(webView.getEngine()));
        });
    }

    public void loadHtml(SiteType siteType, Domain domain, String htmlContent) {
        domainInput.setText(siteType.name + "://" + domain.name + "." + domain.topLevelDomain + "/" + domain.getPath());

        Platform.runLater(() -> {
            webView.getEngine().loadContent(htmlContent);
            if (stage != null) stage.setTitle("Open Autonomous Connection - " + getTitle(webView.getEngine()));
        });
    }
}
