/*
 * Copyright (c) Patrick Magauran 2017.
 *   Licensed under the AGPLv3. All conditions of said license apply.
 *       This file is part of ABOS.
 *
 *       ABOS is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Affero General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       ABOS is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Affero General Public License for more details.
 *
 *       You should have received a copy of the GNU Affero General Public License
 *       along with ABOS.  If not, see <http://www.gnu.org/licenses/>.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.*;
@SuppressWarnings("WeakerAccess")

public class AddUserController {

    @FXML
    private TextField userNameField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField passwordField;
    private Window parentWindow;
    @FXML
    private Accordion yearsPanel;
    private boolean newUser = true;
    private Map<String, ArrayList<String>> checkedUsers = new HashMap();
    private Map<String, Integer> groups = new HashMap<>();

    public AddUserController() {}

    /**
     * Create the dialog.
     */

    @FXML
    private void submit(ActionEvent event) {
        User.createUser(userNameField.getText(), passwordField.getText());
        ArrayList<ArrayList<String>> yearUsers = new ArrayList<>();
        checkedUsers.forEach((year, users) -> {
            ArrayList<String> usersManage = new ArrayList<>();

            users.forEach((user) -> {

                if (!user.isEmpty()) {
                    usersManage.add(user);

                }
            });

            if (!usersManage.isEmpty()) {
                User yearUser = new User(userNameField.getText(), fullNameField.getText(), usersManage, groups.getOrDefault(year, 0));
                yearUser.addToYear(year);
            }
        });

        close();
    }

    @FXML
    private void cancel(ActionEvent event) {
        close();
    }

    private void close() {
        Stage stage = (Stage) userNameField.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    public void initAddUser(Window parWindow) {
        parentWindow = parWindow;
        DbInt.getYears().forEach(year -> {
            TitledPane yPane;
            ComboBox<treeItemPair<String, Integer>> groupBox = new ComboBox<>();
            TreeView<treeItemPair<String, String>> yearTView;
            CheckBoxTreeItem<treeItemPair<String, String>> yearItem = new CheckBoxTreeItem<treeItemPair<String, String>>(new treeItemPair<>(year, ""));

            Group.getGroups(year).forEach(group -> {
                CheckBoxTreeItem<treeItemPair<String, String>> groupItem = new CheckBoxTreeItem<treeItemPair<String, String>>(new treeItemPair<>(group.getName(), ""));
                group.getUsers().forEach(user -> {
                    CheckBoxTreeItem<treeItemPair<String, String>> userItem = createUserTreeItem(new treeItemPair<>(user.getFullName(), user.getUserName()), year);
                    groupItem.getChildren().add(userItem);
                });
                yearItem.getChildren().add(groupItem);
                try {
                    groupBox.getItems().add(new treeItemPair<String, Integer>(group.getName(), group.getID()));
                } catch (Group.GroupNotFoundException e) {}
            });
            yearTView = new TreeView(yearItem);
            yearItem.setExpanded(true);
            yearTView.setCellFactory(CheckBoxTreeCell.forTreeView());
            groupBox.getSelectionModel().selectedItemProperty().addListener(observable -> {

                groups.put(year, groupBox.getSelectionModel().getSelectedItem().getValue());
            });
            BorderPane contents = new BorderPane(new VBox(10, new Label("Users to manage"), yearTView), new HBox(10, new Label("Group to be a part of"), groupBox), null, null, null);

            yPane = new TitledPane(year, contents);
            yearsPanel.getPanes().add(yPane);

        });


    }

    /**
     * Create the dialog.
     */
    public void initAddUser(String user, Window parWindow) {
        newUser = false;
        parentWindow = parWindow;


    }

    private void addUser() {

    }

    private <T> CheckBoxTreeItem<treeItemPair<String, String>> createUserTreeItem(treeItemPair<String, String> value, String year) {

        CheckBoxTreeItem<treeItemPair<String, String>> item = new CheckBoxTreeItem<treeItemPair<String, String>>(value);
        if (!value.getValue().isEmpty()) {
            item.selectedProperty().addListener((obs, wasChecked, isNowChecked) -> {
                if (isNowChecked) {
                    checkedUsers.computeIfPresent(year, (k, v) -> {
                        v.add(value.getValue());
                        return v;
                    });
                    checkedUsers.computeIfAbsent(year, k -> {
                        ArrayList<String> v = new ArrayList();
                        v.add(value.getValue());
                        return v;
                    });

                } else {
                    checkedUsers.compute(year, (k, v) -> {
                        v.remove(value.getValue());
                        return v;
                    });
                }
            });
        }

        return item;
    }

    private class treeItemPair<K, V> extends Pair<K, V> {

        public treeItemPair(K key, V value) {
            super(key, value);
        }

        @Override
        public String toString() {
            return this.getKey().toString();
        }
    }

}
