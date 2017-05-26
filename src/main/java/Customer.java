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

/**
 * Created by patrick on 7/26/16.
 */
@SuppressWarnings("unused")
public class Customer {
    private final String name;
    private final String year;

    public Customer(String name, String year) {

        this.name = name;
        this.year = year;
    }

    public String[] getCustAddressFrmName() {

        String city = DbInt.getCustInf(year, name, "TOWN");
        String State = DbInt.getCustInf(year, name, "STATE");
        String zipCode = DbInt.getCustInf(year, name, "ZIPCODE");
        String strtAddress = DbInt.getCustInf(year, name, "ADDRESS");
        String[] address = new String[4];
        address[0] = city;
        address[1] = State;
        address[2] = zipCode;
        address[3] = strtAddress;
        return address;
    }

    public String getName() {
        return name;
    }

    public String getAddr() {
        return DbInt.getCustInf(year, name, "ADDRESS");
    }

    /**
     * Return Phone number of the customer whose name has been specified.
     *
     * @return The Phone number of the specified customer
     */
    public String getPhone() {
        return DbInt.getCustInf(year, name, "PHONE");
    }

    /**
     * Returns if the customer has paid.
     *
     * @return The Payment status of the specified customer
     */
    public String getPaid() {
        return DbInt.getCustInf(year, name, "PAID");
    }

    /**
     * Return Delivery status of the customer whose name has been specified.
     *
     * @return The Delivery status of the specified customer
     */
    public String getDelivered() {
        return DbInt.getCustInf(year, name, "DELIVERED");
    }

    /**
     * Return Email Address of the customer whose name has been specified.
     *
     * @return The Email Address of the specified customer
     */
    public String getEmail() {
        return DbInt.getCustInf(year, name, "Email");
    }

    /**
     * Return Order ID of the customer whose name has been specified.
     *
     * @return The Order ID of the specified customer
     */
    public String getOrderId() {
        return DbInt.getCustInf(year, name, "ORDERID");
    }

    /**
     * Return Donation amount of the customer whose name has been specified.
     *
     * @return The Donation Amount of the specified customer
     */
    public BigDecimal getDontation() {
        BigDecimal ret = BigDecimal.ZERO;
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM CUSTOMERS WHERE NAME=?")) {


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
}
