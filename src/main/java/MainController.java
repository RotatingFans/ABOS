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

public class MainController {
    private boolean isRightClick;

    // the FXML annotation tells the loader to inject this variable before invoking initialize.
    @FXML
    private TreeView<String> selectNav;
   // @FXML
    //public Pane tabPane;
    //@FXML
   // public Tab tab1;
    @FXML private TabPane tabPane2;
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
            } else {
                Pane newPane = null;
                FXMLLoader loader;
                String tabTitle = "";

                // load new pane
                switch (newValue.getValue()) {
                    case "Add Customer": {
                        loader = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
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
                        loader = new FXMLLoader(getClass().getResource("UI/Map.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
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
                            loader = new FXMLLoader(getClass().getResource("UI/Year.fxml"));
                            try {
                                newPane = loader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            YearController yearCont = loader.getController();
                            yearCont.initYear(newValue.getValue(), this);
                            tabTitle = ("Year View - " + newValue.getValue());

                        } else {
                            loader = new FXMLLoader(getClass().getResource("UI/Customer.fxml"));
                            try {
                                newPane = loader.load();
                            } catch (IOException e) {
                                e.printStackTrace();
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
                    loader = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));
                    try {
                        newPane = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finalNewPane = newPane;

                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                AddCustomerController addCustCont = loader.getController();
                                String tabTitle = ("Add Customer - " + cell.getParent().getValue());
                                addCustCont.initAddCust(cell.getParent().getValue(), this, openTabInCurrentWindow(finalNewPane, tabTitle));

                            }, () -> { //Open In New Tab
                                AddCustomerController addCustCont = loader.getController();
                                String tabTitle = ("Add Customer - " + cell.getParent().getValue());
                                addCustCont.initAddCust(cell.getParent().getValue(), this, addTab(finalNewPane, tabTitle));
                            }, () -> { //Open In New Window
                                AddCustomerController addCustCont = loader.getController();
                                String tabTitle = ("Add Customer - " + cell.getParent().getValue());
                                addCustCont.initAddCust(cell.getParent().getValue(), this, openInNewWindow(finalNewPane, tabTitle));
                            }, null);
                    break;
                }
                case "Reports":
                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                new Reports(tabPane2.getScene().getWindow());

                            }, null, null, null);
                    //  new Reports(tabPane2.getScene().getWindow());
                    break;
                case "View Map":
                    loader = new FXMLLoader(getClass().getResource("UI/Map.fxml"));
                    try {
                        newPane = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // MapController mapCont = loader.getController();
                    // mapCont.initMap(this);
                    // addTab(newPane, "Map");
                    finalNewPane = newPane;

                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                MapController mapCont = loader.getController();
                                mapCont.initMap(this);
                                String tabTitle = ("Map");
                                openTabInCurrentWindow(finalNewPane, tabTitle);

                            }, () -> { //Open In New Tab
                                MapController mapCont = loader.getController();
                                mapCont.initMap(this);
                                String tabTitle = ("Map");
                                addTab(finalNewPane, tabTitle);
                            }, () -> { //Open In New Window
                                MapController mapCont = loader.getController();
                                mapCont.initMap(this);
                                String tabTitle = ("Map");
                                openInNewWindow(finalNewPane, tabTitle);
                            }, null);
                    break;
                case "Add Year":
                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                new AddYear(getWindow());
                            }, null, null, null);
                    // new AddYear(getWindow());
                    break;
                case "Settings":
                    cmContent = createContextMenuContent(
                            //Open
                            () -> {
                                new Settings(tabPane2.getScene().getWindow());
                            }, null, null, null);
                    // new Settings(tabPane2.getScene().getWindow());
                    break;
                default:

                    if (Objects.equals(cell.getParent().getValue(), "Root Node")) {
                        loader = new FXMLLoader(getClass().getResource("UI/Year.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finalNewPane = newPane;
                        cmContent = createContextMenuContent(
                                //Open
                                () -> {
                                    YearController yearCont = loader.getController();
                                    yearCont.initYear(cell.getValue(), this);
                                    String tabTitle = ("Year View - " + cell.getValue());
                                    openTabInCurrentWindow(finalNewPane, tabTitle);
                                }, () -> { //Open In New Tab
                                    YearController yearCont = loader.getController();
                                    yearCont.initYear(cell.getValue(), this);
                                    String tabTitle = ("Year View - " + cell.getValue());
                                    addTab(finalNewPane, tabTitle);
                                }, () -> { //Open In New Window
                                    YearController yearCont = loader.getController();
                                    yearCont.initYear(cell.getValue(), this);
                                    String tabTitle = ("Year View - " + cell.getValue());
                                    openInNewWindow(finalNewPane, tabTitle);
                                }, () -> {
                                    new AddYear(cell.getValue(), this.getWindow());
                                });


                    } else {
                        loader = new FXMLLoader(getClass().getResource("UI/Customer.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finalNewPane = newPane;

                        cmContent = createContextMenuContent(
                                //Open
                                () -> {
                                    CustomerController customerCont = loader.getController();
                                    customerCont.initCustomer(cell.getParent().getValue(), cell.getValue(), this);
                                    String tabTitle = ("Customer View - " + cell.getValue() + " - " + cell.getParent().getValue());
                                    openTabInCurrentWindow(finalNewPane, tabTitle);
                                }, () -> { //Open In New Tab
                                    CustomerController customerCont = loader.getController();
                                    customerCont.initCustomer(cell.getParent().getValue(), cell.getValue(), this);
                                    String tabTitle = ("Customer View - " + cell.getValue() + " - " + cell.getParent().getValue());
                                    addTab(finalNewPane, tabTitle);
                                }, () -> { //Open In New Window
                                    CustomerController customerCont = loader.getController();
                                    customerCont.initCustomer(cell.getParent().getValue(), cell.getValue(), this);
                                    String tabTitle = ("Customer View - " + cell.getValue() + " - " + cell.getParent().getValue());
                                    openInNewWindow(finalNewPane, tabTitle);
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
        refresh.setOnAction(event -> {
            fillTreeView();
        });
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

    public Tab openTabInCurrentWindow(Pane fillPane, String tabTitle) {
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

    public Stage openInNewWindow(Pane fillPane, String tabTitle) {
        Stage stage = new Stage();
        stage.setTitle(tabTitle);
        stage.setScene(new Scene(fillPane));
        stage.show();

        return stage;
    }
    public void openAddCustomer(String year) {
        Pane newPane = null;
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));
        try {
            newPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddCustomerController addCustCont = loader.getController();
        Tab tab = addTab(newPane, "Add Customer - " + year);
        // get children of parent of secPane (the VBox)

        addCustCont.initAddCust(year, this, tab);
    }

    public void openEditCustomer(String year, String custName) {
        Pane newPane = null;
        FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("UI/AddCustomer.fxml"));
        try {
            newPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddCustomerController addCustCont = loader.getController();
        Tab tab = addTab(newPane, "Edit Customer - " + custName + " - " + year);
        // get children of parent of secPane (the VBox)

        addCustCont.initAddCust(year, custName, this, tab);
    }
    /**
     * Adds the year buttons to the main panel.
     */
    public void fillTreeView() {
        Iterable<String> ret = DbInt.getYears();
        TreeItem<String> root = new TreeItem<String>("Root Node");
        root.getChildren().add(new contextTreeItem("Reports"));
        root.getChildren().add(new contextTreeItem("View Map"));
        root.getChildren().add(new contextTreeItem("Settings"));


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

    public abstract class AbstractTreeItem extends TreeItem {
        public abstract ContextMenu getMenu();
    }

    public class contextTreeItem extends AbstractTreeItem {
        // make class vars here like psswd
        public contextTreeItem(String name) {
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
                setText(getItem() == null ? "" : getItem().toString());
                setGraphic(getTreeItem().getGraphic());
                setContextMenu(((AbstractTreeItem) getTreeItem()).getMenu());
            }
        }
    }
}
