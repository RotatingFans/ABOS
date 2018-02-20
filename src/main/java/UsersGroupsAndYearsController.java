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
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Pair;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
    @FXML
    Tab productsTab;
    Window parentWindow;
    @FXML
    private TableView<Product.formattedProductProps> ProductTable;
    @FXML
    private TextField itemTb;
    @FXML
    private TextField sizeTb;
    @FXML
    private TextField rateTb;
    @FXML
    private TextField idTb;
    //private final JDialog parent;
    private Collection<Year.category> rowsCats = new ArrayList<Year.category>();
    private ObservableList<String> categoriesTb = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> categoriesCmbx;
    //private DefaultTableModel tableModel;
    private boolean newYear = false;
    private ObservableList<Product.formattedProductProps> data = FXCollections.observableArrayList();
    public UsersGroupsAndYearsController() {

    }

    /**
     * Initialize the contents of the frame.
     */
    public void initUsersGroupsAndYears(Window parWindow) throws Exception {
        parentWindow = parWindow;

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
            initProductsTab();

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
        yearUserList.getSelectionModel().clearSelection();
        managedUserList.setRoot(new TreeItem());
        userGroup.getItems().clear();
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

    private void initProductsTab() {
        newYear = false;
        Year thisYear = new Year(yearsList.getSelectionModel().getSelectedItem());
        //ProductTable = new TableView<>();

        categoriesCmbx.getItems().clear();
        categoriesTb.clear();
        categoriesTb.add("");
        String browse = "Add Category";
        rowsCats.clear();
        thisYear.getCategories().forEach((category) -> {
            categoriesTb.add(category.catName);
            rowsCats.add(category);
        });

        categoriesTb.add(browse);
        categoriesCmbx.getItems().setAll(categoriesTb);
        ProductTable.getColumns().clear();
        ProductTable.getItems().clear();
        String[][] columnNames = {{"ID", "productID"}, {"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPriceString"}};
        //for (String[] column : columnNames) {
        {
            javafx.scene.control.TableColumn<Product.formattedProductProps, String> idCol = new javafx.scene.control.TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("productID"));
            idCol.setCellFactory(TextFieldTableCell.forTableColumn());
            idCol.setOnEditCommit(t -> {
                t.getRowValue().productID.set(t.getNewValue());
                data.get(t.getTablePosition().getRow()).productID.set(t.getNewValue());
                t.getTableView().refresh();
            });
            ProductTable.getColumns().add(idCol);
            //}
        }
        {
            javafx.scene.control.TableColumn<Product.formattedProductProps, String> nameCol = new javafx.scene.control.TableColumn<>("Item");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            nameCol.setOnEditCommit(t -> {
                t.getRowValue().productName.set(t.getNewValue());
                data.get(t.getTablePosition().getRow()).productName.set(t.getNewValue());
                t.getTableView().refresh();
            });
            ProductTable.getColumns().add(nameCol);
            //}
        }
        {
            javafx.scene.control.TableColumn<Product.formattedProductProps, String> sizeCol = new javafx.scene.control.TableColumn<>("Size");
            sizeCol.setCellValueFactory(new PropertyValueFactory<>("productSize"));
            sizeCol.setCellFactory(TextFieldTableCell.forTableColumn());
            sizeCol.setOnEditCommit(t -> {
                t.getRowValue().productSize.set(t.getNewValue());
                data.get(t.getTablePosition().getRow()).productSize.set(t.getNewValue());
                t.getTableView().refresh();
            });
            ProductTable.getColumns().add(sizeCol);
            //}
        }
        {
            javafx.scene.control.TableColumn<Product.formattedProductProps, String> unitCostCol = new javafx.scene.control.TableColumn<>("Price/Item");
            unitCostCol.setCellValueFactory(new PropertyValueFactory<>("productUnitPriceString"));
            unitCostCol.setCellFactory(TextFieldTableCell.forTableColumn());
            unitCostCol.setOnEditCommit(t -> {
                try {
                    BigDecimal unitPrice = new BigDecimal(t.getNewValue());
                    t.getRowValue().productUnitPriceString.set(t.getNewValue());
                    t.getRowValue().productUnitPrice.set(new BigDecimal(t.getNewValue()));
                    data.get(t.getTablePosition().getRow()).productUnitPriceString.set(t.getNewValue());
                    data.get(t.getTablePosition().getRow()).productUnitPrice.set(new BigDecimal(t.getNewValue()));
                    t.getTableView().refresh();
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid number");
                    alert.setHeaderText("You have entered an invalid number.");
                    alert.show();
                    t.getRowValue().productUnitPriceString.set(t.getOldValue());
                }


            });
            ProductTable.getColumns().add(unitCostCol);
            //}
        }


        javafx.scene.control.TableColumn<Product.formattedProductProps, String> categoryColumn = new javafx.scene.control.TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));

        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categoriesTb));

        categoryColumn.setOnEditCommit(t -> {
            String newVal = catCmbxChanged(t.getNewValue());

            t.getRowValue().productCategory.set(newVal);
            data.get(t.getTablePosition().getRow()).productCategory.set(newVal);

        });
        ProductTable.getColumns().add(categoryColumn);
        // boolean updateDb = true;
        fillTable();
        productsTab.setDisable(false);
    }

    @FXML
    private void submit(ActionEvent event) {


        updateDb(yearsList.getSelectionModel().getSelectedItem());
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Saved");
        alert.setHeaderText("Changes Saved.");
        alert.show();

    }

    @FXML
    private void tableFrmXML(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files", "*.xml", "*.XML");
        chooser.getExtensionFilters().add(filter);

        chooser.setSelectedExtensionFilter(filter);
//        logoLoc.setText(chooser.showOpenDialog(settings).getAbsolutePath());
        File xmlFile = chooser.showOpenDialog(parentWindow);
        if (xmlFile != null) {
            String path = xmlFile.getAbsolutePath();
            createTable(path);
        }
    }

    private void convert(String csvLoc, String xmlLoc) {
        List<String> headers = new ArrayList<>(5);


        File file = new File(csvLoc);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

            Document newDoc = domBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("LawnGarden");
            newDoc.appendChild(rootElement);

            int line = 0;

            String text;
            while ((text = reader.readLine()) != null) {

                StringTokenizer st = new StringTokenizer(text, ";", false);
                String[] rowValues = new String[st.countTokens()];
                int index = 0;
                while (st.hasMoreTokens()) {

                    String next = st.nextToken();
                    rowValues[index] = next;
                    index++;

                }

                //String[] rowValues = text.split(",");

                if (line == 0) { // Header row
                    Collections.addAll(headers, rowValues);
                } else { // Data row
                    Element rowElement = newDoc.createElement("Products");
                    rootElement.appendChild(rowElement);
                    Attr attr = newDoc.createAttribute("id");
                    attr.setValue(Integer.toString(line - 1));
                    rowElement.setAttributeNode(attr);
                    for (int col = 0; col < headers.size(); col++) {
                        String header = headers.get(col);
                        String value;

                        if (col < rowValues.length) {
                            value = rowValues[col].trim();
                        } else {
                            // ?? Default value
                            value = "";
                        }

                        Element curElement = newDoc.createElement(header);
                        curElement.appendChild(newDoc.createTextNode(value));
                        rowElement.appendChild(curElement);
                    }
                }
                line++;
            }

            OutputStreamWriter osw = null;

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                osw = new OutputStreamWriter(baos);

                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                //aTransformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                Source src = new DOMSource(newDoc);
                Result result = new StreamResult(osw);
                aTransformer.transform(src, result);

                osw.flush();
                //System.out.println(new String(baos.toByteArray()));

                try (OutputStream outStream = new FileOutputStream(xmlLoc)) {// writing bytes in to byte output stream

                    baos.writeTo(outStream);
                } catch (IOException e) {
                    LogToFile.log(e, Severity.SEVERE, "Error writing XML file. Please try again.");
                }


            } catch (Exception exp) {
                LogToFile.log(exp, Severity.SEVERE, "Error writing XML file. Please try again.");
            } finally {
                try {
                    osw.close();
                } catch (IOException e) {
                    LogToFile.log(e, Severity.SEVERE, "Error closing file. Please try again.");
                }

            }
        } catch (Exception e) {
            LogToFile.log(e, Severity.SEVERE, "Error reading CSV file. Ensure the path exists, and the software has permission to read it.");
        }
    }

    @FXML
    private void csvToXml(ActionEvent event) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("CSV to XML conversion");

