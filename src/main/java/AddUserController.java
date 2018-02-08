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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

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
    @FXML
    private Button deleteUserButton;
    @FXML
    private CheckBox adminCheckbox;
    private Map<String, ArrayList<String>> checkedUsers = new HashMap();
    private Map<String, ArrayList<String>> checkedFullName = new HashMap();

    private Map<String, Integer> groups = new HashMap<>();

    public AddUserController() {}

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
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

    /**
     * Create the dialog.
     */

    @FXML
    private void submit(ActionEvent event) {
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        boolean hasSpecialChar = p.matcher(userNameField.getText()).find();
        if (hasSpecialChar) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("You have entered an invalid character in the username");
            alert.setContentText("Only Alphanumeric characters are aloud.");
            alert.show();
        } else {
            Set<String> years = new HashSet<>();
            if (newUser) {
                User.createUser(userNameField.getText(), passwordField.getText(), adminCheckbox.isSelected());
            } else {
                User.updateUser(userNameField.getText(), passwordField.getText());

            }
            ArrayList<ArrayList<String>> yearUsers = new ArrayList<>();
            checkedUsers.forEach((year, users) -> {
                ArrayList<String> usersManage = new ArrayList<>();

                users.forEach((user) -> {
                    if (Objects.equals(user, "user@self")) {
                        user = userNameField.getText();
                    }
                    if (!user.isEmpty()) {
                        usersManage.add(user);

                    }
                });

                if (!usersManage.isEmpty()) {
                    years.add(year);
                    User yearUser = new User(userNameField.getText(), fullNameField.getText(), usersManage, years, adminCheckbox.isSelected(), groups.getOrDefault(year, 0));
                    if (newUser) {
                        yearUser.addToYear(year);
                    } else {
                        yearUser.updateYear(year);
                    }
                }
            });


            close();
        }
    }

    @FXML
    private void deleteUser(ActionEvent event) {

        final String user = userNameField.getText();
        if (user != DbInt.getUserName()) {


            Optional<Group> returnGroup = Optional.empty();
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("DELETE USER?");
            dialog.setHeaderText("This will delete ALL customers and data associated with this user.");
// Set the button types.
            ButtonType addGrp = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addGrp, ButtonType.CANCEL);

// Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField verifyUNameTF = new TextField();

            grid.add(new Label("Please re-type the username for verification:"), 0, 0);
            grid.add(verifyUNameTF, 1, 0);


// Enable/Disable login button depending on whether a username was entered.
            javafx.scene.Node deleteUserButton = dialog.getDialogPane().lookupButton(addGrp);
            deleteUserButton.setDisable(true);
            deleteUserButton.setStyle("fx-background-color: Red; fx-color: White");
// Do some validation (using the Java 8 lambda syntax).
            verifyUNameTF.textProperty().addListener((observable, oldValue, newValue) -> {
                if (Objects.equals(newValue, user)) {
                    deleteUserButton.setDisable(false);
                } else {
                    deleteUserButton.setDisable(true);
                }
            });

            dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
            Platform.runLater(() -> verifyUNameTF.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addGrp) {
                    return verifyUNameTF.getText();
                }
                return null;
            });

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(res -> {
                if (Objects.equals(res, user)) {

                    DbInt.getUserYears().forEach(year -> {
                        try (Connection con = DbInt.getConnection(year);
                             PreparedStatement prep = con.prepareStatement("DELETE FROM users WHERE userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                            prep.setString(1, user);
                            prep.execute();
                        } catch (SQLException e) {
                            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                        }
                        try (Connection con = DbInt.getConnection(year);
                             PreparedStatement prep = con.prepareStatement("UPDATE users SET uManage = REPLACE (uManage, ?, '')", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                            prep.setString(1, user);
                            prep.execute();
                        } catch (SQLException e) {
                            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                        }

                    });
                    try (Connection con = DbInt.getConnection("Commons");
                         PreparedStatement prep = con.prepareStatement("DELETE FROM Users WHERE userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                        prep.setString(1, user);
                        prep.execute();
                    } catch (SQLException e) {
                        LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                    }
                    try (Connection con = DbInt.getConnection();
                         PreparedStatement prep = con.prepareStatement("DELETE USER IF EXISTS `" + user + "`@`%`", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                        prep.setString(1, user);
                        prep.execute();
                    } catch (SQLException e) {
                        LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                    }

                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("You cannot delete yourself.");
            alert.showAndWait();
        }

        close();

    }

    public void initAddUser(Window parWindow) {
        parentWindow = parWindow;
        DbInt.getUserYears().forEach(year -> {
            TitledPane yPane;
            yPane = new TitledPane();

            ComboBox<TreeItemPair<String, Integer>> groupBox = new ComboBox<>();
            TreeView<TreeItemPair<String, String>> yearTView;
            CheckBoxTreeItem<TreeItemPair<String, String>> yearItem = new CheckBoxTreeItem<TreeItemPair<String, String>>(new TreeItemPair<>(year, ""));
            CheckBoxTreeItem<TreeItemPair<String, String>> selfItem = createUserTreeItem(new TreeItemPair<>("Themselves", "user@self"), year, yPane);
            yearItem.getChildren().add(selfItem);
            Group.getGroups(year).forEach(group -> {
                CheckBoxTreeItem<TreeItemPair<String, String>> groupItem = new CheckBoxTreeItem<TreeItemPair<String, String>>(new TreeItemPair<>(group.getName(), ""));
                group.getUsers().forEach(user -> {
                    CheckBoxTreeItem<TreeItemPair<String, String>> userItem = createUserTreeItem(new TreeItemPair<>(user.getFullName(), user.getUserName()), year, yPane);
                    groupItem.getChildren().add(userItem);
                });
                yearItem.getChildren().add(groupItem);
                try {
                    groupBox.getItems().add(new TreeItemPair<String, Integer>(group.getName(), group.getID()));
                } catch (Group.GroupNotFoundException e) {}
            });
            yearTView = new TreeView(yearItem);
            yearItem.setExpanded(true);
            yearTView.setCellFactory(CheckBoxTreeCell.forTreeView());
            groupBox.getSelectionModel().selectedItemProperty().addListener(observable -> {

                groups.put(year, groupBox.getSelectionModel().getSelectedItem().getValue());
            });
            BorderPane contents = new BorderPane(new VBox(10, new Label("Users to manage"), yearTView), new HBox(10, new Label("Group to be a part of"), groupBox), null, null, null);
            yPane.setText(year);
            yPane.setContent(contents);
            yearsPanel.getPanes().add(yPane);
            groupBox.getSelectionModel().selectFirst();
        });


    }

    private void addUser() {

    }

    /**
     * Create the dialog.
     */
    public void initAddUser(String userName, Window parWindow) {
        ArrayList<User> users = new ArrayList<User>();
        adminCheckbox.setDisable(true);

        newUser = false;
        parentWindow = parWindow;
        userNameField.setText(userName);
        userNameField.setEditable(false);
        deleteUserButton.setDisable(false);
        DbInt.getUserYears().forEach(year -> {
            TitledPane yPane;
            yPane = new TitledPane();

            ComboBox<TreeItemPair<String, Integer>> groupBox = new ComboBox<>();
            TreeView<TreeItemPair<String, String>> yearTView;
            CheckBoxTreeItem<TreeItemPair<String, String>> yearItem = new CheckBoxTreeItem<TreeItemPair<String, String>>(new TreeItemPair<>(year, ""));
            User currentUser = new User(userName, year, true);
            users.add(currentUser);

            Group.getGroups(year).forEach(group -> {
                CheckBoxTreeItem<TreeItemPair<String, String>> groupItem = new CheckBoxTreeItem<TreeItemPair<String, String>>(new TreeItemPair<>(group.getName(), ""));
                group.getUsers().forEach(user -> {
                    CheckBoxTreeItem<TreeItemPair<String, String>> userItem = createUserTreeItem(new TreeItemPair<>(user.getFullName(), user.getUserName()), year, yPane);
                    if (currentUser.getuManage().contains(user.getUserName())) {
                        userItem.setSelected(true);
/*                        checkedUsers.computeIfPresent(year, (k, v) -> {
                            v.add(user.getUserName());
                            return v;
                        });
                        checkedUsers.computeIfAbsent(year, k -> {
                            ArrayList<String> v = new ArrayList();
                            v.add(user.getUserName());
                            return v;
                        });
                        checkedFullName.compute(year, (k, v) -> {
                            ArrayList<String> vArray = new ArrayList();
                            vArray.addAll(v);
                            vArray.add(user.getFullName());
                            return vArray;
                        });*/
/*                        checkedFullName.computeIfAbsent(year, k -> {
                            ArrayList<String> v = new ArrayList();
                            v.add(user.getFullName());
                            return v;
                        });*/
                    }
                    groupItem.getChildren().add(userItem);
                });
                yearItem.getChildren().add(groupItem);
                try {
                    groupBox.getItems().add(new TreeItemPair<String, Integer>(group.getName(), group.getID()));
                    if (currentUser.getGroupId() == group.getID()) {
                        groupBox.getSelectionModel().selectLast();
                    } else if (currentUser.getGroupId() == 0) {
                        groupBox.getSelectionModel().selectFirst();

                    }

                } catch (Group.GroupNotFoundException e) {
                    e.printStackTrace();
                }
            });
            yearTView = new TreeView(yearItem);
            yearItem.setExpanded(true);
            yearTView.setCellFactory(CheckBoxTreeCell.forTreeView());
            yearTView.refresh();
            groupBox.getSelectionModel().selectedItemProperty().addListener(observable -> {

                groups.put(year, groupBox.getSelectionModel().getSelectedItem().getValue());
            });
            BorderPane contents = new BorderPane(new VBox(10, new Label("Users to manage"), yearTView), new HBox(10, new Label("Group to be a part of"), groupBox), null, null, null);
            yPane.setText(year);
            yPane.setContent(contents);

            yearsPanel.getPanes().add(yPane);
            if (checkedUsers.getOrDefault(year, new ArrayList<>()).isEmpty()) {
                yPane.setText(year + " - Disabled");

            } else {
                yPane.setText(year + " - " + arrayToCSV(checkedFullName.getOrDefault(year, new ArrayList<>())));

            }
            groups.put(year, groupBox.getSelectionModel().getSelectedItem().getValue());

        });
        User latestUser = users.get(users.size() - 1);
        fullNameField.setText(latestUser.getFullName());
        adminCheckbox.setSelected(latestUser.isAdmin());

    }

    private <T> CheckBoxTreeItem<TreeItemPair<String, String>> createUserTreeItem(TreeItemPair<String, String> value, String year, TitledPane titledPane) {

        CheckBoxTreeItem<TreeItemPair<String, String>> item = new CheckBoxTreeItem<TreeItemPair<String, String>>(value);
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
                    checkedFullName.computeIfPresent(year, (k, v) -> {
                        v.add(value.getKey());
                        return v;
                    });
                    checkedFullName.computeIfAbsent(year, k -> {
                        ArrayList<String> v = new ArrayList();
                        v.add(value.getKey());
                        return v;
                    });

                } else {
                    checkedUsers.compute(year, (k, v) -> {
                        v.remove(value.getValue());
                        return v;
                    });
                    checkedFullName.compute(year, (k, v) -> {
                        v.remove(value.getKey());
                        return v;
                    });
                }
                if (checkedUsers.getOrDefault(year, new ArrayList<>()).isEmpty()) {
                    titledPane.setText(year + " - Disabled");

                } else {
                    titledPane.setText(year + " - " + arrayToCSV(checkedFullName.getOrDefault(year, new ArrayList<>())));

                }

            });
        }

        return item;
    }

    private String arrayToCSV(Collection<String> array) {
        final String[] ret = {""};
        array.forEach(value -> {
            if (!ret[0].isEmpty()) {
                ret[0] = ret[0] + ", " + value;
            } else {
                ret[0] = value;
            }
        });
        return ret[0];
    }

}
