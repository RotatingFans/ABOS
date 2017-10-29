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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by patrick on 7/26/16.
 */
@SuppressWarnings("unused")
public class Customer {
    private final String name;
    private String nameEdited = "";
    private String year = "";
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
            ////DbInt.pCon.close();

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
            ////DbInt.pCon.close();

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
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        return ret;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public void setName(String name) {
        this.nameEdited = name;
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
            try (PreparedStatement updateCust = DbInt.getPrep("Set", "UPDATE Customers SET ADDRESS=?, Town=?, STATE=?, ZIPCODE=?, Lat=?, Lon=?, ORDERED='True', NI='False', NH='False' WHERE ADDRESS=?")) {

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

            //Updates customer table in Year DB with new info.
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

    public Integer getId() {
        Integer ret = 0;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT ID FROM CUSTOMERS WHERE NAME=?")) {


            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getInt("ID");

                }
            }
            ////DbInt.pCon.close();

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
     * Return Order ID of the customer whose name has been specified.
     *
     * @return The Order ID of the specified customer
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
            ////DbInt.pCon.close();

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
