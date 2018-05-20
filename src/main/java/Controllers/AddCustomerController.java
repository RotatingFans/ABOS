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

package Controllers;

import Exceptions.addressException;
import Utilities.*;
import Workers.AddCustomerWorker;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Objects;
import java.util.Optional;

//import java.awt.*;

/**
 * A dialog that allows the user to add a new customer or edit an existing customer.
 *
 * @author patrick
 * @version 1.0
 */

@SuppressWarnings("WeakerAccess")
public class AddCustomerController {
    private boolean edit = false; //States whether this is an edit or creation of a customer.
    private Year yearInfo;
    //private final JPanel contentPanel = new JPanel();
    //Editable Field for user to input customer info.
    @FXML
    private CheckBox Delivered;
    @FXML
    private TextField Paid;
    @FXML
    private TableView ProductTable;
    @FXML
    private TextField Name;
    @FXML
    private TextField Address;
    @FXML
    private TextField ZipCode;
    @FXML
    private TextField Town;
    @FXML
    private TextField State;
    @FXML
    private TextField Phone;
    @FXML
    private TextField Email;
    @FXML
    private TextField DonationsT;
    @FXML
    private ComboBox<String> userCmbx;
    @FXML
    private Button okButton;
    @FXML
    private Label runningTotalLabel;
    private Tab parentTab = null;
    private Stage parentStage = null;
    private Pane tPane;

    //Variables used to store regularly accessed info.
    private String year = null;
    private BigDecimal totalCostFinal = BigDecimal.ZERO;
    //Variables used to calculate difference of orders when in edit mode.
    private String NameEditCustomer = null;
    private BigDecimal preEditOrderCost = BigDecimal.ZERO;
    private Customer customerInfo = new Customer();
    private Boolean columnsFilled = false;
    private ObservableList<formattedProductProps> data;
    private MainController mainCont;
    private TreeItem<TreeItemPair<String, Pair<String, Object>>> treeParent;

    private String lastKey = null;


    /**
     * Used to open dialog with already existing customer information from year as specified in Utilities.Customer Report.
     *
     */
    public void initAddCust(Customer customer, MainController mainController, Tab parent, TreeItem<TreeItemPair<String, Pair<String, Object>>> treeParent) {
        initAddCust(customer, mainController, parent, customer.getUser(), treeParent);
    }

    public void initAddCust(Customer customer, MainController mainController, Tab parent, String user, TreeItem<TreeItemPair<String, Pair<String, Object>>> treeParent) {
        this.treeParent = treeParent;
        customer.refreshData();
        mainCont = mainController;
        parentTab = parent;
        year = customer.getYear();
        yearInfo = new Year(year);
        customerInfo = customer;
        edit = true;
        User curUser = new User(year);
        curUser.getuManage().forEach(uMan -> {
            userCmbx.getItems().add(uMan);
        });
        userCmbx.getSelectionModel().select(user);
        userCmbx.setDisable(true);
        //Set the address
        String[] addr = customerInfo.getCustAddressFrmName();
        String city = addr[0];
        String state = addr[1];
        String zip = addr[2];
        String streetAdd = addr[3];
        //Fill in Utilities.Customer info fields.
        Address.setText(streetAdd);
        Town.setText(city);
        State.setText(state);
        ZipCode.setText(zip);
        Phone.setText(customerInfo.getPhone());
        Paid.setText(customerInfo.getPaid().toPlainString());
        Delivered.setSelected(customerInfo.getDelivered());
        Email.setText(customerInfo.getEmail());
        Name.setText(customerInfo.getName());
        DecimalFormat format = new DecimalFormat("#.0");

        DonationsT.setTextFormatter(new TextFormatter<>(c ->
        {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {
                return c;
            }
        }));
        Paid.setTextFormatter(new TextFormatter<>(c ->
        {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {
                return c;
            }
        }));
        DonationsT.setText(customerInfo.getDontation().toPlainString());
        BigDecimal preEditDonations = customerInfo.getDontation();
        //Fill the table with their previous order info on record.
        fillOrderedTable();

        NameEditCustomer = customerInfo.getName();
        edit = true;
        ProductTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Platform.runLater(() -> {
            Name.requestFocus();

        });

        //Add a Event to occur if a cell is changed in the table

    }