// Set the button types.
        ButtonType convertButtonType = new ButtonType("Convert", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(convertButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField csvLoc = new TextField();
        csvLoc.setPromptText("CSV file Location");
        TextField xmlLoc = new TextField();
        xmlLoc.setPromptText("XML Location");
        Button getCsvLoc = new Button("...");
        getCsvLoc.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files", "*.csv", "*.CSV");
            chooser.getExtensionFilters().add(filter);
            chooser.setSelectedExtensionFilter(filter);
            File csv = chooser.showOpenDialog(grid.getScene().getWindow());
            if (csv != null) {
                String path = csv.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) {
                    path += ".csv";
                }
                csvLoc.setText(path);
            }
        });
        Button getXmlLoc = new Button("...");
        getXmlLoc.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files", "*.xml", "*.XML");
            chooser.getExtensionFilters().add(filter);
            chooser.setSelectedExtensionFilter(filter);
            File XML = chooser.showSaveDialog(grid.getScene().getWindow());
            if (XML != null) {
                String path = XML.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".xml")) {
                    path += ".xml";
                }
                xmlLoc.setText(path);
            }
        });
        grid.add(new Label("CSV file Location:"), 0, 0);
        grid.add(csvLoc, 1, 0);
        grid.add(getCsvLoc, 2, 0);
        grid.add(new Label("XML Location:"), 0, 1);
        grid.add(xmlLoc, 1, 1);
        grid.add(getXmlLoc, 2, 1);


