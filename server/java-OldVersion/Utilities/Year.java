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

package Utilities;/*
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

import Exceptions.AccessException;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * Created by patrick on 7/27/16.
 */
public class Year {
    private static final int retInteger = 1;
    private static final int retString = 2;
    private static final int retBigDec = 3;
    private final String year;
    private final String uName;

    public Year(String year) {
        this(year, "");
    }

    public Year(String year, String uName) {
        this.uName = uName;
        this.year = year;
    }

    public void deleteYear() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("WARNING!");
        alert.setHeaderText("You are about to delete an entire Year. This cannot be reversed");
        alert.setContentText("Would you like to continue with the deletion?");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            //DROP USER [ IF EXISTS ] user_name
            try {
                DbInt.getUsers().forEach(user -> {
                    user.deleteFromYear(year);
                });
            } catch (Exception ignored) {
            }
            DbInt.deleteDb(year);
            String command;
            if (DbInt.getDatabaseVersion().greaterThanOrEqual("5.7")) {
                command = "DROP USER IF EXISTS `" + year + "'@'localhost'";
            } else {
                command = "DROP USER '" + year + "'@'localhost'";
            }
            try (Connection con = DbInt.getConnection("Commons");
                 PreparedStatement prep = con.prepareStatement(command, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            try (Connection con = DbInt.getConnection("Commons");
                 PreparedStatement prep = con.prepareStatement("DELETE FROM Years WHERE Year=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setString(1, year);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }


        }
    }

    public boolean addressExists(String address, String zipCode) {
        Boolean exists = false;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Name FROM customerview WHERE streetAddress=? AND Zip=? AND uName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, address);
            prep.setString(2, zipCode);
            prep.setString(3, uName);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                exists = true;

            }
            ////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return exists;
    }

