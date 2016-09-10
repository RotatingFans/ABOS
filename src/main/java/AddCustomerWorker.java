import javax.swing.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Magauran
 */
public class AddCustomerWorker extends SwingWorker<Integer, String> {

    private final String Address;
    private final String Town;
    private final String State;
    private final String year;
    private final Boolean edit;
    private final JTable ProductTable;
    private final String Name;
    private final String ZipCode;
    private final String Phone;
    private final String Email;
    private final String DonationsT;
    private final String NameEditCustomer;
    private final Boolean Paid;
    private final Boolean Delivered;
    private final JLabel StatusLbl;
    private Geolocation Geo = new Geolocation();

    /**
     * Creates an instance of the worker
     * @param address
     * @param name
     * @param zipCode
     * @param phone
     * @param email
     * @param donationsT
     * @param nameEditCustomer
     * @param paid
     * @param delivered
     * @param statusLbl
     */
    public AddCustomerWorker(String Address, String Town, String State, String year, Boolean edit, JTable ProductTable, String name, String zipCode, String phone, String email, String donationsT, String nameEditCustomer, Boolean paid, Boolean delivered, JLabel statusLbl) {
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
        StatusLbl = statusLbl;
    }

    private static void failIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Interrupted while Adding Order");
        }
    }

    @Override
    protected Integer doInBackground() throws Exception {
    /*    Insert Order
          Get ID via Name
          insert Customer INfo
         */
        try {
            String address = String.format("%s %s, %s", Address, Town, State);//Formats address
            publish("Analyzing Address");
            Object[][] coords = Geolocation.GetCoords(address);
            double lat = Double.valueOf(coords[0][0].toString());
            double lon = Double.valueOf(coords[0][1].toString());
            AddCustomerWorker.failIfInterrupted();
            setProgress(0);

            if (!edit) {
                // Inserts order data into order tables
                {
                    publish("Building Order");

                    StringBuilder InsertOrderStringBuilder = new StringBuilder("INSERT INTO ORDERS(NAME VALUES(?");

                    int progressDivisor = 2 * ProductTable.getRowCount();
                    int progressIncrement = (progressDivisor - 15) / progressDivisor;
                    //Loops through And adds product numbers to Order string
                    int insertProductNumberHere = InsertOrderStringBuilder.length() - 9;
                    for (int i = 0; i < ProductTable.getRowCount(); i++) {
                        setProgress(getProgress() + progressIncrement);
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

                        for (int i = 0; i < ProductTable.getRowCount(); i++) {
                            setProgress(getProgress() + progressIncrement);
                            writeOrd.setString(i + 2, ProductTable.getModel().getValueAt(i, 4).toString());
                        }
                        AddCustomerWorker.failIfInterrupted();
                        publish("Adding Order");

                        writeOrd.executeUpdate();
                    }
                }

                //Inserts into customers tables with specified information.
                {
                    int progressIncrement = (100 - getProgress()) / 3;
                    //Gets order ID of customer
                    List<String> Ids = new ArrayList<String>();

                    try (PreparedStatement prep = DbInt.getPrep(year, "SELECT ORDERID FROM ORDERS WHERE NAME=?")) {

                        prep.setString(1, Name);
                        try (ResultSet rs = prep.executeQuery()) {
                            while (rs.next()) {

                                Ids.add(rs.getString(1));

                            }
                        }
                    }
                    setProgress(getProgress() + progressIncrement);
                    AddCustomerWorker.failIfInterrupted();
                    publish("Adding Customer");

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
                    setProgress(getProgress() + progressIncrement);
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
                    setProgress(getProgress() + progressIncrement);
                    setProgress(100);

                }
                //////DbInt.pCon.close();

            }
            if (edit) {
                //Updates Customer table in set DB with new info
                Customer customerInfo = new Customer(Name, year);
                publish("Updating Customer Data");
                int progressDivisor = (2 * ProductTable.getRowCount());
                int progressIncrement = (progressDivisor - 10) / progressDivisor;

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
                setProgress(10);
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
                setProgress(20);
                //////DbInt.pCon.close();
                publish("Building Order Update");

                StringBuilder UpdateOrderString = new StringBuilder("UPDATE ORDERS SET NAME=?");
                //loops through table and adds product number to order string with "=?"
                for (int i = 0; i < ProductTable.getRowCount(); i++) {
                    UpdateOrderString.append('"');
                    UpdateOrderString.append(i);
                    UpdateOrderString.append("\"=?");
                    setProgress(getProgress() + progressIncrement);
                }
                AddCustomerWorker.failIfInterrupted();

                //Uses string to create PreparedStatement that is filled with quantities from table.
                UpdateOrderString.append(" WHERE NAME = ?");
                try (PreparedStatement updateOrders = DbInt.getPrep(year, UpdateOrderString.toString())) {
                    updateOrders.setString(1, Name);
                    for (int i = 0; i < ProductTable.getRowCount(); i++) {
                        updateOrders.setString(i + 2, ProductTable.getModel().getValueAt(i, 4).toString());
                        setProgress(getProgress() + progressIncrement);
                    }
                    AddCustomerWorker.failIfInterrupted();

                    updateOrders.setString(ProductTable.getRowCount() + 2, NameEditCustomer);
                    publish("Running Update");

                    updateOrders.execute();
                }
                setProgress(100);

            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        publish("Done");


        // Return the number of matches found
        return 1;
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

    @Override
    protected void process(List<String> chunks) {
        // Updates the messages text area
        for (String string : chunks) {
            StatusLbl.setText(string);
        }
    }
}