    public void initAddCust(String aYear, MainController mainController, Stage parent, TreeItem<TreeItemPair<String, Pair<String, Object>>> treeParent) {
        parentStage = parent;
        initAddCust(aYear, mainController, (Tab) null, treeParent);
    }

    public void initAddCust(String aYear, MainController mainController, Stage parent, String User, TreeItem<TreeItemPair<String, Pair<String, Object>>> treeParent) {
        parentStage = parent;
        initAddCust(aYear, mainController, (Tab) null, User, treeParent);
    }

    public void initAddCust(String aYear, MainController mainController, Tab parent, TreeItem<TreeItemPair<String, Pair<String, Object>>> treeParent) {
        initAddCust(aYear, mainController, parent, DbInt.getUserName(), treeParent);

    }

    public void initAddCust(String aYear, MainController mainController, Tab parent, String user, TreeItem<TreeItemPair<String, Pair<String, Object>>> treeParent) {
        this.treeParent = treeParent;

        mainCont = mainController;
        parentTab = parent;
        NameEditCustomer = "";
        year = aYear;
        yearInfo = new Year(year);
        User curUser = new User(year);
        curUser.getuManage().forEach(uMan -> {
            userCmbx.getItems().add(uMan);
        });
        userCmbx.getSelectionModel().select(user);
        Name.setText(Config.getProp("CustomerName"));
        //Name.requestFocus();
        Address.setText(Config.getProp("CustomerAddress"));
        Town.setText(Config.getProp("CustomerTown"));
        State.setText(Config.getProp("CustomerState"));
        ZipCode.setText(Config.getProp("CustomerZipCode"));
        ZipCode.setOnKeyTyped(keyEvent -> {
            if (ZipCode.getCharacters().length() >= 4) {
                String zip = ZipCode.getText() + keyEvent.getCharacter();

                String cityAndState = "";
                try {
                    cityAndState = Geolocation.getCityState(zip);
                } catch (IOException e1) {
                    LogToFile.log(e1, Severity.WARNING, "Couldn't contact geolocation service. Please try again or enter the adress manually and contact suport.");
                }
                String[] StateTown = cityAndState.split("&");
                String state = StateTown[1];
                String town = StateTown[0];
                Town.setText(town);
                State.setText(state);
            }
        });
        Phone.setText(Config.getProp("CustomerPhone"));
        Email.setText(Config.getProp("CustomerEmail"));

        Paid.setText(!Config.getProp("CustomerPaid").isEmpty() ? Config.getProp("CustomerPaid") : "0.0");
        Delivered.setSelected(Boolean.valueOf(Config.getProp("CustomerDelivered")));

        DonationsT.setText(!Config.getProp("CustomerDonation").isEmpty() ? Config.getProp("CustomerDonation") : "0.0");
        DecimalFormat format = new DecimalFormat("#.0");

        DonationsT.setTextFormatter(new TextFormatter<>(c ->
        {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {

                return c;
            }
        }));
        Paid.setTextFormatter(new TextFormatter<>(c ->
        {
            if (c.getControlNewText().isEmpty()) {
                return c;
            }

            ParsePosition parsePosition = new ParsePosition(0);
            Object object = format.parse(c.getControlNewText(), parsePosition);

            if (object == null || parsePosition.getIndex() < c.getControlNewText().length()) {
                return null;
            } else {

                return c;
            }
        }));

        fillTable();
        ProductTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        Platform.runLater(() -> {

            Name.requestFocus();
        });
    }

    public void initialize() {

    }

