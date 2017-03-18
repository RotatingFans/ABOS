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
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by patrick on 7/27/16.
 */
class Year {
    private static final int retInteger = 1;
    private static final int retString = 2;
    private static final int retBigDec = 3;
    private final String year;

    public Year(String year) {

        this.year = year;
    }

    public Iterable<String> getCustomerNames() {
        Collection<String> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("NAME"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    private Object getTots(String info, int retType) {
        Object ret = "";

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM TOTALS");
             ResultSet rs = prep.executeQuery()
        ) {

            //prep.setString(1, info);


            while (rs.next()) {
                switch (retType) {
                    case retInteger:
                        ret = rs.getInt(info);
                        break;
                    case retString:
                        ret = rs.getString(info);
                        break;
                    case retBigDec:
                        ret = rs.getBigDecimal(info);
                        break;

                }

            }
            //////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    /**
     * Gets the Total Donations Using getTots Function
     *
     * @return The total donation amount
     */
    public BigDecimal getDonations() {
        return (BigDecimal) getTots("Donations", retBigDec);
    }

    /**
     * Gets the Total Lawn ANd Garden quantities Using getTots Function
     *
     * @return The total Lawn ANd Garden quantities amount
     */
    public int getLG() {
        return (int) getTots("LG", retInteger);
    }

    /**
     * Gets the Total Live Plants quantities Using getTots Function
     *
     * @return The total Live Plants quantities amount
     */
    public int getLP() {
        return (int) getTots("LP", retInteger);
    }

    /**
     * Gets the Total Mulch quantities Using getTots Function
     *
     * @return The total Mulch quantities amount
     */
    public int getMulch() {
        return (int) getTots("MULCH", retInteger);
    }

    /**
     * Gets the order Total Using getTots Function
     *
     * @return The Order total amount
     */
    public BigDecimal getOT() {
        return (BigDecimal) getTots("TOTAL", retBigDec);
    }

    /**
     * Gets the Total Customer Using getTots Function
     *
     * @return The total amount of Customers
     */
    public int getNoCustomers() {
        return (int) getTots("CUSTOMERS", retInteger);
    }

    /**
     * Gets the Total Commissions Using getTots Function
     *
     * @return The total Commissions amount
     */
    public BigDecimal getCommis() {
        return (BigDecimal) getTots("COMMISSIONS", retBigDec);
    }

    /**
     * Gets the Grand Total Using getTots Function
     *
     * @return The Grand total amount
     */
    public BigDecimal getGTot() {
        return (Objects.equals(getTots("GRANDTOTAL", retBigDec), "")) ? (BigDecimal.ZERO) : (BigDecimal) getTots("GRANDTOTAL", retBigDec);
    }

    public int getQuant() {
        return getLG() + getLP();
    }

    public Product.formattedProduct[] getAllProducts() {
        //String[] toGet = {"ID", "PNAME", "SIZE", "UNIT"};
        List<Product.formattedProduct> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {

                ProductInfoArray.add(new Product.formattedProduct(ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("PNAME"), ProductInfoResultSet.getString("SIZE"), ProductInfoResultSet.getString("UNIT"), ProductInfoResultSet.getString("Category"), 0, BigDecimal.ZERO));
            }
            DbInt.pCon.commit();
            ////DbInt.pCon.close();


            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ProductInfoArray.toArray(new Product.formattedProduct[ProductInfoArray.size()]);

    }
}