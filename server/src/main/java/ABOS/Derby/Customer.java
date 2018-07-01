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

package ABOS.Derby;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by patrick on 7/26/16.
 */
@SuppressWarnings("unused")
public class Customer {
    private final String name;
    private String nameEdited;
    private String year;
    private String address = "";
    private String town = "";
    private String state = "";
    private String zipCode = "";
    private Double lat;
    private Double lon;
    private String phone = "";
    private String paid = "";
    private String delivered = "";
    private String email = "";
    private String orderId = "";
    private BigDecimal Donation = BigDecimal.ZERO;

    public Customer(String name, String year, String address,
                    String town,
                    String state,
                    String zipCode,
                    Double lat,
                    Double lon,
                    String phone,
                    String paid,
                    String delivered,
                    String email,
                    String orderId,
                    String nameEdited,
                    BigDecimal Donation) {

        this.name = name;
        this.year = year;
        this.address = address;
        this.town = town;
        this.state = state;
        this.zipCode = zipCode;
        this.lat = lat;
        this.lon = lon;
        this.phone = phone;
        this.paid = paid;
        this.delivered = delivered;
        this.email = email;
        this.orderId = orderId;
        this.Donation = Donation;
        this.nameEdited = nameEdited;
    }

    public Customer(String name, String year) {

        this.name = name;
        this.year = year;
        this.nameEdited = name;
    }

