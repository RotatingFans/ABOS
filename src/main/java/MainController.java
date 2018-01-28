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
import java.util.ArrayList;
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
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(userPass -> {
            if (!DbInt.verifyLogin(userPass)) {
                login(true);
            }


        });
    }

    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    public void initialize(Stage stage) {
        login(false);
        // DbInt.username = "tw";
        ArrayList<String> years = DbInt.getYears();
        User latestUser = new User(years.get(years.size() - 1));
        stage.setTitle("ABOS - " + latestUser.getFullName());
        //loadTreeItems("initial 1", "initial 2", "initial 3");
        fillTreeView();

        selectNav.setShowRoot(false);
        selectNav.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isSecondaryButtonDown()) {
                isRightClick = true;

            }
        });
        selectNav.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (isRightClick) {
                //reset the flag
                isRightClick = false;
            } else if (newValue != null && !Objects.equals(newValue.getValue().getValue(), "RootNode")) {
                Pane newPane = null;
                FXMLLoader loader;
                String tabTitle;

                // load new pane
                switch (newValue.getValue().getValue().getKey()) {
                    case "Window": {
                        switch (newValue.getValue().getKey()) {
                            case "Add Customer": {
                                loader = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));
                                try {
                                    newPane = loader.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                AddCustomerController addCustCont = loader.getController();
                                Tab tab = addTab(newPane, "Add Customer - " + newValue.getValue().getValue().getValue());

                                addCustCont.initAddCust(newValue.getValue().getValue().getValue().toString(), this, tab);

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
                                AddGroup.addGroup(newValue.getValue().getValue().getValue().toString());
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
                        yearCont.initYear(newValue.getValue().getKey(), this);
                        tabTitle = ("Year View - " + newValue.getValue().getKey());
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
                        customerCont.initCustomer((Customer) newValue.getValue().getValue().getValue(), this);
                        tabTitle = ("Customer View - " + newValue.getValue().getKey() + " - " + newValue.getValue().getValue().getValue());
                        addTab(newPane, tabTitle);
                        break;
                    }
                    case "Group": {
                        AddGroup.addGroup(newValue.getValue().getValue().getValue().toString(), newValue.getValue().getKey());

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
                        yearCont.initYear(newValue.getValue().getValue().getValue().toString(), newValue.getValue().getKey(), this);
                        tabTitle = ("Year View - " + newValue.getValue().getValue().getValue() + " - " + newValue.getValue().getKey());
                        addTab(newPane, tabTitle);
                        break;
                    }
                    case "User": {
                        new AddUser(getWindow(), newValue.getValue().getValue().getValue().toString());
                        break;
                    }


                }
            }
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
        if (cell != null && cell.getValue() != null && !Objects.equals(cell.getValue().getValue(), "RootNode")) {
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
                                        String tabTitle = ("Add Customer - " + cell.getValue().getValue().getValue().toString());
                                        addCustCont.initAddCust(cell.getValue().getValue().getValue().toString(), this, openTabInCurrentWindow(NewPane2, tabTitle));

                                    }, () -> { //Open In New Tab
                                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));

                                        Pane NewPane2 = null;
                                        try {
                                            NewPane2 = loader2.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        AddCustomerController addCustCont = loader2.getController();
                                        String tabTitle = ("Add Customer - " + cell.getValue().getValue().getValue().toString());
                                        addCustCont.initAddCust(cell.getValue().getValue().getValue().toString(), this, addTab(NewPane2, tabTitle));
                                    }, () -> { //Open In New Window
                                        FXMLLoader loader2 = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));

                                        Pane NewPane2 = null;
                                        try {
                                            NewPane2 = loader2.load();
                                        } catch (IOException e) {
                                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                        }
                                        AddCustomerController addCustCont = loader2.getController();
                                        String tabTitle = ("Add Customer - " + cell.getValue().getValue().getValue().toString());
                                        addCustCont.initAddCust(cell.getValue().getValue().getValue().toString(), this, openInNewWindow(NewPane2, tabTitle));
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
                                String tabTitle = ("Customer View - " + cell.getValue().getKey() + " - " + cell.getValue().getValue().getValue().toString());
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
                                String tabTitle = ("Customer View - " + cell.getValue().getKey() + " - " + cell.getValue().getValue().getValue().toString());
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
                                String tabTitle = ("Customer View - " + cell.getValue().getKey() + " - " + cell.getValue().getValue().getValue().toString());
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

        addCustCont.initAddCust(customer, this, tab);
    }

    /**
     * Adds the year buttons to the main panel.
     */
    void fillTreeView() {

        Iterable<String> ret = DbInt.getYears();
        TreeItem<TreeItemPair<String, Pair<String, Object>>> root = new TreeItem<>(new TreeItemPair("Root Node", new Pair<String, String>("RootNode", "")));
        contextTreeItem userRoot = new contextTreeItem("Groups/Users", new Pair<String, String>("RootNode", ""));
        root.getChildren().add(new contextTreeItem("Reports", "Window"));
        root.getChildren().add(new contextTreeItem("View Map", "Window"));
        root.getChildren().add(new contextTreeItem("Settings", "Window"));


        ///Select all years
        //Create a button for each year
/*        for (String aRet : ret) {
            JButton b = new JButton(aRet);
            b.addActionListener(e -> {
                //On button click open Year window
                new YearWindow(((AbstractButton) e.getSource()).getText());

            });
            panel_1.add(b);
        }*/
        for (String curYear : ret) {
            contextTreeItem tIYear = new contextTreeItem(curYear, "Year");
            Year year = new Year(curYear);
            User curUser = DbInt.getUser(curYear);
            Iterable<String> uManage = curUser.getuManage();
            for (String uMan : uManage) {
                contextTreeItem uManTi = new contextTreeItem(uMan, new Pair<>("UserCustomerView", curYear));

                Iterable<Customer> customers = year.getCustomers(uMan);
                for (Customer customer : customers) {
                    uManTi.getChildren().add(new contextTreeItem(customer.getName(), new Pair<String, Customer>("Customer", customer)));
                }
                uManTi.getChildren().add(new contextTreeItem("Add Customer", new Pair<String, String>("Window", curYear)));
                if (Objects.equals(uMan, curUser.getUserName())) {
                    tIYear.getChildren().addAll(uManTi.getChildren());
                } else {
                    tIYear.getChildren().add(uManTi);
                }
            }
            root.getChildren().add(tIYear);
            if (DbInt.getUser(curYear).isAdmin()) {
                contextTreeItem yearItem = new contextTreeItem<>(curYear, "RootNode");

                Group.getGroups(curYear).forEach(group -> {
                    contextTreeItem groupItem = new contextTreeItem(group.getName(), "Group");
                    group.getUsers().forEach(user -> {
                        contextTreeItem userItem = new contextTreeItem(user.getFullName(), new Pair<String, String>("User", user.getUserName()));
                        groupItem.getChildren().add(userItem);
                    });
                    yearItem.getChildren().add(groupItem);
                });
                userRoot.getChildren().add(yearItem);
                isAdmin = true;
                yearItem.getChildren().add(new contextTreeItem("Add Group", "Window"));

            }


        }
        root.getChildren().add(new contextTreeItem("Add Year", "Window"));
        userRoot.getChildren().add(new contextTreeItem("Add User", "Window"));

        if (isAdmin) {
            root.getChildren().add(userRoot);
        }
        selectNav.setRoot(root);

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
