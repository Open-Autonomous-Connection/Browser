package me.openautonomousconnection.browser.history;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class HistoryItem implements Serializable {

    private String url;
    private SimpleDateFormat dateFormat;

    public HistoryItem(String url, SimpleDateFormat dateFormat) {
        this.url = url;
        this.dateFormat = dateFormat;
    }

    public final String getUrl() {
        return url;
    }

    public final SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
}