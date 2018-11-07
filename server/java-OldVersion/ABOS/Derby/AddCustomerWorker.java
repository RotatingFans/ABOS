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

package ABOS.Derby;

import javafx.concurrent.Task;
import javafx.scene.control.TableView;

import java.math.BigDecimal;

//import javax.swing.*;

/**
 * @author Patrick Magauran
 */
class AddCustomerWorker extends Task<Integer> {

    private final String Address;
    private final String Town;
    private final String State;
    private final String year;
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
     * old/new is relavant for editing. Db uses name to find customer
     *
     * @param name             (old) name of the customer
     * @param zipCode          ZIpcode
     * @param phone            Phone #
     * @param email            Email address
     * @param donationsT       Total Donations
     * @param nameEditCustomer (new) name of customer
     * @param paid             Did they pay
     * @param delivered        Was it deleivered
     */
    public AddCustomerWorker(String Address, String Town, String State, String year, TableView ProductTable, String name, String zipCode, String phone, String email, String donationsT, String nameEditCustomer, Boolean paid, Boolean delivered) {
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
                if (n < 10) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                // 3 or 4 or 5
                if (n < 1000) {
                    return 3;
                } else {
                    // 4 or 5
                    if (n < 10000) {
                        return 4;
                    } else {
                        return 5;
                    }
                }
            }
        } else {
            // 6 or more
            if (n < 10000000) {
                // 6 or 7
                if (n < 1000000) {
                    return 6;
                } else {
                    return 7;
                }
            } else {
                // 8 to 10
                if (n < 100000000) {
                    return 8;
                } else {
                    // 9 or 10
                    if (n < 1000000000) {
                        return 9;
                    } else {
                        return 10;
                    }
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
        String address = String.format("%s %s, %s", Address, Town, State);//Formats address
        updateMessage("Analyzing Address");
        Object[][] coords;
        coords = Geolocation.GetCoords(address);


        int numRows = ProductTable.getItems().size();

        double lat = Double.valueOf(coords[0][0].toString());
        double lon = Double.valueOf(coords[0][1].toString());
        AddCustomerWorker.failIfInterrupted();
        updateProgress(0, 100);
        String Id = Order.updateOrder(ProductTable.getItems(), Name, year, NameEditCustomer, (p, m) -> updateProgress(p, m), () -> AddCustomerWorker.failIfInterrupted(), (m) -> updateMessage(m), () -> getProgress());
        Customer customer = new Customer(NameEditCustomer, year, Address, Town, State, ZipCode, lat, lon, Phone, Boolean.toString(Paid), Boolean.toString(Delivered), Email,
                Id, Name, new BigDecimal(DonationsT));
        customer.updateValues((p, m) -> updateProgress(p, m), () -> AddCustomerWorker.failIfInterrupted(), (m) -> updateMessage(m), () -> getProgress());
        updateProgress(100, 100);


        updateMessage("Done");


        // Return the number of matches found
        return 1;
    }
}