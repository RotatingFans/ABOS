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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class UsersGroupsAndYearsController {
    @FXML
    ListView<String> yearsList;
    @FXML
    ListView<User> allUsersList;
    @FXML
    ListView<User> yearUserList;
    @FXML
    ListView<Group> groupList;
    @FXML
    TreeView managedUserList;
    @FXML
    ComboBox<Group> userGroup;

    public UsersGroupsAndYearsController() {

    }

    /**
     * Initialize the contents of the frame.
     */
    public void initUsersGroupsAndYears(Window parWindow) throws Exception {
        yearsList.getItems().addAll(DbInt.getYears());
        allUsersList.getItems().addAll(DbInt.getUsers());
        allUsersList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        yearUserList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        yearsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Year year = new Year(newValue);
            yearUserList.getItems().clear();
            try {
                Collection<User> users = year.getUsers();
                yearUserList.getItems().addAll(users);
                allUsersList.getItems().removeIf(user -> {
                    final boolean[] ret = {false};
                    String uName = user.getUserName();
                    users.forEach(user1 -> {
                        if (Objects.equals(uName, user1.getUserName())) {
                            ret[0] = true;
                        }
                    });
                    return ret[0];
                });
            } catch (Exception ignored) {
            }
            groupList.getItems().clear();
            groupList.getItems().addAll(Group.getGroupCollection(newValue));

        });
        yearUserList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //enable bottom pane
            //Add&Move managed Users
            User currentUser = observable.getValue();
            if (currentUser != null) {
                CheckBoxTreeItem<Object> yearItem = new CheckBoxTreeItem<>("");
                userGroup.getItems().clear();
                userGroup.getItems().addAll(Group.getGroupCollection(yearsList.getSelectionModel().getSelectedItem()));
                Group.getGroups(yearsList.getSelectionModel().getSelectedItem()).forEach(group -> {
                    try {
                        if (currentUser.getGroupId() == group.getID()) {
                            userGroup.getSelectionModel().select(group);
                        }
                    } catch (Group.GroupNotFoundException ignored) {
                    }

                    CheckBoxTreeItem<Object> groupItem = new CheckBoxTreeItem<>(group.getName());
                    final int[] numSelected = {0};
                    group.getUsers().forEach(user -> {

                        CheckBoxTreeItem<Object> userItem = new CheckBoxTreeItem<>(user);
                        if (currentUser.getuManage().contains(user.getUserName())) {
                            //userItem.setSelected(true);
                            numSelected[0]++;
                        }
                        userItem.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                            User user2 = yearUserList.getSelectionModel().getSelectedItem();
                            if (t1) {

                                ArrayList<String> uManage = user2.getuManage();
                                if (!uManage.contains(((User) userItem.getValue()).getUserName())) {
                                    uManage.add(((User) userItem.getValue()).getUserName());
                                    user2.setuManage(uManage);
                                }
                            } else {
                                ArrayList<String> uManage = user2.getuManage();
                                while (uManage.contains(((User) userItem.getValue()).getUserName())) {

                                    uManage.remove(((User) userItem.getValue()).getUserName());
                                }
                                user2.setuManage(uManage);
                            }
                        });
                        groupItem.getChildren().add(userItem);

                    });

                    groupItem.setExpanded(true);
                    yearItem.getChildren().add(groupItem);

                });
                managedUserList.setRoot(yearItem);
                managedUserList.setShowRoot(false);
                yearItem.setExpanded(true);
                managedUserList.setCellFactory(CheckBoxTreeCell.forTreeView());
                Platform.runLater(() -> {
                    for (TreeItem<Object> group : yearItem.getChildren()) {
                        for (TreeItem<Object> UserItem : group.getChildren()) {
                            User user = (User) UserItem.getValue();
                            if (yearUserList.getSelectionModel().getSelectedItem().getuManage().contains(user.getUserName())) {
                                ((CheckBoxTreeItem<Object>) UserItem).setSelected(true);
                            }
                        }
                    }
                });


                managedUserList.refresh();
            }
        });
        yearUserList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super User>) change -> {

            managedUserList.setDisable(yearUserList.getSelectionModel().getSelectedIndices().size() > 1);
        });
        userGroup.getSelectionModel().selectedItemProperty().addListener((observableValue, group, t1) -> {
            yearUserList.getSelectionModel().getSelectedItems().forEach(user -> {
                try {
                    user.setGroupId(t1.getID());
                } catch (Group.GroupNotFoundException e) {
                    e.printStackTrace();
                }
            });
        });

    }



    @FXML
    private void saveUser(ActionEvent event) {
        //add users to year
        // update uManage
        //update group
        Year year = new Year(yearsList.getSelectionModel().getSelectedItem());
        ArrayList<User> usersInYear = new ArrayList<User>();
        try {
            usersInYear = year.getUsers();
        } catch (Exception ignored) {
        }
        usersInYear.removeIf(user -> {
            final boolean[] ret = {false};
            String uName = user.getUserName();
            yearUserList.getItems().forEach(user1 -> {
                if (Objects.equals(uName, user1.getUserName())) {
                    ret[0] = true;
                }
            });
            return ret[0];
        });
        yearUserList.getItems().forEach(user -> {

            user.updateYear(yearsList.getSelectionModel().getSelectedItem());


            // user.removeFromYear(yearsList.getSelectionModel().getSelectedItem());


        });
        usersInYear.forEach(user -> {
            user.removeFromYear(yearsList.getSelectionModel().getSelectedItem());
        });


        //saved
        //Close OR put up dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Saved");
        alert.setHeaderText("Changes Saved.");
        alert.show();
    }

    @FXML
    private void moveUserToYear(ActionEvent event) {
        allUsersList.getSelectionModel().getSelectedItems().forEach(user -> {
            yearUserList.getItems().add(user);
            allUsersList.getItems().removeIf(user1 -> {
                final boolean[] ret = {false};
                String uName = user.getUserName();
                if (Objects.equals(uName, user1.getUserName())) {
                    ret[0] = true;
                }

                return ret[0];
            });
            allUsersList.getItems().remove(user);
        });
    }
    @FXML
    private void removeUserFromYear(ActionEvent event) {
        yearUserList.getSelectionModel().getSelectedItems().forEach(user -> {
            allUsersList.getItems().add(user);
            yearUserList.getItems().removeIf(user1 -> {
                final boolean[] ret = {false};
                String uName = user.getUserName();
                if (Objects.equals(uName, user1.getUserName())) {
                    ret[0] = true;
                }

                return ret[0];
            });
        });
    }

    {
    /*@FXML
    private void editYear(ActionEvent event) {
        if (yearsList.getSelectionModel().getSelectedItem() != null) {
            String year = yearsList.getSelectionModel().getSelectedItem();
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Edit Year - " + year);

// Set the button types.
            ButtonType login = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(login, ButtonType.CANCEL);

// Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField yearNameField = new TextField();
            yearNameField.setText(year);


            grid.add(new Label("Name of Year:"), 0, 0);
            grid.add(yearNameField, 1, 0);


// Enable/Disable login button depending on whether a username was entered.
            javafx.scene.Node loginButton = dialog.getDialogPane().lookupButton(login);
            loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
            yearNameField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
            Platform.runLater(() -> yearNameField.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == login) {
                    return yearNameField.getText();
                }
                System.exit(0);
                return null;
            });

            Optional<String> result = dialog.showAndWait();

            result.ifPresent(yearName -> {


            });
        }
    }*/
    }

    @FXML
    private void deleteYear(ActionEvent event) {
        if (yearsList.getSelectionModel().getSelectedItem() != null) {
            String year = yearsList.getSelectionModel().getSelectedItem();
            Year yearObj = new Year(year);
            yearObj.deleteYear();

            yearsList.getItems().remove(year);
        }
    }

    @FXML
    private void addYear(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Year");

// Set the button types.
        ButtonType login = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(login, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField yearNameField = new TextField();
        yearNameField.setPromptText("Name of Year");


        grid.add(new Label("Name of Year:"), 0, 0);
        grid.add(yearNameField, 1, 0);


// Enable/Disable login button depending on whether a username was entered.
        javafx.scene.Node loginButton = dialog.getDialogPane().lookupButton(login);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        yearNameField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> yearNameField.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == login) {
                return yearNameField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(yearName -> {
            ObservableList<Product.formattedProductProps> prodItems = FXCollections.emptyObservableList();
            Collection<Year.category> rowCats = new ArrayList<>();
            Year yearToCreate = new Year(yearName);

            yearToCreate.CreateDb(prodItems, rowCats);
            ArrayList<String> years = DbInt.getUserYears();
            ArrayList<String> usersManage = new ArrayList<>();
            usersManage.add("");
            years.add(yearName);
            Set<String> yearSet = new HashSet<>(years);

            User latestUser = DbInt.getCurrentUser();
            latestUser.setYears(yearSet);
            latestUser.addToYear(yearName);
            yearsList.getItems().add(yearName);

        });
    }

    @FXML
    private void deleteUser(ActionEvent event) {
        if (allUsersList.getSelectionModel().getSelectedItem() != null) {
            User oldUser = allUsersList.getSelectionModel().getSelectedItem();
            String user = oldUser.getUserName();
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
                        try (Connection con = DbInt.getConnection("Commons");
                             //PreparedStatement prep = con.prepareStatement("DROP USER IF EXISTS `" + user + "`@`%`", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                             PreparedStatement prep = con.prepareStatement("DROP USER `" + user + "`@`%`", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                            prep.execute();
                        } catch (SQLException e) {
                            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                        }
                        allUsersList.getItems().remove(oldUser);

                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("You cannot delete yourself.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void editUser(ActionEvent event) {
        if (allUsersList.getSelectionModel().getSelectedItem() != null) {
            User user = allUsersList.getSelectionModel().getSelectedItem();
            User oldUser = user;
            Dialog<Pair<Pair<String, Boolean>, Pair<String, String>>> dialog = new Dialog<>();
            dialog.setTitle("Edit User - " + user.toString());

// Set the button types.
            ButtonType login = new ButtonType("Edit", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(login, ButtonType.CANCEL);

// Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField userNameTextField = new TextField();
            userNameTextField.setText(user.getUserName());
            userNameTextField.setEditable(false);
            TextField fullNameField = new TextField();
            fullNameField.setText(user.getFullName());
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            CheckBox adminCheckBox = new CheckBox("Admin?");
            adminCheckBox.setSelected(user.isAdmin());

            grid.add(new Label("Username:"), 0, 0);
            grid.add(userNameTextField, 1, 0);
            grid.add(new Label("Full Name:"), 0, 1);
            grid.add(fullNameField, 1, 1);
            grid.add(new Label("Password:"), 0, 2);
            grid.add(passwordField, 1, 2);
            grid.add(adminCheckBox, 1, 3);



            dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
            Platform.runLater(() -> userNameTextField.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == login) {
                    return new Pair<Pair<String, Boolean>, Pair<String, String>>(new Pair<>(fullNameField.getText(), adminCheckBox.isSelected()), new Pair<>(userNameTextField.getText(), passwordField.getText()));
                }
                return null;
            });

            Optional<Pair<Pair<String, Boolean>, Pair<String, String>>> result = dialog.showAndWait();

            result.ifPresent(userInfo -> {
                Pattern p = Pattern.compile("[^a-zA-Z0-9]");
                String uName = userInfo.getValue().getKey();
                String pass = userInfo.getValue().getValue();
                String fName = userInfo.getKey().getKey();
                Boolean admin = userInfo.getKey().getValue();
                boolean hasSpecialChar = p.matcher(userInfo.getValue().getKey()).find();
                if (hasSpecialChar) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("");
                    alert.setHeaderText("You have entered an invalid character in the username");
                    alert.setContentText("Only Alphanumeric characters are aloud.");
                    alert.show();
                } else {
                    Set<String> years = new HashSet<>();
                    //User.createUser(uName, pass, fullNameField.getText(), admin);
                    User.updateUser(uName, pass);
                    user.setFullName(fName);
                    user.setAdmin(admin);
                    user.getYears().forEach(year -> {
                        user.updateYear(year);
                    });
                    allUsersList.getItems().remove(oldUser);
                    allUsersList.getItems().add(user);

                    ArrayList<ArrayList<String>> yearUsers = new ArrayList<>();


                }

            });
        }
    }

    @FXML
    private void addUser(ActionEvent event) {
        Dialog<Pair<Pair<String, Boolean>, Pair<String, String>>> dialog = new Dialog<>();
        dialog.setTitle("Add User");

// Set the button types.
        ButtonType login = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(login, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField userNameTextField = new TextField();
        userNameTextField.setPromptText("Username");
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        CheckBox adminCheckBox = new CheckBox("Admin?");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(userNameTextField, 1, 0);
        grid.add(new Label("Full Name:"), 0, 1);
        grid.add(fullNameField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(adminCheckBox, 1, 3);

// Enable/Disable login button depending on whether a username was entered.
        javafx.scene.Node loginButton = dialog.getDialogPane().lookupButton(login);
        loginButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> loginButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> userNameTextField.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == login) {
                return new Pair<Pair<String, Boolean>, Pair<String, String>>(new Pair<>(fullNameField.getText(), adminCheckBox.isSelected()), new Pair<>(userNameTextField.getText(), passwordField.getText()));
            }
            return null;
        });

        Optional<Pair<Pair<String, Boolean>, Pair<String, String>>> result = dialog.showAndWait();

        result.ifPresent(userInfo -> {
            Pattern p = Pattern.compile("[^a-zA-Z0-9]");
            String uName = userInfo.getValue().getKey();
            String pass = userInfo.getValue().getValue();
            String fName = userInfo.getKey().getKey();
            Boolean admin = userInfo.getKey().getValue();
            boolean hasSpecialChar = p.matcher(userInfo.getValue().getKey()).find();
            if (hasSpecialChar) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("");
                alert.setHeaderText("You have entered an invalid character in the username");
                alert.setContentText("Only Alphanumeric characters are aloud.");
                alert.show();
            } else {
                Set<String> years = new HashSet<>();
                allUsersList.getItems().add(User.createUser(uName, pass, fName, admin));
     /*           } else {
                    User.updateUser(uName, pass);

                }*/
                ArrayList<ArrayList<String>> yearUsers = new ArrayList<>();


            }

        });
    }

    @FXML
    private void deleteGroup(ActionEvent event) {
        if (groupList.getSelectionModel().getSelectedItem() != null) {
            Group group = groupList.getSelectionModel().getSelectedItem();
        }
    }

    @FXML
    private void editGroup(ActionEvent event) {
        if (groupList.getSelectionModel().getSelectedItem() != null) {
            Group group = groupList.getSelectionModel().getSelectedItem();
            Group newGroup = AddGroup.addGroup(yearsList.getSelectionModel().getSelectedItem(), group.getName());
            groupList.getItems().remove(group);
            groupList.getItems().add(newGroup);
            userGroup.getItems().remove(group);
            userGroup.getItems().add(newGroup);
        }
    }

    @FXML
    private void addGroup(ActionEvent event) {
        Group newGroup = AddGroup.addGroup(yearsList.getSelectionModel().getSelectedItem());
        groupList.getItems().add(newGroup);
        userGroup.getItems().add(newGroup);

    }
}

