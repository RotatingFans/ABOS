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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.math.BigDecimal;
import java.sql.Connection;
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
    private int ID = -1;
    private String nameEdited = "";

    private String year = "";
    private String address = "";
    private String town = "";
    private String state = "";
    private String zipCode = "";
    private Double lat;
    private Double lon;
    private String phone = "";
    private Boolean paid = false;
    private Boolean delivered = false;
    private String email = "";
    private String orderId = "";
    private String user = "";
    private BigDecimal Donation = BigDecimal.ZERO;

    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress");
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper(this, "message");

    public Customer(Integer ID, String name, String year, String address,
                    String town,
                    String state,
                    String zipCode,
                    Double lat,
                    Double lon,
                    String phone,
                    Boolean paid,
                    Boolean delivered,
                    String email,
                    String nameEdited,
                    BigDecimal Donation) {
        this(ID, name, year, address, town, state, zipCode, lat, lon, phone, paid, delivered, email, nameEdited, Donation, DbInt.getUserName());
    }
    public Customer(Integer ID, String name, String year, String address,
                    String town,
                    String state,
                    String zipCode,
                    Double lat,
                    Double lon,
                    String phone,
                    Boolean paid,
                    Boolean delivered,
                    String email,
                    String nameEdited,
                    BigDecimal Donation,
                    String user) {
        this.ID = ID;
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
        this.user = user;
    }

    public Customer(String name, String year) {
        this.user = DbInt.getUserName();

        this.name = name;
        this.year = year;
        this.nameEdited = name;
    }

    public Customer(int ID, String year) throws CustomerNotFoundException {
        String ret = "";
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Name, uName FROM customerview WHERE idcustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getString("Name");
                    this.user = rs.getString("uName");
                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        if (ret != "") {
            this.ID = ID;
            this.name = ret;
            this.year = year;
            this.nameEdited = name;
        } else {
            throw new CustomerNotFoundException();
        }
    }

    public Customer(int ID, String name, String year) {

        this.ID = ID;
        this.name = name;
        this.year = year;
        this.nameEdited = name;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT uName FROM customerview WHERE idcustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    this.user = rs.getString("uName");
                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

    }

    public Customer() {
        this.ID = -1;
        this.name = "";
        this.year = "";
        this.address = "";
        this.town = "";
        this.state = "";
        this.zipCode = "";
        this.lat = 0.0;
        this.lon = 0.0;
        this.phone = "";
        this.paid = false;
        this.delivered = false;
        this.email = "";
        this.orderId = "";
        this.Donation = BigDecimal.ZERO;
        this.nameEdited = "";
        this.user = DbInt.getUserName();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) obj;
        return this.address.equals(other.address);
    }

    public int hashCode() {
        return address.hashCode();
    }

    public Double getLat() {
        Double ret = lat;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Lat FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getDouble("Lat");

                }
            }
            ////DbInt.pCon.close()

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
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Lon FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getDouble("Lon");

                }
            }
            ////DbInt.pCon.close()

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

    public int updateValues(failCallback fail) throws Exception {
        if (Objects.equals(DbInt.getCustInf(year, ID, "name", ""), "")) {
            //Insert Mode
            double progressIncrement = (100 - getProgress()) / 3;
            progress.set(getProgress() + progressIncrement);
            fail.doAction();
            message.set("Adding Customer");
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement writeCust = con.prepareStatement("INSERT INTO customerview (uName, Name, streetAddress, City, State, Zip, Lat, Lon, Phone, Email, Donation) VALUES (?,?,?,?,?,?,?,?,?,?,?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                writeCust.setString(1, this.user);
                writeCust.setString(2, this.nameEdited);
                writeCust.setString(3, this.address);
                writeCust.setString(4, this.town);
                writeCust.setString(5, this.state);
                writeCust.setString(6, this.zipCode);
                writeCust.setDouble(7, lat);
                writeCust.setDouble(8, lon);
                writeCust.setString(9, this.phone);
                //writeCust.setString(9, this.orderId);
                //writeCust.setString(10, this.paid);
                //writeCust.setString(11, this.delivered);
                writeCust.setString(10, this.email);
                writeCust.setString(11, this.Donation.toPlainString());
                fail.doAction();
                writeCust.execute();
            }
            progress.set(getProgress() + progressIncrement);
     /*       try (PreparedStatement prep1 = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(ADDRESS, TOWN, STATE, ZIPCODE, Lat, Lon, ORDERED, NI, NH) VALUES(?,?,?,?,?,?, 'True','False','False')")) {
                prep1.setString(1, this.address);
                prep1.setString(2, this.town);
                prep1.setString(3, this.state);
                prep1.setString(4, this.zipCode);
                prep1.setDouble(5, lat);
                prep1.setDouble(6, lon);
                fail.doAction();

                prep1.execute();
            }*/
            progress.set(getProgress() + progressIncrement);


        } else {

            progress.set(10);

            //Updates customer table in Year DB with new info.
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement CustomerUpdate = con.prepareStatement("UPDATE customerview SET Name=?, streetAddress=?, City=?, State=?, Zip=?, Lat=?, Lon=?, Phone=?,Email=?, Donation=? WHERE idCustomers = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                CustomerUpdate.setString(1, this.nameEdited);
                CustomerUpdate.setString(2, this.address);
                CustomerUpdate.setString(3, this.town);
                CustomerUpdate.setString(4, this.state);
                CustomerUpdate.setString(5, this.zipCode);
                CustomerUpdate.setDouble(6, lat);
                CustomerUpdate.setDouble(7, lon);
                CustomerUpdate.setString(8, this.phone);
                CustomerUpdate.setString(9, this.email);
                CustomerUpdate.setString(10, this.Donation.toPlainString());
                CustomerUpdate.setInt(11, ID);
                fail.doAction();

                CustomerUpdate.execute();
            }
            progress.set(20);

        }
        Integer cID = 0;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT idcustomers FROM customerview WHERE Name=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, nameEdited);
            try (ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {

                    cID = rs.getInt(1);

                }
            }
        }
        return cID;
    }

    public void deleteCustomer() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("WARNING!");
        alert.setHeaderText("BY CONTINUING YOU ARE PERMANENTLY REMOVING A CUSTOMER! ALL DATA MUST BE REENTERED!");
        alert.setContentText("Would you like to continue?");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {

            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("DELETE FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, ID);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Error deleting customer. Try again or contact support.");
            }
        }
    }

    public String[] getCustAddressFrmName() {

        String city = DbInt.getCustInf(year, ID, "City", town);
        String State = DbInt.getCustInf(year, ID, "State", state);
        String zCode = DbInt.getCustInf(year, ID, "Zip", zipCode);
        String strtAddress = DbInt.getCustInf(year, ID, "streetAddress", address);
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
        Order.orderArray order = Order.createOrderArray(year, ID, true);
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
        Order.orderArray order = Order.createOrderArray(year, ID, true);
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
        Order.orderArray order = Order.createOrderArray(year, ID, true);
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
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT idcustomers FROM customerview WHERE Name=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getInt("idcustomers");

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ID == -1 ? ret : ID;
    }

    public String getAddr() {
        return DbInt.getCustInf(year, ID, "streetAddress", address);
    }

    public String getTown() {
        return DbInt.getCustInf(year, ID, "City", town);
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getState() {
        return DbInt.getCustInf(year, ID, "State", state);
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return DbInt.getCustInf(year, ID, "Zip", zipCode);
    }

    public String getName() {
        return name;
    }

    public String getUser() {
        return user;
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
        return DbInt.getCustInf(year, ID, "Phone", phone);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns if the customer has paid.
     *
     * @return The Payment status of the specified customer
     */
    public Boolean getPaid() {
        return Order.getOrder(year, ID).paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public String getYear() {
        return year;
    }


/**
     * Return Delivery status of the customer whose name has been specified.
     *
     * @return The Delivery status of the specified customer
 */

public Boolean getDelivered() {
    return Order.getOrder(year, ID).delivered;
    }

    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }

    /**
     * Return Email Address of the customer whose name has been specified.
     *
     * @return The Email Address of the specified customer
     */
    public String getEmail() {
        return DbInt.getCustInf(year, ID, "Email", email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Return Order ID of the customer whose name has been specified.
     *
     * @return The Order ID of the specified customer
     */
    public int getOrderId() {
        int ret = 0;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT orderID FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getInt("orderID");

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        return ret;
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
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Donation FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getBigDecimal("Donation");

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        return ret;
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

    public class CustomerNotFoundException extends Exception {}
}
