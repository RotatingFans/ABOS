import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;


/**
 * A dialog that allows the user to add a new customer or edit an existing customer.
 *
 * @author patrick
 * @version 1.0
 */
class AddCustomer extends JDialog {

    private static boolean edit = false; //States whether this is an edit or creation of a customer.
    //private final JPanel contentPanel = new JPanel();

    //Editable Field for user to input customer info.
    private JCheckBox Delivered;
    private JCheckBox Paid;
    private JTable ProductTable;
    private JTextField Name;
    private JTextField Address;
    private JTextField ZipCode;
    private JTextField Town;
    private JTextField State;
    private JTextField Phone;
    private JTextField Email;
    private JTextField DonationsT;
    //Action buttons
    private JButton okButton;
    private JButton cancelButton;
    //Variables used to store regularly accessed info.
    private String year = null;
    private double totalCostFinal = (double) 0.0;
    //Variables used to calculate difference of orders when in edit mode.
    private String NameEditCustomer = null;
    private double totalCostTOr = 0.0;
    private double mulchOr = 0.0;
    private double lpOr = 0.0;
    private double lgOr = 0.0;
    private double donationOr = 0.0;
    private Geolocation Geo = new Geolocation();
    private Customer CustPar = new Customer();
    private AddCustomerWorker addCustWork;
    /**
     * Used to open dialog with already existing customer information from year as specified in Customer Report.
     *
     * @param customerName the name of the customer being edited.
     */
    public AddCustomer(String customerName) {
        year = CustomerReport.year;
        edit = true;
        initUI();
        //Set the address
        String[] addr = CustPar.getCustAddressFrmName(customerName, year);
        String streetAdd = addr[3];
        String city = addr[0];
        String state = addr[1];
        String zip = addr[2];
        //Fill in Customer info fields.
        Address.setText(streetAdd);
        Town.setText(city);
        State.setText(state);
        ZipCode.setText(zip);
        Phone.setText(getPhone(customerName));
        Paid.setSelected(Boolean.getBoolean(getPaid(customerName)));
        Delivered.setSelected(Boolean.getBoolean(getDelivered(customerName)));
        Email.setText(getEmail(customerName));
        Name.setText(customerName);
        DonationsT.setText(getDontation(customerName));
        donationOr = Double.parseDouble(DonationsT.getText());
        //Fill the table with their previous order info on record.
        fillTable(getOrderId(customerName));

        NameEditCustomer = customerName;
        edit = true;
        //Add a Event to occur if a cell is changed in the table
        ProductTable.getModel().addTableModelListener(e -> {
            //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
            if (e.getType() == 0) {
                if (e.getColumn() == 4) {

                    int row = e.getFirstRow();
                    int quantity = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                    double ItemTotalCost = (double) quantity * Double.parseDouble(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));//Removes $ from cost and multiplies to get the total cost for that item
                    if (ItemTotalCost == 0.0) {
                        ItemTotalCost = 0.0;
                    }
                    ProductTable.getModel().setValueAt(ItemTotalCost, row, 5);
                    totalCostFinal = 0.0;
                    for (int i = 0; i < ProductTable.getRowCount(); i++) {
                        totalCostFinal += Double.parseDouble(ProductTable.getModel().getValueAt(i, 5).toString());//Recalculate Order total
                    }


                }
            }
        });

