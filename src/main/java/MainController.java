/*
 * Copyright (c) Patrick Magauran 2018.
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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")

public class MainController {
    public final EventType closeEvent = new EventType("Close");
    private boolean isRightClick;
    // the FXML annotation tells the loader to inject this variable before invoking initialize.
    @FXML
    private TreeView<TreeItemPair<String, Pair<String, Object>>> selectNav;
    // @FXML
    //public Pane tabPane;
    //@FXML
    // public Tab tab1;
    @FXML
    private TabPane tabPane2;
    private Boolean isAdmin = false;
    @FXML
    private FlowPane namePane;
    @FXML
    private BorderPane sidePane;

    private void login(Boolean failed) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Login");

// Set the button types.
        ButtonType login = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(login, ButtonType.CANCEL);
        if (failed) {
            dialog.setHeaderText("Invalid Username/Password");
        }
// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField userNameTextField = new TextField();
        userNameTextField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(userNameTextField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);


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
                return new Pair<String, String>(userNameTextField.getText(), passwordField.getText());
            }
            System.exit(0);
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(userPass -> {
            if (!DbInt.verifyLogin(userPass)) {
                login(true);
            }
            if (!DbInt.testConnection()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Verify Databse?");
                alert.setHeaderText("Failed to connect to the databasse");
                alert.setContentText("Would you like to open the settings window to verify the connection?");

                ButtonType buttonTypeOne = new ButtonType("Open");
                ButtonType buttonTypeTwo = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

                Optional<ButtonType> res = alert.showAndWait();
                if (res.get() == buttonTypeOne) {
                    new Settings();

                }
            }

        });

    }

    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    public void initialize(Stage stage, Application.Parameters parameters) {
        Map<String, String> params = parameters.getNamed();
        if (params.containsKey("username") && params.containsKey("password")) {
            if (!DbInt.verifyLogin(new Pair<>(params.get("username"), params.get("password")))) {
                login(true);
            }
        } else {
            login(false);
        }
        // DbInt.username = "tw";
        ArrayList<String> years = DbInt.getUserYears();
        if (!years.isEmpty()) {
            User latestUser = DbInt.getCurrentUser();
            stage.setTitle("ABOS - " + latestUser.getFullName());
        }
        //loadTreeItems("initial 1", "initial 2", "initial 3");
        fillTreeView();

        selectNav.setShowRoot(false);
        selectNav.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isSecondaryButtonDown()) {
                isRightClick = true;

            }
            if (event.getClickCount() == 2) {

                Platform.runLater(() -> {
                    TreeItemPair<String, Pair<String, Object>> newValue = selectNav.getSelectionModel().getSelectedItem().getValue();
                    if (isRightClick) {
                        //reset the flag
                        isRightClick = false;
                    } else if (newValue != null && !Objects.equals(newValue.getValue().getValue(), "RootNode")) {
                        Pane newPane = null;
                        FXMLLoader loader;
                        String tabTitle;

                        // load new pane
                        switch (newValue.getValue().getKey()) {
                            case "Window": {
                                switch (newValue.getKey()) {
                                    case "Add Customer": {
                                        loader = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));
                                        try {
                                            newPane = loader.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        AddCustomerController addCustCont = loader.getController();
                                        Pair<String, String> data = (Pair<String, String>) newValue.getValue().getValue();
                                        Tab tab = addTab(newPane, "Add Customer - " + data.getKey());

                                        addCustCont.initAddCust(data.getKey(), this, tab, data.getValue(), selectNav.getSelectionModel().getSelectedItem().getParent());

                                        break;
                                    }
                                    case "Users Groups & Years": {
                                        new UsersGroupsAndYears(getWindow());

                                        break;
                                    }
                                    case "Reports":
                                        new Reports(tabPane2.getScene().getWindow());
                                        break;
                                    case "View Map":
                                        loader = new FXMLLoader(getClass().getResource("UI/Map.fxml"));
                                        try {
                                            newPane = loader.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        MapController mapCont = loader.getController();
                                        mapCont.initMap(this);
                                        addTab(newPane, "Map");

                                        break;
                                    case "Add Year":
                                        new AddYear(getWindow());
                                        break;
                                    case "Add User":
                                        new AddUser(getWindow());
                                        break;
                                    case "Add Group":
                                        AddGroup.addGroup(newValue.getValue().getValue().toString());
                                        break;
                                    case "Settings":
                                        new Settings(tabPane2.getScene().getWindow());
                                        break;
                                }
                                break;
                            }

                            case "Year": {
                                loader = new FXMLLoader(getClass().getResource("UI/Year.fxml"));
                                try {
                                    newPane = loader.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                YearController yearCont = loader.getController();
                                yearCont.initYear(newValue.getKey(), this);
                                tabTitle = ("Year View - " + newValue.getKey());
                                addTab(newPane, tabTitle);
                                break;
                            }
                            case "Customer": {
                                loader = new FXMLLoader(getClass().getResource("UI/Customer.fxml"));
                                try {
                                    newPane = loader.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                CustomerController customerCont = loader.getController();
                                customerCont.initCustomer((Customer) newValue.getValue().getValue(), this);
                                tabTitle = ("Customer View - " + newValue.getKey() + " - " + ((Customer) newValue.getValue().getValue()).getYear());
                                addTab(newPane, tabTitle);
                                break;
                            }
                            case "Group": {
                                AddGroup.addGroup(newValue.getValue().getValue().toString(), newValue.getKey());

                                break;
                            }
                            case "UserCustomerView": {
                                loader = new FXMLLoader(getClass().getResource("UI/Year.fxml"));
                                try {
                                    newPane = loader.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                YearController yearCont = loader.getController();
                                yearCont.initYear(newValue.getValue().getValue().toString(), newValue.getKey(), this);
                                tabTitle = ("Year View - " + newValue.getValue().getValue() + " - " + newValue.getKey());
                                addTab(newPane, tabTitle);
                                break;
                            }
                            case "User": {
                                new AddUser(getWindow(), newValue.getValue().getValue().toString());
                                break;
                            }


                        }
                    }
                });
            }
        });

        selectNav.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            
        });
        selectNav.setCellFactory(p -> new TreeCellImpl());


    }

    private ContextMenu createContextMenu(TreeItem<TreeItemPair<String, Pair<String, Object>>> cell) {
        ContextMenu cm = new ContextMenu();
        ContextMenu cmContent = new ContextMenu();
        Pane newPane = null;
        FXMLLoader loader;
        Pane finalNewPane;
        // String tabTitle = "";
        if (cell != null && cell.getValue() != null && !Objects.equals(cell.getValue().getValue().getKey(), "RootNode")) {
            switch (cell.getValue().getValue().getKey()) {
                case "Window": {
                    switch (cell.getValue().getKey()) {
                        case "Add Customer": {
                            cmContent = createContextMenuContent(
                                    //Open
                                    () -> {
                                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));

                                        Pane NewPane2 = null;
                                        try {
                                            NewPane2 = loader2.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        AddCustomerController addCustCont = loader2.getController();
                                        Pair<String, String> data = (Pair<String, String>) cell.getValue().getValue().getValue();

                                        String tabTitle = ("Add Customer - " + data.getKey());
                                        addCustCont.initAddCust(data.getKey(), this, openTabInCurrentWindow(NewPane2, tabTitle), data.getValue(), cell.getParent());

                                    }, () -> { //Open In New Tab
                                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));

                                        Pane NewPane2 = null;
                                        try {
                                            NewPane2 = loader2.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        Pair<String, String> data = (Pair<String, String>) cell.getValue().getValue().getValue();

                                        AddCustomerController addCustCont = loader2.getController();
                                        String tabTitle = ("Add Customer - " + data.getKey());
                                        addCustCont.initAddCust(data.getKey(), this, addTab(NewPane2, tabTitle), data.getValue(), cell.getParent());
                                    }, () -> { //Open In New Window
                                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));

                                        Pane NewPane2 = null;
                                        try {
                                            NewPane2 = loader2.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        Pair<String, String> data = (Pair<String, String>) cell.getValue().getValue().getValue();

                                        AddCustomerController addCustCont = loader2.getController();
                                        String tabTitle = ("Add Customer - " + data.getKey());
                                        addCustCont.initAddCust(data.getKey(), this, openInNewWindow(NewPane2, tabTitle), data.getValue(), cell.getParent());
                                    }, null);
                            break;
                        }
                        case "Reports":
                            cmContent = createContextMenuContent(
                                    //Open
                                    () -> new Reports(tabPane2.getScene().getWindow()), null, null, null);
                            //  new Reports(tabPane2.getScene().getWindow());
                            break;
                        case "View Map":
                            cmContent = createContextMenuContent(
                                    //Open
                                    () -> {
                                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Map.fxml"));

                                        Pane NewPane2 = null;
                                        try {
                                            NewPane2 = loader2.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        MapController mapCont = loader2.getController();
                                        mapCont.initMap(this);
                                        String tabTitle = ("Map");
                                        openTabInCurrentWindow(NewPane2, tabTitle);

                                    }, () -> { //Open In New Tab
                                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Map.fxml"));

                                        Pane NewPane2 = null;
                                        try {
                                            NewPane2 = loader2.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        MapController mapCont = loader2.getController();
                                        mapCont.initMap(this);
                                        String tabTitle = ("Map");
                                        addTab(NewPane2, tabTitle);
                                    }, () -> { //Open In New Window
                                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Map.fxml"));

                                        Pane NewPane2 = null;
                                        try {
                                            NewPane2 = loader2.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        MapController mapCont = loader2.getController();
                                        mapCont.initMap(this);
                                        String tabTitle = ("Map");
                                        openInNewWindow(NewPane2, tabTitle);
                                    }, null);
                            break;
                        case "Add Year":
                            cmContent = createContextMenuContent(
                                    //Open
                                    () -> new AddYear(getWindow()), null, null, null);
                            // new AddYear(getWindow());
                            break;
                        case "Users Groups & Years":
                            cmContent = createContextMenuContent(
                                    //Open
                                    () -> new UsersGroupsAndYears(getWindow()), null, null, null);
                            // new AddYear(getWindow());
                            break;
                        case "Add User":
                            cmContent = createContextMenuContent(
                                    //Open
                                    () -> new AddUser(getWindow()), null, null, null);
                            // new AddYear(getWindow());
                            break;
                        case "Add Group":
                            cmContent = createContextMenuContent(
                                    //Open
                                    () -> AddGroup.addGroup(cell.getValue().getValue().getValue().toString()), null, null, null);
                            // new AddYear(getWindow());
                            break;
                        case "Settings":
                            cmContent = createContextMenuContent(
                                    //Open
                                    () -> new Settings(tabPane2.getScene().getWindow()), null, null, null);
                            // new Settings(tabPane2.getScene().getWindow());
                            break;
                    }
                    break;
                }
                case "Year": {
                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Year.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                YearController yearCont = loader2.getController();
                                yearCont.initYear(cell.getValue().getKey(), this);
                                String tabTitle = ("Year View - " + cell.getValue().getKey());
                                openTabInCurrentWindow(NewPane2, tabTitle);
                            }, () -> { //Open In New Tab
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Year.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                YearController yearCont = loader2.getController();
                                yearCont.initYear(cell.getValue().getKey(), this);
                                String tabTitle = ("Year View - " + cell.getValue().getKey());
                                addTab(NewPane2, tabTitle);
                            }, () -> { //Open In New Window
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Year.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                YearController yearCont = loader2.getController();
                                yearCont.initYear(cell.getValue().getKey(), this);
                                String tabTitle = ("Year View - " + cell.getValue().getKey());
                                openInNewWindow(NewPane2, tabTitle);
                            }, () -> new AddYear(cell.getValue().getKey(), this.getWindow()));

                    break;
                }
                case "Customer": {
                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Customer.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                CustomerController customerCont = loader2.getController();
                                customerCont.initCustomer((Customer) cell.getValue().getValue().getValue(), this);
                                String tabTitle = ("Customer View - " + cell.getValue().getKey() + " - " + ((Customer) cell.getValue().getValue().getValue()).getYear());
                                openTabInCurrentWindow(NewPane2, tabTitle);
                            }, () -> { //Open In New Tab
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Customer.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                CustomerController customerCont = loader2.getController();
                                customerCont.initCustomer((Customer) cell.getValue().getValue().getValue(), this);
                                String tabTitle = ("Customer View - " + cell.getValue().getKey() + " - " + ((Customer) cell.getValue().getValue().getValue()).getYear());
                                addTab(NewPane2, tabTitle);
                            }, () -> { //Open In New Window
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Customer.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                CustomerController customerCont = loader2.getController();
                                customerCont.initCustomer((Customer) cell.getValue().getValue().getValue(), this);
                                String tabTitle = ("Customer View - " + cell.getValue().getKey() + " - " + ((Customer) cell.getValue().getValue().getValue()).getYear());
                                openInNewWindow(NewPane2, tabTitle);
                            }, () -> { //Edit
                                openEditCustomer((Customer) cell.getValue().getValue().getValue());
                            });
                    break;
                }
                case "Group": {
                    cmContent = createContextMenuContent(
                            //Open
                            () -> AddGroup.addGroup(cell.getValue().getValue().getValue().toString(), cell.getValue().getKey()), null, null, null);
                    break;
                }
                case "UserCustomerView": {
                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Year.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                YearController yearCont = loader2.getController();
                                yearCont.initYear(cell.getValue().getValue().getValue().toString(), cell.getValue().getKey(), this);
                                String tabTitle = ("Year View - " + cell.getValue().getValue().getValue() + " - " + cell.getValue().getKey());

                                openTabInCurrentWindow(NewPane2, tabTitle);
                            },
                            () -> {
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Year.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                YearController yearCont = loader2.getController();
                                yearCont.initYear(cell.getValue().getValue().getValue().toString(), cell.getValue().getKey(), this);
                                String tabTitle = ("Year View - " + cell.getValue().getValue().getValue() + " - " + cell.getValue().getKey());

                                addTab(NewPane2, tabTitle);
                            },
                            () -> {
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/Year.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                YearController yearCont = loader2.getController();
                                yearCont.initYear(cell.getValue().getValue().getValue().toString(), cell.getValue().getKey(), this);
                                String tabTitle = ("Year View - " + cell.getValue().getValue().getValue() + " - " + cell.getValue().getKey());

                                openInNewWindow(NewPane2, tabTitle);
                            },
                            null);
                    break;
                }

                case "User": {
                    cmContent = createContextMenuContent(
                            //Open
                            () -> new AddUser(getWindow(), cell.getValue().getValue().getValue().toString()), null, null, null);
                    break;
                }

            }


        }
        cm.getItems().addAll(cmContent.getItems());
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction(event -> fillTreeView());
        cm.getItems().add(refresh);
        // other menu items...
        return cm;
    }

    private ContextMenu createContextMenuContent(contextActionCallback open, contextActionCallback openInNewTab, contextActionCallback openInNewWindow, contextActionCallback edit) {
        ContextMenu cm = new ContextMenu();
        if (open != null) {
            MenuItem openItem = new MenuItem("Open");
            openItem.setOnAction(event -> open.doAction());
            cm.getItems().add(openItem);
        }
        if (openInNewTab != null) {
            MenuItem openItem = new MenuItem("Open in New Tab");
            openItem.setOnAction(event -> openInNewTab.doAction());
            cm.getItems().add(openItem);
        }
        if (openInNewWindow != null) {
            MenuItem openItem = new MenuItem("Open in New Window");
            openItem.setOnAction(event -> openInNewWindow.doAction());
            cm.getItems().add(openItem);
        }
        if (edit != null) {
            MenuItem openItem = new MenuItem("Edit");
            openItem.setOnAction(event -> edit.doAction());
            cm.getItems().add(openItem);
        }

        return cm;
    }

    public javafx.stage.Window getWindow() {
        return tabPane2.getScene().getWindow();
    }

    public Tab addTab(Pane fillPane, String tabTitle) {
        Tab tab = new Tab(tabTitle);
        AnchorPane tabContentPane = new AnchorPane(fillPane);
        AnchorPane.setBottomAnchor(tabContentPane, 0.0);
        AnchorPane.setTopAnchor(tabContentPane, 0.0);
        AnchorPane.setLeftAnchor(tabContentPane, 0.0);
        AnchorPane.setRightAnchor(tabContentPane, 0.0);
        tabContentPane.setMinWidth(0);
        tabContentPane.setMinHeight(0);
        tab.setContent(tabContentPane);
        tab.setClosable(true);

        tabPane2.getTabs().add(tab);
        tabPane2.getSelectionModel().select(tab);
        return tab;
    }

    public void closeTab(Tab tab) {
        tabPane2.getTabs().remove(tab);
    }

    private Tab openTabInCurrentWindow(Pane fillPane, String tabTitle) {
        Tab tab = new Tab(tabTitle);
        AnchorPane tabContentPane = new AnchorPane(fillPane);
        AnchorPane.setBottomAnchor(tabContentPane, 0.0);
        AnchorPane.setTopAnchor(tabContentPane, 0.0);
        AnchorPane.setLeftAnchor(tabContentPane, 0.0);
        AnchorPane.setRightAnchor(tabContentPane, 0.0);
        tab.setContent(tabContentPane);
        tab.setClosable(true);
        if (tabPane2.getTabs().isEmpty()) {
            tabPane2.getTabs().add(tab);
        } else {
            tabPane2.getTabs().set(tabPane2.getSelectionModel().getSelectedIndex(), tab);
        }
        tabPane2.getSelectionModel().select(tab);
        return tab;
    }

    private Stage openInNewWindow(Pane fillPane, String tabTitle) {
        Stage stage = new Stage();
        stage.setTitle(tabTitle);
        stage.setScene(new Scene(fillPane));
        stage.initOwner(getWindow());
        stage.setMinWidth(850);
        stage.setMinHeight(850);
        stage.show();

        return stage;
    }
// --Commented out by Inspection START (10/28/17 5:02 PM):
//    public void openAddCustomer(String year) {
//        Pane newPane = null;
//        FXMLLoader loader;
//        loader = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));
//        try {
//            newPane = loader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        AddCustomerController addCustCont = loader.getController();
//        Tab tab = addTab(newPane, "Add Customer - " + year);
//        // get children of parent of secPane (the VBox)
//
//        addCustCont.initAddCust(year, this, tab);
//    }
// --Commented out by Inspection STOP (10/28/17 5:02 PM)

    public void openEditCustomer(Customer customer) {
        Pane newPane = null;
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));
        try {
            newPane = loader.load();
        } catch (IOException e) {
            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
        }
        AddCustomerController addCustCont = loader.getController();
        Tab tab = addTab(newPane, "Edit Customer - " + customer.getName() + " - " + customer.getYear());
        // get children of parent of secPane (the VBox)

        addCustCont.initAddCust(customer, this, tab, null);
    }

    public void addCustomerToTreeView(Customer customer, TreeItem<TreeItemPair<String, Pair<String, Object>>> parent) {
        parent.getChildren().add(parent.getChildren().size() - 1, new contextTreeItem(customer.getName(), new Pair<String, Customer>("Customer", customer)));

    }
    /**
     * Adds the year buttons to the main panel.
     */
    void fillTreeView() {
        //ProgressDialog progDial = new ProgressDialog();
        ProgressForm progDial = new ProgressForm();
//Do check if new or not, send -1 as ID

        LoadMainWorker loadWorker = new LoadMainWorker(this);

        progDial.activateProgressBar(loadWorker);
        loadWorker.setOnSucceeded(event -> {
            selectNav.setRoot(loadWorker.getValue());
            progDial.getDialogStage().close();

        });
        loadWorker.setOnFailed(event -> {
            progDial.getDialogStage().close();

            Throwable e = loadWorker.getException();

            if (e instanceof SQLException) {
                LogToFile.log((SQLException) e, Severity.SEVERE, CommonErrors.returnSqlMessage(((SQLException) loadWorker.getException())));

            }
            if (e instanceof InterruptedException) {
                if (loadWorker.isCancelled()) {
                    LogToFile.log((InterruptedException) e, Severity.FINE, "Load process canceled.");

                }
            }


        });


        progDial.getDialogStage().show();
        new Thread(loadWorker).start();



    }

    interface contextActionCallback {
        void doAction();
    }

    abstract class AbstractTreeItem extends TreeItem {

        protected abstract ContextMenu getMenu();
    }

    protected class contextTreeItem<K, V> extends AbstractTreeItem {
        // make class vars here like psswd

        public contextTreeItem(String key, Pair<String, String> value) {
            this.setValue(new TreeItemPair<>(key, value));
        }

        public contextTreeItem(String key, String value) {
            this.setValue(new TreeItemPair<>(key, new Pair<String, String>(value, "")));
        }

        public contextTreeItem(String key) {
            this.setValue(new TreeItemPair<>(key, null));
        }

        @Override
        public ContextMenu getMenu() {

            return createContextMenu(this);
        }
    }

    private final class TreeCellImpl<K, V> extends TreeCell<TreeItemPair<String, Pair<String, String>>> {

        @Override
        public void updateItem(TreeItemPair<String, Pair<String, String>> item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getItem() == null ? "" : getItem().getKey());
                setGraphic(getTreeItem().getGraphic());
                setContextMenu(((AbstractTreeItem) getTreeItem()).getMenu());
            }
        }
    }


}
