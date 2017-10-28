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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
@SuppressWarnings("WeakerAccess")

public class CustomerController {
    public static String year = "2017";
    //public String year;
    private String name;
    //TODO Add Active search with only results shown

    private Customer customerDbInfo;
    @FXML
    private VBox customerInfo;
    @FXML
    private TableView customerOrders;
    private String totQuant = "0";
    private String totCost = "$0.00";
    private Boolean columnsFilled = false;
    private ObservableList<Product.formattedProductProps> data;
    private MainController mainController;

    public void initCustomer(String cYear, String cName, MainController mainCont) {
        year = cYear;
        name = cName;
        mainController = mainCont;
        customerDbInfo = new Customer(name, year);
        fillTable();

        //frame.setTitle("ABOS - Customer View - " + name + " - " + year);

        List<infoValPair> customerInfoStrings = new ArrayList<>();
        customerInfoStrings.add(new infoValPair("Name", name));
        customerInfoStrings.add(new infoValPair("Address", customerDbInfo.getAddr()));
        customerInfoStrings.add(new infoValPair("Phone #", customerDbInfo.getPhone()));
        customerInfoStrings.add(new infoValPair("Email", customerDbInfo.getEmail()));
        customerInfoStrings.add(new infoValPair("Paid", customerDbInfo.getPaid()));
        customerInfoStrings.add(new infoValPair("Delivered", customerDbInfo.getDelivered()));
        customerInfoStrings.add(new infoValPair("Total Quantity", totQuant));
        customerInfoStrings.add(new infoValPair("Total Cost", totCost));


        customerInfoStrings.forEach((pair) -> {
            javafx.scene.control.Label keyLabel = new javafx.scene.control.Label(pair.info + ":");
            javafx.scene.control.Label valLabel = new javafx.scene.control.Label(pair.value);
            keyLabel.setId("CustomerDescription");
            valLabel.setId("CustomerValue");
            customerInfo.getChildren().add(new VBox(keyLabel, valLabel));
        });


    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {


    }

    @FXML
    public void editCustomer(ActionEvent event) {
        mainController.openEditCustomer(year, customerDbInfo.getName());
    }

    @FXML
    public void refresh(ActionEvent event) {
        initialize();
    }

    private void fillTable() {
        Order.orderArray order = new Order().createOrderArray(year, name, true);
        data = FXCollections.observableArrayList();

        int i = 0;
        for (Product.formattedProduct productOrder : order.orderData) {
            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
            Product.formattedProductProps prodProps = new Product.formattedProductProps(productOrder.productID, productOrder.productName, productOrder.productSize, productOrder.productUnitPrice, productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
            data.add(prodProps);
            i++;
        }
        if (!columnsFilled) {
            String[][] columnNames = {{"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}, {"Quantity", "orderedQuantity"}, {"Price", "extendedCost"}};
            for (String[] column : columnNames) {
                TableColumn<Product.formattedProductProps, String> tbCol = new TableColumn<>(column[0]);
                tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
                customerOrders.getColumns().add(tbCol);
            }
        }
        columnsFilled = true;


        customerOrders.setItems(data);

        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


        totQuant = (Integer.toString(order.totalQuantity));
        totCost = ("$" + order.totalCost.toPlainString());

    }

    @FXML
    private void deleteCustomer(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("WARNING!");
        alert.setHeaderText("BY CONTINUING YOU ARE PERMANENTLY REMOVING A CUSTOMER! ALL DATA MUST BE REENTERED!");
        alert.setContentText("Would you like to continue?");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            fillTable();
            BigDecimal preEditOrderCost = BigDecimal.ZERO;
            Order.orderArray order = new Order().createOrderArray(year, customerDbInfo.getName(), false);
            for (Product.formattedProduct productOrder : order.orderData) {
                preEditOrderCost = preEditOrderCost.add(productOrder.extendedCost);
            }
            Year yearInfo = new Year(year);
            BigDecimal donations = yearInfo.getDonations().subtract(customerDbInfo.getDontation());
            int Lg = yearInfo.getLG() - getNoLawnProductsOrdered();
            int LP = yearInfo.getLP() - getNoLivePlantsOrdered();
            int Mulch = yearInfo.getMulch() - getNoMulchOrdered();
            BigDecimal OT = yearInfo.getOT().subtract(preEditOrderCost);
            int Customers = (yearInfo.getNoCustomers() - 1);
            BigDecimal GTot = yearInfo.getGTot().subtract(preEditOrderCost.add(customerDbInfo.getDontation()));
            BigDecimal Commis = getCommission(GTot);
            try (PreparedStatement totalInsertString = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(?,?,?,?,?,?,?,?)")) {
                totalInsertString.setBigDecimal(1, (donations.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                totalInsertString.setInt(2, Lg);
                totalInsertString.setInt(3, (LP));
                totalInsertString.setInt(4, (Mulch));
                totalInsertString.setBigDecimal(5, (OT.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                totalInsertString.setInt(6, (Customers));
                totalInsertString.setBigDecimal(7, (Commis.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                totalInsertString.setBigDecimal(8, (GTot.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                totalInsertString.execute();

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Could not update year totals. Please delete and recreate the order.");
            }
            try (PreparedStatement prep = DbInt.getPrep(year, "DELETE FROM ORDERS WHERE NAME=?")) {

                prep.setString(1, name);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Error deleting customer. Try again or contact support.");
            }
            try (PreparedStatement prep = DbInt.getPrep(year, "DELETE FROM Customers WHERE NAME=?")) {

                prep.setString(1, name);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Error deleting customer. Try again or contact support.");
            }
            mainController.fillTreeView();
        }
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

    // --Commented out by Inspection START (1/2/2016 12:01 PM):
//    public void setTable(JTable customerOrders) {
//        this.customerOrders = customerOrders;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)
    private class infoValPair {
        public String info;
        public String value;

        public infoValPair(String inf, String val) {
            this.info = inf;
            this.value = val;
        }
    }

}
