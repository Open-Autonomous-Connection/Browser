package me.openautonomousconnection.browser;

import me.finn.libraries.configurationutils.file.YamlConfiguration;
import me.openautonomousconnection.protocol.APIInformation;

import java.io.File;
import java.io.IOException;

public class Config {

    private static File configFile = new File("./config.yml");
    private static YamlConfiguration config;
    private static APIInformation apiInformation;

    public static void init() throws IOException {
        if (!configFile.exists()) configFile.createNewFile();
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.isSet("dns.host")) config.set("dns.host", "82.197.95.202");
        if (!config.isSet("dns.port")) config.set("dns.port", 9382);

        if (!config.isSet("api.username")) config.set("api.username", "");
        if (!config.isSet("api.application")) config.set("api.application", "");
        if (!config.isSet("api.key")) config.set("api.key", "");

        config.save(configFile);
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
        config.save(configFile);
    }

    public static int getDNSPort() {
        return config.getInt("dns.port");
    }

    public static void setDNSPort(int port) throws IOException {
        config.set("dns.port", port);
        config.save(configFile);
    }

    public static String getAPIUsername() {
        return config.getString("api.username");
    }

    public static void setAPIUsername(String username) throws IOException {
        config.set("api.username", username);
        config.save(configFile);
    }

    public static String getAPIApplication() {
        return config.getString("api.application");
    }

    public static void setAPIApplication(String application) throws IOException {
        config.set("api.application", application);
        config.save(configFile);
    }

    public static String getAPIKey() {
        return config.getString("api.key");
    }

    public static void setAPIKey(String key) throws IOException {
        config.set("api.key", key);
        config.save(configFile);
    }
}
