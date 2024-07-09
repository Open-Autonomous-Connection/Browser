/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.browser.history;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final File historyFile = new File("history.txt");
    private static List<HistoryItem> historyItems = new ArrayList<>();
    private static int currentPosition = -1;

    public static List<HistoryItem> getHistoryItems() {
        return historyItems;
    }

    public static void addHistoryItem(String url) {
        if (currentPosition < historyItems.size() - 1)
            historyItems.subList(currentPosition + 1, historyItems.size()).clear();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

        historyItems.add(new HistoryItem(url, dateFormat));
        currentPosition++;

        saveHistory();
    }

    public static void saveHistory() {
        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
                return;
            }
        }

        if (historyFile.length() == 0) return;

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(historyFile))) {
            oos.writeObject(historyItems);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void loadHistory() {
        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
                return;
            }
        }

        if (historyFile.length() == 0) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(historyFile))) {
            historyItems = (List<HistoryItem>) ois.readObject();
            currentPosition = historyItems.size() - 1;
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public static String navigateBack() {
        if (currentPosition > 0) {
            currentPosition--;
            return historyItems.get(currentPosition).getUrl();
        }

        return null;
    }

    public static String navigateForward() {
        if (currentPosition < historyItems.size() - 1) {
            currentPosition++;
            return historyItems.get(currentPosition).getUrl();
        }

        return null;
    }

    public static void clear() {
        historyItems.clear();
        currentPosition = -1;
    }
}
