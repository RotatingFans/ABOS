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

    public static String updateOrder(ObservableList<Product.formattedProductProps> orders, String name, String year, Integer custID, updateProgCallback updateProg, failCallback fail, updateMessageCallback updateMessage, getProgCallback getProgress) throws Exception {
        List<Integer> Ids = new ArrayList<>();
        int numRows = orders.size();
        ObservableList<Product.formattedProductProps> orderNoZero = FXCollections.observableArrayList();
        orders.forEach(order -> {
            if (order.getOrderedQuantity() > 0) {
                orderNoZero.add(order);
            }
        });
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT idOrders FROM ordersview WHERE custId=?")) {

            prep.setInt(1, custID);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    Ids.add(rs.getInt(1));

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

            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT idOrders FROM ordersview WHERE custId=?")) {

                prep.setInt(1, custID);
                try (ResultSet rs = prep.executeQuery()) {
                    while (rs.next()) {

                        Ids.add(rs.getInt(1));

                    }
                }
            }
            int OrderID = Ids.get(Ids.size() - 1);
            String uName = DbInt.getUserName(year);

            try (PreparedStatement prep = DbInt.getPrep(year, "DELETE FROM orderedproductsview WHERE orderID=?")) {

                prep.setInt(1, OrderID);
                prep.execute();

            }
            try (PreparedStatement writeOrd = DbInt.getPrep(year, "INSERT INTO orderedproductsview(uName,custId, orderID, ProductID, Quantity) VALUES(?,?,?,?,?)")) {
                for (Product.formattedProductProps prod : orderNoZero) {

                    writeOrd.setString(1, uName);
                    writeOrd.setInt(2, custID);
                    writeOrd.setInt(3, OrderID);
                    writeOrd.setInt(4, prod.getProductKey());
                    writeOrd.setInt(5, prod.getOrderedQuantity());

                    fail.doAction();
                    updateMessage.doAction("Adding Order");

                    writeOrd.executeUpdate();
                }
            }
        } //Insert Mode
        else {
            updateMessage.doAction("Building Order");

            StringBuilder InsertOrderStringBuilder = new StringBuilder("INSERT INTO ORDERS(uName,custId) VALUES(?,?)");

            progressDivisor = 2 * numRows;
            progressIncrement = 50 / progressDivisor;
            //Loops through And adds product numbers to Order string
/*            int insertProductNumberHere = InsertOrderStringBuilder.length() - 9;

            for (int i = 0; i < numRows; i++) {
                updateProg.doAction(getProgress.doAction() + progressIncrement, 100);
                InsertOrderStringBuilder.insert(insertProductNumberHere, ",\"");
                InsertOrderStringBuilder.insert(insertProductNumberHere + 2, i);
                InsertOrderStringBuilder.insert(insertProductNumberHere + 2 + IntegerLength(i), '"');
                insertProductNumberHere += 3 + IntegerLength(i);
                InsertOrderStringBuilder.append(",?");
            }
            InsertOrderStringBuilder.insert(insertProductNumberHere, ") ");
            InsertOrderStringBuilder.append(')');*/

            fail.doAction();

            //Creates prepared Statement and replaces ? with quantities and names
            try (PreparedStatement writeOrd = DbInt.getPrep(year, "INSERT INTO ordersview(uName,custId) VALUES(?,?)")) {
                writeOrd.setString(1, DbInt.getUserName(year));
                writeOrd.setInt(2, custID);

                fail.doAction();
                updateMessage.doAction("Adding Order");

                writeOrd.executeUpdate();
            }
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT idOrders FROM ordersview WHERE custId=?")) {

                prep.setInt(1, custID);
                try (ResultSet rs = prep.executeQuery()) {
                    while (rs.next()) {

                        Ids.add(rs.getInt(1));

                    }
                }
            }
            int OrderID = Ids.get(Ids.size() - 1);
            String uName = DbInt.getUserName(year);


            try (PreparedStatement writeOrd = DbInt.getPrep(year, "INSERT INTO orderedproductsview(uName,custId, orderID, ProductID, Quantity) VALUES(?,?,?,?,?)")) {
                for (Product.formattedProductProps prod : orderNoZero) {

                    writeOrd.setString(1, uName);
                    writeOrd.setInt(2, custID);
                    writeOrd.setInt(3, OrderID);
                    writeOrd.setInt(4, prod.getProductKey());
                    writeOrd.setInt(5, prod.getOrderedQuantity());

                    fail.doAction();
                    updateMessage.doAction("Adding Order");

                    writeOrd.executeUpdate();
                }
            }
        }
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT idOrders FROM ordersview WHERE custId=?")) {

            prep.setInt(1, custID);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    Ids.add(rs.getInt(1));

                }
            }
        }
        return Ids.get(Ids.size() - 1).toString();
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


        //Table rows array
        ArrayList<Product.formattedProduct> allProducts = new ArrayList<Product.formattedProduct>();
        int OrderID = customer.getOrderId();
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity


            int quant = 0;
        if (!Objects.equals(Category, "*")) {
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM (SELECT * FROM products WHERE Category=?) products LEFT JOIN (SELECT SUM(Quantity),ProductId FROM orderedproductsview WHERE orderID=10 GROUP BY ProductId) orderedproductsview ON orderedproductsview.ProductId=products.idproducts ORDER BY products.idproducts")) {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, Category);

                prep.setInt(2, OrderID);

                try (ResultSet rs = prep.executeQuery()) {
                    quant = rs.getInt("SUM(Quantity)");
                    if (((quant > 0) && excludeZeroOrders) || !excludeZeroOrders) {
                        BigDecimal unitCost = rs.getBigDecimal("Cost");
                        allProducts.add(new Product.formattedProduct(rs.getInt("idproducts"), rs.getString("ID"), rs.getString("Name"), rs.getString("UnitSize"), rs.getBigDecimal("Cost"), rs.getString("Category"), quant, unitCost.multiply(new BigDecimal(quant))));
                        totL = unitCost.multiply(new BigDecimal(quant)).add(totL);
                        QuantL += quant;
                        noProductsOrdered++;

                    }
                }

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        } else {
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM (SELECT * FROM products) products LEFT JOIN (SELECT SUM(Quantity),ProductId FROM orderedproductsview WHERE orderID=? GROUP BY ProductId) orderedproductsview ON orderedproductsview.ProductId=products.idproducts ORDER BY products.idproducts")) {

                //prep.setString(1, Integer.toString(i));
                prep.setInt(1, OrderID);

                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {
                        quant = rs.getInt("SUM(Quantity)");
                        if (((quant > 0) && excludeZeroOrders) || !excludeZeroOrders) {
                            BigDecimal unitCost = rs.getBigDecimal("Cost");
                            allProducts.add(new Product.formattedProduct(rs.getInt("idproducts"), rs.getString("ID"), rs.getString("Name"), rs.getString("UnitSize"), rs.getBigDecimal("Cost"), rs.getString("Category"), quant, unitCost.multiply(new BigDecimal(quant))));
                            totL = unitCost.multiply(new BigDecimal(quant)).add(totL);
                            QuantL += quant;
                            noProductsOrdered++;

                        }
                    }
                }

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        }

        //Fills row array for table with info



        //Re create rows to remove blank rows
        Product.formattedProduct[] orderedProducts = new Product.formattedProduct[noProductsOrdered];

        System.arraycopy(allProducts.toArray(), 0, orderedProducts, 0, noProductsOrdered);
        return new orderArray(orderedProducts, totL, QuantL);


    }

    public orderArray createOrderArray(String year) {

        List<Product> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM products");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {
                ProductInfoArray.add(new Product(ProductInfoResultSet.getInt("idproducts"), ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("Name"), ProductInfoResultSet.getString("UnitSize"), ProductInfoResultSet.getBigDecimal("Cost"), ProductInfoResultSet.getString("Category")));

            }
            //Close prepared statement
            ProductInfoResultSet.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        //Table rows array
        Product.formattedProduct[] allProducts = new Product.formattedProduct[ProductInfoArray.size()];
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity

        int quant = 0;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT SUM(Quantity), ProductId FROM orderedproductsview GROUP BY ProductId")) {

            //prep.setString(1, Integer.toString(i));
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {
                    quant = rs.getInt("SUM(Quantity)");
                    if (quant > 0) {
                        int productId = rs.getInt("ProductId");
                        BigDecimal unitCost = ProductInfoArray.get(productId).productUnitPrice;
                        allProducts[noProductsOrdered] = new Product.formattedProduct(ProductInfoArray.get(productId).productKey, ProductInfoArray.get(productId).productID, ProductInfoArray.get(productId).productName, ProductInfoArray.get(productId).productSize, ProductInfoArray.get(productId).productUnitPrice, ProductInfoArray.get(productId).productCategory, quant, unitCost.multiply(new BigDecimal(quant)));
                        totL = unitCost.multiply(new BigDecimal(quant)).add(totL);
                        QuantL += quant;
                        noProductsOrdered++;

                    }
                    //DbInt.pCon.close();
                }
            }

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        //Fills row array for table with info


        //Re create rows to remove blank rows
        Product.formattedProduct[] orderedProducts = new Product.formattedProduct[noProductsOrdered];

        System.arraycopy(allProducts, 0, orderedProducts, 0, noProductsOrdered);
        return new orderArray(orderedProducts, totL, QuantL);
    }

    public orderArray createNewOrder(Product.formattedProductProps[] orderData) {
        Product.formattedProduct[] orders = new Product.formattedProduct[orderData.length];
        for (int i = 0; i < orderData.length; i++) {
            orders[i] = new Product.formattedProduct(orderData[i].getProductKey(), orderData[i].getProductID(), orderData[i].getProductName(), orderData[i].getProductSize(), orderData[i].getProductUnitPrice(), orderData[i].getProductCategory(), orderData[i].getOrderedQuantity(), orderData[i].getExtendedCost());
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
