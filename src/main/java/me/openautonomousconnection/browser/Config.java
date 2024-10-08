/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.browser;

import me.finn.unlegitlibrary.file.ConfigurationManager;
import me.openautonomousconnection.protocol.utils.APIInformation;

import java.io.File;
import java.io.IOException;

public class Config {

    private static File configFile = new File("./config.txt");
    private static ConfigurationManager config;
    private static APIInformation apiInformation;

    public static void init() throws IOException {
        if (!configFile.exists()) configFile.createNewFile();
        config = new ConfigurationManager(configFile);
        config.loadProperties();

        if (!config.isSet("dns.host")) config.set("dns.host", "45.155.173.50");
        if (!config.isSet("dns.port")) config.set("dns.port", 9382);

        if (!config.isSet("api.username")) config.set("api.username", "");
        if (!config.isSet("api.application")) config.set("api.application", "");
        if (!config.isSet("api.key")) config.set("api.key", "");

        config.saveProperties();
        apiInformation = new APIInformation(getAPIUsername(), getAPIApplication(), getAPIKey());
    }

    public static APIInformation getApiInformation() {
        return apiInformation;
    }

    public static String getDNSHost() {
        return config.getString("dns.host");
    }

    public static void setDNSHost(String host) throws IOException {
        config.set("dns.host", host);
        config.saveProperties();
    }

    public static int getDNSPort() {
        return config.getInt("dns.port");
    }

    public static void setDNSPort(int port) throws IOException {
        config.set("dns.port", port);
        config.saveProperties();
    }

    public static String getAPIUsername() {
        return config.getString("api.username");
    }

    public static void setAPIUsername(String username) throws IOException {
        config.set("api.username", username);
        config.saveProperties();
    }

    public static String getAPIApplication() {
        return config.getString("api.application");
    }

    public static void setAPIApplication(String application) throws IOException {
        config.set("api.application", application);
        config.saveProperties();
    }

    public static String getAPIKey() {
        return config.getString("api.key");
    }

    public static void setAPIKey(String key) throws IOException {
        config.set("api.key", key);
        config.saveProperties();
    }
}
