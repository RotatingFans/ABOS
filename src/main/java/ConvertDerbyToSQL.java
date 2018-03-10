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
//import ABOS.Derby.DbInt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//import ABOS.Derby.Product;

public class ConvertDerbyToSQL extends Application {
    private static String derbyLocation;
    private static String userName;
    private static String password;
    private static String fullName;
    private static String adminUser;
    private static String adminPass;
    private ObservableList<formattedProductProps> data;
    private Boolean columnsFilled = false;

    public static void main(String[] args) {
        derbyLocation = args[0];
        userName = args[1];
        password = args[2];
        fullName = args[3];
        adminUser = args[4];
        adminPass = args[5];

        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        Convert();
        stage.close();
        Platform.exit();
    }

    public void Convert() {
        try {

            ABOS.Derby.DbInt.DbLoc = derbyLocation;
            Iterable<String> ret = ABOS.Derby.DbInt.getYears();
            Set<String> years = new HashSet<>();

            Boolean newUser = !DbInt.verifyLogin(new Pair<>(userName, password));
            if (!DbInt.verifyLoginAndUser(new Pair<>(adminUser, adminPass))) {
                throw new Exception("Invalid Admin Username/Password");

            }
            if (newUser) {
                User.createUser(userName, password, "", true);
            } else {
                User.updateUser(userName, password, "", true);

            }

            ArrayList<ArrayList<String>> yearUsers = new ArrayList<>();


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
            for (String year : ret) {
                ArrayList<String> usersManage = new ArrayList<>();
                usersManage.add(userName);
                years.add(year);

                ABOS.Derby.Year yearObj = new ABOS.Derby.Year(year);
                Year yearNew = new Year(year);
                Collection<Year.category> rowCats = new ArrayList<>();
                ObservableList<formattedProductProps> productList = FXCollections.observableArrayList();
                yearObj.getCategories().forEach(category -> {
                    rowCats.add(new Year.category(category.catName, category.catDate));
                });
                int productKey = 1;
                for (ABOS.Derby.Product.formattedProduct product : yearObj.getAllProducts()) {
                    productList.add(new formattedProductProps(productKey++, product.productID, product.productName, product.productSize, new BigDecimal(product.productUnitPrice.replace("$", "")), product.productCategory, product.orderedQuantity, product.extendedCost));
                }

                yearNew.CreateDb(productList, rowCats);
                User yearUser = new User(userName, fullName, usersManage, years, true, 1);
                DbInt.verifyLoginAndUser(new Pair<>(userName, password));
                try (Connection con = DbInt.getConnection(year);
                     PreparedStatement prep = con.prepareStatement("DELETE FROM users WHERE userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    prep.setString(1, userName);
                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
                if (newUser) {
                    yearUser.addToYear(year);
                } else {
                    yearUser.updateYear(year);
                }
                {


                    Iterable<String> customers = yearObj.getCustomerNames();
                    for (String customer : customers) {
                        TableView productTable = new TableView();
                        ABOS.Derby.Customer customerDbInfo = new ABOS.Derby.Customer(customer, year);
                        Integer cID = customerDbInfo.getId();
                        ABOS.Derby.Order.orderArray order = new ABOS.Derby.Order().createOrderArray(year, customerDbInfo.getName(), false);
                        data = FXCollections.observableArrayList();

                        int i = 1;
                        for (ABOS.Derby.Product.formattedProduct productOrder : order.orderData) {
                            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
                            formattedProductProps prodProps = new formattedProductProps(i, productOrder.productID, productOrder.productName, productOrder.productSize, new BigDecimal(productOrder.productUnitPrice.replace("$", "")), productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
                            data.add(prodProps);
                            i++;
                        }
                        if (!columnsFilled) {
                            String[][] columnNames = {{"ID", "productID"}, {"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}};
                            for (String[] column : columnNames) {
                                TableColumn<ABOS.Derby.Product.formattedProductProps, String> tbCol = new TableColumn<>(column[0]);
                                tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
                                productTable.getColumns().add(tbCol);
                            }
                        }
                        //{"Quantity", "orderedQuantity"}, {"Price", "extendedCost"}
                        TableColumn<ABOS.Derby.Product.formattedProductProps, String> quantityCol = new TableColumn<>("Quantity");
                        TableColumn<ABOS.Derby.Product.formattedProductProps, String> priceCol = new TableColumn<>("Price");
                        quantityCol.setCellValueFactory(new PropertyValueFactory<>("orderedQuantityString"));

                        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn());

                        quantityCol.setOnEditCommit(t -> {
                            //t.getTableView().getItems().get(t.getTablePosition().getRow()).orderedQuantity.set(Integer.valueOf(t.getNewValue()));
                            int quantity = Integer.valueOf(t.getNewValue());
                            BigDecimal unitCost = new BigDecimal(t.getTableView().getItems().get(t.getTablePosition().getRow()).productUnitPrice.get().replaceAll("\\$", ""));
                            //Removes $ from cost and multiplies to get the total cost for that item
                            BigDecimal ItemTotalCost = unitCost.multiply(new BigDecimal(quantity));
                            t.getRowValue().extendedCost.set(ItemTotalCost);
                            t.getRowValue().orderedQuantity.set(quantity);
                            t.getRowValue().orderedQuantityString.set(String.valueOf(quantity));

                            data.get(t.getTablePosition().getRow()).orderedQuantity.set(quantity);
                            data.get(t.getTablePosition().getRow()).extendedCost.set(ItemTotalCost);
                            t.getTableView().refresh();


                        });
                        priceCol.setCellValueFactory(new PropertyValueFactory<>("extendedCost"));
                        productTable.getColumns().addAll(quantityCol, priceCol);

                        columnsFilled = true;

                        productTable.setItems(data);

                        //Fills original totals to calculate new values to insert in TOTALS table


                        {
                            ProgressForm progDial = new ProgressForm();

                            AddCustomerWorker addCustWork = new AddCustomerWorker(-1, customerDbInfo.getAddr(),
                                    customerDbInfo.getTown(),
                                    customerDbInfo.getState(),
                                    year,
                                    productTable,
                                    customerDbInfo.getName(),
                                    customerDbInfo.getZip(),
                                    customerDbInfo.getPhone(),
                                    customerDbInfo.getEmail(),
                                    customerDbInfo.getDontation().toPlainString(),
                                    customerDbInfo.getName(),
                                    Boolean.valueOf(customerDbInfo.getPaid()),
                                    Boolean.valueOf(customerDbInfo.getDelivered()),
                                    userName);

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
                    }
                }
            }

        } catch (Exception e) {
            LogToFile.log(e, Severity.SEVERE, "Something went wrong converting Database. See log for details.");

        }
    }
}
