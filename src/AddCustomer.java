import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * A dialog that allows the user to add a new customer or edit an existing customer.
 *
 * @author patrick
 * @version 1.0
 */
public class AddCustomer extends JDialog {


    private static boolean edit = false; //States whether this is an edit or creation of a customer.
    //private final JPanel contentPanel = new JPanel();

    //Editable Field for user to input customer info.
    JCheckBox Delivered;
    JCheckBox Paid;    
    Object[][] OldOrder;
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
    private double totalCostFinal = 0;
    //Variables used to calculate difference of orders when in edit mode.
    private String NameEditCustomer;
    private double totalCostTOr = 0;
    private double mulchOr = 0;
    private double lpOr = 0;
    private double lgOr = 0;
    private double donationOr;

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
        String[] addr = new String[4];
        //Todo deprecate getAddr Function
        try {
            addr = getAddress(getAddr(customerName).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        ProductTable.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
                if (e.getType() == 0) {
                    if (e.getColumn() == 4) {

                        int row = e.getFirstRow();
                        int quantity = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                        double ItemTotalCost = quantity * Double.parseDouble(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));//Removes $ from cost and multiplies to get the total cost for that item
                        ProductTable.getModel().setValueAt(ItemTotalCost, row, 5);
                        totalCostFinal = 0;
                        for (int i = 0; i < ProductTable.getRowCount(); i++) {
                            totalCostFinal = totalCostFinal + Double.parseDouble(ProductTable.getModel().getValueAt(i, 5).toString());//Recalculate Order total
                        }


                    }
                }
            }
        });

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commitChanges();
                updateTots();
                dispose();
                setVisible(false);

            }
        });
        okButton.setActionCommand("OK");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        cancelButton.setActionCommand("Cancel");
        this.setVisible(true);
    }

    /**
     * Used to open dialog with already existing customer information from a specific year.
     *
     * @param customerName The name of the customer being edited.
     * @param Year         The year of the order to be edited.
     */
    public AddCustomer(String customerName, String Year) {
        //Assign variables
        year = Year;
        edit = true;

        initUI();

        //Fill in Customer Info Fields
        Address.setText(getAddr(customerName).toString());
        Phone.setText(getPhone(customerName));
        Paid.setSelected(Boolean.getBoolean(getPaid(customerName)));
        Delivered.setSelected(Boolean.getBoolean(getDelivered(customerName)));
        Email.setText(getEmail(customerName));
        Name.setText(customerName);
        DonationsT.setText(getDontation(customerName));

        //Assign Values for original order
        donationOr = Double.parseDouble(DonationsT.getText());
        NameEditCustomer = customerName;
        edit = true;

        fillTable(getOrderId(customerName));

        //Add a Event to occur if a cell is changed in the table
        ProductTable.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
                if (e.getType() == 0) {
                    if (e.getColumn() == 4) {

                        int row = e.getFirstRow();
                        int quantity = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                        double ItemTotalCost = quantity * Double.parseDouble(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));//Removes $ from cost and multiplies to get the total cost for that item
                        ProductTable.getModel().setValueAt(ItemTotalCost, row, 5);
                        totalCostFinal = 0;
                        for (int i = 0; i < ProductTable.getRowCount(); i++) {
                            totalCostFinal = totalCostFinal + Double.parseDouble(ProductTable.getModel().getValueAt(i, 5).toString());//Recalculate Order total
                        }


                    }
                }
            }
        });

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commitChanges();
                updateTots();
                dispose();
                setVisible(false);
            }
        });
        okButton.setActionCommand("OK");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                setVisible(false);
            }
        });
        cancelButton.setActionCommand("Cancel");
        this.setVisible(true);
    }

    public AddCustomer() {

        year = Year.year;

        initUI();

        //Add a Event to occur if a cell is changed in the table
        ProductTable.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
                if (e.getType() == 0) {
                    if (e.getColumn() == 4) {

                        int row = e.getFirstRow();
                        int q = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                        //Removes $ from cost and multiplies to get the total cost for that item
                        double tCost = q * Double.parseDouble(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));

                        totalCostFinal = totalCostFinal + tCost;//Recalculate Order total
                        ProductTable.getModel().setValueAt(tCost, row, 5);
                    }
                }
            }
        });
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                commitChanges();
                updateTots();
                dispose();
                setVisible(false);
            }
        });
        okButton.setActionCommand("OK");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        cancelButton.setActionCommand("Cancel");

    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            AddCustomer dialog = new AddCustomer();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Return Address of the customer whose name has been specified.
     * @param name The name of the customer
     * @return The Address of the specified customer
     */
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
     *
     *
     */
    private void initUI() {
        setSize(1100, 700);
        getContentPane().setLayout(new BorderLayout());
/*        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);*/
        //contentPanel.setLayout(BorderLayout);

        //Add Table in scrollpane
        {
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(0, 102, 857, 547);
            getContentPane().add(scrollPane);
            {
                ProductTable = new JTable();
                ProductTable.setCellSelectionEnabled(true);
                ProductTable.setColumnSelectionAllowed(true);
                if (!edit) {
                    fillTable();
                }

                ProductTable.setFillsViewportHeight(true);

                scrollPane.setViewportView(ProductTable);
            }
        }

        //Add Customer info fields and lables
        {
            JPanel CustomerInfo = new JPanel(new BorderLayout());
            {
                JPanel North = new JPanel();
                North.setLayout(new FlowLayout());
                {
                    JLabel lblNewLabel = new JLabel("Name");
                    //lblNewLabel.setBounds(10, 25, 46, 14);
                    North.add(lblNewLabel);
                }
                {
                    Name = new JTextField();
                    //Name.setBounds(136, 11, 173, 28);
                    North.add(Name);
                    Name.setColumns(15);
                }
                {
                    JLabel lblNewLabel_1 = new JLabel("Street Address");
                    //lblNewLabel_1.setBounds(329, 18, 46, 14);
                    North.add(lblNewLabel_1);
                }
                {
                    Address = new JTextField();
                    Address.setColumns(20);
                    //Address.setBounds(385, 11, 173, 28);
                    North.add(Address);
                }
                {
                    JLabel ZipCodeL = new JLabel("ZipCode");
                    //lblNewLabel_1.setBounds(329, 18, 46, 14);
                    North.add(ZipCodeL);
                }
                {
                    ZipCode = new JTextField();
                    ZipCode.setColumns(5);
                    ZipCode.addActionListener(new MyTextActionListener());
                    ZipCode.getDocument().addDocumentListener(new MyDocumentListener());
                    //ZipCode.getDocument().putProperty("ZipCode", "Text Field");


                    //Address.setBounds(385, 11, 173, 28);
                    North.add(ZipCode);
                }
                {
                    JLabel TownL = new JLabel("Town");
                    //lblNewLabel_1.setBounds(329, 18, 46, 14);
                    North.add(TownL);
                }
                {
                    Town = new JTextField();
                    Town.setColumns(10);
                    //Address.setBounds(385, 11, 173, 28);
                    North.add(Town);
                }
                {
                    JLabel StateL = new JLabel("State");
                    //lblNewLabel_1.setBounds(329, 18, 46, 14);
                    North.add(StateL);
                }
                {
                    State = new JTextField();
                    State.setColumns(15);
                    //Address.setBounds(385, 11, 173, 28);
                    North.add(State);
                }
                CustomerInfo.add(North, BorderLayout.NORTH);
                //CustomerInfo.add(North);
            }
            {
                JPanel South = new JPanel(new FlowLayout());
                {
                    JLabel lblPhone = new JLabel("Phone #");
                    //lblPhone.setBounds(10, 66, 46, 14);
                    South.add(lblPhone);
                }
                {
                    Phone = new JTextField();
                    Phone.setColumns(10);
                    //Phone.setBounds(136, 59, 173, 28);
                    South.add(Phone);
                }
                {
                    JLabel lblNewLabel_2 = new JLabel("Email Address");
                    //lblNewLabel_2.setBounds(568, 15, 76, 21);
                    South.add(lblNewLabel_2);
                }
                {
                    Email = new JTextField();
                    Email.setColumns(10);
                    //	Email.setBounds(654, 11, 173, 28);
                    South.add(Email);
                }
                {
                    Paid = new JCheckBox("Paid");
                    //Paid.setBounds(385, 62, 55, 23);
                    South.add(Paid);
                }

                {
                    Delivered = new JCheckBox("Delivered");
                    //Delivered.setBounds(473, 62, 83, 23);
                    South.add(Delivered);
                }

                {
                    JLabel lblNewLabel_3 = new JLabel("Donations");
                    //lblNewLabel_3.setBounds(568, 66, 76, 14);
                    South.add(lblNewLabel_3);
                }

                {
                    DonationsT = new JTextField();
                    DonationsT.setColumns(4);
                    //DonationsT.setBounds(654, 59, 173, 28);
                    DonationsT.setText("0.0");
                    South.add(DonationsT);
                }
                CustomerInfo.add(South, BorderLayout.SOUTH);
            }
            getContentPane().add(CustomerInfo, BorderLayout.NORTH);
        }

        //Add button pane to bottom of Window
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
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
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        this.setVisible(true);
    }

    /**
     * Fills the table with quantitys set to 0.
     */
    @SuppressWarnings("serial")
    private void fillTable() {

        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"

        //Variables for inserting info into table
        ArrayList<String> productIDs;
        ArrayList<String> productNames;
        ArrayList<String> Size;
        ArrayList<String> Unit;
        String toGet[] = {"ID", "PNAME", "SIZE", "UNIT"};
        ArrayList<ArrayList<String>> ProductInfoArray = new ArrayList<ArrayList<String>>(); //Single array to store all data to add to table.
        PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");//Get a prepared statement to retrieve data

        try {
            //Run through Data set and add info to ProductInfoArray
            ResultSet ProductInfoResultSet = prep.executeQuery();
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
            ProductInfoResultSet = null;
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

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
    }

    /**Fills product table with info with quantities set to Amount customer ordered.
     * @param OrderID the Order Id of the customer whose order is being displayed
     */
    @SuppressWarnings("serial")
    private void fillTable(String OrderID) {

        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"

        //Variables for inserting info into table
        String toGet[] = {"ID", "PNAME", "SIZE", "UNIT"};
        ArrayList<ArrayList<String>> ProductInfoArray = new ArrayList<ArrayList<String>>(); //Single array to store all data to add to table.
        PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");//Get a prepared statement to retrieve data

        try {
            //Run through Data set and add info to ProductInfoArray
            ResultSet ProductInfoResultSet = prep.executeQuery();
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
            ProductInfoResultSet = null;
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
        OldOrder = new Object[ProductInfoArray.get(1).size()][3];

        //Defines Arraylist of order quanitities
        ArrayList<String> OrderQuantities = new ArrayList<String>();
        //Fills OrderQuantities Array
        for (int i = 0; i < ProductInfoArray.get(1).size(); i++) {

            int quant = 0;
            prep = DbInt.getPrep(year, "SELECT * FROM ORDERS WHERE ORDERID=?");
            try {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, OrderID);
                ResultSet rs = prep.executeQuery();

                while (rs.next()) {

                    OrderQuantities.add(rs.getString(Integer.toString(i)));

                }
                ////DbInt.pCon.close();

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
            rows[i][5] = quant * Double.parseDouble(ProductInfoArray.get(3).get(i).replaceAll("\\$", ""));

            //Defines info for the order prior to editing
            OldOrder[i][0] = i;
            OldOrder[i][1] = rows[i][4];
            OldOrder[i][2] = rows[i][5];
            totalCostFinal = totalCostFinal + Double.parseDouble(rows[i][5].toString());
            totalCostTOr = totalCostTOr + Double.parseDouble(rows[i][5].toString());

        }
        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};

        //Sets up table.
        ProductTable.setModel(new DefaultTableModel(
                rows,
                new String[]{
                        "ID", "Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
                }
        ) {

            boolean[] columnEditables = new boolean[]{
                    false, false, false, false, true, false
            };

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        //Fills original totals
        mulchOr = getMulchOrdered();
        lpOr = getLpOrdered();
        lgOr = getLgOrdered();

    }

    /**
     * Commits table to the Database
     */
    private void commitChanges() {
        /**
         * Insert Order
         * Get ID via Name
         * insert Customer INfo
         */
        try {
            String address = String.format("%s %s, %s", Address.getText().toString(), Town.getText().toString(), State.getText().toString());//Formats address

            if (!edit) {
                // Inserts order data into order tables

                {
                    String InsertOrderString = "INSERT INTO ORDERS(NAME";

                    int cols = DbInt.getNoCol(year, "ORDERS");

                    //Loops through And adds product numbers to Order string
                    for (int i = 0; i <= cols - 3; i++) {
                        InsertOrderString = String.format("%s, \"%s\"", InsertOrderString, Integer.toString(i));
                    }

                    //Adds ? for customer name and each quantity amount to use in prepared statement.
                    InsertOrderString = String.format("%s) VALUES(?", InsertOrderString);
                    for (int i = 0; i < ProductTable.getRowCount(); i++) {
                        InsertOrderString = String.format("%s, %s", InsertOrderString, "?");//table.getModel().getValueAt(i, 4)
                    }
                    InsertOrderString = String.format("%s)", InsertOrderString);

                    //Creates prepared Statement and replaces ? with quantities and names
                    PreparedStatement writeOrd = DbInt.getPrep(year, InsertOrderString);
                    writeOrd.setString(1, Name.getText().toString());
                    for (int i = 0; i < ProductTable.getRowCount(); i++) {
                        writeOrd.setString(i + 2, ProductTable.getModel().getValueAt(i, 4).toString());
                    }

                    System.out.println(writeOrd.executeUpdate());
                }

                //Inserts into customers tables with specified information.
                {
                    //Gets order ID of customer
                    ArrayList<String> Ids = new ArrayList<String>();
                    PreparedStatement prep = DbInt.getPrep(year, "SELECT ORDERID FROM ORDERS WHERE NAME=?");

                    prep.setString(1, Name.getText().toString());
                    ResultSet rs = prep.executeQuery();
                    while (rs.next()) {

                        Ids.add(rs.getString(1));

                    }

                    //Inserts into customer table for year
                    String Id = Ids.get(Ids.size() - 1);
                    PreparedStatement writeCust = DbInt.getPrep(year, "INSERT INTO CUSTOMERS(NAME,ADDRESS,PHONE, ORDERID , PAID,DELIVERED, EMAIL, DONATION) VALUES (?,?,?,?,?,?,?,?)");
                    writeCust.setString(1, Name.getText().toString());
                    writeCust.setString(2, address);
                    writeCust.setString(3, Phone.getText().toString());
                    writeCust.setString(4, Id);
                    writeCust.setString(5, Boolean.toString(Paid.isSelected()));
                    writeCust.setString(6, Boolean.toString(Delivered.isSelected()));
                    writeCust.setString(7, Email.getText().toString());
                    writeCust.setString(8, DonationsT.getText().toString());
                    writeCust.execute();

                    //Inserts into customer table for all years.
                    writeCust = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(ADDRESS, ORDERED, NI, NH) VALUES(?,'True','False','False')");
                    writeCust.setString(1, address);
                    writeCust.execute();
                }
                //////DbInt.pCon.close();

            }
            if (edit) {
                //Updates Customer table in set DB with new info
                PreparedStatement updateCust = DbInt.getPrep("Set", "UPDATE CUSTOMERS SET ADDRESS=?, ORDERED='True', NI='False', NH='False' WHERE ADDRESS=?");
                updateCust.setString(1, address);
                updateCust.setString(2, getAddr(NameEditCustomer));
                updateCust.execute();

                //Updates customer table in Year DB with new info.
                PreparedStatement CustomerUpdate = DbInt.getPrep(year, "UPDATE CUSTOMERS SET NAME=?, ADDRESS=?,PHONE=?,PAID=?,DELIVERED=?, EMAIL=?, DONATION=? WHERE NAME = ?");
                CustomerUpdate.setString(1, Name.getText().toString());
                CustomerUpdate.setString(2, address);
                CustomerUpdate.setString(3, Phone.getText().toString());
                CustomerUpdate.setString(4, Boolean.toString(Paid.isSelected()));
                CustomerUpdate.setString(5, Boolean.toString(Delivered.isSelected()));
                CustomerUpdate.setString(6, Email.getText().toString());
                CustomerUpdate.setString(7, DonationsT.getText().toString());
                CustomerUpdate.setString(8, NameEditCustomer);

                CustomerUpdate.execute();


                //////DbInt.pCon.close();

                String UpdateOrderString = "UPDATE ORDERS SET NAME=?";
                //loops through table and adds product number to order string with "=?"
                for (int i = 0; i < ProductTable.getRowCount(); i++) {
                    UpdateOrderString = String.format("%s, \"%s\"=?", UpdateOrderString, Integer.toString(i));//table.getModel().getValueAt(i, 4)
                }

                //Uses string to create PreparedStatement that is filled with quantities from table.
                UpdateOrderString = String.format("%s WHERE NAME = ?", UpdateOrderString);
                PreparedStatement updateOrders = DbInt.getPrep(year, UpdateOrderString);
                updateOrders.setString(1, Name.getText().toString());
                for (int i = 0; i < ProductTable.getRowCount(); i++) {
                    updateOrders.setString(i + 2, ProductTable.getModel().getValueAt(i, 4).toString());

                }
                updateOrders.setString(ProductTable.getRowCount() + 2, NameEditCustomer);
                updateOrders.execute();


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes a zipcode and returns the city and state of the customer.
     *
     * @param zipCode The Zipcode of the customer
     * @return The City and state of the customer
     * @throws IOException
     */
    public String getCityState(String zipCode) throws IOException {
        //String AddressF = Address.replace(" ","+");
        //The URL for the MapquestAPI
        String url = String.format("http://open.mapquestapi.com/nominatim/v1/search.php?key=CCBtW1293lbtbxpRSnImGBoQopnvc4Mz&format=xml&q=%s&addressdetails=1&limit=1&accept-language=en-US", zipCode);

        //Defines connection
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        //Creates Response buffer for Web response
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        //Fill String buffer with response
        while ((inputLine = in.readLine()) != null) {
            //inputLine = StringEscapeUtils.escapeHtml4(inputLine);
            //inputLine = StringEscapeUtils.escapeXml11(inputLine);
            response.append(inputLine);
        }
        in.close();


        String city = "";
        String State = "";

        //Parses XML response and fills City and State Variables
        try {
            InputSource is = new InputSource(new StringReader(response.toString()));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("place");


            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


                    city = eElement.getElementsByTagName("city").item(0).getTextContent();
                    State = eElement.getElementsByTagName("state").item(0).getTextContent();


                    //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Formats City and state into one string to return
        String fullName = city.concat("&");
        fullName = fullName.concat(State);
        //print result
        //	return parseCoords(response.toString());
        return fullName;
    }

    public String[] getAddress(String Address) throws IOException {
        String AddressF = Address.replace(" ", "+");
        String url = String.format("http://open.mapquestapi.com/nominatim/v1/search.php?key=CCBtW1293lbtbxpRSnImGBoQopnvc4Mz&format=xml&q=%s&addressdetails=1&limit=1&accept-language=en-US", AddressF);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            //inputLine = StringEscapeUtils.escapeHtml4(inputLine);
            //inputLine = StringEscapeUtils.escapeXml11(inputLine);
            response.append(inputLine);
        }
        in.close();

        Object[] coords = new Object[2];
        String city = "";
        String State = "";
        String zipCode = "";
        String hN = "";
        String strt = "";
        String srtAdd = "";
        //String city = "";
        try {
            InputSource is = new InputSource(new StringReader(response.toString()));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("place");


            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


                    city = eElement.getElementsByTagName("city").item(0).getTextContent();
                    State = eElement.getElementsByTagName("state").item(0).getTextContent();
                    zipCode = eElement.getElementsByTagName("postcode").item(0).getTextContent();
                    hN = eElement.getElementsByTagName("house_number").item(0).getTextContent();
                    strt = eElement.getElementsByTagName("road").item(0).getTextContent();


                    //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fullName = city.concat("&");
        fullName = fullName.concat(State);
        //print result
        //	return parseCoords(response.toString());
        String[] address = new String[4];
        address[0] = city;
        address[1] = State;
        address[2] = zipCode;
        address[3] = hN + " " + strt;
        return address;
    }


    /** Gets info from Totals Table in current year
     * @param info the info to be gotten
     * @return THe info to be wanten
     */
    private String getTots(String info) {
        String ret = "";

        PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM TOTALS");
        try {

            //prep.setString(1, info);

            ResultSet rs = prep.executeQuery();

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
     * @return The total donation amount
     */
    private String getDonations() {
        return getTots("Donations");
    }

    /**
     * Gets the Total Lawn ANd Garden quantities Using getTots Function
     * @return The total Lawn ANd Garden quantities amount
     */
    private String getLG() {
        return getTots("LG");
    }

    /**
     * Gets the Total Live Plants quantities Using getTots Function
     * @return The total Live Plants quantities amount
     */
    private String getLP() {
        return getTots("LP");
    }

    /**
     * Gets the Total Mulch quantities Using getTots Function
     * @return The total Mulch quantities amount
     */
    private String getMulch() {
        return getTots("MULCH");
    }

    /**
     * Gets the order Total Using getTots Function
     * @return The Order total amount
     */
    private String getOT() {
        return getTots("TOTAL");
    }

    /**
     * Gets the Total Customer Using getTots Function
     * @return The total amount of Customers
     */
    private String getCustomers() {
        return getTots("CUSTOMERS");
    }

    /**
     * Gets the Total Commissions Using getTots Function
     * @return The total Commissions amount
     */
    private String getCommis() {
        return getTots("COMMISSIONS");
    }

    /**
     * Gets the Grand Total Using getTots Function
     *
     * @return The Grand total amount
     */
    private String getGTot() {
        return getTots("GRANDTOTAL");
    }


    /**Loops through Table to get total amount of Bulk Mulch ordered.
     * @return The amount of Bulk mulch ordered
     */
    private double getMulchOrdered() {
        double quant = 0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 1).toString().contains("Mulch")) {
                if (ProductTable.getModel().getValueAt(i, 1).toString().contains("Bulk")) {
                    quant = quant + Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());
                }
            }
        }

        return quant;

    }


    /**Loops through Table to get total amount of Lawn and Garden Products ordered.
     * @return The amount of Lawn and Garden Products ordered
     */
    private double getLpOrdered() {
        double lp = 0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 0).toString().contains("-P")) {
                lp = lp + Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());

            }
        }
        return lp;
    }

    /**Loops through Table to get total amount of Live Plants ordered.
     * @return The amount of Live Plants ordered
     */
    private double getLgOrdered() {
        double lg = 0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 0).toString().contains("-L")) {
                lg = lg + Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());

            }
        }
        return lg;
    }

    /** Calculates the amount of commission to be earned.
     * @param tcost the Sub total for all orders
     * @return
     */
    private double getCommission(double tcost) {
        double comm = 0;
        if (tcost > 299.99) {
            if (tcost < 500.01) {
                comm = tcost * 0.05;
            }
            if (tcost > 500.01) {
                if (tcost < 1000.99) {
                    comm = tcost * 0.1;
                }

            }
            if (tcost >= 1001) {
                comm = tcost * 0.15;
            }
        }
        return comm;

    }

    /**
     * Updates the totals tables
     */
    private void updateTots() {
        /**
         * get current totals
         * add to them
         * update
         *
         */
        try {
            if (!edit) {
                Double donations = Double.parseDouble(getDonations()) + (Double.parseDouble(DonationsT.getText().toString()) - donationOr);
                Double Lg = Double.parseDouble(getLG()) + getLgOrdered();
                Double LP = Double.parseDouble(getLP()) + getLpOrdered();
                Double Mulch = Double.parseDouble(getMulch()) + getMulchOrdered();
                Double OT = Double.parseDouble(getOT()) + totalCostFinal;
                Double Customers = Double.parseDouble(getCustomers()) + 1;
                Double GTot = Double.parseDouble(getGTot()) + (totalCostFinal - totalCostTOr) + (Double.parseDouble(DonationsT.getText().toString()) - donationOr);

                Double Commis = getCommission(GTot);

                PreparedStatement writeTots = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(?,?,?,?,?,?,?,?)");
                writeTots.setString(1, Double.toString(donations));
                writeTots.setString(2, Double.toString(Lg));
                writeTots.setString(3, Double.toString(LP));
                writeTots.setString(4, Double.toString(Mulch));
                writeTots.setString(5, Double.toString(OT));
                writeTots.setString(6, Double.toString(Customers));
                writeTots.setString(7, Double.toString(Commis));
                writeTots.setString(8, Double.toString(GTot));

                writeTots.execute();
                //////DbInt.pCon.close();

            } else if (edit) {
                Double donations = Double.parseDouble(getDonations()) + (Double.parseDouble(DonationsT.getText().toString()) - donationOr);
                Double Lg = Double.parseDouble(getLG()) + (getLgOrdered() - lgOr);
                Double LP = Double.parseDouble(getLP()) + (getLpOrdered() - lpOr);
                Double Mulch = Double.parseDouble(getMulch()) + (getMulchOrdered() - mulchOr);
                Double OT = Double.parseDouble(getOT()) + (totalCostFinal - totalCostTOr);
                Double Customers = Double.parseDouble(getCustomers());
                Double GTot = Double.parseDouble(getGTot()) + (totalCostFinal - totalCostTOr) + (Double.parseDouble(DonationsT.getText().toString()) - donationOr);

                Double Commis = getCommission(GTot);
                PreparedStatement writeTots = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(?,?,?,?,?,?,?,?)");
                writeTots.setString(1, Double.toString(donations));
                writeTots.setString(2, Double.toString(Lg));
                writeTots.setString(3, Double.toString(LP));
                writeTots.setString(4, Double.toString(Mulch));
                writeTots.setString(5, Double.toString(OT));
                writeTots.setString(6, Double.toString(Customers));
                writeTots.setString(7, Double.toString(Commis));
                writeTots.setString(8, Double.toString(GTot));
                writeTots.execute();
                //////DbInt.pCon.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class MyDocumentListener implements DocumentListener {
        final String newline = "\n";

        public void insertUpdate(DocumentEvent e) {
            updateLog(e, "inserted into");
        }

        public void removeUpdate(DocumentEvent e) {
            updateLog(e, "removed from");
        }

        public void changedUpdate(DocumentEvent e) {
            //Plain text components don't fire these events.
        }

        public void updateLog(DocumentEvent e, String action) {

        }
    }

    class MyTextActionListener implements ActionListener {
        /**
         * Handle the text field Return.
         */
        public void actionPerformed(ActionEvent e) {
            String zip = ZipCode.getText().toString();
            if (zip.length() > 4) {
                String FullName = "";
                try {
                    FullName = getCityState(zip);
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
