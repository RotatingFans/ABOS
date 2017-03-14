/*
 * Copyright (c) Patrick Magauran 2017.
 * Licensed under the AGPLv3. All conditions of said license apply.
 *     This file is part of LawnAndGarden.
 *
 *     LawnAndGarden is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     LawnAndGarden is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with LawnAndGarden.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 7/27/16.
 */
public class Order {
    // --Commented out by Inspection (7/27/16 3:02 PM):Product product = new Product();
    private BigDecimal totL = BigDecimal.ZERO;
    private int QuantL = 0;

    public orderArray createOrderArray(String year, String name, Boolean excludeZeroOrders) {

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
        String OrderID = DbInt.getCustInf(year, name, "ORDERID");
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity
        for (int i = 0; i < ProductInfoArray.size(); i++) {

            int quant = 0;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ORDERS WHERE ORDERID=?")) {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, OrderID);
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
