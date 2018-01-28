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

package ABOS.Derby;

import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by patrick on 7/27/16.
 */
public class Order {
    // --Commented out by Inspection (7/27/16 3:02 PM):Product product = new Product();
    private BigDecimal totL = BigDecimal.ZERO;
    private int QuantL = 0;

    public static String updateOrder(ObservableList<Product.formattedProductProps> orders, String name, String year, String preEditName, updateProgCallback updateProg, failCallback fail, updateMessageCallback updateMessage, getProgCallback getProgress) throws Exception {
        List<String> Ids = new ArrayList<>();
        int numRows = orders.size();
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT ORDERID FROM ORDERS WHERE NAME=?")) {

            prep.setString(1, preEditName);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    Ids.add(rs.getString(1));

                }
            }
        }
        int progressDivisor = (2 * numRows);
        int progressIncrement = 50 / progressDivisor;
        updateProg.doAction(getProgress.doAction() + progressIncrement, 100);
        fail.doAction();

        //Inserts into customer table for year
        //Edit mode
        if (Ids.size() > 0) {
            updateMessage.doAction("Building Order Update");

            StringBuilder UpdateOrderString = new StringBuilder("UPDATE ORDERS SET NAME=?");
            //loops through table and adds product number to order string with "=?"
            for (int i = 0; i < numRows; i++) {
                UpdateOrderString.append(", \"");
                UpdateOrderString.append(i);
                UpdateOrderString.append("\"=?");
                updateProg.doAction(getProgress.doAction() + progressIncrement, 100);
            }
            fail.doAction();

            //Uses string to create PreparedStatement that is filled with quantities from table.
            UpdateOrderString.append(" WHERE NAME = ?");
            try (PreparedStatement updateOrders = DbInt.getPrep(year, UpdateOrderString.toString())) {
                updateOrders.setString(1, name);
                for (int i = 0; i < numRows; i++) {
                    updateOrders.setString(i + 2, String.valueOf(orders.get(i).getOrderedQuantity()));
                    updateProg.doAction(getProgress.doAction() + progressIncrement, 100);
                }
                fail.doAction();

                updateOrders.setString(numRows + 2, preEditName);
                updateMessage.doAction("Running Update");

                updateOrders.execute();
            }
        } //Insert Mode
        else {
            updateMessage.doAction("Building Order");

            StringBuilder InsertOrderStringBuilder = new StringBuilder("INSERT INTO ORDERS(NAME VALUES(?");

            progressDivisor = 2 * numRows;
            progressIncrement = 50 / progressDivisor;
            //Loops through And adds product numbers to Order string
            int insertProductNumberHere = InsertOrderStringBuilder.length() - 9;

            for (int i = 0; i < numRows; i++) {
                updateProg.doAction(getProgress.doAction() + progressIncrement, 100);
                InsertOrderStringBuilder.insert(insertProductNumberHere, ",\"");
                InsertOrderStringBuilder.insert(insertProductNumberHere + 2, i);
                InsertOrderStringBuilder.insert(insertProductNumberHere + 2 + IntegerLength(i), '"');
                insertProductNumberHere += 3 + IntegerLength(i);
                InsertOrderStringBuilder.append(",?");
            }
            InsertOrderStringBuilder.insert(insertProductNumberHere, ") ");
            InsertOrderStringBuilder.append(')');

            fail.doAction();

            //Creates prepared Statement and replaces ? with quantities and names
            try (PreparedStatement writeOrd = DbInt.getPrep(year, InsertOrderStringBuilder.toString())) {
                writeOrd.setString(1, name);
                for (int i = 0; i < numRows; i++) {
                    writeOrd.setString(i + 2, String.valueOf(orders.get(i).getOrderedQuantity()));

                    updateProg.doAction(getProgress.doAction() + progressIncrement, 100);
                }
                fail.doAction();
                updateMessage.doAction("Adding Order");

                writeOrd.executeUpdate();
            }
        }
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT ORDERID FROM ORDERS WHERE NAME=?")) {

            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    Ids.add(rs.getString(1));

                }
            }
        }
        return Ids.get(Ids.size() - 1);
    }

    private static int IntegerLength(int n) {
        if (n < 100000) {
            // 5 or less
            if (n < 100) {
                // 1 or 2
                if (n < 10) { return 1; } else { return 2; }
            } else {
                // 3 or 4 or 5
                if (n < 1000) { return 3; } else {
                    // 4 or 5
                    if (n < 10000) { return 4; } else { return 5; }
                }
            }
        } else {
            // 6 or more
            if (n < 10000000) {
                // 6 or 7
                if (n < 1000000) { return 6; } else { return 7; }
            } else {
                // 8 to 10
                if (n < 100000000) { return 8; } else {
                    // 9 or 10
                    if (n < 1000000000) { return 9; } else { return 10; }
                }
            }
        }

    }

    public orderArray createOrderArray(String year, String name, Boolean excludeZeroOrders) {
        return createOrderArray(year, name, excludeZeroOrders, "*");
    }

    public orderArray createOrderArray(String year, String name, Boolean excludeZeroOrders, String Category) {
        Customer customer = new Customer(name, year);
        List<Product> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data
        try (PreparedStatement prep = DbInt.getPrep(year, Objects.equals(Category, "*") ? "SELECT * FROM PRODUCTS" : "SELECT * FROM PRODUCTS WHERE Category = ?")
        ) {
            if (!Objects.equals(Category, "*")) {
                prep.setString(1, Category);
            }
            ResultSet ProductInfoResultSet = prep.executeQuery();
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {
                ProductInfoArray.add(new Product(ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("PNAME"), ProductInfoResultSet.getString("SIZE"), ProductInfoResultSet.getString("UNIT"), ProductInfoResultSet.getString("Category")));
                DbInt.pCon.commit();

            }
            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        //Table rows array
        Product.formattedProduct[] allProducts = new Product.formattedProduct[ProductInfoArray.size()];
        String OrderID = customer.getOrderId();
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity
        for (int i = 0; i < ProductInfoArray.size(); i++) {

            int quant = 0;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ORDERS WHERE NAME=?")) {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, name);
                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {
                        quant = Integer.parseInt(rs.getString(String.valueOf(i)));
                        //DbInt.pCon.close();
                    }
                }

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Fills row array for table with info


            if (((quant > 0) && excludeZeroOrders) || !excludeZeroOrders) {
                BigDecimal unitCost = new BigDecimal(ProductInfoArray.get(i).productUnitPrice.replaceAll("\\$", ""));
                allProducts[noProductsOrdered] = new Product.formattedProduct(ProductInfoArray.get(i).productID, ProductInfoArray.get(i).productName, ProductInfoArray.get(i).productSize, ProductInfoArray.get(i).productUnitPrice, ProductInfoArray.get(i).productCategory, quant, unitCost.multiply(new BigDecimal(quant)));
                totL = unitCost.multiply(new BigDecimal(quant)).add(totL);
                QuantL += quant;
                noProductsOrdered++;

            }
        }
        //Re create rows to remove blank rows
        Product.formattedProduct[] orderedProducts = new Product.formattedProduct[noProductsOrdered];

        System.arraycopy(allProducts, 0, orderedProducts, 0, noProductsOrdered);
        return new orderArray(orderedProducts, totL, QuantL);


    }

    public orderArray createOrderArray(String year) {

        List<Product> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {
                ProductInfoArray.add(new Product(ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("PNAME"), ProductInfoResultSet.getString("SIZE"), ProductInfoResultSet.getString("UNIT"), ProductInfoResultSet.getString("Category")));
                DbInt.pCon.commit();

            }
            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        //Table rows array
        Product.formattedProduct[] allProducts = new Product.formattedProduct[ProductInfoArray.size()];
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity
        for (int i = 0; i < ProductInfoArray.size(); i++) {

            int quant = 0;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ORDERS")) {

                //prep.setString(1, Integer.toString(i));
                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {
                        quant += Integer.parseInt(rs.getString(String.valueOf(i)));
                        //DbInt.pCon.close();
                    }
                }

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Fills row array for table with info


            if (quant > 0) {
                BigDecimal unitCost = new BigDecimal(ProductInfoArray.get(i).productUnitPrice.replaceAll("\\$", ""));
                allProducts[noProductsOrdered] = new Product.formattedProduct(ProductInfoArray.get(i).productID, ProductInfoArray.get(i).productName, ProductInfoArray.get(i).productSize, ProductInfoArray.get(i).productUnitPrice, ProductInfoArray.get(i).productCategory, quant, unitCost.multiply(new BigDecimal(quant)));
                totL = unitCost.multiply(new BigDecimal(quant)).add(totL);
                QuantL += quant;
                noProductsOrdered++;

            }
        }
        //Re create rows to remove blank rows
        Product.formattedProduct[] orderedProducts = new Product.formattedProduct[noProductsOrdered];

        System.arraycopy(allProducts, 0, orderedProducts, 0, noProductsOrdered);
        return new orderArray(orderedProducts, totL, QuantL);
    }

    public orderArray createNewOrder(Product.formattedProductProps[] orderData) {
        Product.formattedProduct[] orders = new Product.formattedProduct[orderData.length];
        for (int i = 0; i < orderData.length; i++) {
            orders[i] = new Product.formattedProduct(orderData[i].getProductID(), orderData[i].getProductName(), orderData[i].getProductSize(), orderData[i].getProductUnitPrice(), orderData[i].getProductCategory(), orderData[i].getOrderedQuantity(), new BigDecimal(orderData[i].getExtendedCost().toString()));
            totL = totL.add(orders[i].extendedCost);
            QuantL += orders[i].orderedQuantity;
        }
        return new orderArray(orders, totL, QuantL);

    }

    interface updateProgCallback {
        void doAction(double progress, int max);
    }

    interface failCallback {
        void doAction() throws InterruptedException;
    }

    interface updateMessageCallback {
        void doAction(String message);
    }

    interface getProgCallback {
        double doAction();
    }

    public static class orderArray {
        public final Product.formattedProduct[] orderData;
        public BigDecimal totalCost;
        public int totalQuantity;

        public orderArray(Product.formattedProduct[] orderData, BigDecimal totalCost, int totalQuantity) {
            this.orderData = orderData;
            this.totalCost = totalCost;
            this.totalQuantity = totalQuantity;
        }
    }
}
