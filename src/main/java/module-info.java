module me.openautonomousconnection.browser {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires jcefmaven;
    requires jcef;
    requires me.openautonomousconnection.protocol;
    requires json.simple;
    requires networksystem;
    requires networkutils;
    requires eventsystem;
    requires configurationutils;
    requires java.sql;

    exports me.openautonomousconnection.browser.listener;
    exports me.openautonomousconnection.browser;
    opens me.openautonomousconnection.browser to javafx.fxml;
    exports me.openautonomousconnection.browser.controller;
    opens me.openautonomousconnection.browser.controller to javafx.fxml;
}