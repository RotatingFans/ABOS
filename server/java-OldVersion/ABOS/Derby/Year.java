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

package ABOS.Derby;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by patrick on 7/27/16.
 */
public class Year {
    private static final int retInteger = 1;
    private static final int retString = 2;
    private static final int retBigDec = 3;
    private final String year;

    public Year(String year) {

        this.year = year;
    }

    public Iterable<String> getCustomerNames() {
        Collection<String> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM customers");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("NAME"));

            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    public Collection<category> getCategories() {
        Collection<category> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM Categories");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(new category(rs.getString("NAME"), rs.getString("DATE")));
                ////Utilities.DbInt.pCon.close();
            }
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
            //////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    public void deleteYear() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("WARNING!");
        alert.setHeaderText("You are about to delete an entire Year. This cannot be reversed");
        alert.setContentText("Would you like to continue with the deletion?");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            DbInt.deleteDb(year);
            try (PreparedStatement prep = DbInt.getPrep("Set", "DELETE FROM YEARS WHERE YEARS=?")) {
                prep.setString(1, year);
                prep.execute();
            } catch (SQLException Se) {
                LogToFile.log(Se, Severity.SEVERE, CommonErrors.returnSqlMessage(Se));
            }


        }
    }

    public void updateTots(BigDecimal donations, Integer Lg, Integer LP, Integer Mulch, BigDecimal OT, Integer Customers, BigDecimal Commis, BigDecimal GTot) {
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
     * @return The Utilities.Order total amount
     */
    public BigDecimal getOT() {
        return (BigDecimal) getTots("TOTAL", retBigDec);
    }

    /**
     * Gets the Total Utilities.Customer Using getTots Function
     *
     * @return The total amount of customers
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
            ////Utilities.DbInt.pCon.close();


            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //Utilities.DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ProductInfoArray.toArray(new Product.formattedProduct[0]);

    }

    public boolean addressExists(String address, String zipCode) {
        Boolean exists = false;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM customers WHERE ADDRESS=? AND ZIPCODE=?")) {
            prep.setString(1, address);
            prep.setString(2, zipCode);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                exists = true;

            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return exists;
    }

    /**
     * Creates Database for the year specified.
     */
    public void CreateDb(ObservableList<Product.formattedProductProps> products, Collection<category> rowsCats) {
        DbInt.deleteDb(year);
        if (DbInt.createDb(year)) {
            //Create Tables
            //Create customers Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE CUSTOMERS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),NAME varchar(255),ADDRESS varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), PHONE varchar(255), ORDERID varchar(255), PAID varchar(255),DELIVERED varchar(255), EMAIL varchar(255), DONATION VARCHAR(255))")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Products Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255), Category VARCHAR(255))")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Totals Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE TOTALS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),DONATIONS DECIMAL(7,2),LG INTEGER,LP INTEGER,MULCH INTEGER,TOTAL DECIMAL(7,2),CUSTOMERS INTEGER,COMMISSIONS DECIMAL(7,2),GRANDTOTAL DECIMAL(7,2))")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }

/*            //Create Residence Table
            try (PreparedStatement prep = Utilities.DbInt.getPrep(year, "CREATE TABLE Residence(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Address varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), Action varchar(255))")) {
                prep.execute();
            } catch (SQLException e) {
                Utilities.LogToFile.log(e, Utilities.Severity.SEVERE, Utilities.CommonErrors.returnSqlMessage(e));
            }*/

            //Create Categories Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE Categories(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Name varchar(255), Date DATE)")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Insert products into Product table
            //Insert products into Product table
            String col = "";
            for (int i = 0; i < products.size(); i++) {
                Product.formattedProductProps curRow = products.get(i);
                String cat = (curRow.getProductCategory() != null) ? curRow.getProductCategory() : "";
                col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
                try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO PRODUCTS(ID, PName, Unit, Size, Category) VALUES (?,?,?,?,?)")) {
                    prep.setString(1, curRow.getProductID());
                    prep.setString(2, curRow.getProductName());
                    prep.setString(3, curRow.getProductUnitPrice());
                    prep.setString(4, curRow.getProductSize());
                    prep.setString(5, cat);
                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
            }
            //Add Categories
            rowsCats.forEach(cat -> {
                try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO Categories(Name, Date) VALUES (?,?)")) {
                    prep.setString(1, cat.catName);
                    prep.setDate(2, Date.valueOf(cat.catDate));

                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
            });

            //ORDers Table
            try (PreparedStatement prep = DbInt.getPrep(year, String.format("CREATE TABLE ORDERS(OrderID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NAME VARChAR(255) %s)", col))) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Add default values to TOTALS
            try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(0,0,0,0,0,0,0,0)")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //ADD to Utilities.Year
            addYear();
        } else {
            LogToFile.log(null, Severity.WARNING, "Year already exists: Please rename the year you are adding or delete the Year you are trying to overwrite.");
        }

    }

    public void updateDb(String year, ObservableList<Product.formattedProductProps> products, Collection<category> rowsCats) {
        //Delete Utilities.Year Utilities.Customer table

        try (PreparedStatement addCol = DbInt.getPrep(year, "DROP TABLE \"PRODUCTS\"")) {
            addCol.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        try (PreparedStatement addCol = DbInt.getPrep(year, "DROP TABLE \"Categories\"")) {
            addCol.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        //Recreate Utilities.Year Utilities.Customer table

        try (PreparedStatement addCol = DbInt.getPrep(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255), Category VARCHAR(255))")) {
            addCol.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE Categories(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Name varchar(255), Date DATE)")) {
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        //Add Categories
        rowsCats.forEach(cat -> {
            try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO Categories(Name, Date) VALUES (?,?)")) {
                prep.setString(1, cat.catName);
                prep.setDate(2, Date.valueOf(cat.catDate));

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        });
        //Insert products into Product table
        String col = "";
        for (int i = 0; i < products.size(); i++) {
            Product.formattedProductProps curRow = products.get(i);
            String cat = (curRow.getProductCategory() != null) ? curRow.getProductCategory() : "";
            col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
            try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO PRODUCTS(ID, PName, Unit, Size, Category) VALUES (?,?,?,?,?)")) {
                prep.setString(1, curRow.getProductID());
                prep.setString(2, curRow.getProductName());
                prep.setString(3, curRow.getProductUnitPrice());
                prep.setString(4, curRow.getProductSize());
                prep.setString(5, cat);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        }
    }

    public void addYear() {
        try (PreparedStatement prep = DbInt.getPrep("Set", "INSERT INTO YEARS(YEARS) VALUES(?)")) {
            prep.setString(1, year);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public static class category {
        public String catName;
        public String catDate;

        public category(String name, String date) {
            catName = name;
            catDate = date;
        }
    }
}