    @FXML
    public void submit(ActionEvent event) {

        if (infoEntered()) {
            if (!edit && yearInfo.addressExists(Address.getText(), ZipCode.getText())) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Duplicate");
                alert.setHeaderText("The address you have entered appears to be a duplicate");
                alert.setContentText("Would you like to continue?");


                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    commitChanges();
                    //updateTots();
                    //close
                    // ... user chose OK
                }
            } else if (new BigDecimal(Paid.getText()).subtract(totalCostFinal).compareTo(BigDecimal.ZERO) > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Overpaid");
                alert.setHeaderText("You entered more paid than total order.");
                alert.setContentText("Would you like to place the extra as a donation?");


                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    DonationsT.setText(new BigDecimal(Paid.getText()).subtract(totalCostFinal).toPlainString());
                    commitChanges();
                    //updateTots();
                    //close
                    // ... user chose OK
                }
            } else {
                commitChanges();

            }

        } else {
            //javafx.scene.control.Dialog dialog = new Dialog();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("");
            alert.setHeaderText("It appears you have not entered any data");
            alert.setContentText("Would you like to re-enter the data?");


            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.CANCEL) {
                if (parentTab != null) {
                    mainCont.closeTab(parentTab);
                } else {
                    parentStage.close();
                }
                //close
                // ... user chose OK
            }
        }
    }

    @FXML
    public void cancel(ActionEvent event) {
        if (parentTab != null) {
            mainCont.closeTab(parentTab);
        } else {
            parentStage.close();
        }
        //close
        // ... user chose OK

    }

    /**
     * Fills the table with quantitys set to 0.
     */
    private void fillTable() {

        formattedProduct[] productArray = yearInfo.getAllProducts();
        Object[][] rows = new Object[productArray.length][6];
        data = FXCollections.observableArrayList();

        int i = 0;
        for (formattedProduct productOrder : productArray) {
            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
            formattedProductProps prodProps = new formattedProductProps(productOrder.productKey, productOrder.productID, productOrder.productName, productOrder.productSize, productOrder.productUnitPrice, productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
            data.add(prodProps);
            i++;
        }

        // ProductTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //ProductTable.getSelectionModel().setCellSelectionEnabled(true);
        // ProductTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        ProductTable.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (ProductTable.getEditingCell() == null && t.getCode() == KeyCode.ENTER) {
                if (t.isShiftDown()) {
                    ProductTable.getSelectionModel().selectAboveCell();
                } else {
                    ProductTable.getSelectionModel().selectBelowCell();
                }
                t.consume();
            } else {
                TablePosition tp;
                if (!t.isControlDown() &&
                        (t.getCode().isDigitKey())) {
                    lastKey = t.getText();
                    tp = ProductTable.getFocusModel().getFocusedCell();
                    ProductTable.edit(tp.getRow(), tp.getTableColumn());
                    lastKey = null;
                }
            }

            /*//I decided not to override the default tab behavior
            //using ctrl tab for cell traversal, but arrow keys are better
            if (t.isControlDown() && t.getCode() == KeyCode.TAB) {
                if (t.isShiftDown()) {
                    tv.getSelectionModel().selectLeftCell();
                } else {
                    tv.getSelectionModel().selectRightCell();
                }
                t.consume();
            }*/
        });

        /*ProductTable.setOnKeyPressed((KeyEvent t) -> {
            TablePosition tp;
            if (!t.isControlDown() &&
                    (t.getCode().isLetterKey() || t.getCode().isDigitKey())) {
                lastKey = t.getText();
                tp = ProductTable.getFocusModel().getFocusedCell();
                ProductTable.edit(tp.getRow(),tp.getTableColumn());
                lastKey = null;
            }
        });*/
        Callback<TableColumn<formattedProductProps, String>, TableCell<formattedProductProps, String>> txtCellFactory =
                (TableColumn<formattedProductProps, String> p) -> {return new EditingCell();};

        if (!columnsFilled) {
            String[][] columnNames = {{"ID", "productID"}, {"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}};
            for (String[] column : columnNames) {
                TableColumn<formattedProductProps, String> tbCol = new TableColumn<>(column[0]);
                tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
                ProductTable.getColumns().add(tbCol);
            }
        }


        //{"Quantity", "orderedQuantity"}, {"Price", "extendedCost"}
        TableColumn<formattedProductProps, String> quantityCol = new TableColumn<>("Quantity");
        TableColumn<formattedProductProps, String> priceCol = new TableColumn<>("Price");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("orderedQuantityString"));

        quantityCol.setCellFactory(txtCellFactory);

        quantityCol.setOnEditCommit(t -> {
            //t.getTableView().getItems().get(t.getTablePosition().getRow()).orderedQuantity.set(Integer.valueOf(t.getNewValue()));
            try {
                int quantity = Integer.valueOf(t.getNewValue());

                BigDecimal unitCost = t.getTableView().getItems().get(t.getTablePosition().getRow()).productUnitPrice.get();
                //Removes $ from cost and multiplies to get the total cost for that item
                BigDecimal ItemTotalCost = unitCost.multiply(new BigDecimal(quantity));
                t.getRowValue().extendedCost.set(ItemTotalCost);
                t.getRowValue().orderedQuantity.set(quantity);
                t.getRowValue().orderedQuantityString.set(String.valueOf(quantity));

                data.get(t.getTablePosition().getRow()).orderedQuantity.set(quantity);
                data.get(t.getTablePosition().getRow()).extendedCost.set(ItemTotalCost);
                t.getTableView().refresh();
                totalCostFinal = new BigDecimal(DonationsT.getText());
                t.getTableView().getItems().forEach(item -> {
                    totalCostFinal = totalCostFinal.add(item.getExtendedCost());//Recalculate Utilities.Order total

                });
                runningTotalLabel.setText("Total: " + totalCostFinal.toPlainString());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid number");
                alert.setHeaderText("You have entered an invalid number.");
                alert.show();
                t.getRowValue().productUnitPriceString.set(t.getOldValue());
                t.getTableView().getSelectionModel().selectAboveCell();

                t.getTableView().refresh();
            }

        });
        priceCol.setCellValueFactory(new PropertyValueFactory<>("extendedCost"));


        ProductTable.getColumns().addAll(quantityCol, priceCol);

        columnsFilled = true;

        ProductTable.setItems(data);


    }

    /**
     * Fills product table with info with quantities set to Amount customer ordered.
     */
    private void fillOrderedTable() {
        Order.orderArray order = Order.createOrderArray(year, customerInfo.getId(), false);
        data = FXCollections.observableArrayList();
        Callback<TableColumn<formattedProductProps, String>, TableCell<formattedProductProps, String>> txtCellFactory =
                (TableColumn<formattedProductProps, String> p) -> {return new EditingCell();};
        ProductTable.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent t) -> {
            if (ProductTable.getEditingCell() == null && t.getCode() == KeyCode.ENTER) {
                if (t.isShiftDown()) {
                    ProductTable.getSelectionModel().selectAboveCell();
                } else {
                    ProductTable.getSelectionModel().selectBelowCell();
                }
                t.consume();
            } else {
                TablePosition tp;
                if (!t.isControlDown() &&
                        (t.getCode().isDigitKey())) {
                    lastKey = t.getText();
                    tp = ProductTable.getFocusModel().getFocusedCell();
                    ProductTable.edit(tp.getRow(), tp.getTableColumn());
                    lastKey = null;
                }
            }

            /*//I decided not to override the default tab behavior
            //using ctrl tab for cell traversal, but arrow keys are better
            if (t.isControlDown() && t.getCode() == KeyCode.TAB) {
                if (t.isShiftDown()) {
                    tv.getSelectionModel().selectLeftCell();
                } else {
                    tv.getSelectionModel().selectRightCell();
                }
                t.consume();
            }*/
        });
        totalCostFinal = new BigDecimal(DonationsT.getText());


        int i = 0;
        for (formattedProduct productOrder : order.orderData) {
            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
            formattedProductProps prodProps = new formattedProductProps(productOrder.productKey, productOrder.productID, productOrder.productName, productOrder.productSize, productOrder.productUnitPrice, productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
            data.add(prodProps);
            totalCostFinal = totalCostFinal.add(productOrder.extendedCost);
            i++;
        }
        if (!columnsFilled) {
            String[][] columnNames = {{"ID", "productID"}, {"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}};
            for (String[] column : columnNames) {
                TableColumn<formattedProductProps, String> tbCol = new TableColumn<>(column[0]);
                tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
                ProductTable.getColumns().add(tbCol);
            }
        }
        //{"Quantity", "orderedQuantity"}, {"Price", "extendedCost"}
        TableColumn<formattedProductProps, String> quantityCol = new TableColumn<>("Quantity");
        TableColumn<formattedProductProps, String> priceCol = new TableColumn<>("Price");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("orderedQuantityString"));

        quantityCol.setCellFactory(txtCellFactory);

        quantityCol.setOnEditCommit(t -> {
            //t.getTableView().getItems().get(t.getTablePosition().getRow()).orderedQuantity.set(Integer.valueOf(t.getNewValue()));
            try {
                int quantity = Integer.valueOf(t.getNewValue());

                BigDecimal unitCost = t.getTableView().getItems().get(t.getTablePosition().getRow()).productUnitPrice.get();
                //Removes $ from cost and multiplies to get the total cost for that item
                BigDecimal ItemTotalCost = unitCost.multiply(new BigDecimal(quantity));
                t.getRowValue().extendedCost.set(ItemTotalCost);
                t.getRowValue().orderedQuantity.set(quantity);
                t.getRowValue().orderedQuantityString.set(String.valueOf(quantity));

                data.get(t.getTablePosition().getRow()).orderedQuantity.set(quantity);
                data.get(t.getTablePosition().getRow()).extendedCost.set(ItemTotalCost);
                t.getTableView().refresh();
                totalCostFinal = new BigDecimal(DonationsT.getText());
                t.getTableView().getItems().forEach(item -> {
                    totalCostFinal = totalCostFinal.add(item.getExtendedCost());//Recalculate Utilities.Order total

                });
                runningTotalLabel.setText("Total: " + totalCostFinal.toPlainString());

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid number");
                alert.setHeaderText("You have entered an invalid number.");
                alert.show();
                t.getRowValue().productUnitPriceString.set(t.getOldValue());
                t.getTableView().getSelectionModel().selectAboveCell();

                t.getTableView().refresh();
            }

        });
        priceCol.setCellValueFactory(new PropertyValueFactory<>("extendedCost"));
        ProductTable.getColumns().addAll(quantityCol, priceCol);

        columnsFilled = true;

        ProductTable.setItems(data);

        //Fills original totals to calculate new values to insert in TOTALS table
        int preEditMulchSales = getNoMulchOrdered();
        int preEditLawnProductSales = getNoLawnProductsOrdered();
        int preEditLivePlantSales = getNoLivePlantsOrdered();
        runningTotalLabel.setText("Total: " + totalCostFinal.toPlainString());

    }

    /**
     * Commits table to the Database
     */
    private void commitChanges() {

        //ProgressDialog progDial = new ProgressDialog();
        ProgressForm progDial = new ProgressForm();
//Do check if new or not, send -1 as ID

        AddCustomerWorker addCustWork = new AddCustomerWorker(edit ? customerInfo.getId() : -1, Address.getText(),
                Town.getText(),
                State.getText(),
                year,
                ProductTable,
                Name.getText(),
                ZipCode.getText(),
                Phone.getText(),
                Email.getText(),
                DonationsT.getText().isEmpty() ? "0.0" : DonationsT.getText(),
                Objects.equals(NameEditCustomer, "") ? Name.getText() : NameEditCustomer,
                Paid.getText().isEmpty() ? "0.0" : Paid.getText(),
                Delivered.isSelected(),
                userCmbx.getSelectionModel().getSelectedItem());

        /*addCustWork.addPropertyChangeListener(event -> {
            switch (event.getPropertyName()) {
                case "progress":
                    progDial.progressBar.setIndeterminate(false);
                    progDial.progressBar.setValue((Integer) event.getNewValue());
                    break;
                case "state":
                    switch ((SwingWorker.StateValue) event.getNewValue()) {
                        case DONE:
                            try {
                                int success = addCustWork.get();
                                if (success == 1) {
                                    updateTots();
                                    dispose();
                                    setVisible(false);
                                }
                            } catch (CancellationException e) {
                                Utilities.LogToFile.log(e, Utilities.Severity.INFO, "The process was cancelled.");
                            } catch (Exception e) {
                                Utilities.LogToFile.log(e, Utilities.Severity.WARNING, "The process Failed.");
                            }
                            addCustWork = null;
                            progDial.dispose();
                            break;
                        case STARTED:
                        case PENDING:
                            progDial.progressBar.setVisible(true);
                            progDial.progressBar.setIndeterminate(true);
                            break;
                    }
                    break;
            }
        });*/
        progDial.activateProgressBar(addCustWork);
        addCustWork.setOnSucceeded(event -> {
            Pane newPane = null;
            FXMLLoader loader;
            progDial.getDialogStage().close();
//            updateTots();
            loader = new FXMLLoader(getClass().getResource("/UI/Year.fxml"));
            try {
                newPane = loader.load();
            } catch (IOException e) {
                LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
            }
            if (treeParent != null) {
                mainCont.addCustomerToTreeView(addCustWork.getValue(), treeParent);
            }
            YearController yearCont = loader.getController();
            yearCont.initYear(year, mainCont);
            if (parentTab == null) {
                parentStage.close();
            } else {
                mainCont.closeTab(parentTab);

            }
            //mainCont.addTab(newPane, "Year View - " + year);
        });
        addCustWork.setOnFailed(event -> {
            progDial.getDialogStage().close();

            Throwable e = addCustWork.getException();
            if (e instanceof addressException) {
                LogToFile.log(null, Severity.WARNING, "Invalid Address. Please Verify spelling and numbers are correct.");

            }
            if (e instanceof SQLException) {
                LogToFile.log((SQLException) e, Severity.SEVERE, CommonErrors.returnSqlMessage(((SQLException) addCustWork.getException())));

            }
            if (e instanceof InterruptedException) {
                if (addCustWork.isCancelled()) {
                    LogToFile.log((InterruptedException) e, Severity.FINE, "Add Customer process canceled.");

                }
            }
            if (e instanceof IOException) {
                LogToFile.log((IOException) e, Severity.WARNING, "Error contacting geolaction service. Please try again or contasct support.");
            }

        });


        progDial.getDialogStage().show();
        new Thread(addCustWork).start();
    }

    /**
     * Loops through Table to get total amount of Bulk Mulch ordered.
     *
     * @return The amount of Bulk mulch ordered
     */
    private int getNoMulchOrdered() {
        int quantMulchOrdered = 0;
        for (formattedProductProps aData : data) {
            if ((aData.getProductName().contains("Mulch")) && (aData.getProductName().contains("Bulk"))) {
                quantMulchOrdered += aData.getOrderedQuantity();
            }
        }
        return quantMulchOrdered;
    }

    /**
     * Loops through Table to get total amount of Lawn and Garden Products ordered.
     *
     * @return The amount of Lawn and Garden Products ordered
     */
    private int getNoLivePlantsOrdered() {
        int livePlantsOrdered = 0;
        for (formattedProductProps aData : data) {
            if (aData.getProductName().contains("-P") || aData.getProductName().contains("-FV")) {
                livePlantsOrdered += aData.getOrderedQuantity();
            }
        }
        return livePlantsOrdered;
    }

    /**
     * Loops through Table to get total amount of Lawn Products ordered.
     *
     * @return The amount of Live Plants ordered
     */
    private int getNoLawnProductsOrdered() {
        int lawnProductsOrdered = 0;
        for (formattedProductProps aData : data) {
            if (aData.getProductName().contains("-L")) {
                lawnProductsOrdered += aData.getOrderedQuantity();
            }
        }
        return lawnProductsOrdered;
    }

    /**
     * Calculates the amount of commission to be earned.
     *
     * @param totalCost the Sub total for all orders
     * @return Commission to be earned
     */
    private BigDecimal getCommission(BigDecimal totalCost) {
        BigDecimal commision = BigDecimal.ZERO;
        if ((totalCost.compareTo(new BigDecimal("299.99")) > 0) && (totalCost.compareTo(new BigDecimal("500.01")) < 0)) {
            commision = totalCost.multiply(new BigDecimal("0.05"));
        } else if ((totalCost.compareTo(new BigDecimal("500.01")) > 0) && (totalCost.compareTo(new BigDecimal("1000.99")) < 0)) {
            commision = totalCost.multiply(new BigDecimal("0.1"));
        } else if (totalCost.compareTo(new BigDecimal("1000")) >= 0) {
            commision = totalCost.multiply(new BigDecimal("0.15"));
        }
        return commision;
    }

    /**
     * Anaylizes if any text was entered into both the Address and Name field and if both are empty returns false, else true
     *
     * @return if required info was entered
     */
    private boolean infoEntered() {
        return !((Name.getText().isEmpty()) && (Address.getText().isEmpty()));
    }

    private class EditingCell<S, T> extends TableCell<S, T> {

        private TextField textField;

        @Override
        public void commitEdit(T item) {
            // This block is necessary to support commit on losing focus, because
            // the baked-in mechanism sets our editing state to false before we can
            // intercept the loss of focus. The default commitEdit(...) method
            // simply bails if we are not editing...
            if (!isEditing() && !item.equals(getItem())) {
                TableView<S> table = getTableView();
                if (table != null) {
                    TableColumn<S, T> column = getTableColumn();
                    TableColumn.CellEditEvent<S, T> event = new TableColumn.CellEditEvent<>(
                            table, new TablePosition<S, T>(table, getIndex(), column),
                            TableColumn.editCommitEvent(), item
                    );
                    Event.fireEvent(column, event);
                }
            }

            super.commitEdit(item);
        }

        private void commitEditString(String val) {
            commitEdit((T) val);
        }
        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                //setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                Platform.runLater(() -> {//without this space erases text, f2 doesn't
                    textField.requestFocus();//also selects
                });
                if (lastKey != null) {
                    textField.setText(lastKey);
                    Platform.runLater(() -> {
                        textField.deselect();
                        textField.end();
                    });
                }
            }
        }

        public void commit() {
            commitEditString(textField.getText());
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            try {
                setText(getItem().toString());
            } catch (Exception ignored) {}
            setGraphic(null);
        }

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
                if (getTableColumn().getText().equals("amount")) { setAlignment(Pos.CENTER_RIGHT); }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());

            //doesn't work if clicking a different cell, only focusing out of table
            textField.focusedProperty().addListener(
                    (ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
                        if (!arg2) commitEditString(textField.getText());
                    });

            textField.setOnKeyReleased((KeyEvent t) -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEditString(textField.getText());
                    EditingCell.this.getTableView().getSelectionModel().selectBelowCell();
                    EditingCell.this.getTableView().requestFocus();
                } else if (t.getCode() == KeyCode.RIGHT) {
                    getTableView().getSelectionModel().selectRightCell();
                    t.consume();
                } else if (t.getCode() == KeyCode.LEFT) {
                    getTableView().getSelectionModel().selectLeftCell();
                    t.consume();
                } else if (t.getCode() == KeyCode.UP) {
                    getTableView().getSelectionModel().selectAboveCell();
                    t.consume();
                } else if (t.getCode() == KeyCode.DOWN) {
                    getTableView().getSelectionModel().selectBelowCell();
                    t.consume();
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });

            textField.addEventFilter(KeyEvent.KEY_RELEASED, (KeyEvent t) -> {
                if (t.getCode() == KeyCode.DELETE) {
                    t.consume();//stop from deleting line in table keyevent
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

}
