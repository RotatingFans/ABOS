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

import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by patrick on 7/27/16.
 */
public class Year {
    private static final int retInteger = 1;
    private static final int retString = 2;
    private static final int retBigDec = 3;
    private final String year;
    private String prefix = "ABOS-Test-";
    public Year(String year) {

        this.year = year;
    }

    public Iterable<String> getCustomerNames() {
        Collection<String> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Name FROM customerview");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("Name"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    public Iterable<category> getCategories() {
        Collection<category> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM Categories");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(new category(rs.getString("NAME"), rs.getString("DATE")));
                ////DbInt.pCon.close();
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    private Object getTots(String info, int retType) {
        Object ret = "";
        if (info == "Donations") {
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Doantion FROM customerview");
                 ResultSet rs = prep.executeQuery()
            ) {

                //prep.setString(1, info);


                while (rs.next()) {
                    switch (retType) {
                        case retBigDec:
                            ret = rs.getBigDecimal("Donation");
                            break;

                    }

                }
                //////DbInt.pCon.close();

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        } else {
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ordersview");
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
        }

        return ret;
    }

    public void deleteYear() {
/*        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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


        }*/
    }

    /*public void updateTots(BigDecimal donations, Integer Lg, Integer LP, Integer Mulch, BigDecimal OT, Integer Customers, BigDecimal Commis, BigDecimal GTot) {
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
    }*/



    /**
     * Gets the Total Lawn ANd Garden quantities Using getTots Function
     *
     * @return The total Lawn ANd Garden quantities amount
     *//*
    public int getLG() {
        return (int) getTots("LG", retInteger);
    }

    *//**
     * Gets the Total Live Plants quantities Using getTots Function
     *
     * @return The total Live Plants quantities amount
     *//*
    public int getLP() {
        return (int) getTots("LP", retInteger);
    }

    *//**
     * Gets the Total Mulch quantities Using getTots Function
     *
     * @return The total Mulch quantities amount
     *//*
    public int getMulch() {
        return (int) getTots("MULCH", retInteger);
    }
*/
    /**
     * Gets the order Total Using getTots Function
     *
     * @return The Order total amount
     */
    /**
     * Gets the Total Donations Using getTots Function
     *
     * @return The total donation amount
     */
    public BigDecimal getDonations() {
        BigDecimal ret = BigDecimal.ZERO;

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Donation FROM customerview");
             ResultSet rs = prep.executeQuery()
        ) {

            //prep.setString(1, info);


            while (rs.next()) {
                ret = ret.add(rs.getBigDecimal("Donation"));


            }
            //////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    public BigDecimal getOT() {
        BigDecimal ret = BigDecimal.ZERO;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Cost FROM ordersview");
             ResultSet rs = prep.executeQuery()
        ) {

            //prep.setString(1, info);


            while (rs.next()) {

                ret = ret.add(rs.getBigDecimal("Cost"));

            }


            //////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    /**
     * Gets the Total Customer Using getTots Function
     *
     * @return The total amount of Customers
     */
    public int getNoCustomers() {
        int ret = 0;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT COUNT(*) FROM customerview");
             ResultSet rs = prep.executeQuery()
        ) {

            //prep.setString(1, info);


            while (rs.next()) {

                ret = rs.getInt("COUNT(*)");

            }


            //////DbInt.pCon.close();

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
     *aeaeaeae
     * @return The Grand total amount
     */
    public BigDecimal getGTot() {
        return getDonations().add(getOT());
    }

    public int getQuant() {
        int ret = 0;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT SUM(Quant) FROM ordersview");
             ResultSet rs = prep.executeQuery()
        ) {

            //prep.setString(1, info);


            while (rs.next()) {

                ret = rs.getInt("SUM(Quant)");

            }


            //////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    public Product.formattedProduct[] getAllProducts() {
        //String[] toGet = {"ID", "PNAME", "SIZE", "UNIT"};
        List<Product.formattedProduct> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM products");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {

                ProductInfoArray.add(new Product.formattedProduct(ProductInfoResultSet.getInt("idproducts"), ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("Name"), ProductInfoResultSet.getString("UnitSize"), ProductInfoResultSet.getBigDecimal("Cost"), ProductInfoResultSet.getString("Category"), 0, BigDecimal.ZERO));
            }


        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ProductInfoArray.toArray(new Product.formattedProduct[ProductInfoArray.size()]);

    }

    public boolean addressExists(String address, String zipCode) {
        Boolean exists = false;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Name FROM customerview WHERE streetAddress=? AND Zip=?")) {
            prep.setString(1, address);
            prep.setString(2, zipCode);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                exists = true;

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return exists;
    }
//LEFT(USER(), (LOCATE('@', USER()) - 1))
    /**
     * Creates Database for the year specified.
     */
    public void CreateDb(ObservableList<Product.formattedProductProps> products, Collection<category> rowsCats) {
        DbInt.deleteDb(year);
        if (DbInt.createDb(year)) {
            //Create Tables
            //Create groups Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE `groups` (\n" +
                    "  `ID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `Name` varchar(45) NOT NULL,\n" +
                    "  PRIMARY KEY (`ID`)\n" +
                    ")")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Users Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE `users` (\n" +
                    "  `idusers` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `userName` varchar(255) NOT NULL,\n" +
                    "  `fullName` varchar(255) NOT NULL,\n" +
                    "  `uManage` varchar(255) NOT NULL,\n" +
                    "  `Admin` int(11) DEFAULT NULL,\n" +
                    "  `commonsID` int(11) NOT NULL,\n" +
                    "  PRIMARY KEY (`idusers`),\n" +
                    "UNIQUE INDEX `userName_UNIQUE` (`userName` ASC))")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Customers Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE `customers` (\n" +
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
                    "CONSTRAINT `fk_customers_1` FOREIGN KEY (`uName`) REFERENCES `users` (`userName`) ON DELETE CASCADE ON UPDATE CASCADE)")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Products Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE `products` (\n" +
                    "  `idproducts` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `ID` varchar(255) NOT NULL,\n" +
                    "  `Name` varchar(255) NOT NULL,\n" +
                    "  `UnitSize` varchar(255) NOT NULL,\n" +
                    "  `Cost` decimal(9,2) NOT NULL,\n" +
                    "  `Category` varchar(255) NOT NULL,\n" +
                    "  PRIMARY KEY (`idproducts`)\n" +
                    ")")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }


            //Create orders Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE `orders` (\n" +
                    "  `idOrders` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `uName` varchar(255) NOT NULL COMMENT '\t',\n" +
                    "  `custId` int(11) NOT NULL,\n" +
                    "  `Cost` decimal(9,2) NOT NULL DEFAULT 0,\n" +
                    "  `Quant` int(11) NOT NULL DEFAULT 0,\n" +
                    "  PRIMARY KEY (`idOrders`),\n" +
                    "  KEY `fk_Orders_1_idx` (`custId`),\n" +
                    "  CONSTRAINT `fk_Orders_1` FOREIGN KEY (`custId`) REFERENCES `customers` (`idcustomers`) ON DELETE CASCADE ON UPDATE CASCADE\n" +
                    ")")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create orderedProducts Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE `ordered_products` (\n" +
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
                    ")")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }

