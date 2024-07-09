/*
 * Copyright (C) 2024 Open Autonomous Connection - All Rights Reserved
 *
 * You are unauthorized to remove this copyright.
 * You have to give Credits to the Author in your project and link this GitHub site: https://github.com/Open-Autonomous-Connection
 * See LICENSE-File if exists
 */

package me.openautonomousconnection.browser.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import me.openautonomousconnection.browser.history.HistoryItem;
import me.openautonomousconnection.browser.history.HistoryManager;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class History implements Initializable {
    public Button btnClear;
    public ListView historyList;

    public void onClearClick(ActionEvent actionEvent) {
        HistoryManager.clear();
        HistoryManager.saveHistory();
        updateHistoryListView();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateHistoryListView();
    }

    private void updateHistoryListView() {
        if (HistoryManager.getHistoryItems().isEmpty()) return;

        ObservableList<String> items = FXCollections.observableArrayList();
        for (HistoryItem item : HistoryManager.getHistoryItems())
            items.add(item.getUrl() + " - " + item.getDateFormat().format(new Date()));
        historyList.setItems(items);
    }
}
