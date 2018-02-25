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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.Connection;
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

    private final ObservableList<Product.formattedProductProps> orders;
    private final String name;
    private final String year;
    private final Integer custID;
    private final Boolean paid;
    private final Boolean delivered;
    private final String uName;

    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress");
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper(this, "message");

    public Order(ObservableList<Product.formattedProductProps> orders, String name, String year, Integer custID, Boolean paid, Boolean delivered, String uName) {
        this.orders = orders;
        this.name = name;
        this.year = year;
        this.custID = custID;
        this.paid = paid;
        this.delivered = delivered;
        this.uName = uName;
    }

    public Order(ObservableList<Product.formattedProductProps> orders, String name, String year, Integer custID, Boolean paid, Boolean delivered) {
        this(orders, name, year, custID, paid, delivered, DbInt.getUserName());
    }
    public static orderDetails getOrder(String year, Integer id) {
        orderDetails order = null;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Cost, Quant, paid, delivered FROM ordersview WHERE custId=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            //prep.setString(1, Integer.toString(i));
            prep.setInt(1, id);


            try (ResultSet rs = prep.executeQuery()) {
                rs.next();
                order = new orderDetails(rs.getBigDecimal("Cost"), rs.getInt("Quant"), rs.getInt("paid"), rs.getInt("delivered"));
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return order;
    }

    public static orderArray createOrderArray(String year, int cID, Boolean excludeZeroOrders) {
        return createOrderArray(year, cID, excludeZeroOrders, "*");
    }

    public static orderArray createOrderArray(String year, int cID, Boolean excludeZeroOrders, String Category) {
        BigDecimal totL = BigDecimal.ZERO;
        int QuantL = 0;
        Customer customer = null;
        try {
            customer = new Customer(cID, year);
        } catch (Customer.CustomerNotFoundException e) {
            LogToFile.log(e, Severity.WARNING, "Customer not found. Refresh and try again.");
        }


        //Table rows array
        ArrayList<Product.formattedProduct> allProducts = new ArrayList<Product.formattedProduct>();
        int OrderID = customer.getOrderId();
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity


            int quant = 0;
        if (!Objects.equals(Category, "*")) {
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT * FROM (SELECT * FROM products WHERE Category=?) products LEFT JOIN (SELECT SUM(Quantity),ProductId FROM orderedproductsview WHERE orderID=? GROUP BY ProductId) orderedproductsview ON orderedproductsview.ProductId=products.idproducts ORDER BY products.idproducts", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                //prep.setString(1, Integer.toString(i));
                prep.setString(1, Category);

                prep.setInt(2, OrderID);

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
        } else {
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT * FROM (SELECT * FROM products) products LEFT JOIN (SELECT SUM(Quantity),ProductId FROM orderedproductsview WHERE orderID=? GROUP BY ProductId) orderedproductsview ON orderedproductsview.ProductId=products.idproducts ORDER BY products.idproducts", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
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

    public static orderArray createOrderArray(String year) {
        BigDecimal totL = BigDecimal.ZERO;
        int QuantL = 0;

        //Table rows array
        ArrayList<Product.formattedProduct> allProducts = new ArrayList<Product.formattedProduct>();
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity

        int quant = 0;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM (SELECT * FROM products) products LEFT JOIN (SELECT SUM(Quantity),ProductId FROM orderedproductsview GROUP BY ProductId) orderedproductsview ON orderedproductsview.ProductId=products.idproducts ORDER BY products.idproducts", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            //prep.setString(1, Integer.toString(i));

            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {
                    quant = rs.getInt("SUM(Quantity)");
                    if (quant > 0) {
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
        //Fills row array for table with info


        //Re create rows to remove blank rows
        Product.formattedProduct[] orderedProducts = new Product.formattedProduct[noProductsOrdered];

        System.arraycopy(allProducts.toArray(), 0, orderedProducts, 0, noProductsOrdered);
        return new orderArray(orderedProducts, totL, QuantL);
    }

    public static orderArray createOrderArray(String year, String uName) {
        BigDecimal totL = BigDecimal.ZERO;
        int QuantL = 0;

        //Table rows array
        ArrayList<Product.formattedProduct> allProducts = new ArrayList<Product.formattedProduct>();
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity

        int quant = 0;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM (SELECT * FROM products) products LEFT JOIN (SELECT SUM(Quantity),ProductId FROM orderedproductsview WHERE uName=? GROUP BY ProductId) orderedproductsview ON orderedproductsview.ProductId=products.idproducts ORDER BY products.idproducts", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);

            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {
                    quant = rs.getInt("SUM(Quantity)");
                    if (quant > 0) {
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
        //Fills row array for table with info


        //Re create rows to remove blank rows
        Product.formattedProduct[] orderedProducts = new Product.formattedProduct[noProductsOrdered];

        System.arraycopy(allProducts.toArray(), 0, orderedProducts, 0, noProductsOrdered);
        return new orderArray(orderedProducts, totL, QuantL);
    }

    public static orderArray createNewOrder(Product.formattedProductProps[] orderData) {
        BigDecimal totL = BigDecimal.ZERO;
        int QuantL = 0;
        Product.formattedProduct[] orders = new Product.formattedProduct[orderData.length];
        for (int i = 0; i < orderData.length; i++) {
            orders[i] = new Product.formattedProduct(orderData[i].getProductKey(), orderData[i].getProductID(), orderData[i].getProductName(), orderData[i].getProductSize(), orderData[i].getProductUnitPrice(), orderData[i].getProductCategory(), orderData[i].getOrderedQuantity(), orderData[i].getExtendedCost());
            totL = totL.add(orders[i].extendedCost);
            QuantL += orders[i].orderedQuantity;
        }
        return new orderArray(orders, totL, QuantL);

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

    public String updateOrder(failCallback fail) throws Exception {
        List<Integer> Ids = new ArrayList<>();
        int numRows = orders.size();
        ObservableList<Product.formattedProductProps> orderNoZero = FXCollections.observableArrayList();
        try {
            for (Product.formattedProductProps order : orders) {


                if (order.getOrderedQuantity() > 0) {
                    orderNoZero.add(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT idOrders FROM ordersview WHERE custId=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, custID);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    Ids.add(rs.getInt(1));

                }
            }
        }
        int progressDivisor = (2 * numRows);
        int progressIncrement = 50 / progressDivisor;
        progress.set(getProgress() + progressIncrement);
        fail.doAction();

        //Inserts into customer table for year
        //Edit mode
        if (Ids.size() > 0) {
            message.set("Building Order Update");

            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT idOrders FROM ordersview WHERE custId=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, custID);
                try (ResultSet rs = prep.executeQuery()) {
                    while (rs.next()) {

                        Ids.add(rs.getInt(1));

                    }
                }
            }
            int OrderID = Ids.get(Ids.size() - 1);

            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("DELETE FROM orderedproductsview WHERE orderID=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, OrderID);
                prep.execute();
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement writeOrd = con.prepareStatement("INSERT INTO orderedproductsview(uName,custId, orderID, ProductID, Quantity) VALUES(?,?,?,?,?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                for (Product.formattedProductProps prod : orderNoZero) {

                    writeOrd.setString(1, uName);
                    writeOrd.setInt(2, custID);
                    writeOrd.setInt(3, OrderID);
                    writeOrd.setInt(4, prod.getProductKey());
                    writeOrd.setInt(5, prod.getOrderedQuantity());

                    fail.doAction();
                    message.set("Adding Order");

                    writeOrd.executeUpdate();
                }
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement writeOrd = con.prepareStatement("UPDATE ordersview SET paid=?, delivered=? WHERE idOrders=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                writeOrd.setInt(1, paid ? 1 : 0);
                writeOrd.setInt(2, delivered ? 1 : 0);
                writeOrd.setInt(3, OrderID);

                fail.doAction();
                message.set("Adding Order");

                writeOrd.executeUpdate();
            }
        } //Insert Mode
        else {
            message.set("Building Order");


            progressDivisor = 2 * numRows;
            progressIncrement = 50 / progressDivisor;
            //Loops through And adds product numbers to Order string
/*            int insertProductNumberHere = InsertOrderStringBuilder.length() - 9;

            for (int i = 0; i < numRows; i++) {
                progress.set(getProgress() + progressIncrement, 100);
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
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement writeOrd = con.prepareStatement("INSERT INTO ordersview(uName,custId, paid, delivered) VALUES(?,?, ?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                writeOrd.setString(1, uName);
                writeOrd.setInt(2, custID);
                writeOrd.setInt(3, paid ? 1 : 0);
                writeOrd.setInt(4, delivered ? 1 : 0);

                fail.doAction();
                message.set("Adding Order");

                writeOrd.executeUpdate();
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT idOrders FROM ordersview WHERE custId=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, custID);

                try (ResultSet rs = prep.executeQuery()) {
                    while (rs.next()) {

                        Ids.add(rs.getInt(1));

                    }
                }
            }
            int OrderID = Ids.get(Ids.size() - 1);


            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement writeOrd = con.prepareStatement("INSERT INTO orderedproductsview(uName,custId, orderID, ProductID, Quantity) VALUES(?,?,?,?,?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                for (Product.formattedProductProps prod : orderNoZero) {

                    writeOrd.setString(1, uName);
                    writeOrd.setInt(2, custID);
                    writeOrd.setInt(3, OrderID);
                    writeOrd.setInt(4, prod.getProductKey());
                    writeOrd.setInt(5, prod.getOrderedQuantity());

                    fail.doAction();
                    message.set("Adding Order");

                    writeOrd.executeUpdate();
                }
            }
        }
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT idOrders FROM ordersview WHERE custId=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, custID);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    Ids.add(rs.getInt(1));

                }
            }
        }
        return Ids.get(Ids.size() - 1).toString();
    }

    public double getProgress() {
        return progress.get();
    }

    public ReadOnlyDoubleProperty progressProperty() {
        return progress.getReadOnlyProperty();
    }

    public String getMessage() {
        return message.get();
    }

    public ReadOnlyStringProperty messageProperty() {
        return message.getReadOnlyProperty();
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

    public static class orderDetails {
        public final BigDecimal totalCost;
        public final int totalQuantity;
        public final boolean paid;
        public final boolean delivered;

        public orderDetails(BigDecimal totalCost, int totalQuantity, boolean paid, boolean delivered) {
            this.totalCost = totalCost;
            this.totalQuantity = totalQuantity;
            this.paid = paid;
            this.delivered = delivered;
        }

        public orderDetails(BigDecimal totalCost, int totalQuantity, int paid, int delivered) {
            this.totalCost = totalCost;
            this.totalQuantity = totalQuantity;
            this.paid = paid == 1;
            this.delivered = delivered == 1;
        }
    }
}
