/*
 * Copyright (c) Patrick Magauran 2017.
 *   Licensed under the AGPLv3. All conditions of said license apply.
 *       This file is part of ABOS.
 *
 *       ABOS is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Affero General Public License as updateMessageed by
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

import javafx.concurrent.Task;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//import javax.swing.*;

/**
 * @author Patrick Magauran
 */
class AddCustomerWorker extends Task<Integer> {

    private final String Address;
    private final String Town;
    private final String State;
    private final String year;
    private final Boolean edit;
    private final TableView<Product.formattedProductProps> ProductTable;
    private final String Name;
    private final String ZipCode;
    private final String Phone;
    private final String Email;
    private final String DonationsT;
    private final String NameEditCustomer;
    private final Boolean Paid;
    private final Boolean Delivered;

    /**
     * Creates an instance of the worker
     * @param name
     * @param zipCode
     * @param phone
     * @param email
     * @param donationsT
     * @param nameEditCustomer
     * @param paid
     * @param delivered
     */
    public AddCustomerWorker(String Address, String Town, String State, String year, Boolean edit, TableView ProductTable, String name, String zipCode, String phone, String email, String donationsT, String nameEditCustomer, Boolean paid, Boolean delivered) {
        this.Address = Address;
        this.Town = Town;
        this.State = State;
        this.year = year;
        this.edit = edit;
        this.ProductTable = ProductTable;
        Name = name;
        ZipCode = zipCode;
        Phone = phone;
        Email = email;
        DonationsT = donationsT;
        NameEditCustomer = nameEditCustomer;
        Paid = paid;
        Delivered = delivered;
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
    protected Integer call() throws Exception {
        try {
            String address = String.format("%s %s, %s", Address, Town, State);//Formats address
            updateMessage("Analyzing Address");
            Object[][] coords = new Object[0][];
            try {
                coords = Geolocation.GetCoords(address);

            } catch (addressException e) {
                LogToFile.log(null, Severity.WARNING, "Invalid Address. Please Verify spelling and numbers are correct.");

            }
            int numRows = ProductTable.getItems().size();

            double lat = Double.valueOf(coords[0][0].toString());
            double lon = Double.valueOf(coords[0][1].toString());
            AddCustomerWorker.failIfInterrupted();
            updateProgress(0, 100);

            if (!edit) {
                // Inserts order data into order tables
                {
                    updateMessage("Building Order");

                    StringBuilder InsertOrderStringBuilder = new StringBuilder("INSERT INTO ORDERS(NAME VALUES(?");

                    int progressDivisor = 2 * numRows;
                    int progressIncrement = 50 / progressDivisor;
                    //Loops through And adds product numbers to Order string
                    int insertProductNumberHere = InsertOrderStringBuilder.length() - 9;

                    for (int i = 0; i < numRows; i++) {
                        updateProgress(getProgress() + progressIncrement, 100);
                        InsertOrderStringBuilder.insert(insertProductNumberHere, ",\"");
                        InsertOrderStringBuilder.insert(insertProductNumberHere + 2, i);
                        InsertOrderStringBuilder.insert(insertProductNumberHere + 2 + IntegerLength(i), '"');
                        insertProductNumberHere += 3 + IntegerLength(i);
                        InsertOrderStringBuilder.append(",?");
                    }
                    InsertOrderStringBuilder.insert(insertProductNumberHere, ") ");
                    InsertOrderStringBuilder.append(')');

                    AddCustomerWorker.failIfInterrupted();

                    //Creates prepared Statement and replaces ? with quantities and names
                    try (PreparedStatement writeOrd = DbInt.getPrep(year, InsertOrderStringBuilder.toString())) {
                        writeOrd.setString(1, Name);
                        for (int i = 0; i < numRows; i++) {
                            writeOrd.setString(i + 2, String.valueOf(ProductTable.getItems().get(i).getOrderedQuantity()));
                            updateProgress(getProgress() + progressIncrement, 100);
                        }
                        AddCustomerWorker.failIfInterrupted();
                        updateMessage("Adding Order");

                        writeOrd.executeUpdate();
                    }
                }

                //Inserts into customers tables with specified information.
                {
                    double progressIncrement = (100 - getProgress()) / 3;
                    //Gets order ID of customer
                    List<String> Ids = new ArrayList<>();

                    try (PreparedStatement prep = DbInt.getPrep(year, "SELECT ORDERID FROM ORDERS WHERE NAME=?")) {

                        prep.setString(1, Name);
                        try (ResultSet rs = prep.executeQuery()) {
                            while (rs.next()) {

                                Ids.add(rs.getString(1));

                            }
                        }
                    }
                    updateProgress(getProgress() + progressIncrement, 100);
                    AddCustomerWorker.failIfInterrupted();
                    updateMessage("Adding Customer");

                    //Inserts into customer table for year
                    String Id = Ids.get(Ids.size() - 1);
                    try (PreparedStatement writeCust = DbInt.getPrep(year, "INSERT INTO CUSTOMERS(NAME,ADDRESS, TOWN, STATE, ZIPCODE, Lat, Lon, PHONE, ORDERID , PAID,DELIVERED, EMAIL, DONATION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                        writeCust.setString(1, Name);
                        writeCust.setString(2, Address);
                        writeCust.setString(3, Town);
                        writeCust.setString(4, State);
                        writeCust.setString(5, ZipCode);
                        writeCust.setDouble(6, lat);
                        writeCust.setDouble(7, lon);
                        writeCust.setString(8, Phone);
                        writeCust.setString(9, Id);
                        writeCust.setString(10, Boolean.toString(Paid));
                        writeCust.setString(11, Boolean.toString(Delivered));
                        writeCust.setString(12, Email);
                        writeCust.setString(13, DonationsT);
                        AddCustomerWorker.failIfInterrupted();

                        writeCust.execute();
                    }
                    updateProgress(getProgress() + progressIncrement, 100);
                    //Inserts into customer table for all years.
                    try (PreparedStatement prep1 = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(ADDRESS, TOWN, STATE, ZIPCODE, Lat, Lon, ORDERED, NI, NH) VALUES(?,?,?,?,?,?, 'True','False','False')")) {
                        prep1.setString(1, Address);
                        prep1.setString(2, Town);
                        prep1.setString(3, State);
                        prep1.setString(4, ZipCode);
                        prep1.setDouble(5, lat);
                        prep1.setDouble(6, lon);
                        AddCustomerWorker.failIfInterrupted();

                        prep1.execute();
                    }
                    updateProgress(getProgress() + progressIncrement, 100);
                    updateProgress(100, 100);

                }
                //////DbInt.pCon.close();

            }
            if (edit) {
                //Updates Customer table in set DB with new info
                Customer customerInfo = new Customer(Name, year);
                updateMessage("Updating Customer Data");
                int progressDivisor = (2 * numRows);
                int progressIncrement = 50 / progressDivisor;

                try (PreparedStatement updateCust = DbInt.getPrep("Set", "UPDATE Customers SET ADDRESS=?, Town=?, STATE=?, ZIPCODE=?, Lat=?, Lon=?, ORDERED='True', NI='False', NH='False' WHERE ADDRESS=?")) {

                    updateCust.setString(1, Address);
                    updateCust.setString(2, Town);
                    updateCust.setString(3, State);
                    updateCust.setString(4, ZipCode);
                    updateCust.setDouble(5, lat);
                    updateCust.setDouble(6, lon);
                    updateCust.setString(7, customerInfo.getAddr());
                    AddCustomerWorker.failIfInterrupted();

                    updateCust.execute();

                }
                updateProgress(10, 100);
                //Updates customer table in Year DB with new info.
                try (PreparedStatement CustomerUpdate = DbInt.getPrep(year, "UPDATE CUSTOMERS SET NAME=?, ADDRESS=?, TOWN=?, STATE=?, ZIPCODE=?, Lat=?, Lon=?, PHONE=?,PAID=?,DELIVERED=?, EMAIL=?, DONATION=? WHERE NAME = ?")) {
                    CustomerUpdate.setString(1, Name);
                    CustomerUpdate.setString(2, Address);
                    CustomerUpdate.setString(3, Town);
                    CustomerUpdate.setString(4, State);
                    CustomerUpdate.setString(5, ZipCode);
                    CustomerUpdate.setDouble(6, lat);
                    CustomerUpdate.setDouble(7, lon);
                    CustomerUpdate.setString(8, Phone);
                    CustomerUpdate.setString(9, Boolean.toString(Paid));
                    CustomerUpdate.setString(10, Boolean.toString(Delivered));
                    CustomerUpdate.setString(11, Email);
                    CustomerUpdate.setString(12, DonationsT);
                    CustomerUpdate.setString(13, NameEditCustomer);
                    AddCustomerWorker.failIfInterrupted();

                    CustomerUpdate.execute();
                }
                updateProgress(20, 100);
                //////DbInt.pCon.close();
                updateMessage("Building Order Update");

                StringBuilder UpdateOrderString = new StringBuilder("UPDATE ORDERS SET NAME=?");
                //loops through table and adds product number to order string with "=?"
                for (int i = 0; i < numRows; i++) {
                    UpdateOrderString.append(", \"");
                    UpdateOrderString.append(i);
                    UpdateOrderString.append("\"=?");
                    updateProgress(getProgress() + progressIncrement, 100);
                }
                AddCustomerWorker.failIfInterrupted();

                //Uses string to create PreparedStatement that is filled with quantities from table.
                UpdateOrderString.append(" WHERE NAME = ?");
                try (PreparedStatement updateOrders = DbInt.getPrep(year, UpdateOrderString.toString())) {
                    updateOrders.setString(1, Name);
                    for (int i = 0; i < numRows; i++) {
                        updateOrders.setString(i + 2, String.valueOf(ProductTable.getItems().get(i).getOrderedQuantity()));
                        updateProgress(getProgress() + progressIncrement, 100);
                    }
                    AddCustomerWorker.failIfInterrupted();

                    updateOrders.setString(numRows + 2, NameEditCustomer);
                    updateMessage("Running Update");

                    updateOrders.execute();
                }
                updateProgress(100, 100);

            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        } catch (InterruptedException e) {
            if (isCancelled()) {
                updateMessage("Cancelled");
            }
            LogToFile.log(e, Severity.FINE, "Add Customer process canceled.");
        } catch (IOException e) {
            LogToFile.log(e, Severity.WARNING, "Error contacting geolaction service. Please try again or contasct support.");
        }
        updateMessage("Done");


        // Return the number of matches found
        return 1;   
    }
}