    public Customer(int ID, String year) {
        String ret = "";
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM CUSTOMERS WHERE ID=?")) {


            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getString("NAME");

                }
            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        this.name = ret;
        this.year = year;
        this.nameEdited = name;
    }

    public Double getLat() {
        Double ret = lat;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Lat FROM CUSTOMERS WHERE NAME=?")) {


            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getDouble("Lat");

                }
            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        return ret;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        Double ret = lon;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Lon FROM CUSTOMERS WHERE NAME=?")) {


            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getDouble("Lon");

                }
            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        return ret;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setDonation(BigDecimal donation) {
        Donation = donation;
    }

    public void updateValues(updateProgCallback updateProg, failCallback fail, updateMessageCallback updateMessage, getProgCallback getProgress) throws Exception {
        if (Objects.equals(DbInt.getCustInf(year, name, "NAME", ""), "")) {
            //Insert Mode
            double progressIncrement = (100 - getProgress.doAction()) / 3;
            updateProg.doAction(getProgress.doAction() + progressIncrement, 100);
            fail.doAction();
            updateMessage.doAction("Adding Customer");
            try (PreparedStatement writeCust = DbInt.getPrep(year, "INSERT INTO CUSTOMERS(NAME,ADDRESS, TOWN, STATE, ZIPCODE, Lat, Lon, PHONE, ORDERID , PAID,DELIVERED, EMAIL, DONATION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                writeCust.setString(1, this.nameEdited);
                writeCust.setString(2, this.address);
                writeCust.setString(3, this.town);
                writeCust.setString(4, this.state);
                writeCust.setString(5, this.zipCode);
                writeCust.setDouble(6, lat);
                writeCust.setDouble(7, lon);
                writeCust.setString(8, this.phone);
                writeCust.setString(9, this.orderId);
                writeCust.setString(10, this.paid);
                writeCust.setString(11, this.delivered);
                writeCust.setString(12, this.email);
                writeCust.setString(13, this.Donation.toPlainString());
                fail.doAction();
                writeCust.execute();
            }
            updateProg.doAction(getProgress.doAction() + progressIncrement, 100);
            try (PreparedStatement prep1 = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(ADDRESS, TOWN, STATE, ZIPCODE, Lat, Lon, ORDERED, NI, NH) VALUES(?,?,?,?,?,?, 'True','False','False')")) {
                prep1.setString(1, this.address);
                prep1.setString(2, this.town);
                prep1.setString(3, this.state);
                prep1.setString(4, this.zipCode);
                prep1.setDouble(5, lat);
                prep1.setDouble(6, lon);
                fail.doAction();

                prep1.execute();
            }
            updateProg.doAction(getProgress.doAction() + progressIncrement, 100);


        } else {
            //edit mode
            try (PreparedStatement updateCust = DbInt.getPrep("Set", "UPDATE customers SET ADDRESS=?, Town=?, STATE=?, ZIPCODE=?, Lat=?, Lon=?, ORDERED='True', NI='False', NH='False' WHERE ADDRESS=?")) {

                updateCust.setString(1, this.address);
                updateCust.setString(2, this.town);
                updateCust.setString(3, this.state);
                updateCust.setString(4, this.zipCode);
                updateCust.setDouble(5, lat);
                updateCust.setDouble(6, lon);
                updateCust.setString(7, getAddr());
                fail.doAction();

                updateCust.execute();

            }
            updateProg.doAction(10, 100);

            //Updates customer table in Utilities.Year DB with new info.
            try (PreparedStatement CustomerUpdate = DbInt.getPrep(year, "UPDATE CUSTOMERS SET NAME=?, ADDRESS=?, TOWN=?, STATE=?, ZIPCODE=?, Lat=?, Lon=?, PHONE=?,PAID=?,DELIVERED=?, EMAIL=?, DONATION=? WHERE NAME = ?")) {
                CustomerUpdate.setString(1, this.nameEdited);
                CustomerUpdate.setString(2, this.address);
                CustomerUpdate.setString(3, this.town);
                CustomerUpdate.setString(4, this.state);
                CustomerUpdate.setString(5, this.zipCode);
                CustomerUpdate.setDouble(6, lat);
                CustomerUpdate.setDouble(7, lon);
                CustomerUpdate.setString(8, this.phone);
                CustomerUpdate.setString(9, this.paid);
                CustomerUpdate.setString(10, this.delivered);
                CustomerUpdate.setString(11, this.email);
                CustomerUpdate.setString(12, this.Donation.toPlainString());
                CustomerUpdate.setString(13, name);
                fail.doAction();

                CustomerUpdate.execute();
            }
            updateProg.doAction(20, 100);

        }
    }

    public void deleteCustomer() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("WARNING!");
        alert.setHeaderText("BY CONTINUING YOU ARE PERMANENTLY REMOVING A CUSTOMER! ALL DATA MUST BE REENTERED!");
        alert.setContentText("Would you like to continue?");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            BigDecimal preEditOrderCost = BigDecimal.ZERO;
            Order.orderArray order = new Order().createOrderArray(year, getName(), false);
            for (Product.formattedProduct productOrder : order.orderData) {
                preEditOrderCost = preEditOrderCost.add(productOrder.extendedCost);
            }
            Year yearInfo = new Year(year);
            BigDecimal donations = yearInfo.getDonations().subtract(getDontation());
            int Lg = yearInfo.getLG() - getNoLawnProductsOrdered();
            int LP = yearInfo.getLP() - getNoLivePlantsOrdered();
            int Mulch = yearInfo.getMulch() - getNoMulchOrdered();
            BigDecimal OT = yearInfo.getOT().subtract(preEditOrderCost);
            int Customers = (yearInfo.getNoCustomers() - 1);
            BigDecimal GTot = yearInfo.getGTot().subtract(preEditOrderCost.add(getDontation()));
            BigDecimal Commis = getCommission(GTot);
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
            try (PreparedStatement prep = DbInt.getPrep(year, "DELETE FROM ORDERS WHERE NAME=?")) {

                prep.setString(1, name);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Error deleting customer. Try again or contact support.");
            }
            try (PreparedStatement prep = DbInt.getPrep(year, "DELETE FROM customers WHERE NAME=?")) {

                prep.setString(1, name);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Error deleting customer. Try again or contact support.");
            }
        }
    }

    public String[] getCustAddressFrmName() {

        String city = DbInt.getCustInf(year, name, "TOWN", town);
        String State = DbInt.getCustInf(year, name, "STATE", state);
        String zCode = DbInt.getCustInf(year, name, "ZIPCODE", zipCode);
        String strtAddress = DbInt.getCustInf(year, name, "ADDRESS", address);
        String[] address = new String[4];
        address[0] = city;
        address[1] = State;
        address[2] = zCode;
        address[3] = strtAddress;
        return address;
    }

    /**
     * Loops through Table to get total amount of Bulk Mulch ordered.
     *
     * @return The amount of Bulk mulch ordered
     */
    public int getNoMulchOrdered() {
        Order.orderArray order = new Order().createOrderArray(year, name, true);
        int quantMulchOrdered = 0;
        for (Product.formattedProduct productOrder : order.orderData) {
            if ((productOrder.productName.contains("Mulch")) && (productOrder.productName.contains("Bulk"))) {
                quantMulchOrdered += productOrder.orderedQuantity;
            }
        }

        return quantMulchOrdered;
    }

    private BigDecimal getCommission(BigDecimal totalCost) {
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
     * Loops through Table to get total amount of Lawn and Garden Products ordered.
     *
     * @return The amount of Lawn and Garden Products ordered
     */
    public int getNoLivePlantsOrdered() {
        Order.orderArray order = new Order().createOrderArray(year, name, true);
        int livePlantsOrdered = 0;
        for (Product.formattedProduct productOrder : order.orderData) {
            if ((productOrder.productName.contains("-P")) && (productOrder.productName.contains("-FV"))) {
                livePlantsOrdered += productOrder.orderedQuantity;
            }
        }

        return livePlantsOrdered;
    }

    /**
     * Loops through Table to get total amount of Lawn Products ordered.
     *
     * @return The amount of Live Plants ordered
     */
    public int getNoLawnProductsOrdered() {
        Order.orderArray order = new Order().createOrderArray(year, name, true);
        int lawnProductsOrdered = 0;
        for (Product.formattedProduct productOrder : order.orderData) {
            if (productOrder.productName.contains("-L")) {
                lawnProductsOrdered += productOrder.orderedQuantity;
            }
        }
        return lawnProductsOrdered;
    }

    public Integer getId() {
        Integer ret = 0;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT ID FROM CUSTOMERS WHERE NAME=?")) {


            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getInt("ID");

                }
            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    public String getAddr() {
        return DbInt.getCustInf(year, name, "ADDRESS", address);
    }

    public String getTown() {
        return DbInt.getCustInf(year, name, "TOWN", town);
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getState() {
        return DbInt.getCustInf(year, name, "STATE", state);
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return DbInt.getCustInf(year, name, "ZIPCODE", zipCode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.nameEdited = name;
    }

    /**
     * Return Phone number of the customer whose name has been specified.
     *
     * @return The Phone number of the specified customer
     */
    public String getPhone() {
        return DbInt.getCustInf(year, name, "PHONE", phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns if the customer has paid.
     *
     * @return The Payment status of the specified customer
     */
    public String getPaid() {
        return DbInt.getCustInf(year, name, "PAID", paid);
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    /**
     * Return Delivery status of the customer whose name has been specified.
     *
     * @return The Delivery status of the specified customer
     */
    public String getDelivered() {
        return DbInt.getCustInf(year, name, "DELIVERED", delivered);
    }

    public void setDelivered(String delivered) {
        this.delivered = delivered;
    }

    /**
     * Return Email Address of the customer whose name has been specified.
     *
     * @return The Email Address of the specified customer
     */
    public String getEmail() {
        return DbInt.getCustInf(year, name, "Email", email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Return Utilities.Order ID of the customer whose name has been specified.
     *
     * @return The Utilities.Order ID of the specified customer
     */
    public String getOrderId() {
        return DbInt.getCustInf(year, name, "ORDERID", orderId);
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    /**
     * Return Donation amount of the customer whose name has been specified.
     *
     * @return The Donation Amount of the specified customer
     */
    public BigDecimal getDontation() {
        BigDecimal ret = Donation;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT DONATION FROM CUSTOMERS WHERE NAME=?")) {


            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getBigDecimal("DONATION");

                }
            }
            ////Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        return ret;
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
}
