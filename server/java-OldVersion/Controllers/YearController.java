/*******************************************************************************
 * ABOS
 * Copyright (C) 2018 Patrick Magauran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package Controllers;

import Utilities.Order;
import Utilities.Year;
import Utilities.formattedProduct;
import Utilities.formattedProductProps;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//import javax.swing.*;
@SuppressWarnings("WeakerAccess")

public class YearController {

    public static String year = "2017";
    @FXML
    private TableView yearOrders;
    @FXML
    private VBox yearInfo;
    private Boolean columnsFilled = false;
    private String uName;

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Launch the application.
//     */
//    public static void main(String Years, String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    year = Years;
//                    Utilities.Year window = new Utilities.Year(Years);
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
    public void initYear(String Years) {
        this.initYear(Years, "");
    }

    /**
     * Initialize the contents of the frame.
     */
    public void initYear(String Years, String userName) {
        year = Years;
        uName = userName;
        Year yearDbInfo = new Year(year, userName);
        yearInfo.getChildren().removeAll();
        //West

        VBox East = new VBox();
        List<infoValPair> yearInfoStrings = new ArrayList<>();
        yearInfoStrings.add(new infoValPair("customers", Integer.toString(yearDbInfo.getNoCustomers())));
        //yearDbInfo.getCategories().forEach(category -> yearInfoStrings.add(new infoValPair(category.catName + " Products", Integer.toString(yearDbInfo.getLG()))));
        /*yearInfoStrings.add(new infoValPair("Lawn and Garden Products", Integer.toString(yearDbInfo.getLG())));
        yearInfoStrings.add(new infoValPair("Live Plant Products", Integer.toString(yearDbInfo.getLP())));
        yearInfoStrings.add(new infoValPair("Mulch", Integer.toString(yearDbInfo.getMulch())));*/
        yearInfoStrings.add(new infoValPair("Order Total", yearDbInfo.getOT().toPlainString()));
        yearInfoStrings.add(new infoValPair("Donations", yearDbInfo.getDonations().toPlainString()));
        yearInfoStrings.add(new infoValPair("Grand Total", yearDbInfo.getGTot().toPlainString()));
        yearInfoStrings.add(new infoValPair("Commission", yearDbInfo.getCommis().toPlainString()));


        yearInfoStrings.forEach((pair) -> {
            Label keyLabel = new Label(pair.info + ":");
            Label valLabel = new Label(pair.value);
            keyLabel.setId("YearDescription");
            valLabel.setId("YearValue");
            VBox info = new VBox(keyLabel, valLabel);
            info.getStyleClass().add("informationPane");
            yearInfo.getChildren().add(info);
        });


        fillTable();
    }

    public void initialize() {

    }

    @FXML
    public void refresh(ActionEvent event) {
        initialize();
    }

/*    @FXML
    public void deleteYear(ActionEvent event) {
        Utilities.Year yearObj = new Utilities.Year(year);
        yearObj.deleteYear();
        mainController.fillTreeView();

    }

    @FXML
    public void editYear(ActionEvent event) {
        new Launchers.AddYear(year, mainController.getWindow());
    }*/

    /**
     * Fills the Table of order amounts
     */
    private void fillTable() {
        Order.orderArray order;
        if (Objects.equals(uName, "")) {
            order = Order.createOrderArray(year, "");

        } else {
            order = Order.createOrderArray(year, uName);

        }
        ObservableList<formattedProductProps> data = FXCollections.observableArrayList();

        int i = 0;
        for (formattedProduct productOrder : order.orderData) {
            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
            formattedProductProps prodProps = new formattedProductProps(productOrder.productKey, productOrder.productID, productOrder.productName, productOrder.productSize, productOrder.productUnitPrice, productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
            data.add(prodProps);
            i++;
        }
        if (!columnsFilled) {
            String[][] columnNames = {{"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}, {"Quantity", "orderedQuantity"}, {"Price", "extendedCost"}};
            for (String[] column : columnNames) {
                TableColumn<formattedProductProps, String> tbCol = new TableColumn<>(column[0]);
                tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
                yearOrders.getColumns().add(tbCol);
            }
        }
        columnsFilled = true;


        yearOrders.setItems(data);
        yearOrders.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private class infoValPair {
        public String info;
        public String value;

        public infoValPair(String inf, String val) {
            this.info = inf;
            this.value = val;
        }
    }

}