            //Create Triggers
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`ordered_products_BEFORE_INSERT` BEFORE INSERT ON `ordered_products` FOR EACH ROW\n" +
                    "BEGIN\n" +
                    "SET NEW.ExtendedCost = (NEW.Quantity * (SELECT Cost FROM products WHERE idproducts = NEW.ProductId));\n" +
                    "END")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }

            //Create Triggers
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`ordered_products_AFTER_INSERT` AFTER INSERT ON `ordered_products` FOR EACH ROW\n" +
                    "BEGIN\n" +
                    "UPDATE orders \n" +
                    "SET \n" +
                    "    Cost = (SELECT \n" +
                    "            SUM(ExtendedCost)\n" +
                    "        FROM\n" +
                    "            ordered_products\n" +
                    "        WHERE\n" +
                    "            idOrders = NEW.orderID)\n" +
                    "WHERE\n" +
                    "    idOrders = NEW.orderID;\n" +
                    "UPDATE orders \n" +
                    "SET \n" +
                    "    Quant = (SELECT \n" +
                    "            SUM(Quantity)\n" +
                    "        FROM\n" +
                    "            ordered_products\n" +
                    "        WHERE\n" +
                    "            idOrders = NEW.orderID)\n" +
                    "WHERE\n" +
                    "    idOrders = NEW.orderID;\n" +
                    "END")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Triggers
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`ordered_products_BEFORE_UPDATE` BEFORE UPDATE ON `ordered_products` FOR EACH ROW\n" +
                    "BEGIN\n" +
                    "SET NEW.ExtendedCost = (NEW.Quantity * (SELECT Cost FROM products WHERE idproducts = NEW.ProductId));\n" +
                    "\n" +
                    "END")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Triggers
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`ordered_products_AFTER_UPDATE` AFTER UPDATE ON `ordered_products` FOR EACH ROW\n" +
                    "BEGIN\n" +
                    "UPDATE orders \n" +
                    "SET \n" +
                    "    Cost = (SELECT \n" +
                    "            SUM(ExtendedCost)\n" +
                    "        FROM\n" +
                    "            ordered_products\n" +
                    "        WHERE\n" +
                    "            idOrders = NEW.orderID)\n" +
                    "WHERE\n" +
                    "    idOrders = NEW.orderID;\n" +
                    "UPDATE orders \n" +
                    "SET \n" +
                    "    Quant = (SELECT \n" +
                    "            SUM(Quantity)\n" +
                    "        FROM\n" +
                    "            ordered_products\n" +
                    "        WHERE\n" +
                    "            idOrders = NEW.orderID)\n" +
                    "WHERE\n" +
                    "    idOrders = NEW.orderID;\n" +
                    "END")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            } //Create Triggers
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`orders_AFTER_INSERT` AFTER INSERT ON `orders` FOR EACH ROW\n" +
                    "BEGIN\n" +
                    "UPDATE customers SET orderID = NEW.idOrders, Ordered=1 WHERE idcustomers=NEW.custId;\n" +
                    "END")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }//Create Triggers
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE DEFINER=`" + year + "`@`localhost` TRIGGER `" + prefix + year + "`.`products_AFTER_UPDATE` AFTER UPDATE ON `products` FOR EACH ROW\n" +
                    "BEGIN\n" +
                    "UPDATE ordered_products SET extendedCost = (quantity * NEW.Cost) WHERE ProductId = NEW.idproducts;\n" +
                    "END")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }//Create Views
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE ALGORITHM=UNDEFINED DEFINER=`" + year + "`@`localhost` SQL SECURITY DEFINER VIEW `" + prefix + year + "`.`customerview` AS select `" + prefix + year + "`.`customers`.`idcustomers` AS `idcustomers`,`" + prefix + year + "`.`customers`.`uName` AS `uName`,`" + prefix + year + "`.`customers`.`Name` AS `Name`,`" + prefix + year + "`.`customers`.`streetAddress` AS `streetAddress`,`" + prefix + year + "`.`customers`.`City` AS `City`,`" + prefix + year + "`.`customers`.`State` AS `State`,`" + prefix + year + "`.`customers`.`Zip` AS `Zip`,`" + prefix + year + "`.`customers`.`Phone` AS `Phone`,`" + prefix + year + "`.`customers`.`Email` AS `Email`,`" + prefix + year + "`.`customers`.`Lat` AS `Lat`,`" + prefix + year + "`.`customers`.`Lon` AS `Lon`,`" + prefix + year + "`.`customers`.`Ordered` AS `Ordered`,`" + prefix + year + "`.`customers`.`nH` AS `nH`,`" + prefix + year + "`.`customers`.`nI` AS `nI`,`" + prefix + year + "`.`customers`.`orderID` AS `orderID`,`" + prefix + year + "`.`customers`.`Donation` AS `Donation` from `" + prefix + year + "`.`customers` where (`" + prefix + year + "`.`customers`.`uName` = LEFT(USER(),LOCATE('@',USER()) - 1)) WITH CASCADED CHECK OPTION;\n")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }//Create Views
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE ALGORITHM=UNDEFINED DEFINER=`" + year + "`@`localhost` SQL SECURITY DEFINER VIEW `" + prefix + year + "`.`orderedproductsview` AS select `" + prefix + year + "`.`ordered_products`.`idordered_products` AS `idordered_products`,`" + prefix + year + "`.`ordered_products`.`custID` AS `custID`,`" + prefix + year + "`.`ordered_products`.`uName` AS `uName`,`" + prefix + year + "`.`ordered_products`.`orderID` AS `orderID`,`" + prefix + year + "`.`ordered_products`.`ProductId` AS `ProductId`,`" + prefix + year + "`.`ordered_products`.`Quantity` AS `Quantity`,`" + prefix + year + "`.`ordered_products`.`ExtendedCost` AS `ExtendedCost` from `" + prefix + year + "`.`ordered_products` where (`" + prefix + year + "`.`ordered_products`.`uName` = LEFT(USER(),LOCATE('@',USER()) - 1)) WITH CASCADED CHECK OPTION;\n")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }//Create Views
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE ALGORITHM=UNDEFINED DEFINER=`" + year + "`@`localhost` SQL SECURITY DEFINER VIEW `" + prefix + year + "`.`ordersview` AS select `" + prefix + year + "`.`orders`.`idOrders` AS `idOrders`,`" + prefix + year + "`.`orders`.`uName` AS `uName`,`" + prefix + year + "`.`orders`.`custId` AS `custId`,`" + prefix + year + "`.`orders`.`Cost` AS `Cost`,`" + prefix + year + "`.`orders`.`Quant` AS `Quant` from `" + prefix + year + "`.`orders` where (`" + prefix + year + "`.`orders`.`uName` = LEFT(USER(),LOCATE('@',USER()) - 1)) WITH CASCADED CHECK OPTION;\n")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }//Create Views
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE ALGORITHM=UNDEFINED DEFINER=`" + year + "`@`localhost` SQL SECURITY DEFINER VIEW `" + prefix + year + "`.`usersview` AS select `" + prefix + year + "`.`users`.`idusers` AS `idusers`,`" + prefix + year + "`.`users`.`userName` AS `userName`,`" + prefix + year + "`.`users`.`fullName` AS `fullName`,`" + prefix + year + "`.`users`.`uManage` AS `uManage`,`" + prefix + year + "`.`users`.`Admin` AS `Admin`,`" + prefix + year + "`.`users`.`commonsID` AS `commonsID` from `" + prefix + year + "`.`users` where (`" + prefix + year + "`.`users`.`userName` = LEFT(USER(),LOCATE('@',USER()) - 1)) WITH CASCADED CHECK OPTION;\n")) {

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }



