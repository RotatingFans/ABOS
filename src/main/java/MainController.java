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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Objects;

public class MainController {
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

        selectNav.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
        });
        selectNav.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> p) {
                TreeCell<String> cell = new TreeCell<String>() {
                    @Override
                    protected void updateItem(String file, boolean empty) {
                        super.updateItem(file, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            // maybe use a more appropriate string for display here
                            // e.g. if you were using a regular java.io.File you would
                            // likely want file.getName()
                            setText(file.toString());
                        }
                    }
                };
                ContextMenu cm = createContextMenu(cell);
                cell.setContextMenu(cm);
                return cell;
            }
        });

    }

    private ContextMenu createContextMenu(TreeCell<String> cell) {
        ContextMenu cm = new ContextMenu();
        MenuItem openItem = new MenuItem("Open File");
        openItem.setOnAction(event -> {
            String file = cell.getItem();
            if (file != null) {
                // open the file...
            }
        });
        cm.getItems().add(openItem);
        // other menu items...
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
        root.getChildren().add(new TreeItem<>("Reports"));
        root.getChildren().add(new TreeItem<>("View Map"));
        root.getChildren().add(new TreeItem<>("Settings"));


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
            TreeItem<String> tIYear = new TreeItem<String>(itemString);
            Year year = new Year(itemString);
            Iterable<String> customers = year.getCustomerNames();
            for (String customer : customers) {
                tIYear.getChildren().add(new TreeItem<String>(customer));
            }
            tIYear.getChildren().add(new TreeItem<>("Add Customer"));
            root.getChildren().add(tIYear);
        }
        root.getChildren().add(new TreeItem<>("Add Year"));

        selectNav.setRoot(root);

    }
}
