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

package ABOS.Derby;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")

public class MainController {
    private boolean isRightClick;

    // the FXML annotation tells the loader to inject this variable before invoking initialize.
    @FXML
    private TreeView<String> selectNav;
    // @FXML
    //public Pane tabPane;
    //@FXML
    // public Tab tab1;
    @FXML
    private TabPane tabPane2;

    // the initialize method is automatically invoked by the FXMLLoader - it's magic
    public void initialize() {
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
            } else if (newValue != null) {
                Pane newPane = null;
                FXMLLoader loader;
                String tabTitle;

                // load new pane
                switch (newValue.getValue()) {
                    case "Add Customer": {
                        loader = new FXMLLoader(getClass().getResource("/UI/AddCustomer.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                        }
                        AddCustomerController addCustCont = loader.getController();
                        Tab tab = addTab(newPane, "Add Customer - " + newValue.getParent().getValue());

                        addCustCont.initAddCust(newValue.getParent().getValue(), this, tab);

                        break;
                    }
                    case "Reports":
                        new Reports(tabPane2.getScene().getWindow());
                        break;
                    case "View Map":
                        loader = new FXMLLoader(getClass().getResource("/UI/Map.fxml"));
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
                    case "Settings":
                        new Settings(tabPane2.getScene().getWindow());
                        break;
                    default:

                        if (Objects.equals(observable.getValue().getParent().getValue(), "Root Node")) {
                            loader = new FXMLLoader(getClass().getResource("/UI/Year.fxml"));
                            try {
                                newPane = loader.load();
                            } catch (IOException e) {
                                LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                            }
                            YearController yearCont = loader.getController();
                            yearCont.initYear(newValue.getValue(), this);
                            tabTitle = ("Year View - " + newValue.getValue());

                        } else {
                            loader = new FXMLLoader(getClass().getResource("/UI/Customer.fxml"));
                            try {
                                newPane = loader.load();
                            } catch (IOException e) {
                                LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                            }
                            CustomerController customerCont = loader.getController();
                            customerCont.initCustomer(observable.getValue().getParent().getValue(), newValue.getValue(), this);
                            tabTitle = ("Customer View - " + newValue.getValue() + " - " + observable.getValue().getParent().getValue());
                        }
                        addTab(newPane, tabTitle);
                        break;
                }
            }
        });
        selectNav.setCellFactory(p -> new TreeCellImpl());

    }

    private ContextMenu createContextMenu(TreeItem<String> cell) {
        ContextMenu cm = new ContextMenu();
        ContextMenu cmContent = new ContextMenu();
        Pane newPane = null;
        FXMLLoader loader;
        Pane finalNewPane;
        // String tabTitle = "";
        if (cell != null && cell.getValue() != null) {
            switch (cell.getValue()) {
                case "Add Customer": {
/*                    loader = new FXMLLoader(getClass().getResource("/UI/AddCustomer.fxml"));
                    try {
                        newPane = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finalNewPane = newPane;*/

                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/AddCustomer.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                AddCustomerController addCustCont = loader2.getController();
                                String tabTitle = ("Add Customer - " + cell.getParent().getValue());
                                addCustCont.initAddCust(cell.getParent().getValue(), this, openTabInCurrentWindow(NewPane2, tabTitle));

                            }, () -> { //Open In New Tab
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/AddCustomer.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                AddCustomerController addCustCont = loader2.getController();
                                String tabTitle = ("Add Customer - " + cell.getParent().getValue());
                                addCustCont.initAddCust(cell.getParent().getValue(), this, addTab(NewPane2, tabTitle));
                            }, () -> { //Open In New Window
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/AddCustomer.fxml"));

                                Pane NewPane2 = null;
                                try {
                                    NewPane2 = loader2.load();
                                } catch (IOException e) {
                                    LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                }
                                AddCustomerController addCustCont = loader2.getController();
                                String tabTitle = ("Add Customer - " + cell.getParent().getValue());
                                addCustCont.initAddCust(cell.getParent().getValue(), this, openInNewWindow(NewPane2, tabTitle));
                            }, null);
                    break;
                }
                case "Reports":
                    cmContent = createContextMenuContent(
                            //Open
                            () -> new Reports(tabPane2.getScene().getWindow()), null, null, null);
                    //  new Launchers.Reports(tabPane2.getScene().getWindow());
                    break;
                case "View Map":
/*                    loader = new FXMLLoader(getClass().getResource("/UI/Map.fxml"));
                    try {
                        newPane = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Controllers.MapController mapCont = loader.getController();
                    // mapCont.initMap(this);
                    // addTab(newPane, "Map");
                    finalNewPane = newPane;*/

                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Map.fxml"));

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
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Map.fxml"));

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
                                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Map.fxml"));

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
                    // new Launchers.AddYear(getWindow());
                    break;
                case "Settings":
                    cmContent = createContextMenuContent(
                            //Open
                            () -> new Settings(tabPane2.getScene().getWindow()), null, null, null);
                    // new Launchers.Settings(tabPane2.getScene().getWindow());
                    break;
                default:

                    if (Objects.equals(cell.getParent().getValue(), "Root Node")) {
                /*        loader = new FXMLLoader(getClass().getResource("/UI/Utilities.Year.fxml"));
                       try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finalNewPane = newPane;*/
                        cmContent = createContextMenuContent(
                                //Open
                                () -> {
                                    FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Year.fxml"));

                                    Pane NewPane2 = null;
                                    try {
                                        NewPane2 = loader2.load();
                                    } catch (IOException e) {
                                        LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                    }
                                    YearController yearCont = loader2.getController();
                                    yearCont.initYear(cell.getValue(), this);
                                    String tabTitle = ("Year View - " + cell.getValue());
                                    openTabInCurrentWindow(NewPane2, tabTitle);
                                }, () -> { //Open In New Tab
                                    FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Year.fxml"));

                                    Pane NewPane2 = null;
                                    try {
                                        NewPane2 = loader2.load();
                                    } catch (IOException e) {
                                        LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                    }
                                    YearController yearCont = loader2.getController();
                                    yearCont.initYear(cell.getValue(), this);
                                    String tabTitle = ("Year View - " + cell.getValue());
                                    addTab(NewPane2, tabTitle);
                                }, () -> { //Open In New Window
                                    FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Year.fxml"));

                                    Pane NewPane2 = null;
                                    try {
                                        NewPane2 = loader2.load();
                                    } catch (IOException e) {
                                        LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                    }
                                    YearController yearCont = loader2.getController();
                                    yearCont.initYear(cell.getValue(), this);
                                    String tabTitle = ("Year View - " + cell.getValue());
                                    openInNewWindow(NewPane2, tabTitle);
                                }, () -> new AddYear(cell.getValue(), this.getWindow()));


                    } else {
/*                        loader = new FXMLLoader(getClass().getResource("/UI/Utilities.Customer.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            Utilities.LogToFile.log(e,Utilities.Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                        }
                        finalNewPane = newPane;*/

                        cmContent = createContextMenuContent(
                                //Open
                                () -> {
                                    FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Customer.fxml"));

                                    Pane NewPane2 = null;
                                    try {
                                        NewPane2 = loader2.load();
                                    } catch (IOException e) {
                                        LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                    }
                                    CustomerController customerCont = loader2.getController();
                                    customerCont.initCustomer(cell.getParent().getValue(), cell.getValue(), this);
                                    String tabTitle = ("Customer View - " + cell.getValue() + " - " + cell.getParent().getValue());
                                    openTabInCurrentWindow(NewPane2, tabTitle);
                                }, () -> { //Open In New Tab
                                    FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Customer.fxml"));

                                    Pane NewPane2 = null;
                                    try {
                                        NewPane2 = loader2.load();
                                    } catch (IOException e) {
                                        LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                    }
                                    CustomerController customerCont = loader2.getController();
                                    customerCont.initCustomer(cell.getParent().getValue(), cell.getValue(), this);
                                    String tabTitle = ("Customer View - " + cell.getValue() + " - " + cell.getParent().getValue());
                                    addTab(NewPane2, tabTitle);
                                }, () -> { //Open In New Window
                                    FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/UI/Customer.fxml"));

                                    Pane NewPane2 = null;
                                    try {
                                        NewPane2 = loader2.load();
                                    } catch (IOException e) {
                                        LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                                    }
                                    CustomerController customerCont = loader2.getController();
                                    customerCont.initCustomer(cell.getParent().getValue(), cell.getValue(), this);
                                    String tabTitle = ("Customer View - " + cell.getValue() + " - " + cell.getParent().getValue());
                                    openInNewWindow(NewPane2, tabTitle);
                                }, () -> { //Edit
                                    openEditCustomer(cell.getParent().getValue(), cell.getValue());
                                });

                    }
                    //addTab(newPane, tabTitle);
                    break;
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
        stage.show();

        return stage;
    }
