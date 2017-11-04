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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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
    private static boolean edit = false; //States whether this is an edit or creation of a customer.
    private Year yearInfo;
    //private final JPanel contentPanel = new JPanel();
    //Editable Field for user to input customer info.
    @FXML
    private CheckBox Delivered;
    @FXML
    private CheckBox Paid;
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
    private Button okButton;
    private Tab parentTab = null;
    private Stage parentStage = null;
    private Pane tPane;

    //Variables used to store regularly accessed info.
    private String year = null;
    private BigDecimal totalCostFinal = BigDecimal.ZERO;
    //Variables used to calculate difference of orders when in edit mode.
    private String NameEditCustomer = null;
    private int preEditMulchSales = 0;
    private int preEditLawnProductSales = 0;
    private int preEditLivePlantSales = 0;
    private BigDecimal preEditDonations = BigDecimal.ZERO;
    private BigDecimal preEditOrderCost = BigDecimal.ZERO;
    private Customer customerInfo = null;
    private int newCustomer = 0;
    private Boolean columnsFilled = false;
    private ObservableList<Product.formattedProductProps> data;
    private MainController mainCont;

    /**
     * Used to open dialog with already existing customer information from year as specified in Customer Report.
     *
     * @param customerName the name of the customer being edited.
     */
    public void initAddCust(String aYear, String customerName, MainController mainController, Tab parent) {

        mainCont = mainController;
        parentTab = parent;
        year = aYear;
        yearInfo = new Year(year);
        customerInfo = new Customer(customerName, year);
        edit = true;

        //Set the address
        String[] addr = customerInfo.getCustAddressFrmName();
        String city = addr[0];
        String state = addr[1];
        String zip = addr[2];
        String streetAdd = addr[3];
        //Fill in Customer info fields.
        Address.setText(streetAdd);
        Town.setText(city);
        State.setText(state);
        ZipCode.setText(zip);
        Phone.setText(customerInfo.getPhone());
        Paid.setSelected(customerInfo.getPaid());
        Delivered.setSelected(customerInfo.getDelivered());
        Email.setText(customerInfo.getEmail());
        Name.setText(customerName);
        DonationsT.setText(customerInfo.getDontation().toPlainString());
        preEditDonations = customerInfo.getDontation();
        //Fill the table with their previous order info on record.
        fillOrderedTable();

        NameEditCustomer = customerName;
        edit = true;
        //Add a Event to occur if a cell is changed in the table

    }

    public void initAddCust(String aYear, MainController mainController, Stage parent) {
        parentStage = parent;
        initAddCust(aYear, mainController, (Tab) null);
    }

    public void initAddCust(String aYear, MainController mainController, Tab parent) {
        mainCont = mainController;
        newCustomer = 1;
        parentTab = parent;
        NameEditCustomer = "";
        year = aYear;
        yearInfo = new Year(year);

        Name.setText(Config.getProp("CustomerName"));

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

        Paid.setSelected(Boolean.valueOf(Config.getProp("CustomerPaid")));
        Delivered.setSelected(Boolean.valueOf(Config.getProp("CustomerDelivered")));

        DonationsT.setText((Config.getProp("CustomerDonation") != null) ? Config.getProp("CustomerDonation") : "0.0");
        fillTable();

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
                Stage stage = (Stage) okButton.getScene().getWindow();

                stage.close();
                //close
                // ... user chose OK
            }
        }
    }

    /**
     * Fills the table with quantitys set to 0.
     */
    private void fillTable() {

        Product.formattedProduct[] productArray = yearInfo.getAllProducts();
        Object[][] rows = new Object[productArray.length][6];
        data = FXCollections.observableArrayList();

        int i = 0;
        for (Product.formattedProduct productOrder : productArray) {
            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
            Product.formattedProductProps prodProps = new Product.formattedProductProps(productOrder.productKey, productOrder.productID, productOrder.productName, productOrder.productSize, productOrder.productUnitPrice, productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
            data.add(prodProps);
            i++;
        }
        if (!columnsFilled) {
            String[][] columnNames = {{"ID", "productID"}, {"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}};
            for (String[] column : columnNames) {
                TableColumn<Product.formattedProductProps, String> tbCol = new TableColumn<>(column[0]);
                tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
                ProductTable.getColumns().add(tbCol);
            }
        }
        //{"Quantity", "orderedQuantity"}, {"Price", "extendedCost"}
        TableColumn<Product.formattedProductProps, String> quantityCol = new TableColumn<>("Quantity");
        TableColumn<Product.formattedProductProps, String> priceCol = new TableColumn<>("Price");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("orderedQuantityString"));

        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn());

        quantityCol.setOnEditCommit(t -> {
            //t.getTableView().getItems().get(t.getTablePosition().getRow()).orderedQuantity.set(Integer.valueOf(t.getNewValue()));
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
            totalCostFinal = BigDecimal.ZERO;
            t.getTableView().getItems().forEach(item -> {
                totalCostFinal = totalCostFinal.add(item.getExtendedCost());//Recalculate Order total

            });

        });
        priceCol.setCellValueFactory(new PropertyValueFactory<>("extendedCost"));
/*        priceCol.setCellValueFactory(cellData -> {
            Product.formattedProductProps data = cellData.getValue();
            return Bindings.createStringBinding(
                    () -> {
                        try {
                            BigDecimal price = new BigDecimal(data.getProductUnitPrice());
                            int quantity = data.getOrderedQuantity();
                            return price.multiply(new BigDecimal(quantity)).toPlainString();
                        } catch (NumberFormatException nfe) {
                            return "0.0" ;
                        }
                    },
                    data.productUnitPrice,
                    data.orderedQuantity
            );
        });*/

        ProductTable.getColumns().addAll(quantityCol, priceCol);

        columnsFilled = true;

        ProductTable.setItems(data);


    }

    /**
     * Fills product table with info with quantities set to Amount customer ordered.
     */
    private void fillOrderedTable() {
        Order.orderArray order = new Order().createOrderArray(year, customerInfo.getName(), false);
        data = FXCollections.observableArrayList();

        int i = 0;
        for (Product.formattedProduct productOrder : order.orderData) {
            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
            Product.formattedProductProps prodProps = new Product.formattedProductProps(productOrder.productKey, productOrder.productID, productOrder.productName, productOrder.productSize, productOrder.productUnitPrice, productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
            data.add(prodProps);
            i++;
        }
        if (!columnsFilled) {
            String[][] columnNames = {{"ID", "productID"}, {"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}};
            for (String[] column : columnNames) {
                TableColumn<Product.formattedProductProps, String> tbCol = new TableColumn<>(column[0]);
                tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
                ProductTable.getColumns().add(tbCol);
            }
        }
        //{"Quantity", "orderedQuantity"}, {"Price", "extendedCost"}
        TableColumn<Product.formattedProductProps, String> quantityCol = new TableColumn<>("Quantity");
        TableColumn<Product.formattedProductProps, String> priceCol = new TableColumn<>("Price");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("orderedQuantityString"));

        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn());

        quantityCol.setOnEditCommit(t -> {
            //t.getTableView().getItems().get(t.getTablePosition().getRow()).orderedQuantity.set(Integer.valueOf(t.getNewValue()));
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
            totalCostFinal = BigDecimal.ZERO;
            t.getTableView().getItems().forEach(item -> {
                totalCostFinal = totalCostFinal.add(item.getExtendedCost());//Recalculate Order total

            });

        });
        priceCol.setCellValueFactory(new PropertyValueFactory<>("extendedCost"));
        ProductTable.getColumns().addAll(quantityCol, priceCol);

        columnsFilled = true;

        ProductTable.setItems(data);

        //Fills original totals to calculate new values to insert in TOTALS table
        preEditMulchSales = getNoMulchOrdered();
        preEditLawnProductSales = getNoLawnProductsOrdered();
        preEditLivePlantSales = getNoLivePlantsOrdered();

    }

    /**
     * Commits table to the Database
     */
    private void commitChanges() {

        //ProgressDialog progDial = new ProgressDialog();
        ProgressForm progDial = new ProgressForm();

        AddCustomerWorker addCustWork = new AddCustomerWorker(Address.getText(),
                Town.getText(),
                State.getText(),
                year,
                ProductTable,
                Name.getText(),
                ZipCode.getText(),
                Phone.getText(),
                Email.getText(),
                DonationsT.getText(),
                Objects.equals(NameEditCustomer, "") ? Name.getText() : NameEditCustomer,
                Paid.isSelected(),
                Delivered.isSelected());

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
                                LogToFile.log(e, Severity.INFO, "The process was cancelled.");
                            } catch (Exception e) {
                                LogToFile.log(e, Severity.WARNING, "The process Failed.");
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
            loader = new FXMLLoader(getClass().getResource("UI/Year.fxml"));
            try {
                newPane = loader.load();
            } catch (IOException e) {
                LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
            }
            YearController yearCont = loader.getController();
            yearCont.initYear(year, mainCont);
            if (parentTab == null) {
                parentStage.close();
            } else {
                mainCont.closeTab(parentTab);

            }
            mainCont.addTab(newPane, "Year View - " + year);

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
        addCustWork.run();

    }

    /**
     * Loops through Table to get total amount of Bulk Mulch ordered.
     *
     * @return The amount of Bulk mulch ordered
     */
    private int getNoMulchOrdered() {
        int quantMulchOrdered = 0;
        for (Product.formattedProductProps aData : data) {
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
        for (Product.formattedProductProps aData : data) {
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
        for (Product.formattedProductProps aData : data) {
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

    /**
     * Updates the totals tables
     *//*
    private void updateTots() {
        *//*
          get current totals
          add to them
          update
         *//*
        BigDecimal donationChange = new BigDecimal((Objects.equals(DonationsT.getText(), "")) ? "0" : DonationsT.getText()).subtract(preEditDonations);
        BigDecimal donations = yearInfo.getDonations().add(donationChange);
        int Lg = yearInfo.getLG() + (getNoLawnProductsOrdered() - preEditLawnProductSales);
        int LP = yearInfo.getLP() + (getNoLivePlantsOrdered() - preEditLivePlantSales);
        int Mulch = yearInfo.getMulch() + (getNoMulchOrdered() - preEditMulchSales);
        BigDecimal OT = yearInfo.getOT().add(totalCostFinal.subtract(preEditOrderCost));
        int Customers = (yearInfo.getNoCustomers() + newCustomer);
        BigDecimal GTot = yearInfo.getGTot().add(totalCostFinal.subtract(preEditOrderCost).add(donationChange));
        BigDecimal Commis = getCommission(GTot);
        yearInfo.updateTots(donations, Lg, LP, Mulch, OT, Customers, Commis, GTot);
    }*/


}