// Enable/Disable login button depending on whether a username was entered.
        javafx.scene.Node convertButton = dialog.getDialogPane().lookupButton(convertButtonType);
        convertButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        csvLoc.textProperty().addListener((observable, oldValue, newValue) -> convertButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> csvLoc.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == convertButtonType) {
                return new Pair<>(csvLoc.getText(), xmlLoc.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(fileLocations -> {
            convert(fileLocations.getKey(), fileLocations.getValue());
            createTable(fileLocations.getValue());
        });




/*        CSV2XML csv = new CSV2XML(parent);
        String xmlFile = csv.getXML();
        if (!xmlFile.isEmpty()) {
            createTable(xmlFile);
        }*/
    }

    @FXML
    private void catCmbxChanged(ActionEvent event) {
        if (Objects.equals(categoriesCmbx.getSelectionModel().getSelectedItem(), "Add Category")) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add new category");

// Set the button types.
            ButtonType addCat = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addCat, ButtonType.CANCEL);

// Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField catName = new TextField();
            catName.setPromptText("Category Name");
            DatePicker catDate = new DatePicker(LocalDate.now());
            catDate.setPromptText("Category Due Date");

            grid.add(new Label("Category Name:"), 0, 0);
            grid.add(catName, 1, 0);
            grid.add(new Label("Category Due Date:"), 0, 1);
            grid.add(catDate, 1, 1);


// Enable/Disable login button depending on whether a username was entered.
            javafx.scene.Node addCatButton = dialog.getDialogPane().lookupButton(addCat);
            addCatButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
            catName.textProperty().addListener((observable, oldValue, newValue) -> addCatButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
            Platform.runLater(() -> catName.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addCat) {
                    return new Pair<String, String>(catName.getText(), catDate.getValue().toString());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(category -> {
                rowsCats.add(new Year.category(category.getKey(), category.getValue()));
                Platform.runLater(() -> refreshCmbx());

            });


        }

    }

    private String catCmbxChanged(String newVal) {
        final Year.category newCat = new Year.category("", "");
        if (Objects.equals(newVal, "Add Category")) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add new category");

// Set the button types.
            ButtonType addCat = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addCat, ButtonType.CANCEL);

// Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField catName = new TextField();
            catName.setPromptText("Category Name");
            DatePicker catDate = new DatePicker(LocalDate.now());
            catDate.setPromptText("Category Due Date");

            grid.add(new Label("Category Name:"), 0, 0);
            grid.add(catName, 1, 0);
            grid.add(new Label("Category Due Date:"), 0, 1);
            grid.add(catDate, 1, 1);


// Enable/Disable login button depending on whether a username was entered.
            javafx.scene.Node addCatButton = dialog.getDialogPane().lookupButton(addCat);
            addCatButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
            catName.textProperty().addListener((observable, oldValue, newValue) -> addCatButton.setDisable(newValue.trim().isEmpty()));

            dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
            Platform.runLater(() -> catName.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addCat) {
                    return new Pair<String, String>(catName.getText(), catDate.getValue().toString());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(category -> {
                newCat.catName = category.getKey();
                newCat.catDate = category.getValue();
                rowsCats.add(newCat);
                Platform.runLater(() -> refreshCmbx());

            });


        }

        return newCat.catName;
    }

    @FXML
    private void addBtnPressed(ActionEvent event) {
        int count = ProductTable.getItems().size() + 1;
        data.add(new Product.formattedProductProps(0, idTb.getText(), itemTb.getText(), sizeTb.getText(), new BigDecimal(rateTb.getText()), categoriesCmbx.getSelectionModel().getSelectedItem(), 0, BigDecimal.ZERO));
        ProductTable.setItems(data);
    }

   /* @FXML
    private void submit(ActionEvent event) {
        DbInt.getUserYears().forEach(year -> {
            if (Objects.equals(year, yearText.getText())) {
                newYear = false;
            }
        });
        if (chkboxCreateDatabase.isSelected() && newYear) {
            CreateDb();
        } else if (newYear) {
            addYear();
            updateDb(yearText.getText());
        } else {
            updateDb(yearText.getText());
        }

        close();
    }*/

    private void refreshCmbx() {
        categoriesCmbx.getItems().clear();
        categoriesTb.clear();
        categoriesTb.add("");
        String browse = "Add Category";

        rowsCats.forEach(cat -> categoriesTb.add(cat.catName));


        categoriesTb.add(browse);
        categoriesCmbx.getItems().setAll(categoriesTb);

    }

    private void updateDb(String year) {
        Year yearToUpdate = new Year(year);
        yearToUpdate.updateDb(year, ProductTable.getItems(), rowsCats);
    }

    /**
     * Parses XML file to insert into products table on screen
     *
     * @param FLoc the location of the XML file
     */
    private void createTable(String FLoc) {
        try {

            File fXmlFile = new File(FLoc);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nListCats = doc.getElementsByTagName("Categories");

            // Collection<String[]> rowsCatsL = new ArrayList<>();

            for (int temp = 0; temp < nListCats.getLength(); temp++) {

                Node nNode = nListCats.item(temp);


                if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    rowsCats.add(new Year.category(eElement.getElementsByTagName("CategoryName").item(0).getTextContent(), eElement.getElementsByTagName("CategoryDate").item(0).getTextContent()));
                }
            }
            //rowsCats = rowsCatsL;
            NodeList nList = doc.getElementsByTagName("Products");

            Object[][] rows = new Object[nList.getLength()][5];

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


                    //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
                    Product.formattedProductProps prodProps = new Product.formattedProductProps(0, eElement.getElementsByTagName(
                            "ProductID").item(0).getTextContent(),
                            eElement.getElementsByTagName("ProductName").item(0).getTextContent(),
                            eElement.getElementsByTagName("Size").item(0).getTextContent(),
                            new BigDecimal(eElement.getElementsByTagName("UnitCost").item(0).getTextContent()),
                            (eElement.getElementsByTagName("Category").item(0) != null) ? eElement.getElementsByTagName("Category").item(0).getTextContent() : "",
                            0,
                            BigDecimal.ZERO
                    );
                    data.add(prodProps);
                    ProductTable.setItems(data);

                }


            }
        } catch (Exception e) {
            LogToFile.log(e, Severity.SEVERE, "Error Converting XML file to table. Please try again or contact support.");
        }
        refreshCmbx();
    }

    /**
     * Creates an XML file from the table
     *
     * @param SavePath Path to save the created XML file
     */
    private void createXML(String SavePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;

            docBuilder = docFactory.newDocumentBuilder();


            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("LawnGarden");
            doc.appendChild(rootElement);
            Iterable<Year.category> caters;
            caters = rowsCats;
            int[] i = {0};
            //caters = getCategories(yearText.getText());
            caters.forEach(cat -> {
                        Element cats = doc.createElement("Categories");
                        rootElement.appendChild(cats);
                        Attr attr = doc.createAttribute("id");
                        attr.setValue(Integer.toString(i[0]));
                        cats.setAttributeNode(attr);


                        //CateName elements
                        Element ProductID = doc.createElement("CategoryName");
                        ProductID.appendChild(doc.createTextNode(cat.catName));
                        cats.appendChild(ProductID);

                        //CatDate elements
                        Element ProductName = doc.createElement("CategoryDate");
                        ProductName.appendChild(doc.createTextNode(cat.catDate));
                        cats.appendChild(ProductName);
                        i[0]++;
                    }
            );

            // staff elements


            // set attribute to staff element
            for (int i2 = 0; i2 < ProductTable.getItems().size(); i2++) {

                Element staff = doc.createElement("Products");
                rootElement.appendChild(staff);
                Attr attr = doc.createAttribute("id");
                attr.setValue(Integer.toString(i2));
                staff.setAttributeNode(attr);

                //ProductID elements
                Element ProductID = doc.createElement("ProductID");
                ProductID.appendChild(doc.createTextNode(ProductTable.getItems().get(i2).getProductID()));
                staff.appendChild(ProductID);

                // Prodcut Name elements
                Element ProductName = doc.createElement("ProductName");
                ProductName.appendChild(doc.createTextNode(ProductTable.getItems().get(i2).getProductName()));
                staff.appendChild(ProductName);

                // Unit COst elements
                Element UnitCost = doc.createElement("UnitCost");
                UnitCost.appendChild(doc.createTextNode(ProductTable.getItems().get(i2).getProductUnitPrice().toPlainString()));
                staff.appendChild(UnitCost);

                // Size elements
                Element Size = doc.createElement("Size");
                Size.appendChild(doc.createTextNode(ProductTable.getItems().get(i2).getProductSize()));
                staff.appendChild(Size);

                // Category elements

                String cat = (ProductTable.getItems().get(i2).getProductCategory() != null) ? ProductTable.getItems().get(i2).getProductCategory() : "";
                Element category = doc.createElement("Category");
                category.appendChild(doc.createTextNode(cat));
                staff.appendChild(category);
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source source = new DOMSource(doc);
            Result result = new StreamResult(new FileOutputStream(SavePath));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            //System.out.println("File saved!");
        } catch (ParserConfigurationException e) {
            LogToFile.log(e, Severity.SEVERE, "Error creating XML file: Parser error. Contact support.");
        } catch (TransformerException e) {
            LogToFile.log(e, Severity.SEVERE, "Error creating XML file: Parser Error. Contact support.");
        } catch (FileNotFoundException e) {
            LogToFile.log(e, Severity.SEVERE, "Error creating XML file: Error writing to file. Make sure the directory is readable by the software.");
        }
    }

    /**
     * Fills the table from a DB table
     */
    private void fillTable() {
        String year = yearsList.getSelectionModel().getSelectedItem();
        Year yearInfo = new Year(year);

        Product.formattedProduct[] productArray = yearInfo.getAllProducts();
        Object[][] rows = new Object[productArray.length][6];
        // data = FXCollections.observableArrayList();

        int i = 0;
        for (Product.formattedProduct productOrder : productArray) {
            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
            Product.formattedProductProps prodProps = new Product.formattedProductProps(productOrder.productKey, productOrder.productID, productOrder.productName, productOrder.productSize, productOrder.productUnitPrice, productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
            data.add(prodProps);
            i++;
        }

        ProductTable.setItems(data);

    }

    @FXML
    private void tablefromDb(ActionEvent event) {

        fillTable();
    }

    @FXML
    private void xmlFromTable(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files", "*.xml", "*.XML");
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        File XML = chooser.showSaveDialog(parentWindow);
        if (XML != null) {
            String path = XML.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xml")) {
                path += ".xml";
            }
            createXML(path);
        }
    }
}

