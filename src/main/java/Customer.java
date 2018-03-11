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
    private Settable<String> name = new Settable<>("");
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress");
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper(this, "message");
    private Settable<Integer> ID = new Settable(-1);
    private Settable<Integer> orderID = new Settable(-1);
    private Settable<String> nameEdited = new Settable("");
    private Settable<String> year = new Settable("");
    private Settable<String> address = new Settable("");
    private Settable<String> town = new Settable("");
    private Settable<String> state = new Settable("");
    private Settable<String> zipCode = new Settable("");
    private Settable<Double> lat = new Settable<>();
    private Settable<Double> lon = new Settable<>();
    private Settable<String> phone = new Settable("");
    private Settable<Boolean> paid = new Settable();
    private Settable<Boolean> delivered = new Settable();
    private Settable<String> email = new Settable();
    private Settable<String> user = new Settable("");
    private Settable<BigDecimal> Donation = new Settable();
    private Settable<Order.orderArray> orders = new Settable();

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
        this.ID.set(ID);
        this.name.set(name);
        this.year.set(year);
        this.address.set(address);
        this.town.set(town);
        this.state.set(state);
        this.zipCode.set(zipCode);
        this.lat.set(lat);
        this.lon.set(lon);
        this.phone.set(phone);
        this.paid.set(paid);
        this.delivered.set(delivered);
        this.email.set(email);
        this.Donation.set(Donation);
        this.nameEdited.set(nameEdited);
        this.user.set(user);
    }

    public Customer(String name, String year) {
        this(-1, name, year, null, null, null, null, null, null, null, false, false, null, null, null, DbInt.getUserName());
    }

    public Customer(int ID, String year) throws CustomerNotFoundException {
        String ret = "";
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT Name, uName FROM customerview WHERE idcustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getString("Name");
                    this.user.set(rs.getString("uName"));
                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        if (!Objects.equals(ret, "")) {
            this.ID.set(ID);
            this.name.set(ret);
            this.year.set(year);
            this.nameEdited = name;
        } else {
            throw new CustomerNotFoundException();
        }
    }

    public Customer(int ID, String name, String year) {

        this.ID.set(ID);
        this.name.set(name);
        this.year.set(year);
        this.nameEdited.set(name);
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT uName FROM customerview WHERE idcustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, ID);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    this.user.set(rs.getString("uName"));
                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

    }

    public Customer() {
        this(null, null, null, null, null, null, null, null, null, null, false, false, null, null, null, DbInt.getUserName());
    }



    public int updateValues(failCallback fail) throws Exception {
        if (Objects.equals(DbInt.getCustInf(year.get(), ID.get(), "name", ""), "")) {
            //Insert Mode
            double progressIncrement = (100 - getProgress()) / 3;
            progress.set(getProgress() + progressIncrement);
            fail.doAction();
            message.set("Adding Customer");
            try (Connection con = DbInt.getConnection(year.get());
                 PreparedStatement writeCust = con.prepareStatement("INSERT INTO customerview (uName, Name, streetAddress, City, State, Zip, Lat, Lon, Phone, Email, Donation) VALUES (?,?,?,?,?,?,?,?,?,?,?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                writeCust.setString(1, this.user.get());
                writeCust.setString(2, this.nameEdited.get());
                writeCust.setString(3, this.address.get());
                writeCust.setString(4, this.town.get());
                writeCust.setString(5, this.state.get());
                writeCust.setString(6, this.zipCode.get());
                writeCust.setDouble(7, lat.get());
                writeCust.setDouble(8, lon.get());
                writeCust.setString(9, this.phone.get());
                //writeCust.setString(9, this.orderId);
                //writeCust.setString(10, this.paid);
                //writeCust.setString(11, this.delivered);
                writeCust.setString(10, this.email.get());
                writeCust.setString(11, this.Donation.orElseGetAndSet(() -> {return BigDecimal.ZERO;}).toPlainString());
                fail.doAction();
                writeCust.execute();
            }
            progress.set(getProgress() + progressIncrement);
     /*       try (PreparedStatement prep1 = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(ADDRESS, TOWN, STATE, ZIPCODE, Lat, Lon, ORDERED, NI, NH) VALUES(?,?,?,?,?,?, 'True','False','False')")) {
                prep1.setString(1, this.address.get());
                prep1.setString(2, this.town.get());
                prep1.setString(3, this.state.get());
                prep1.setString(4, this.zipCode.get());
                prep1.setDouble(5, lat);
                prep1.setDouble(6, lon);
                fail.doAction();

                prep1.execute();
            }*/
            progress.set(getProgress() + progressIncrement);


        } else {

            progress.set(10);

            //Updates customer table in Year DB with new info.
            try (Connection con = DbInt.getConnection(year.get());
                 PreparedStatement CustomerUpdate = con.prepareStatement("UPDATE customerview SET Name=?, streetAddress=?, City=?, State=?, Zip=?, Lat=?, Lon=?, Phone=?,Email=?, Donation=? WHERE idCustomers = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                CustomerUpdate.setString(1, this.nameEdited.get());
                CustomerUpdate.setString(2, this.address.get());
                CustomerUpdate.setString(3, this.town.get());
                CustomerUpdate.setString(4, this.state.get());
                CustomerUpdate.setString(5, this.zipCode.get());
                CustomerUpdate.setDouble(6, lat.get());
                CustomerUpdate.setDouble(7, lon.get());
                CustomerUpdate.setString(8, this.phone.get());
                CustomerUpdate.setString(9, this.email.get());
                CustomerUpdate.setString(10, this.Donation.orElseGetAndSet(() -> {
                    return BigDecimal.ZERO;
                }).toPlainString());
                CustomerUpdate.setInt(11, ID.get());
                fail.doAction();

                CustomerUpdate.execute();
            }
            progress.set(20);

        }
        Integer cID = 0;
        try (Connection con = DbInt.getConnection(year.get());
             PreparedStatement prep = con.prepareStatement("SELECT idcustomers FROM customerview WHERE Name=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, nameEdited.get());
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

            try (Connection con = DbInt.getConnection(year.get());
                 PreparedStatement prep = con.prepareStatement("DELETE FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, ID.get());
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Error deleting customer. Try again or contact support.");
            }
        }
    }

    public String[] getCustAddressFrmName() {

        String city = getTown();
        String State = getState();
        String zCode = getZip();
        String strtAddress = getAddr();
        String[] address = new String[4];
        address[0] = city;
        address[1] = State;
        address[2] = zCode;
        address[3] = strtAddress;
        return address;
    }

    public Order.orderArray getOrderArray() {
        return this.orders.orElseGetAndSet(() -> Order.createOrderArray(year.get(), ID.get(), true));
    }
    /**
     * Loops through Table to get total amount of Bulk Mulch ordered.
     *
     * @return The amount of Bulk mulch ordered
     */
    public int getNoMulchOrdered() {
        Order.orderArray order = getOrderArray();
        int quantMulchOrdered = 0;
        for (formattedProduct productOrder : order.orderData) {
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
        Order.orderArray order = getOrderArray();
        int livePlantsOrdered = 0;
        for (formattedProduct productOrder : order.orderData) {
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
        Order.orderArray order = getOrderArray();
        int lawnProductsOrdered = 0;
        for (formattedProduct productOrder : order.orderData) {
            if (productOrder.productName.contains("-L")) {
                lawnProductsOrdered += productOrder.orderedQuantity;
            }
        }
        return lawnProductsOrdered;
    }

    public Double getLat() {
        return this.lat.orElseGetAndSet(() -> {
            Double ret = lat.get();
            try (Connection con = DbInt.getConnection(year.get());
                 PreparedStatement prep = con.prepareStatement("SELECT Lat FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, ID.get());
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
        });
    }

    public void setLat(Double lat) {
        this.lat.set(lat);
    }

    public Double getLon() {
        return this.lon.orElseGetAndSet(() -> {
            Double ret = lon.get();
            try (Connection con = DbInt.getConnection(year.get());
                 PreparedStatement prep = con.prepareStatement("SELECT Lon FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, ID.get());
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
        });
    }

    public void setLon(Double lon) {
        this.lon.set(lon);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setZipCode(String zipCode) {
        this.zipCode.set(zipCode);
    }

    public void setDonation(BigDecimal donation) {
        Donation.set(donation);
    }

    public Integer getId() {
        return this.ID.orElseGetAndSet(() -> {
            Integer ret = 0;
            try (Connection con = DbInt.getConnection(year.get());
                 PreparedStatement prep = con.prepareStatement("SELECT idcustomers FROM customerview WHERE Name=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setString(1, name.get());
                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {

                        ret = rs.getInt("idcustomers");

                    }
                }
                ////DbInt.pCon.close()

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            return ret;
        });

    }

    public String getAddr() {
        return this.address.orElseGetAndSet(() -> {
            return DbInt.getCustInf(year.get(), ID.get(), "streetAddress", address.get());
        });
    }

    public String getFormattedAddress() {
        return getAddr() + " " + getTown() + ", " + getState() + " " + getZip();
    }

    public String getTown() {
        return this.town.orElseGetAndSet(() -> { return DbInt.getCustInf(year.get(), ID.get(), "City", town.get());});
    }

    public void setTown(String town) {
        this.town.set(town);
    }

    public String getState() {
        return this.state.orElseGetAndSet(() -> {
            return DbInt.getCustInf(year.get(), ID.get(), "State", state.get());
        });
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public String getZip() {
        return this.zipCode.orElseGetAndSet(() -> {
            return DbInt.getCustInf(year.get(), ID.get(), "Zip", zipCode.get());
        });
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.nameEdited.set(name);
    }

    public String getUser() {
        return user.get();
    }

    /**
     * Return Phone number of the customer whose name has been specified.
     *
     * @return The Phone number of the specified customer
     */
    public String getPhone() {
        return this.phone.orElseGetAndSet(() -> {
            return DbInt.getCustInf(year.get(), ID.get(), "Phone", phone.get());
        });
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    /**
     * Returns if the customer has paid.
     *
     * @return The Payment status of the specified customer
     */
    public Boolean getPaid() {
        return this.paid.orElseGetAndSet(() -> { return Order.getOrder(year.get(), ID.get()).paid;});
    }

    public void setPaid(Boolean paid) {
        this.paid.set(paid);
    }

    public String getYear() {
        return year.get();
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    /**
     * Return Delivery status of the customer whose name has been specified.
     *
     * @return The Delivery status of the specified customer
     */

    public Boolean getDelivered() {
        return this.delivered.orElseGetAndSet(() -> { return Order.getOrder(year.get(), ID.get()).delivered;});
    }

    public void setDelivered(Boolean delivered) {
        this.delivered.set(delivered);
    }

    /**
     * Return Email Address of the customer whose name has been specified.
     *
     * @return The Email Address of the specified customer
     */
    public String getEmail() {
        return this.email.orElseGetAndSet(() -> {
            return DbInt.getCustInf(year.get(), ID.get(), "Email", email.get());
        });
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    /**
     * Return Order ID of the customer whose name has been specified.
     *
     * @return The Order ID of the specified customer
     */
    public int getOrderId() {
        return this.orderID.orElseGetAndSet(() -> {
            int ret = 0;
            try (Connection con = DbInt.getConnection(year.get());
                 PreparedStatement prep = con.prepareStatement("SELECT orderID FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, ID.get());
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
        });
    }



    /**
     * Return Donation amount of the customer whose name has been specified.
     *
     * @return The Donation Amount of the specified customer
     */
    public BigDecimal getDontation() {
        return this.Donation.orElseGetAndSet(() -> {
            BigDecimal ret = BigDecimal.ZERO;
            try (Connection con = DbInt.getConnection(year.get());
                 PreparedStatement prep = con.prepareStatement("SELECT Donation FROM customerview WHERE idCustomers=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, ID.get());
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
        });
    }

    public void refreshData() {
        orderID.clear();
        nameEdited.clear();
        year.clear();
        address.clear();
        town.clear();
        state.clear();
        zipCode.clear();
        lat.clear();
        lon.clear();
        phone.clear();
        paid.clear();
        delivered.clear();
        email.clear();
        user.clear();
        Donation.clear();
        orders.clear();
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

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) obj;
        return this.address.get().equals(other.address.get());
    }

    public int hashCode() {
        return address.hashCode();
    }


    interface failCallback {
        void doAction() throws InterruptedException;
    }


}
