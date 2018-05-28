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

package ABOS.Derby;//import javax.swing.*;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 *
 */
@SuppressWarnings("unused")
public class DbInt {
    public static Connection pCon = null;
    public static String DbLoc = "";


    /**
     * Gets the specified Utilities.Customer info
     *
     * @param yearL The year to search
     * @param name  The customer name
     * @param info  The info to search for
     * @return A string with the resulting data
     */
    public static String getCustInf(String yearL, String name, String info) {
        return getCustInf(yearL, name, info, "");
    }

    /**
     * Gets the specified Utilities.Customer info
     *
     * @param yearL      The year to search
     * @param name       The customer name
     * @param info       The info to search for
     * @param defaultVal The default value to return if there is no data
     * @return A string with the resulting data
     */
    public static String getCustInf(String yearL, String name, String info, String defaultVal) {
        String ret = defaultVal;

        try (PreparedStatement prep = DbInt.getPrep(yearL, "SELECT * FROM CUSTOMERS WHERE NAME=?")) {


            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getString(info);

                }
            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    /**
     * Creates a Prepared statemtn from provided Parameters.
     *
     * @param Db      The database to create the statement for
     * @param Command The Base command for the statement
     * @return the PreparedStatemtn that was created.
     */
    public static PreparedStatement getPrep(String Db, String Command) throws SQLException {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }
        pCon = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", DbLoc, Db);
        System.setProperty("derby.system.home",
                Config.getDbLoc());
        try {


            pCon = DriverManager.getConnection(url);
            pCon.setAutoCommit(true);
            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;

            PreparedStatement st = pCon.prepareStatement(Command, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if (st != null) {
                return st;
            }

        } catch (SQLException ex) {


            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                LogToFile.log(ex, Severity.FINER, "Derby shut down normally");

            } else {
                if (Objects.equals(ex.getSQLState(), "XJ004")) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText("The program cannot find the specified database");
                    alert.setContentText("Would you like to open the settings Dialog to create it?");


                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        new Launchers.Settings();
                        return getPrep(Db, Command);
                    } else {
                        Alert closingWarning = new Alert(Alert.AlertType.WARNING);
                        closingWarning.setTitle("Warning!");
                        closingWarning.setHeaderText("The program cannot run withou the database");
                        closingWarning.setContentText("Application is closing. Please restart application and create the database in the setting dialog.");


                        closingWarning.showAndWait();
                        System.exit(0);
                    }
                    LogToFile.log(ex, Severity.SEVERE, "");
                } else {
                    LogToFile.log(ex, Severity.WARNING, "");
                }
            }

        }
        throw new SQLException("Unable to acquire connection", "08001");
    }



    /**
     * Creates a database with specified name
     *
     * @param DB The name of the DB to create
     */
    public static Boolean createDb(String DB) {


        String url = String.format("jdbc:derby:%s/%s;create=true", Config.getDbLoc(), DB);//;create=true

        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement()) {


        } catch (SQLException ex) {


            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                LogToFile.log(ex, Severity.FINER, "Derby shut down normally");

            } else if (ex.getErrorCode() == 1007) {
                return false;
            } else {

                LogToFile.log(ex, Severity.SEVERE, ex.getMessage());
            }

        }
        return true;
    }

    public static void createSetAndTables() {
        DbInt.createDb("Set");

        try (PreparedStatement prep = DbInt.getPrep("Set", "CREATE TABLE Customers(CustomerID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), Address varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), Ordered VARChAR(255), NI VARChAR(255), NH VARChAR(255))")) {
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (PreparedStatement prep = DbInt.getPrep("Set", "CREATE TABLE YEARS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), YEARS varchar(255))")) {
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public static void deleteDb(String DB) {


        String url = String.format("%s/%s", Config.getDbLoc(), DB);
        File oldName = new File(url);
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime dateobj = LocalDateTime.now();
        //create destination File object
        File newName = new File(url + ".bak-" + dateobj.format(df));
        boolean isFileRenamed = oldName.renameTo(newName);


    }

    public static Iterable<String> getAllCustomers() {
        Collection<String> ret = new ArrayList<>();
        Iterable<String> years = getYears();
        for (String year : years) {

            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers");
                 ResultSet rs = prep.executeQuery()
            ) {


                while (rs.next()) {
                    String name = rs.getString("NAME");
                    if (!ret.contains(name)) {
                        ret.add(name);
                    }

                }
                ////Utilities.DbInt.pCon.close();

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        }


        return ret;
    }

    public static Iterable<String> getYears() {
        Collection<String> ret = new ArrayList<>();
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT YEARS FROM Years");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("YEARS"));

            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    public static String getCategoryDate(String catName, String year) {
        Date ret = null;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Date FROM Categories WHERE Name=?")) {


            prep.setString(1, catName);

            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getDate(1);

                }
            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        String output;
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        output = formatter.format(ret);
        return output;
    }


// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Closes the database connection.
//     */
//    public void close() {
//        try {
//            DriverManager.getConnection("jdbc:derby:;shutdown=true");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

}