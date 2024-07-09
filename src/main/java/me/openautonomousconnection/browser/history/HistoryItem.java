/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

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