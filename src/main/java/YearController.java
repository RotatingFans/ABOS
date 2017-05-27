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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import javax.swing.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class YearController {

    public static String year = "2017";
    @FXML
    private TableView yearOrders;
    @FXML
    private VBox yearInfo;
    private Boolean columnsFilled = false;
    private MainController mainController;

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Launch the application.
//     */
//    public static void main(String Years, String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    year = Years;
//                    Year window = new Year(Years);
//                    window.frame.setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    /**
     * Initialize the contents of the frame.
     */
    public void initYear(String Years, MainController mainCont) {
        year = Years;
        mainController = mainCont;
        Year yearDbInfo = new Year(year);
        yearInfo.getChildren().removeAll();
        //West

        VBox East = new VBox();
        HashMap<String, String> yearInfoStrings = new HashMap<>();
        yearInfoStrings.put("Donations", yearDbInfo.getDonations().toPlainString());
        yearInfoStrings.put("Lawn and Garden Products", Integer.toString(yearDbInfo.getLG()));
        yearInfoStrings.put("Live Plant Products", Integer.toString(yearDbInfo.getLP()));
        yearInfoStrings.put("Mulch", Integer.toString(yearDbInfo.getMulch()));
        yearInfoStrings.put("Order Total", yearDbInfo.getOT().toPlainString());
        yearInfoStrings.put("Grand Total", yearDbInfo.getGTot().toPlainString());
        yearInfoStrings.put("Commission", yearDbInfo.getCommis().toPlainString());
        yearInfoStrings.put("Customers", Integer.toString(yearDbInfo.getNoCustomers()));


        yearInfoStrings.forEach((key, val) -> {
            Label keyLabel = new Label(key + ":");
            Label valLabel = new Label(val);
            keyLabel.setId("YearDescription");
            valLabel.setId("YearValue");
            yearInfo.getChildren().add(new VBox(keyLabel, valLabel));
        });


        fillTable();
    }

    public void initialize() {

    }

    @FXML
    public void refresh(ActionEvent event) {
        initialize();
    }

    @FXML
    public void deleteYear(ActionEvent event) {

        String message = "<html><head><style>" +
                "h3 {text-align:center;}" +
                "h4 {text-align:center;}" +
                "</style></head>" +
                "<body><h3>WARNING !</h3>" +
                "<h3>You are about to delete an entire Year.</h3>" +
                "<h3>This action is irreversible.</h3>" +
                "<h4>Would you like to continue with the deletion?</h4>" +
                "</body>" +
                "</html>";
        int cont = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        if (cont == 0) {
            DbInt.deleteDb(year);
            try (PreparedStatement prep = DbInt.getPrep("Set", "DELETE FROM YEARS WHERE YEARS=?")) {
                prep.setString(1, year);
                prep.execute();
            } catch (SQLException Se) {
                LogToFile.log(Se, Severity.SEVERE, CommonErrors.returnSqlMessage(Se));
            }



            mainController.fillTreeView();
        }
    }

    @FXML
    public void editYear(ActionEvent event) {
        new AddYear(year);
    }

    /**
     * Fills the Table of order amounts
     */
    private void fillTable() {
        Order.orderArray order = new Order().createOrderArray(year);
        ObservableList<Product.formattedProductProps> data = FXCollections.observableArrayList();

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
                yearOrders.getColumns().add(tbCol);
            }
        }
        columnsFilled = true;


        yearOrders.setItems(data);

    }


}