    /**
     * Creates Database for the year specified.
     */
    public void CreateDb(ObservableList<formattedProductProps> products, Collection<category> rowsCats) {
        String prefix = DbInt.prefix;


        if (DbInt.createDb(year)) {
            char[] possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789").toCharArray();
            String randomStr = RandomStringUtils.random(15, 0, possibleCharacters.length - 1, false, false, possibleCharacters, new SecureRandom());
            String createAndGrantCommand = "CREATE USER '" + year + "'@'localhost' IDENTIFIED BY '" + randomStr + "'";

            if (DbInt.getDatabaseVersion().greaterThanOrEqual("5.7")) {
                createAndGrantCommand = "CREATE USER IF NOT EXISTS '" + year + "'@'localhost' IDENTIFIED BY '" + randomStr + "'";
            }
            try (Connection con = DbInt.getConnection();
                 PreparedStatement prep = con.prepareStatement("", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                prep.addBatch(createAndGrantCommand);
                prep.executeBatch();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            try (Connection con = DbInt.getConnection();
                 PreparedStatement prep = con.prepareStatement("", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                prep.addBatch("GRANT SELECT, INSERT, UPDATE, DELETE, INDEX, SHOW VIEW, TRIGGER ON `" + DbInt.prefix + year + "`.* TO '" + year + "'@'localhost'");
                prep.executeBatch();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Tables
            //Create groups Table
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE TABLE `groups` (\n" +
                         "  `ID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                         "  `Name` varchar(45) NOT NULL,\n" +
                         "  PRIMARY KEY (`ID`)\n" +
                         ")", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("INSERT INTO groups(Name) Values('Ungrouped')", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Users Table
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE TABLE `users` (\n" +
                         "  `idusers` int(11) NOT NULL AUTO_INCREMENT,\n" +
                         "  `userName` varchar(255) NOT NULL,\n" +
                         "  `fullName` varchar(255) NOT NULL,\n" +
                         "  `uManage` varchar(255) NOT NULL,\n" +
                         "  `Admin` int(11) DEFAULT NULL,\n" +
                         "  `ACL` int(11) NOT NULL DEFAULT 1,\n" +
                         "  `commonsID` int(11) NOT NULL,\n" +
                         "  `groupId` int(11) NULL,\n" +
                         "  PRIMARY KEY (`idusers`),\n" +
                         "UNIQUE INDEX `userName_UNIQUE` (`userName` ASC)," +
                         "CONSTRAINT `fk_users_1` FOREIGN KEY (`groupId`) REFERENCES `groups` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create customers Table
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE TABLE `customers` (\n" +
                         "  `idcustomers` int(11) NOT NULL AUTO_INCREMENT,\n" +
                         "  `uName` varchar(255) NOT NULL,\n" +
                         "  `Name` varchar(255) NOT NULL,\n" +
                         "  `streetAddress` varchar(255) NOT NULL,\n" +
                         "  `City` varchar(255) NOT NULL,\n" +
                         "  `State` varchar(255) NOT NULL,\n" +
                         "  `Zip` varchar(5) NOT NULL,\n" +
                         "  `Phone` varchar(255) NULL,\n" +
                         "  `Email` varchar(255) NULL,\n" +
                         "  `Lat` double NOT NULL,\n" +
                         "  `Lon` double NOT NULL,\n" +
                         "  `Ordered` int(11) DEFAULT NULL,\n" +
                         "  `nH` int(11) DEFAULT NULL,\n" +
                         "  `nI` int(11) DEFAULT NULL,\n" +
                         "  `orderID` varchar(45) DEFAULT NULL,\n" +
                         "  `Donation` DECIMAL(9,2) NULL,\n" +
                         "  PRIMARY KEY (`idcustomers`),\n" +
                         "KEY `fk_customers_1_idx` (`uName`),\n" +
                         "CONSTRAINT `fk_customers_1` FOREIGN KEY (`uName`) REFERENCES `users` (`userName`) ON DELETE CASCADE ON UPDATE CASCADE)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Categories Table
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE TABLE `categories` (\n" +
                         "  `idcategories` INT NOT NULL AUTO_INCREMENT,\n" +
                         "  `catName` VARCHAR(255) NOT NULL,\n" +
                         "  `catDate` DATE NULL,\n" +
                         "UNIQUE INDEX `catName_UNIQUE` (`catName` ASC)," +
                         "  PRIMARY KEY (`idcategories`));\n", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Products Table
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE TABLE `products` (\n" +
                         "  `idproducts` int(11) NOT NULL AUTO_INCREMENT,\n" +
                         "  `ID` varchar(255) NOT NULL,\n" +
                         "  `Name` varchar(255) NOT NULL,\n" +
                         "  `UnitSize` varchar(255) NOT NULL,\n" +
                         "  `Cost` decimal(9,2) NOT NULL,\n" +
                         "  `Category` varchar(255) NOT NULL,\n" +
                         "UNIQUE INDEX `ID_UNIQUE` (`ID` ASC)," +
                         "  PRIMARY KEY (`idproducts`)\n" +
                         ")", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }


            //Create orders Table
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE TABLE `orders` (\n" +
                         "  `idOrders` int(11) NOT NULL AUTO_INCREMENT,\n" +
                         "  `uName` varchar(255) NOT NULL COMMENT '\t',\n" +
                         "  `custId` int(11) NOT NULL,\n" +
                         "  `Cost` decimal(9,2) NOT NULL DEFAULT 0,\n" +
                         "  `Quant` int(11) NOT NULL DEFAULT 0,\n" +
                         "  `paid` int(11) NULL DEFAULT 0,\n" +
                         "  `delivered` int(11) NULL DEFAULT 0,\n" +
                         "  PRIMARY KEY (`idOrders`),\n" +
                         "  KEY `fk_Orders_1_idx` (`custId`),\n" +
                         "  CONSTRAINT `fk_Orders_1` FOREIGN KEY (`custId`) REFERENCES `customers` (`idcustomers`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                         ")", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create orderedProducts Table
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE TABLE `ordered_products` (\n" +
                         "  `idordered_products` int(11) NOT NULL AUTO_INCREMENT,\n" +
                         "  `uName` varchar(255) NOT NULL,\n" +
                         "  `custID` int(11) NOT NULL,\n" +
                         "  `orderID` int(11) NOT NULL,\n" +
                         "  `ProductId` int(11) NOT NULL,\n" +
                         "  `Quantity` int(11) DEFAULT NULL,\n" +
                         "  `ExtendedCost` decimal(9,2) DEFAULT NULL,\n" +
                         "  PRIMARY KEY (`idordered_products`),\n" +
                         "  KEY `fk_ordered_products_1_idx` (`custID`),\n" +
                         "  KEY `fk_ordered_products_2_idx` (`orderID`),\n" +
                         "  KEY `fk_ordered_products_3_idx` (`ProductId`),\n" +
                         "  CONSTRAINT `fk_ordered_products_1` FOREIGN KEY (`custID`) REFERENCES `customers` (`idcustomers`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                         "  CONSTRAINT `fk_ordered_products_2` FOREIGN KEY (`orderID`) REFERENCES `orders` (`idOrders`) ON DELETE CASCADE ON UPDATE CASCADE,\n" +
                         "  CONSTRAINT `fk_ordered_products_3` FOREIGN KEY (`ProductId`) REFERENCES `products` (`idproducts`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                         ")", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE TABLE `Preferences` (\n" +
                         "  `key` VARCHAR(45) NOT NULL,\n" +
                         "  `Value` VARCHAR(255) NULL,\n" +
                         "  PRIMARY KEY (`key`));\n", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Triggers
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`ordered_products_BEFORE_INSERT` BEFORE INSERT ON `ordered_products` FOR EACH ROW\n" +
                         "BEGIN\n" +
                         "SET NEW.ExtendedCost = (NEW.Quantity * (SELECT Cost FROM products WHERE idproducts = NEW.ProductId));\n" +
                         "END", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }

            //Create Triggers
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`ordered_products_AFTER_INSERT` AFTER INSERT ON `ordered_products` FOR EACH ROW\n" +
                         "BEGIN\n" +
                         "UPDATE orders \n" +
                         "SET \n" +
                         "    Cost = (SELECT \n" +
                         "            SUM(ExtendedCost)\n" +
                         "        FROM\n" +
                         "            ordered_products\n" +
                         "        WHERE\n" +
                         "            orderID = NEW.orderID)\n" +
                         "WHERE\n" +
                         "    idOrders = NEW.orderID;\n" +
                         "UPDATE orders \n" +
                         "SET \n" +
                         "    Quant = (SELECT \n" +
                         "            SUM(Quantity)\n" +
                         "        FROM\n" +
                         "            ordered_products\n" +
                         "        WHERE\n" +
                         "            orderID = NEW.orderID)\n" +
                         "WHERE\n" +
                         "    idOrders = NEW.orderID;\n" +
                         "END", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Triggers
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`ordered_products_BEFORE_UPDATE` BEFORE UPDATE ON `ordered_products` FOR EACH ROW\n" +
                         "BEGIN\n" +
                         "SET NEW.ExtendedCost = (NEW.Quantity * (SELECT Cost FROM products WHERE idproducts = NEW.ProductId));\n" +
                         "\n" +
                         "END", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Triggers
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`ordered_products_AFTER_UPDATE` AFTER UPDATE ON `ordered_products` FOR EACH ROW\n" +
                         "BEGIN\n" +
                         "UPDATE orders \n" +
                         "SET \n" +
                         "    Cost = (SELECT \n" +
                         "            SUM(ExtendedCost)\n" +
                         "        FROM\n" +
                         "            ordered_products\n" +
                         "        WHERE\n" +
                         "            orderID = NEW.orderID)\n" +
                         "WHERE\n" +
                         "    idOrders = NEW.orderID;\n" +
                         "UPDATE orders \n" +
                         "SET \n" +
                         "    Quant = (SELECT \n" +
                         "            SUM(Quantity)\n" +
                         "        FROM\n" +
                         "            ordered_products\n" +
                         "        WHERE\n" +
                         "            orderID = NEW.orderID)\n" +
                         "WHERE\n" +
                         "    idOrders = NEW.orderID;\n" +
                         "END", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`orders_AFTER_INSERT` AFTER INSERT ON `orders` FOR EACH ROW\n" +
                         "BEGIN\n" +
                         "UPDATE customers SET orderID = NEW.idOrders, Ordered=1 WHERE idcustomers=NEW.custId;\n" +
                         "END", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`products_AFTER_UPDATE` AFTER UPDATE ON `products` FOR EACH ROW\n" +
                         "BEGIN\n" +
                         "UPDATE ordered_products SET extendedCost = (quantity * NEW.Cost) WHERE ProductId = NEW.idproducts;\n" +
                         "END", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Views
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE ALGORITHM=UNDEFINED DEFINER=`" + year + "`@`localhost` SQL SECURITY DEFINER VIEW `" + prefix + year + "`.`usersview` AS select `" + prefix + year + "`.`users`.`idusers` AS `idusers`,`" + prefix + year + "`.`users`.`userName` AS `userName`,`" + prefix + year + "`.`users`.`fullName` AS `fullName`,`" + prefix + year + "`.`users`.`uManage` AS `uManage`,`" + prefix + year + "`.`users`.`Admin` AS `Admin`,`" + prefix + year + "`.`users`.`ACL` AS `ACL`,`" + prefix + year + "`.`users`.`commonsID` AS `commonsID`,\n" +
                         "        `users`.`groupId` AS `groupId` from `" + prefix + year + "`.`users` where (`" + prefix + year + "`.`users`.`userName` = LEFT(USER(),LOCATE('@',USER()) - 1)) WITH CASCADED CHECK OPTION;\n", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }


            //Create Views
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE \n" +
                         "    ALGORITHM = UNDEFINED \n" +
                         "    DEFINER = `" + year + "`@`localhost` \n" +
                         "    SQL SECURITY DEFINER\n" +
                         "VIEW `customerview` AS\n" +
                         "    SELECT \n" +
                         "        `customers`.`idcustomers` AS `idcustomers`,\n" +
                         "        `customers`.`uName` AS `uName`,\n" +
                         "        `customers`.`Name` AS `Name`,\n" +
                         "        `customers`.`streetAddress` AS `streetAddress`,\n" +
                         "        `customers`.`City` AS `City`,\n" +
                         "        `customers`.`State` AS `State`,\n" +
                         "        `customers`.`Zip` AS `Zip`,\n" +
                         "        `customers`.`Phone` AS `Phone`,\n" +
                         "        `customers`.`Email` AS `Email`,\n" +
                         "        `customers`.`Lat` AS `Lat`,\n" +
                         "        `customers`.`Lon` AS `Lon`,\n" +
                         "        `customers`.`Ordered` AS `Ordered`,\n" +
                         "        `customers`.`nH` AS `nH`,\n" +
                         "        `customers`.`nI` AS `nI`,\n" +
                         "        `customers`.`orderID` AS `orderID`,\n" +
                         "        `customers`.`Donation` AS `Donation`\n" +
                         "    FROM\n" +
                         "        `customers`\n" +
                         "    WHERE\n" +
                         "        FIND_IN_SET(`customers`.`uName`,\n" +
                         "                (SELECT \n" +
                         "                        `usersview`.`uManage`\n" +
                         "                    FROM\n" +
                         "                        `usersview`)) WITH CASCADED CHECK OPTION", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE \n" +
                         "    ALGORITHM = UNDEFINED \n" +
                         "    DEFINER = `" + year + "`@`localhost` \n" +
                         "    SQL SECURITY DEFINER\n" +
                         "VIEW `orderedproductsview` AS\n" +
                         "    SELECT \n" +
                         "        `ordered_products`.`idordered_products` AS `idordered_products`,\n" +
                         "        `ordered_products`.`custID` AS `custID`,\n" +
                         "        `ordered_products`.`uName` AS `uName`,\n" +
                         "        `ordered_products`.`orderID` AS `orderID`,\n" +
                         "        `ordered_products`.`ProductId` AS `ProductId`,\n" +
                         "        `ordered_products`.`Quantity` AS `Quantity`,\n" +
                         "        `ordered_products`.`ExtendedCost` AS `ExtendedCost`\n" +
                         "    FROM\n" +
                         "        `ordered_products`\n" +
                         "    WHERE\n" +
                         "        FIND_IN_SET(`ordered_products`.`uName`,\n" +
                         "                (SELECT \n" +
                         "                        `usersview`.`uManage`\n" +
                         "                    FROM\n" +
                         "                        `usersview`)) WITH CASCADED CHECK OPTION", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("CREATE \n" +
                         "    ALGORITHM = UNDEFINED \n" +
                         "    DEFINER = `" + year + "`@`localhost` \n" +
                         "    SQL SECURITY DEFINER\n" +
                         "VIEW `ordersview` AS\n" +
                         "    SELECT \n" +
                         "        `orders`.`idOrders` AS `idOrders`,\n" +
                         "        `orders`.`uName` AS `uName`,\n" +
                         "        `orders`.`custId` AS `custId`,\n" +
                         "        `orders`.`Cost` AS `Cost`,\n" +
                         "        `orders`.`Quant` AS `Quant`,\n" +
                         "        `orders`.`paid` AS `paid`,\n" +
                         "        `orders`.`delivered` AS `delivered`\n" +
                         "    FROM\n" +
                         "        `orders`\n" +
                         "    WHERE\n" +
                         "        FIND_IN_SET(`orders`.`uName`,\n" +
                         "                (SELECT \n" +
                         "                        `usersview`.`uManage`\n" +
                         "                    FROM\n" +
                         "                        `usersview`)) WITH CASCADED CHECK OPTION", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }



/*
            //Create Categories Table
            try (PreparedStatement prep = Utilities.DbInt.getPrep(year, "CREATE TABLE Categories(ID int(11) NOT NULL AUTO_INCREMENT,Name varchar(255), Date DATE )")) {
                prep.execute();
            } catch (SQLException e) {
                Utilities.LogToFile.log(e, Utilities.Severity.SEVERE, Utilities.CommonErrors.returnSqlMessage(e));
            }*/

            //Insert products into Product table
            //Insert products into Product table
            //  String col = "";

            // col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("INSERT INTO products(ID, Name, Cost, UnitSize, Category) VALUES (?,?,?,?,?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                for (formattedProductProps curRow : products) {
                    String cat = (curRow.getProductCategory() != null) ? curRow.getProductCategory() : "";
                    prep.setString(1, curRow.getProductID());
                    prep.setString(2, curRow.getProductName());
                    prep.setBigDecimal(3, curRow.getProductUnitPrice());
                    prep.setString(4, curRow.getProductSize());
                    prep.setString(5, cat);
                    prep.execute();
                }
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }

            //Add Categories
            rowsCats.forEach(cat -> {
                try (Connection con = DbInt.getConnection(year);
                     PreparedStatement prep = con.prepareStatement("INSERT INTO categories(catName, catDate) VALUES (?,?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    prep.setString(1, cat.catName);
                    prep.setDate(2, Date.valueOf(cat.catDate));

                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
            });

            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("INSERT INTO Preferences(key, Value) Values (?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setString(1, "Version");
                prep.setString(2, Config.getProgramVersion().toString());

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }

            addYear();
        } else {
            updateDb(year, products, rowsCats);
        }

    }

    public void updateDb(String year, ObservableList<formattedProductProps> products, Collection<category> rowsCats) {
        //Delete Utilities.Year Utilities.Customer table

       /* try (Connection con = Utilities.DbInt.getConnection(year);
             PreparedStatement addCol = con.prepareStatement("DELETE FROM products", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            addCol.addBatch("DELETE FROM products");
            addCol.addBatch("ALTER TABLE products AUTO_INCREMENT = 1");
            addCol.executeBatch();
        } catch (SQLException e) {
            Utilities.LogToFile.log(e, Utilities.Severity.SEVERE, Utilities.CommonErrors.returnSqlMessage(e));
        }

        try (Connection con = Utilities.DbInt.getConnection(year);
             PreparedStatement addCol = con.prepareStatement("TRUCNATE TABLE categories", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            addCol.addBatch("DELETE FROM categories");
            addCol.addBatch("ALTER TABLE categories AUTO_INCREMENT = 1");
            addCol.executeBatch();
        } catch (SQLException e) {
            Utilities.LogToFile.log(e, Utilities.Severity.SEVERE, Utilities.CommonErrors.returnSqlMessage(e));
        }*/
        //Recreate Utilities.Year Utilities.Customer table

        //Create Products Table


     /*   try (PreparedStatement prep = Utilities.DbInt.getPrep(year, "CREATE TABLE Categories(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Name varchar(255), Date DATE)")) {
            prep.execute();
        } catch (SQLException e) {
            Utilities.LogToFile.log(e, Utilities.Severity.SEVERE, Utilities.CommonErrors.returnSqlMessage(e));
        }*/
        //Add Categories
        //Add Categories
        rowsCats.forEach(cat -> {
            try (Connection con = DbInt.getConnection(year);

                 PreparedStatement prep = con.prepareStatement("INSERT INTO categories(catName, catDate) VALUES (?,?)" +
                         "ON DUPLICATE KEY UPDATE catName=?, catDate=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setString(1, cat.catName);
                prep.setDate(2, Date.valueOf(cat.catDate));
                prep.setString(3, cat.catName);
                prep.setDate(4, Date.valueOf(cat.catDate));

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        });
        //Insert products into Product table

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("INSERT INTO products(ID, Name, Cost, UnitSize, Category) VALUES (?,?,?,?,?)" +
                     "ON DUPLICATE KEY UPDATE ID=?, Name=?, Cost=?, UnitSize=?, Category=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            for (formattedProductProps curRow : products) {
                String cat = (curRow.getProductCategory() != null) ? curRow.getProductCategory() : "";
                prep.setString(1, curRow.getProductID());
                prep.setString(2, curRow.getProductName());
                prep.setBigDecimal(3, curRow.getProductUnitPrice());
                prep.setString(4, curRow.getProductSize());
                prep.setString(5, cat);

                prep.setString(6, curRow.getProductID());
                prep.setString(7, curRow.getProductName());
                prep.setBigDecimal(8, curRow.getProductUnitPrice());
                prep.setString(9, curRow.getProductSize());
                prep.setString(10, cat);
                prep.execute();
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public void addYear() {
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("INSERT INTO Years(Year) VALUES (?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, year);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public ArrayList<User> getUsers() throws AccessException {
        if (DbInt.isAdmin()) {
            ArrayList<User> ret = new ArrayList<>();
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT userName, fullName, Admin, groupId, uManage, ACL FROM users", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    if (rs.getInt("ACL") > 0) {
                        ret.add(new User(rs.getString("userName"), rs.getString("fullName"), rs.getString("uManage"), DbInt.getYearsForUser(rs.getString("userName")), rs.getInt("Admin") == 1, rs.getInt("groupId")));
                    }

                }
                ////Utilities.DbInt.pCon.close()

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            return ret;
        } else {
            throw new AccessException("You are not Admin.");
        }
    }

    private Object getTots(String info, int retType) {
        Object ret = "";
        if (Objects.equals(info, "Donations")) {
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT Doantion FROM customerview WHERE " + (Objects.equals(uName, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setString(1, uName);

                try (ResultSet rs = prep.executeQuery()) {


                    //prep.setString(1, info);


                    while (rs.next()) {
                        switch (retType) {
                            case retBigDec:
                                ret = rs.getBigDecimal("Donation");
                                break;

                        }

                    }
                }
                //////Utilities.DbInt.pCon.close()

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        } else {
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT * FROM ordersview WHERE " + (Objects.equals(uName, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setString(1, uName);

                try (ResultSet rs = prep.executeQuery()) {

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
                }
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        }

        return ret;
    }

    public formattedProduct[] getAllProducts() {
        //String[] toGet = {"ID", "PNAME", "SIZE", "UNIT"};
        List<formattedProduct> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM products", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {

                ProductInfoArray.add(new formattedProduct(ProductInfoResultSet.getInt("idproducts"), ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("Name"), ProductInfoResultSet.getString("UnitSize"), ProductInfoResultSet.getBigDecimal("Cost"), ProductInfoResultSet.getString("Category"), 0, BigDecimal.ZERO));
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ProductInfoArray.toArray(new formattedProduct[0]);

    }

    public Iterable<String> getCustomerNames() {
        Collection<String> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Name FROM customerview WHERE " + (Objects.equals(uName, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);

            try (ResultSet rs = prep.executeQuery()) {


                while (rs.next()) {

                    ret.add(rs.getString("Name"));

                }
            }
            ////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    public Iterable<String> getCustomerNames(String user) {
        Collection<String> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Name FROM customerview  WHERE " + (Objects.equals(uName, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);

            try (ResultSet rs = prep.executeQuery()) {


                while (rs.next()) {

                    ret.add(rs.getString("Name"));

                }
            }
            ////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    public Iterable<Customer> getCustomers() {
        return getCustomers(uName);
    }

    public Iterable<Customer> getCustomers(String user) {
        Collection<Customer> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT `idCustomers`, `uName`,\n" +
                     "    `Name`,\n" +
                     "    `streetAddress`,\n" +
                     "    `City`,\n" +
                     "    `State`,\n" +
                     "    `Zip`,\n" +
                     "    `Phone`,\n" +
                     "    `Email`,\n" +
                     "    `Lat`,\n" +
                     "    `Lon`,\n" +
                     "    `Ordered`,\n" +
                     "    `nH`,\n" +
                     "    `nI`,\n" +
                     "    `orderID`,\n" +
                     "    `Donation` FROM " + (DbInt.isAdmin() ? "customers" : "customerview") + " WHERE " + (Objects.equals(user, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, user);

            try (ResultSet rs = prep.executeQuery()) {


                while (rs.next()) {
//        this(-1, name, year, null, null, null, null, null, null, null, false, false, null, null, null, Utilities.DbInt.getUserName());

                    ret.add(new Customer(rs.getInt("idCustomers"), rs.getString("Name"), year, rs.getString("streetAddress"), rs.getString("City"), rs.getString("State"), rs.getString("Zip"), rs.getDouble("lat"), rs.getDouble("lon"), rs.getString("Phone"), null, null, rs.getString("Email"), null, rs.getBigDecimal("Donation"), rs.getString("uName")));

                }
            }
            ////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    public Iterable<category> getCategories() {
        Collection<category> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM categories", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {

                ret.add(new category(rs.getString("catName"), rs.getString("catDate")));
                ////Utilities.DbInt.pCon.close();
            }
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
        BigDecimal ret = BigDecimal.ZERO;

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Donation FROM customerview WHERE " + (Objects.equals(uName, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);

            try (ResultSet rs = prep.executeQuery()) {

                //prep.setString(1, info);


                while (rs.next()) {
                    ret = ret.add(rs.getBigDecimal("Donation"));


                }
            }
            //////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    public BigDecimal getOT() {
        BigDecimal ret = BigDecimal.ZERO;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Cost FROM ordersview WHERE " + (Objects.equals(uName, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);

            try (ResultSet rs = prep.executeQuery()) {

                //prep.setString(1, info);


                while (rs.next()) {

                    ret = ret.add(rs.getBigDecimal("Cost"));

                }
            }

            //////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    /**
     * Gets the Total Utilities.Customer Using getTots Function
     *
     * @return The total amount of customers
     */
    public int getNoCustomers() {
        int ret = 0;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT COUNT(*) FROM customerview WHERE " + (Objects.equals(uName, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);

            try (ResultSet rs = prep.executeQuery()) {

                //prep.setString(1, info);


                while (rs.next()) {

                    ret = rs.getInt("COUNT(*)");

                }
            }

            //////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    /**
     * Gets the Total Commissions Using getTots Function
     *
     * @return The total Commissions amount
     */
    public BigDecimal getCommis() {
        BigDecimal totalCost = getGTot();
        BigDecimal commision = BigDecimal.ZERO;
        if ((totalCost.compareTo(new BigDecimal("299.99")) > 0) && (totalCost.compareTo(new BigDecimal("500.01")) < 0)) {
            commision = totalCost.multiply(new BigDecimal("0.05"));
        } else if ((totalCost.compareTo(new BigDecimal("500.01")) > 0) && (totalCost.compareTo(new BigDecimal("1000.99")) < 0)) {
            commision = totalCost.multiply(new BigDecimal("0.1"));
        } else if (totalCost.compareTo(new BigDecimal("1000")) >= 0) {
            commision = totalCost.multiply(new BigDecimal("0.15"));
        }
        return commision;
    }

    /**
     * Gets the Grand Total Using getTots Function
     * aeaeaeae
     *
     * @return The Grand total amount
     */
    public BigDecimal getGTot() {
        return getDonations().add(getOT());
    }

    public int getQuant() {
        int ret = 0;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT SUM(Quant) FROM ordersview WHERE " + (Objects.equals(uName, "") ? "''=?" : "uName=?"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);

            try (ResultSet rs = prep.executeQuery()) {

                //prep.setString(1, info);


                while (rs.next()) {

                    ret = rs.getInt("SUM(Quant)");

                }
            }

            //////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
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