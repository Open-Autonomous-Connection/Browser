package me.openautonomousconnection.browser.controller;

import javafx.application.Platform;
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
import me.openautonomousconnection.protocol.domain.RequestDomain;
import me.openautonomousconnection.protocol.utils.DomainUtils;
import me.openautonomousconnection.protocol.utils.SiteType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
            navigate("oac://browser-start.root/");
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

        if (url.startsWith("oac-local://")) {
            loadLocalDomain(url);
            return;
        }

        if (url.startsWith("http://")) url = url.substring(7);
        if (url.startsWith("https://")) url = url.substring(8);
        if (url.startsWith("www.")) url = url.substring(4);
        if (!url.startsWith("oac://")) url = "oac://" + url;

        String tld = DomainUtils.getTopLevelDomain(url);
        String name = DomainUtils.getDomainName(url);

        // TODO: Navigate
        Main.protocolBridge.getProtocolClient().resolveSite(new RequestDomain(name, tld));
    }

    private void loadLocalDomain(String url) throws IOException, URISyntaxException, ClassNotFoundException {
        if (url == null) return;
        if (url.isEmpty()) return;
        if (url.isBlank()) return;

        if (url.startsWith("oac//") || url.startsWith("http://") || url.startsWith("Https://") || url.startsWith("www.")) {
            navigate(url);
            return;
        }

        if (!url.startsWith("oac-local://")) url = "oac-local://" + url;
        File file = new File(url.substring(12));

        if (!file.exists()) {
            try {
                loadFile(new File(getClass().getResource("sites/file_not_found.html").toURI()));
            } catch (URISyntaxException exception) {
                exception.printStackTrace();
            }

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
    }

    public void loadFile(File file) {
        domainInput.setText("oac-local://" + file.getAbsolutePath());

        Platform.runLater(() -> {
            webView.getEngine().load(file.toURI().toString());
            if (stage != null) stage.setTitle("Open Autonomous Connection - " + getTitle(webView.getEngine()));
        });
    }

    public void loadHtml(SiteType siteType, Domain domain, String htmlContent) {
        domainInput.setText(siteType.name + "://" + domain.name + "." + domain.topLevelDomain);

        Platform.runLater(() -> {
        webView.getEngine().loadContent(htmlContent);
        if (stage != null) stage.setTitle("Open Autonomous Connection - " + getTitle(webView.getEngine()));
        });
    }
}
