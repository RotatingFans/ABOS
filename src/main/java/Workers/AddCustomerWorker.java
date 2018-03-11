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

package Workers;

import Utilities.Customer;
import Utilities.Geolocation;
import Utilities.Order;
import Utilities.formattedProductProps;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;

import java.math.BigDecimal;

//import javax.swing.*;

/**
 * @author Patrick Magauran
 */
public class AddCustomerWorker extends Task<Customer> {
    private final Integer ID;

    private final String Address;
    private final String Town;
    private final String State;
    private final String year;
    private final TableView<formattedProductProps> ProductTable;
    private final String Name;
    private final String ZipCode;
    private final String Phone;
    private final String Email;
    private final String DonationsT;
    private final String NameEditCustomer;
    private final String uName;
    private final Boolean Paid;
    private final Boolean Delivered;

    /**
     * Creates an instance of the worker
     * old/new is relavant for editing. Db uses name to find customer
     *
     * @param id               The ID of the Utilities.Customer
     * @param name             (old) name of the customer
     * @param zipCode          ZIpcode
     * @param phone            Phone #
     * @param email            Email address
     * @param donationsT       Total Donations
     * @param nameEditCustomer (new) name of customer
     * @param paid             Did they pay
     * @param delivered        Was it deleivered
     */
    public AddCustomerWorker(Integer id, String Address, String Town, String State, String year, TableView ProductTable, String name, String zipCode, String phone, String email, String donationsT, String nameEditCustomer, Boolean paid, Boolean delivered, String uName) {
        ID = id;
        this.Address = Address;
        this.Town = Town;
        this.State = State;
        this.year = year;
        this.ProductTable = ProductTable;
        Name = name;
        ZipCode = zipCode;
        Phone = phone;
        Email = email;
        DonationsT = donationsT;
        NameEditCustomer = nameEditCustomer;
        Paid = paid;
        Delivered = delivered;
        this.uName = uName;
    }

    private static void failIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Interrupted while Adding Order");
        }
    }

    private int IntegerLength(int n) {
        if (n < 100000) {
            // 5 or less
            if (n < 100) {
                // 1 or 2
                if (n < 10) { return 1; } else { return 2; }
            } else {
                // 3 or 4 or 5
                if (n < 1000) { return 3; } else {
                    // 4 or 5
                    if (n < 10000) { return 4; } else { return 5; }
                }
            }
        } else {
            // 6 or more
            if (n < 10000000) {
                // 6 or 7
                if (n < 1000000) { return 6; } else { return 7; }
            } else {
                // 8 to 10
                if (n < 100000000) { return 8; } else {
                    // 9 or 10
                    if (n < 1000000000) { return 9; } else { return 10; }
                }
            }
        }

    }

/*    @Override
    protected void process(List<String> chunks) {
        // Updates the messages text area
        chunks.forEach(StatusLbl::setText);
    }*/

    @Override
    protected Customer call() throws Exception {
        String address = String.format("%s %s, %s", Address, Town, State);//Formats address
        updateMessage("Analyzing Address");
        Object[][] coords;
        coords = Geolocation.GetCoords(address);


        int numRows = ProductTable.getItems().size();

        double lat = Double.valueOf(coords[0][0].toString());
        double lon = Double.valueOf(coords[0][1].toString());
        AddCustomerWorker.failIfInterrupted();
        updateProgress(0, 100);
        Customer customer = new Customer(ID, NameEditCustomer, year, Address, Town, State, ZipCode, lat, lon, Phone, Paid, Delivered, Email,
                Name, new BigDecimal(DonationsT), uName);
        customer.progressProperty().addListener(((observableValue, oldProgress, newProgress) -> {
            updateProgress(newProgress.doubleValue(), 100);
        }));
        customer.messageProperty().addListener(((observableValue, oldMessage, newMessage) -> {
            updateMessage(newMessage);
        }));
        Integer custID = customer.updateValues(() -> AddCustomerWorker.failIfInterrupted());
        Order order = new Order(ProductTable.getItems(), year, custID, Paid, Delivered, uName);
        order.progressProperty().addListener(((observableValue, oldProgress, newProgress) -> {
            updateProgress(newProgress.doubleValue(), 100);
        }));
        order.messageProperty().addListener(((observableValue, oldMessage, newMessage) -> {
            updateMessage(newMessage);
        }));
        String Id = order.updateOrder(() -> AddCustomerWorker.failIfInterrupted());

        updateProgress(100, 100);


        updateMessage("Done");



        // Return the number of matches found
        return customer;
    }
}