// --Commented out by Inspection START (10/28/17 5:02 PM):
//    public void openAddCustomer(String year) {
//        Pane newPane = null;
//        FXMLLoader loader;
//        loader = new FXMLLoader(getClass().getResource("/UI/AddCustomer.fxml"));
//        try {
//            newPane = loader.load();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Controllers.AddCustomerController addCustCont = loader.getController();
//        Tab tab = addTab(newPane, "Add Utilities.Customer - " + year);
//        // get children of parent of secPane (the VBox)
//
//        addCustCont.initAddCust(year, this, tab);
//    }
// --Commented out by Inspection STOP (10/28/17 5:02 PM)

    public void openEditCustomer(String year, String custName) {
        Pane newPane = null;
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("/UI/AddCustomer.fxml"));
        try {
            newPane = loader.load();
        } catch (IOException e) {
            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
        }
        AddCustomerController addCustCont = loader.getController();
        Tab tab = addTab(newPane, "Edit Customer - " + custName + " - " + year);
        // get children of parent of secPane (the VBox)

        addCustCont.initAddCust(year, custName, this, tab);
    }

    /**
     * Adds the year buttons to the main panel.
     */
    void fillTreeView() {
        Iterable<String> ret = DbInt.getYears();
        TreeItem<String> root = new TreeItem<>("Root Node");
        root.getChildren().add(new contextTreeItem("Reports"));
        root.getChildren().add(new contextTreeItem("View Map"));
        root.getChildren().add(new contextTreeItem("Settings"));


        ///Select all years
        //Create a button for each year
/*        for (String aRet : ret) {
            JButton b = new JButton(aRet);
            b.addActionListener(e -> {
                //On button click open Utilities.Year window
                new YearWindow(((AbstractButton) e.getSource()).getText());

            });
            panel_1.add(b);
        }*/
        for (String itemString : ret) {
            contextTreeItem tIYear = new contextTreeItem(itemString);
            Year year = new Year(itemString);
            Iterable<String> customers = year.getCustomerNames();
            for (String customer : customers) {
                tIYear.getChildren().add(new contextTreeItem(customer));
            }
            tIYear.getChildren().add(new contextTreeItem("Add Customer"));
            root.getChildren().add(tIYear);
        }
        root.getChildren().add(new contextTreeItem("Add Year"));

        selectNav.setRoot(root);

    }

    interface contextActionCallback {
        void doAction();
    }

    abstract class AbstractTreeItem extends TreeItem {
        protected abstract ContextMenu getMenu();
    }

    protected class contextTreeItem extends AbstractTreeItem {
        // make class vars here like psswd
        contextTreeItem(String name) {
            this.setValue(name);
        }

        @Override
        public ContextMenu getMenu() {

            return createContextMenu(this);
        }
    }

    private final class TreeCellImpl extends TreeCell<String> {

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getItem() == null ? "" : getItem());
                setGraphic(getTreeItem().getGraphic());
                setContextMenu(((AbstractTreeItem) getTreeItem()).getMenu());
            }
        }
    }
}
