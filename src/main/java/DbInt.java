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

//import javax.swing.*;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.util.Pair;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 */
@SuppressWarnings("unused")
public class DbInt {
    public static Connection pCon = null;
    public static String prefix = "ABOS-Test-";
    private static ComboPooledDataSource cpds = new ComboPooledDataSource();
    private static String username;
    private static String password;
    private static boolean isAdmin;

    /**
     * Gets the specified Customer info
     *
     * @param yearL The year to search
     * @param info  The info to search for
     * @return A string with the resulting data
     */
    public static String getCustInf(String yearL, int id, String info) {
        return getCustInf(yearL, id, info, "");
    }

    /**
     * Gets the specified Customer info
     *
     * @param yearL      The year to search
     * @param info       The info to search for
     * @param defaultVal The default value to return if there is no data
     * @return A string with the resulting data
     */
    public static String getCustInf(String yearL, Integer ID, String info, String defaultVal) {
        String ret = defaultVal;

        try (Connection con = DbInt.getConnection(yearL);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getString(info);

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    public static Connection getConnection(String Db) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }
        Statement st = null;
        ResultSet rs = null;
        pCon = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:mysql://%s/%s?useSSL=%s", Config.getDbLoc(), prefix + Db, Config.getSSL());
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }
        cpds.setJdbcUrl(url);
        cpds.setUser(username);
        cpds.setPassword(password);
        try {


            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;


            return cpds.getConnection();

        } catch (CommunicationsException e) {
            promptConfig();
        } catch (SQLException ex) {

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                LogToFile.log(ex, Severity.FINER, "Derby shut down normally");

            } else {
                if (Objects.equals(ex.getSQLState(), "42000")) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("ERROR!");
                    alert.setHeaderText("The program cannot find the specified database");
                    alert.setContentText("Would you like to open the settings Dialog to create it?");


                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        new Settings(null);
                        return getConnection(Db);
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

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }


            } catch (SQLException ex) {
                LogToFile.log(ex, Severity.WARNING, ex.getMessage());
            }
        }
        return null;
    }

    public static boolean testConnection() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }
        Statement st = null;
        ResultSet rs = null;
        pCon = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:mysql://%s/%s?useSSL=%s", Config.getDbLoc(), prefix + "Commons", Config.getSSL());
        try {
            cpds.setDriverClass("com.mysql.jdbc.Driver"); //loads the jdbc driver
        } catch (PropertyVetoException e) {
            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }
        cpds.setJdbcUrl(url);
        cpds.setUser(username);
        cpds.setPassword(password);
        cpds.setAcquireRetryAttempts(2);
        try {


            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;


            return cpds.getConnection().isValid(15);

        } catch (Exception e) {
            return false;
        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }


            } catch (SQLException ex) {
                LogToFile.log(ex, Severity.WARNING, ex.getMessage());
            }
        }
    }

    /**
     * Creates a Prepared statemtn from provided Parameters.
     *
     * @return the PreparedStatemtn that was created.
     */
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }
        Statement st = null;
        ResultSet rs = null;
        pCon = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:mysql://%s/?useSSL=%s", Config.getDbLoc(), Config.getSSL());
        //cpds.deb
        cpds.setJdbcUrl(url);
        cpds.setUser(username);
        cpds.setPassword(password);
        try {


            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;


            return cpds.getConnection();

        } catch (CommunicationsException e) {
            promptConfig();
        } catch (SQLException ex) {


            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                LogToFile.log(ex, Severity.FINER, "Derby shut down normally");

            } else {


                LogToFile.log(ex, Severity.WARNING, "");

            }

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }


            } catch (SQLException ex) {
                LogToFile.log(ex, Severity.WARNING, ex.getMessage());
            }
        }
        return null;
    }



    /**
     * Creates a database with specified name
     *
     * @param DB The name of the DB to create
     */
    public static Boolean createDb(String DB) {
/*

 */


        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }

        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:mysql://%s/?useSSL=%s", Config.getDbLoc(), Config.getSSL());

        try (Connection con = DriverManager.getConnection(url, username, password);
             Statement st = con.createStatement()) {
            int Result = st.executeUpdate("CREATE DATABASE `" + prefix + DB + "`");

        } catch (CommunicationsException e) {
            promptConfig();
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

    public static void deleteAllDB() {
        getYears().forEach(year -> {
            String createAndGrantCommand = "DROP USER IF EXISTS'" + year + "'@'localhost'";
            try (Connection con = DbInt.getConnection();
                 PreparedStatement prep = con.prepareStatement("", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                prep.addBatch(createAndGrantCommand);
                prep.executeBatch();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            deleteDb(year);
        });
        DbInt.deleteDb("Commons");

    }

    public static void createSetAndTables() {

/*
CREATE TABLE `ABOS-Test-Commons`.`Years` (
  `year` INT NOT NULL,
  PRIMARY KEY (`year`));
 */
        DbInt.createDb("Commons");

        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("CREATE TABLE `Users` (\n" +
                     "  `idUsers` int(11) NOT NULL AUTO_INCREMENT,\n" +
                     "  `userName` varchar(255) NOT NULL,\n" +
                     "  `fullName` varchar(255) NOT NULL,\n" +
                     "  `Admin` int(11) NOT NULL,\n" +
                     "  `Years` varchar(255) NOT NULL,\n" +
                     "  PRIMARY KEY (`idUsers`)\n" +
                     ")", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("CREATE TABLE `Years` (\n" +
                             "  `idYear` int(11) NOT NULL AUTO_INCREMENT,\n" +
                             "  `Year` varchar(4) NOT NULL,\n" +
                             "  PRIMARY KEY (`idYear`));",
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("CREATE\n" +
                     "    ALGORITHM = UNDEFINED\n" +
                     "    DEFINER = `admin`@`localhost`\n" +
                     "    SQL SECURITY DEFINER\n" +
                     "VIEW `" + prefix + "Commons`.`userView` AS\n" +
                     "    SELECT\n" +
                     "        `" + prefix + "Commons`.`Users`.`idUsers` AS `idUsers`,\n" +
                     "        `" + prefix + "Commons`.`Users`.`userName` AS `userName`,\n" +
                     "        `" + prefix + "Commons`.`Users`.`fullName` AS `fullName`,\n" +
                     "        `" + prefix + "Commons`.`Users`.`Admin` AS `Admin`,\n" +

                     "        `" + prefix + "Commons`.`Users`.`Years` AS `Years`\n" +
                     "    FROM\n" +
                     "        `" + prefix + "Commons`.`Users`\n" +
                     "    WHERE\n" +
                     "        (`" + prefix + "Commons`.`Users`.`userName` = LEFT(USER(), (LOCATE('@', USER()) - 1))) WITH CASCADED CHECK OPTION", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("INSERT INTO Users(userName, fullName, Admin, Years) Values (?, ?, 1, '')", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, username);
            prep.setString(2, username);

            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public static ArrayList<User> getUsers() throws AccessException {
        if (isAdmin) {
            ArrayList<User> ret = new ArrayList<>();
            try (Connection con = DbInt.getConnection("Commons");
                 PreparedStatement prep = con.prepareStatement("SELECT userName, Years, fullName, Admin FROM Users", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    ret.add(new User(rs.getString("userName"), rs.getString("fullName"), rs.getString("Years"), rs.getInt("Admin") == 1));

                }
                ////DbInt.pCon.close()

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            return ret;
        } else {
            throw new AccessException("You are not an Admin");
        }
    }

    public static void deleteDb(String DB) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }

        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:mysql://%s/?useSSL=%s", Config.getDbLoc(), Config.getSSL());

        try (Connection con = DriverManager.getConnection(url, username, password);
             Statement st = con.createStatement()) {
            int Result = st.executeUpdate("DROP DATABASE `" + prefix + DB + "`");

        } catch (CommunicationsException e) {
            promptConfig();
        } catch (SQLException ex) {


            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                LogToFile.log(ex, Severity.FINER, "Derby shut down normally");

            } else if (ex.getErrorCode() == 1007) {
            } else {

                LogToFile.log(ex, Severity.SEVERE, ex.getMessage());
            }

        }


    }

    public static Iterable<String> getAllCustomerNames() {
        Collection<String> ret = new ArrayList<>();
        Iterable<String> years = getUserYears();
        for (String year : years) {

            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT Name FROM customerview", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = prep.executeQuery()
            ) {
                while (rs.next()) {
                    String name = rs.getString("Name");
                    if (!ret.contains(name)) {
                        ret.add(name);
                    }

                }
                ////DbInt.pCon.close()

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        }


        return ret;
    }

    public static Iterable<Customer> getAllCustomers() {
        Collection<String> names = new ArrayList<>();
        Collection<Customer> ret = new ArrayList<>();
        Iterable<String> years = getUserYears();
        for (String year : years) {

            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT Name,idCustomers FROM customerview", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("Name");
                    if (!names.contains(name)) {
                        names.add(name);
                        ret.add(new Customer(rs.getInt("idCustomers"), year));
                    }

                }
                ////DbInt.pCon.close()

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            } catch (Customer.CustomerNotFoundException ignored) {
            }
        }


        return ret;
    }

    public static String getYearsForUser(String uName) {
        String csvRet = "";

        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("SELECT Years FROM Users WHERE userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    csvRet = (rs.getString("Years"));

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return csvRet;
    }

    public static ArrayList<String> getUserYears() {
        String csvRet = "";
        ArrayList<String> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("SELECT YEARS FROM userView", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {

                csvRet = (rs.getString("YEARS"));

            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        List<String> retL = new ArrayList<String>(Arrays.asList(csvRet.split("\\s*,\\s*")));
        retL.forEach(year -> {
            if (!year.isEmpty()) {
                ret.add(year);
            }
        });
        return ret;
    }

    public static ArrayList<String> getYears() {
        String csvRet = "";
        ArrayList<String> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("SELECT Year FROM Years", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {

                ret.add(rs.getString(1));

            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }


    public static String getCategoryDate(String catName, String year) {
        Date ret = null;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT CatDate FROM categories WHERE catName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, catName);

            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getDate(1);

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        String output;
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("MM/dd/yyyy");
        output = formatter.format(ret);
        return output;
    }

    public static String getUserName() {
        String ret = "";

        try (Connection con = DbInt.getConnection();
             PreparedStatement prep = con.prepareStatement("SELECT LEFT(USER(), (LOCATE('@', USER()) - 1)) as 'uName'", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {


            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getString("uName");

                }
            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));

        }


        return ret;
    }

    public static User getUser(String year) {
        return new User(year);
    }

    public static User getCurrentUser() {
        User curUser = null;
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("SELECT Years, fullName, Admin FROM userView", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {

                curUser = new User(getUserName(), rs.getString("fullName"), rs.getString("Years"), rs.getInt("Admin") == 1);

            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return curUser;
    }

    public static Boolean verifyLogin(Pair<String, String> userPass) {
        username = userPass.getKey();
        password = userPass.getValue();
        Boolean successful = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {

            LogToFile.log(e, Severity.SEVERE, "Error loading database library. Please try reinstalling or contacting support.");
        }
        Statement st = null;
        ResultSet rs = null;
        pCon = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:mysql://%s/?useSSL=%s", Config.getDbLoc(), Config.getSSL());

        try {


            pCon = DriverManager.getConnection(url, username, password);
            if (pCon.isValid(2)) {
                User curUser = getCurrentUser();
                if (curUser != null) {
                    successful = true;
                    isAdmin = curUser.isAdmin();
                }
            }

            ////DbInt.pCon.close();

        } catch (CommunicationsException e) {
            promptConfig();
        } catch (SQLException e) {

            if (Objects.equals(e.getSQLState(), "28000")) {
                successful = false;
            } else {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));

            }
        }

        return successful;
    }

    private static void promptConfig() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Verify Databse?");
        alert.setHeaderText("Failed to connect to the databasse");
        alert.setContentText("Would you like to open the settings window to verify the connection?");

        ButtonType buttonTypeOne = new ButtonType("Open");
        ButtonType buttonTypeTwo = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            new Settings();

        }
    }

    public static boolean isAdmin() {
        return isAdmin;
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