/*
            //Create Categories Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE Categories(ID int(11) NOT NULL AUTO_INCREMENT,Name varchar(255), Date DATE )")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }*/

            //Insert products into Product table
            //Insert products into Product table
            //  String col = "";

            // col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
            try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO products(ID, Name, Cost, UnitSize, Category) VALUES (?,?,?,?,?)")) {
                for (int i = 0; i < products.size(); i++) {
                    Product.formattedProductProps curRow = products.get(i);
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

           /* //Add Categories
            rowsCats.forEach(cat -> {
                try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO Categories(Name, Date) VALUES (?,?)")) {
                    prep.setString(1, cat.catName);
                    prep.setDate(2, Date.valueOf(cat.catDate));

                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
            });*/

          /*  //ORDers Table
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
            }*/
            //ADD to Year
            addYear();
        } else {
            LogToFile.log(null, Severity.WARNING, "Year already exists: Please rename the year you are adding or delete the Year you are trying to overwrite.");
        }

    }

    public void updateDb(String year, ObservableList<Product.formattedProductProps> products, Collection<category> rowsCats) {
        //Delete Year Customer table

        try (PreparedStatement addCol = DbInt.getPrep(year, "TRUNCATE TABLE products")) {
            addCol.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

/*        try (PreparedStatement addCol = DbInt.getPrep(year, "DROP TABLE \"Categories\"")) {
            addCol.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }*/
        //Recreate Year Customer table

        //Create Products Table


     /*   try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE Categories(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Name varchar(255), Date DATE)")) {
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
        });*/
        //Insert products into Product table
        try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO products(ID, Name, Cost, UnitSize, Category) VALUES (?,?,?,?,?)")) {
            for (int i = 0; i < products.size(); i++) {
                Product.formattedProductProps curRow = products.get(i);
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
    }

    public void addYear() {
        /*try (PreparedStatement prep = DbInt.getPrep("Commons", "INSERT INTO YEARS(YEARS) VALUES(?)")) {
            prep.setString(1, year);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }*/
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