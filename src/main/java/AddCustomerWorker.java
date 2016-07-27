import javax.swing.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches the text files under the given directory and counts the number of instances a given word is found
 * in these file.
 *
 * @author Albert Attard
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
     * zip     * @param address
     *
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
            throw new InterruptedException("Interrupted while searching files");
        }
    }

    @Override
    protected Integer doInBackground() throws Exception {
    /*
          Insert Order
          Get ID via Name
          insert Customer INfo
         */
        try {
            String address = String.format("%s %s, %s", Address, Town, State);//Formats address
            publish("Analyzing Address");
            Object[][] coords = Geo.GetCoords(address);
            double lat = Double.valueOf(coords[0][0].toString());
            double lon = Double.valueOf(coords[0][1].toString());
            AddCustomerWorker.failIfInterrupted();
            setProgress(0);
            int progress = 0;

            if (!edit) {
                // Inserts order data into order tables

                {
                    publish("Building Order");

                    String InsertOrderString = "INSERT INTO ORDERS(NAME";

                    int cols = DbInt.getNoCol(year, "ORDERS");
                    int progressDivisor = (cols - 3) + (2 * ProductTable.getRowCount());
                    int progressIncrement = (progressDivisor - 15) / progressDivisor;
                    //Loops through And adds product numbers to Order string
                    for (int i = 0; i <= (cols - 3); i++) {
                        setProgress(progress + progressIncrement);
                        progress += progressIncrement;
                        InsertOrderString = String.format("%s, \"%s\"", InsertOrderString, Integer.toString(i));
                    }
                    AddCustomerWorker.failIfInterrupted();

                    //Adds ? for customer name and each quantity amount to use in prepared statement.
                    InsertOrderString = String.format("%s) VALUES(?", InsertOrderString);
                    for (int i = 0; i < ProductTable.getRowCount(); i++) {
                        setProgress(progress + progressIncrement);
                        progress += progressIncrement;
                        InsertOrderString = String.format("%s, %s", InsertOrderString, "?");//table.getModel().getValueAt(i, 4)
                    }

                    AddCustomerWorker.failIfInterrupted();

                    InsertOrderString = String.format("%s)", InsertOrderString);

                    //Creates prepared Statement and replaces ? with quantities and names
                    try (PreparedStatement writeOrd = DbInt.getPrep(year, InsertOrderString)) {
                        writeOrd.setString(1, Name);
                        for (int i = 0; i < ProductTable.getRowCount(); i++) {
                            setProgress(progress + progressIncrement);
                            progress += progressIncrement;
                            writeOrd.setString(i + 2, ProductTable.getModel().getValueAt(i, 4).toString());
                        }
                        AddCustomerWorker.failIfInterrupted();
                        publish("Adiing Order");

                        writeOrd.executeUpdate();
                    }
                }

                //Inserts into customers tables with specified information.
                {
                    int progressIncrement = (100 - progress) / 3;
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
                    setProgress(progress + progressIncrement);
                    progress += progressIncrement;
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
                    setProgress(progress + progressIncrement);
                    progress += progressIncrement;
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
                    setProgress(progress + progressIncrement);
                    progress += progressIncrement;
                    setProgress(100);
                    progress = 100;

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

                String UpdateOrderString = "UPDATE ORDERS SET NAME=?";
                //loops through table and adds product number to order string with "=?"
                for (int i = 0; i < ProductTable.getRowCount(); i++) {
                    UpdateOrderString = String.format("%s, \"%s\"=?", UpdateOrderString, Integer.toString(i));//table.getModel().getValueAt(i, 4)
                    setProgress(getProgress() + progressIncrement);
                }
                AddCustomerWorker.failIfInterrupted();

                //Uses string to create PreparedStatement that is filled with quantities from table.
                UpdateOrderString = String.format("%s WHERE NAME = ?", UpdateOrderString);
                try (PreparedStatement updateOrders = DbInt.getPrep(year, UpdateOrderString)) {
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

    @Override
    protected void process(List<String> chunks) {
        // Updates the messages text area
        for (String string : chunks) {
            StatusLbl.setText(string);
        }
    }
}