        okButton.addActionListener(e -> {
            if (infoEntered()) {

                commitChanges();
                updateTots();
                dispose();
                setVisible(false);
            } else {
                String message = "<html><head><style>" +
                        "h3 {text-align:center;}" +
                        "h4 {text-align:center;}" +
                        "</style></head>" +
                        "<body><h3>Uh Oh!</h3>" +
                        "<h3>It appears you have not entered any data</h3>" +
                        "<h4>Would you like to re-enter the data?</h4>" +
                        "</body>" +
                        "</html>";
                int cont = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (cont == 1) {
                    dispose();
                    setVisible(false);
                }
            }


        });
        okButton.setActionCommand("OK");
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setActionCommand("Cancel");
        setVisible(true);
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Used to open dialog with already existing customer information from a specific year.
//     *
//     * @param customerName The name of the customer being edited.
//     * @param Year         The year of the order to be edited.
//     */
//    public AddCustomer(String customerName, String Year) {
//        //Assign variables
//        year = Year;
//        edit = true;
//
//        initUI();
//
//        //Fill in Customer Info Fields
//        Address.setText(getAddr(customerName).toString());
//        Phone.setText(getPhone(customerName));
//        Paid.setSelected(Boolean.getBoolean(getPaid(customerName)));
//        Delivered.setSelected(Boolean.getBoolean(getDelivered(customerName)));
//        Email.setText(getEmail(customerName));
//        Name.setText(customerName);
//        DonationsT.setText(getDontation(customerName));
//
//        //Assign Values for original order
//        donationOr = Double.parseDouble(DonationsT.getText());
//        NameEditCustomer = customerName;
//        edit = true;
//
//        fillTable(getOrderId(customerName));
//
//        //Add a Event to occur if a cell is changed in the table
//        ProductTable.getModel().addTableModelListener(new TableModelListener() {
//
//            public void tableChanged(TableModelEvent e) {
//                //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
//                if (e.getType() == 0) {
//                    if (e.getColumn() == 4) {
//
//                        int row = e.getFirstRow();
//                        int quantity = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
//                        double ItemTotalCost = quantity * Double.parseDouble(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));//Removes $ from cost and multiplies to get the total cost for that item
//                        ProductTable.getModel().setValueAt(ItemTotalCost, row, 5);
//                        totalCostFinal = 0;
//                        for (int i = 0; i < ProductTable.getRowCount(); i++) {
//                            totalCostFinal += Double.parseDouble(ProductTable.getModel().getValueAt(i, 5).toString());//Recalculate Order total
//                        }
//
//
//                    }
//                }
//            }
//        });
//
//        okButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (infoEntered()) {
//
//                    commitChanges();
//                    updateTots();
//                    dispose();
//                    setVisible(false);
//                }
//            }
//        });
//        okButton.setActionCommand("OK");
//        cancelButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                setVisible(false);
//            }
//        });
//        cancelButton.setActionCommand("Cancel");
//        setVisible(true);
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    public AddCustomer() {

        year = Year.year;

        initUI();

        //Add a Event to occur if a cell is changed in the table
        ProductTable.getModel().addTableModelListener(e -> {
            //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
            if (e.getType() == 0) {
                if (e.getColumn() == 4) {

                    int row = e.getFirstRow();
                    int q = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                    //Removes $ from cost and multiplies to get the total cost for that item
                    double tCost = (double) q * Double.parseDouble(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));

                    totalCostFinal += tCost;//Recalculate Order total
                    ProductTable.getModel().setValueAt(tCost, row, 5);
                }
            }
        });
        okButton.addActionListener(e -> {
            if (infoEntered()) {
                commitChanges();
                updateTots();
                dispose();
                setVisible(false);
            } else {
                String message = "<html><head><style>" +
                        "h3 {text-align:center;}" +
                        "h4 {text-align:center;}" +
                        "</style></head>" +
                        "<body><h3>Uh Oh!</h3>" +
                        "<h3>It appears you have not entered any data</h3>" +
                        "<h4>Would you like to re-enter the data?</h4>" +
                        "</body>" +
                        "</html>";
                int cont = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (cont == 1) {
                    dispose();
                    setVisible(false);
                }
            }
        });
        okButton.setActionCommand("OK");
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setActionCommand("Cancel");

    }

    /**
     * Launch the application.
     */
    public static void main(String... args) {
        try {
            AddCustomer dialog = new AddCustomer();
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns Street Address of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Address of the specified customer
     * @deprecated
     */
    @Deprecated
    private String getAddr(String name) {
        return DbInt.getCustInf(year, name, "ADDRESS");
    }

    /**
     * Return Phone number of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Phone number of the specified customer
     */
    private String getPhone(String name) {
        return DbInt.getCustInf(year, name, "PHONE");
    }

    /**
     * Returns if the customer has paid.
     *
     * @param name The name of the customer
     * @return The Payment status of the specified customer
     */
    private String getPaid(String name) {
        return DbInt.getCustInf(year, name, "PAID");
    }

    /**
     * Return Delivery status of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Delivery status of the specified customer
     */
    private String getDelivered(String name) {
        return DbInt.getCustInf(year, name, "DELIVERED");
    }

    /**
     * Return Email Address of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Email Address of the specified customer
     */
    private String getEmail(String name) {
        return DbInt.getCustInf(year, name, "Email");
    }

    /**
     * Return Order ID of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Order ID of the specified customer
     */
    private String getOrderId(String name) {
        return DbInt.getCustInf(year, name, "ORDERID");
    }

    /**
     * Return Donation amount of the customer whose name has been specified.
     *
     * @param name The name of the customer
     * @return The Donation Amount of the specified customer
     */
    private String getDontation(String name) {
        return DbInt.getCustInf(year, name, "DONATION");
    }

    /**
     * Create the dialog.
     */
    private void initUI() {
        setSize(1100, 700);
        getContentPane().setLayout(new BorderLayout());
        WrapLayout flow = new WrapLayout();
/*        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);*/
        //contentPanel.setLayout(BorderLayout);

        //Add Table in scrollpane


        //Add Customer info fields and lables
        {
            JPanel North = new JPanel();


            North.setLayout(flow);
            {
                JPanel name = new JPanel(flow);
                {
                    JLabel lblNewLabel = new JLabel("Name");
                    //lblNewLabel.setBounds(10, 25, 46, 14);
                    name.add(lblNewLabel);
                }
                {
                    Name = new JTextField(Config.getProp("CustomerName"));
                    //Name.setBounds(136, 11, 173, 28);
                    Name.setColumns(15);

                    name.add(Name);
                }
                North.add(name);
            }
            {
                JPanel name = new JPanel(flow);
                {
                    JLabel lblNewLabel_1 = new JLabel("Street Address");
                    //lblNewLabel_1.setBounds(329, 18, 46, 14);
                    name.add(lblNewLabel_1);
                }
                {
                    Address = new JTextField(Config.getProp("CustomerAddress"));
                    Address.setColumns(20);
                    //Address.setBounds(385, 11, 173, 28);
                    name.add(Address);
                }
                North.add(name);
            }
            {
                JPanel name = new JPanel(flow);
                {
                    JLabel ZipCodeL = new JLabel("ZipCode");
                    //lblNewLabel_1.setBounds(329, 18, 46, 14);
                    name.add(ZipCodeL);
                }
                {
                    ZipCode = new JTextField(Config.getProp("CustomerZipCode"));
                    ZipCode.setColumns(5);
                    ZipCode.addActionListener(new MyTextActionListener());
                    ZipCode.getDocument().addDocumentListener(new MyDocumentListener());
                    name.add(ZipCode);
                }
                North.add(name);
            }
            {
                JPanel name = new JPanel(flow);
                {
                    JLabel TownL = new JLabel("Town");
                    //lblNewLabel_1.setBounds(329, 18, 46, 14);
                    name.add(TownL);
                }
                {
                    Town = new JTextField(Config.getProp("CustomerTown"));
                    Town.setColumns(10);
                    //Address.setBounds(385, 11, 173, 28);
                    name.add(Town);
                }
                North.add(name);
            }
            {
                JPanel name = new JPanel(flow);
                {
                    JLabel StateL = new JLabel("State");
                    //lblNewLabel_1.setBounds(329, 18, 46, 14);
                    name.add(StateL);
                }
                {
                    State = new JTextField(Config.getProp("CustomerState"));
                    State.setColumns(15);
                    //Address.setBounds(385, 11, 173, 28);
                    name.add(State);
                }

                North.add(name);
            }
//                JPanel South = new JPanel();
//                South.setLayout(new FlowLayout());
            {
                JPanel name = new JPanel(flow);
                {
                    JLabel lblPhone = new JLabel("Phone #");
                    //lblPhone.setBounds(10, 66, 46, 14);
                    name.add(lblPhone);
                }
                {
                    Phone = new JTextField(Config.getProp("CustomerPhone"));
                    Phone.setColumns(10);
                    //Phone.setBounds(136, 59, 173, 28);
                    name.add(Phone);
                }
                North.add(name);
            }
            {
                JPanel name = new JPanel(flow);
                {
                    JLabel lblNewLabel_2 = new JLabel("Email Address");
                    //lblNewLabel_2.setBounds(568, 15, 76, 21);
                    name.add(lblNewLabel_2);
                }
                {
                    Email = new JTextField(Config.getProp("CustomerEmail"));
                    Email.setColumns(10);
                    //	Email.setBounds(654, 11, 173, 28);
                    name.add(Email);
                }
                North.add(name);
            }
            {

                {
                    Paid = new JCheckBox("Paid");
                    Paid.setSelected(Boolean.valueOf(Config.getProp("CustomerPaid")));
                    //Paid.setBounds(385, 62, 55, 23);
                    North.add(Paid);
                }

                {
                    Delivered = new JCheckBox("Delivered");
                    Paid.setSelected(Boolean.valueOf(Config.getProp("CustomerDelivered")));
                    //Delivered.setBounds(473, 62, 83, 23);
                    North.add(Delivered);
                }
            }
            {
                JPanel name = new JPanel(flow);
                {
                    JLabel lblNewLabel_3 = new JLabel("Donations");
                    //lblNewLabel_3.setBounds(568, 66, 76, 14);
                    name.add(lblNewLabel_3);
                }

                {
                    DonationsT = new JTextField(Config.getProp("CustomerDonations"));
                    DonationsT.setColumns(4);
                    //DonationsT.setBounds(654, 59, 173, 28);
                    if (Config.getProp("CustomerDonations") == null) {
                        DonationsT.setText("0.0");
                    }
                    name.add(DonationsT);
                }
                North.add(name);
            }

            getContentPane().add(North, BorderLayout.PAGE_START);
        }
        {
            JScrollPane scrollPane = new JScrollPane();
            {
                ProductTable = new MyJTable();
                //ProductTable.setRowSelectionAllowed(true);
                // ProductTable.setCellSelectionEnabled(true);
                //ProductTable.setDefaultRenderer(String.class, new CustomTableCellRenderer());
                if (!edit) {
                    fillTable();
                }

                ProductTable.setFillsViewportHeight(true);

                scrollPane.setViewportView(ProductTable);
            }
            //scrollPane.setBounds(0, 102, 857,547)
            getContentPane().add(scrollPane, BorderLayout.CENTER);

        }
        //Add button pane to bottom of Window
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.PAGE_END);
            {
                okButton = new JButton("OK");

                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
        }
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setVisible(true);
    }

    /**
     * Fills the table with quantitys set to 0.
     */
    private void fillTable() {

        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"

        //Variables for inserting info into table

        String[] toGet = {"ID", "PNAME", "SIZE", "UNIT"};
        List<ArrayList<String>> ProductInfoArray = new ArrayList<ArrayList<String>>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray

            for (int i = 0; i < 4; i++) {
                ProductInfoArray.add(new ArrayList<String>());
                while (ProductInfoResultSet.next()) {

                    ProductInfoArray.get(i).add(ProductInfoResultSet.getString(toGet[i]));

                }
                ProductInfoResultSet.beforeFirst();
                DbInt.pCon.commit();
                ////DbInt.pCon.close();

            }

            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //define array of rows
        Object[][] rows = new Object[ProductInfoArray.get(1).size()][6];
        //loop through ProductInfoArray and add data to Rows
        for (int i = 0; i < ProductInfoArray.get(1).size(); i++) {
            rows[i][0] = ProductInfoArray.get(0).get(i);
            rows[i][1] = ProductInfoArray.get(1).get(i);
            rows[i][2] = ProductInfoArray.get(2).get(i);
            rows[i][3] = ProductInfoArray.get(3).get(i);
            rows[i][4] = 0;
            rows[i][5] = 0;

        }
        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};

        //Define table properties

        ProductTable.setModel(new DefaultTableModel(
                rows,
                new String[]{
                        "ID", "Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
                }
        ) {

            boolean[] columnEditables = new boolean[]{
                    false, false, false, false, true, false
            };

            @Override
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
    }

    /**
     * Fills product table with info with quantities set to Amount customer ordered.
     *
     * @param OrderID the Order Id of the customer whose order is being displayed
     */
    private void fillTable(String OrderID) {

        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"

        //Variables for inserting info into table
        String[] toGet = {"ID", "PNAME", "SIZE", "UNIT"};
        List<ArrayList<String>> ProductInfoArray = new ArrayList<ArrayList<String>>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()
        ) {
            //Run through Data set and add info to ProductInfoArray
            for (int i = 0; i < 4; i++) {
                ProductInfoArray.add(new ArrayList<String>());
                while (ProductInfoResultSet.next()) {

                    ProductInfoArray.get(i).add(ProductInfoResultSet.getString(toGet[i]));

                }
                ProductInfoResultSet.beforeFirst();
                DbInt.pCon.commit();
                ////DbInt.pCon.close();

            }

            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Table rows array
        Object[][] rows = new Object[ProductInfoArray.get(1).size()][6];

        //Defines array fo quantities of Order prior to editing.
        Object[][] oldOrder = new Object[ProductInfoArray.get(1).size()][3];

        //Defines Arraylist of order quanitities
        List<String> OrderQuantities = new ArrayList<String>();
        //Fills OrderQuantities Array
        for (int i = 0; i < ProductInfoArray.get(1).size(); i++) {

            int quant;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ORDERS WHERE ORDERID=?")) {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, OrderID);
                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {

                        OrderQuantities.add(rs.getString(Integer.toString(i)));

                    }
                    ////DbInt.pCon.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Fills row array for table with info
            quant = Integer.parseInt(OrderQuantities.get(OrderQuantities.size() - 1));
            rows[i][0] = ProductInfoArray.get(0).get(i);
            rows[i][1] = ProductInfoArray.get(1).get(i);
            rows[i][2] = ProductInfoArray.get(2).get(i);
            rows[i][3] = ProductInfoArray.get(3).get(i);
            rows[i][4] = quant;
            rows[i][5] = (double) quant * Double.parseDouble(ProductInfoArray.get(3).get(i).replaceAll("\\$", ""));

            //Defines info for the order prior to editing
            oldOrder[i][0] = i;
            oldOrder[i][1] = rows[i][4];
            oldOrder[i][2] = rows[i][5];
            totalCostFinal += Double.parseDouble(rows[i][5].toString());
            totalCostTOr += Double.parseDouble(rows[i][5].toString());

        }
        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};

        //Sets up table.


        ProductTable.setModel(new MyDefaultTableModel(rows));
        //Fills original totals
        mulchOr = getMulchOrdered();
        lpOr = getLpOrdered();
        lgOr = getLgOrdered();

    }

    /**
     * Commits table to the Database
     */
    private void commitChanges() {
       /* *//*
          Insert Order
          Get ID via Name
          insert Customer INfo
         *//*
        try {
            String address = String.format("%s %s, %s", Address.getText(), Town.getText(), State.getText());//Formats address
            Object[][] coords = Geo.GetCoords(address);
            double lat = Double.valueOf(coords[0][0].toString());
            double lon = Double.valueOf(coords[0][1].toString());
            if (!edit) {
                // Inserts order data into order tables

                {
                    String InsertOrderString = "INSERT INTO ORDERS(NAME";

                    int cols = DbInt.getNoCol(year, "ORDERS");

                    //Loops through And adds product numbers to Order string
                    for (int i = 0; i <= (cols - 3); i++) {
                        InsertOrderString = String.format("%s, \"%s\"", InsertOrderString, Integer.toString(i));
                    }

                    //Adds ? for customer name and each quantity amount to use in prepared statement.
                    InsertOrderString = String.format("%s) VALUES(?", InsertOrderString);
                    for (int i = 0; i < ProductTable.getRowCount(); i++) {
                        InsertOrderString = String.format("%s, %s", InsertOrderString, "?");//table.getModel().getValueAt(i, 4)
                    }
                    InsertOrderString = String.format("%s)", InsertOrderString);

                    //Creates prepared Statement and replaces ? with quantities and names
                    try (PreparedStatement writeOrd = DbInt.getPrep(year, InsertOrderString)) {
                        writeOrd.setString(1, Name.getText());
                        for (int i = 0; i < ProductTable.getRowCount(); i++) {
                            writeOrd.setString(i + 2, ProductTable.getModel().getValueAt(i, 4).toString());
                        }

                        writeOrd.executeUpdate();
                    }
                }

                //Inserts into customers tables with specified information.
                {
                    //Gets order ID of customer
                    List<String> Ids = new ArrayList<String>();
                    try (PreparedStatement prep = DbInt.getPrep(year, "SELECT ORDERID FROM ORDERS WHERE NAME=?")) {

                        prep.setString(1, Name.getText());
                        try (ResultSet rs = prep.executeQuery()) {
                            while (rs.next()) {

                                Ids.add(rs.getString(1));

                            }
                        }
                    }

                    //Inserts into customer table for year
                    String Id = Ids.get(Ids.size() - 1);
                    try (PreparedStatement writeCust = DbInt.getPrep(year, "INSERT INTO CUSTOMERS(NAME,ADDRESS, TOWN, STATE, ZIPCODE, Lat, Lon, PHONE, ORDERID , PAID,DELIVERED, EMAIL, DONATION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                        writeCust.setString(1, Name.getText());
                        writeCust.setString(2, Address.getText());
                        writeCust.setString(3, Town.getText());
                        writeCust.setString(4, State.getText());
                        writeCust.setString(5, ZipCode.getText());
                        writeCust.setDouble(6, lat);
                        writeCust.setDouble(7, lon);
                        writeCust.setString(8, Phone.getText());
                        writeCust.setString(9, Id);
                        writeCust.setString(10, Boolean.toString(Paid.isSelected()));
                        writeCust.setString(11, Boolean.toString(Delivered.isSelected()));
                        writeCust.setString(12, Email.getText());
                        writeCust.setString(13, DonationsT.getText());
                        writeCust.execute();
                    }
                    //Inserts into customer table for all years.
                    try (PreparedStatement prep1 = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(ADDRESS, TOWN, STATE, ZIPCODE, Lat, Lon, ORDERED, NI, NH) VALUES(?,?,?,?,?,?, 'True','False','False')")) {
                        prep1.setString(1, Address.getText());
                        prep1.setString(2, Town.getText());
                        prep1.setString(3, State.getText());
                        prep1.setString(4, ZipCode.getText());
                        prep1.setDouble(5, lat);
                        prep1.setDouble(6, lon);
                        prep1.execute();
                    }
                }
                //////DbInt.pCon.close();

            }
            if (edit) {
                //Updates Customer table in set DB with new info
                try (PreparedStatement updateCust = DbInt.getPrep("Set", "UPDATE Customers SET ADDRESS=?, Town=?, STATE=?, ZIPCODE=?, Lat=?, Lon=?, ORDERED='True', NI='False', NH='False' WHERE ADDRESS=?")) {

                    updateCust.setString(1, Address.getText());
                    updateCust.setString(2, Town.getText());
                    updateCust.setString(3, State.getText());
                    updateCust.setString(4, ZipCode.getText());
                    updateCust.setDouble(5, lat);
                    updateCust.setDouble(6, lon);
                    updateCust.setString(7, getAddr(NameEditCustomer));
                    updateCust.execute();

                }

                //Updates customer table in Year DB with new info.
                try (PreparedStatement CustomerUpdate = DbInt.getPrep(year, "UPDATE CUSTOMERS SET NAME=?, ADDRESS=?, TOWN=?, STATE=?, ZIPCODE=?, Lat=?, Lon=?, PHONE=?,PAID=?,DELIVERED=?, EMAIL=?, DONATION=? WHERE NAME = ?")) {
                    CustomerUpdate.setString(1, Name.getText());
                    CustomerUpdate.setString(2, Address.getText());
                    CustomerUpdate.setString(3, Town.getText());
                    CustomerUpdate.setString(4, State.getText());
                    CustomerUpdate.setString(5, ZipCode.getText());
                    CustomerUpdate.setDouble(6, lat);
                    CustomerUpdate.setDouble(7, lon);
                    CustomerUpdate.setString(8, Phone.getText());
                    CustomerUpdate.setString(9, Boolean.toString(Paid.isSelected()));
                    CustomerUpdate.setString(10, Boolean.toString(Delivered.isSelected()));
                    CustomerUpdate.setString(11, Email.getText());
                    CustomerUpdate.setString(12, DonationsT.getText());
                    CustomerUpdate.setString(13, NameEditCustomer);

                    CustomerUpdate.execute();
                }

                //////DbInt.pCon.close();

                String UpdateOrderString = "UPDATE ORDERS SET NAME=?";
                //loops through table and adds product number to order string with "=?"
                for (int i = 0; i < ProductTable.getRowCount(); i++) {
                    UpdateOrderString = String.format("%s, \"%s\"=?", UpdateOrderString, Integer.toString(i));//table.getModel().getValueAt(i, 4)
                }

                //Uses string to create PreparedStatement that is filled with quantities from table.
                UpdateOrderString = String.format("%s WHERE NAME = ?", UpdateOrderString);
                try (PreparedStatement updateOrders = DbInt.getPrep(year, UpdateOrderString)) {
                    updateOrders.setString(1, Name.getText());
                    for (int i = 0; i < ProductTable.getRowCount(); i++) {
                        updateOrders.setString(i + 2, ProductTable.getModel().getValueAt(i, 4).toString());

                    }
                    updateOrders.setString(ProductTable.getRowCount() + 2, NameEditCustomer);
                    updateOrders.execute();
                }

            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }*/
        ProgressDialog progDial = new ProgressDialog();

        addCustWork = new AddCustomerWorker(Address.getText(), Town.getText(), State.getText(), year, edit, ProductTable, Name.getText(), ZipCode.getText(), Phone.getText(), Email.getText(), DonationsT.getText(), NameEditCustomer, Paid.isSelected(), Delivered.isSelected(), progDial.statusLbl);
        addCustWork.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                switch (event.getPropertyName()) {
                    case "progress":
                        progDial.progressBar.setIndeterminate(false);
                        progDial.progressBar.setValue((Integer) event.getNewValue());
                        break;
                    case "state":
                        switch ((SwingWorker.StateValue) event.getNewValue()) {
                            case DONE:
                                try {

                                } catch (CancellationException e) {
                                    JOptionPane.showMessageDialog(AddCustomer.this, "The process was cancelled", "Add Order",
                                            JOptionPane.WARNING_MESSAGE);
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(AddCustomer.this, "The process failed", "Add Order",
                                            JOptionPane.ERROR_MESSAGE);
                                }

                                addCustWork = null;
                                progDial.dispose();
                                break;
                            case STARTED:
                            case PENDING:
                                progDial.progressBar.setVisible(true);
                                progDial.progressBar.setIndeterminate(true);
                                break;
                        }
                        break;
                }
            }
        });
        addCustWork.execute();
    }

    /**
     * Takes a zipcode and returns the city and state of the customer.
     *
     * @param zipCode The Zipcode of the customer
     * @return The City and state of the customer
     * @throws IOException
     */


    /**
     * Gets info from Totals Table in current year
     *
     * @param info the info to be gotten
     * @return THe info to be wanten
     */
    private String getTots(String info) {
        String ret = "";

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM TOTALS");
             ResultSet rs = prep.executeQuery()
        ) {

            //prep.setString(1, info);


            while (rs.next()) {

                ret = rs.getString(info);

            }
            //////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Gets the Total Donations Using getTots Function
     *
     * @return The total donation amount
     */
    private String getDonations() {
        return getTots("Donations");
    }

    /**
     * Gets the Total Lawn ANd Garden quantities Using getTots Function
     *
     * @return The total Lawn ANd Garden quantities amount
     */
    private String getLG() {
        return getTots("LG");
    }

    /**
     * Gets the Total Live Plants quantities Using getTots Function
     *
     * @return The total Live Plants quantities amount
     */
    private String getLP() {
        return getTots("LP");
    }

    /**
     * Gets the Total Mulch quantities Using getTots Function
     *
     * @return The total Mulch quantities amount
     */
    private String getMulch() {
        return getTots("MULCH");
    }

    /**
     * Gets the order Total Using getTots Function
     *
     * @return The Order total amount
     */
    private String getOT() {
        return getTots("TOTAL");
    }

    /**
     * Gets the Total Customer Using getTots Function
     *
     * @return The total amount of Customers
     */
    private String getCustomers() {
        return getTots("CUSTOMERS");
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Gets the Total Commissions Using getTots Function
//     * @return The total Commissions amount
//     */
//    private String getCommis() {
//        return getTots("COMMISSIONS");
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    /**
     * Gets the Grand Total Using getTots Function
     *
     * @return The Grand total amount
     */
    private String getGTot() {
        String tot = getTots("GRANDTOTAL") == "" ? ("0") : getTots("GRANDTOTAL");
        return tot;
    }


    /**
     * Loops through Table to get total amount of Bulk Mulch ordered.
     *
     * @return The amount of Bulk mulch ordered
     */
    private double getMulchOrdered() {
        double quant = 0.0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 1).toString().contains("Mulch")) {
                if (ProductTable.getModel().getValueAt(i, 1).toString().contains("Bulk")) {
                    quant += Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());
                }
            }
        }

        return quant;

    }


    /**
     * Loops through Table to get total amount of Lawn and Garden Products ordered.
     *
     * @return The amount of Lawn and Garden Products ordered
     */
    private double getLpOrdered() {
        double lp = 0.0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 0).toString().contains("-P") || ProductTable.getModel().getValueAt(i, 0).toString().contains("-FV")) {
                lp += Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());

            }
        }
        return lp;
    }

    /**
     * Loops through Table to get total amount of Live Plants ordered.
     *
     * @return The amount of Live Plants ordered
     */
    private double getLgOrdered() {
        double lg = 0.0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 0).toString().contains("-L")) {
                lg += Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());

            }
        }
        return lg;
    }

    /**
     * Calculates the amount of commission to be earned.
     *
     * @param tcost the Sub total for all orders
     * @return Commission to be earned
     */
    private double getCommission(double tcost) {
        double comm = 0.0;
        if (tcost > 299.99) {
            if (tcost < 500.01) {
                comm = tcost * 0.05;
            }
            if (tcost > 500.01) {
                if (tcost < 1000.99) {
                    comm = tcost * 0.1;
                }

            }
            if (tcost >= 1001.0) {
                comm = tcost * 0.15;
            }
        }
        return comm;

    }

    private boolean infoEntered() {
        return !((Name.getText().length() < 1) && (Address.getText().length() < 1));

    }

    /**
     * Updates the totals tables
     */
    private void updateTots() {
        /*
          get current totals
          add to them
          update

         */
        try {
            if (!edit) {
                Double donations = Double.parseDouble(getDonations()) + (Double.parseDouble(DonationsT.getText()) - donationOr);
                Double Lg = Double.parseDouble(getLG()) + getLgOrdered();
                Double LP = Double.parseDouble(getLP()) + getLpOrdered();
                Double Mulch = Double.parseDouble(getMulch()) + getMulchOrdered();
                Double OT = Double.parseDouble(getOT()) + totalCostFinal;
                Double Customers = Double.parseDouble(getCustomers()) + 1.0;
                Double GTot = Double.parseDouble(getGTot()) + (totalCostFinal - totalCostTOr) + (Double.parseDouble(DonationsT.getText()) - donationOr);

                Double Commis = getCommission(GTot);

                try (PreparedStatement writeTots = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(?,?,?,?,?,?,?,?)")) {
                    writeTots.setString(1, Double.toString(donations));
                    writeTots.setString(2, Double.toString(Lg));
                    writeTots.setString(3, Double.toString(LP));
                    writeTots.setString(4, Double.toString(Mulch));
                    writeTots.setString(5, Double.toString(OT));
                    writeTots.setString(6, Double.toString(Customers));
                    writeTots.setString(7, Double.toString(Commis));
                    writeTots.setString(8, Double.toString(GTot));

                    writeTots.execute();
                }
                //////DbInt.pCon.close();

            } else if (edit) {
                Double donations = Double.parseDouble(getDonations()) + (Double.parseDouble(DonationsT.getText()) - donationOr);
                Double Lg = Double.parseDouble(getLG()) + (getLgOrdered() - lgOr);
                Double LP = Double.parseDouble(getLP()) + (getLpOrdered() - lpOr);
                Double Mulch = Double.parseDouble(getMulch()) + (getMulchOrdered() - mulchOr);
                Double OT = Double.parseDouble(getOT()) + (totalCostFinal - totalCostTOr);
                Double Customers = Double.parseDouble(getCustomers());
                Double GTot = Double.parseDouble(getGTot()) + (totalCostFinal - totalCostTOr) + (Double.parseDouble(DonationsT.getText()) - donationOr);

                Double Commis = getCommission(GTot);
                try (PreparedStatement writeTots = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(?,?,?,?,?,?,?,?)")) {


                    writeTots.setString(1, Double.toString(donations));
                    writeTots.setString(2, Double.toString(Lg));
                    writeTots.setString(3, Double.toString(LP));
                    writeTots.setString(4, Double.toString(Mulch));
                    writeTots.setString(5, Double.toString(OT));
                    writeTots.setString(6, Double.toString(Customers));
                    writeTots.setString(7, Double.toString(Commis));
                    writeTots.setString(8, Double.toString(GTot));
                    writeTots.execute();
                }
                //////DbInt.pCon.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static class MyDefaultTableModel extends DefaultTableModel {

        boolean[] columnEditables;

        public MyDefaultTableModel(Object[][] rows) {
            super(rows, new String[]{
                    "ID", "Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
            });
            columnEditables = new boolean[]{
                    false, false, false, false, true, false
            };
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return columnEditables[column];
        }

    }

    private static class MyJTable extends JTable {
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {


            return super.prepareRenderer(renderer, row, column);
        }
    }

    static class MyDocumentListener implements DocumentListener {
        // --Commented out by Inspection (1/2/2016 12:01 PM):final String newline = "\n";

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateLog();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateLog();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //Plain text components don't fire these events.
        }

        public void updateLog() {

        }
    }

    private class MyTextActionListener implements ActionListener {
        /**
         * Handle the text field Return.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String zip = ZipCode.getText();
            if (zip.length() > 4) {
                String FullName = "";
                try {
                    FullName = Geo.getCityState(zip);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                String[] StateTown = FullName.split("&");
                String state = StateTown[1];
                String town = StateTown[0];
                Town.setText(town);
                State.setText(state);
            }
        }
    }


}
