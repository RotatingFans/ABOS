import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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


public class AddCustomer extends JDialog {

    private static boolean edit = false;
    private final JPanel contentPanel = new JPanel();
    JCheckBox Delivered;
    JCheckBox Paid;
    Object[][] OldOrder;
    private JTable table;
    private JTextField Name;
    private JTextField Address;
    private JTextField ZipCode;
    private JTextField Town;
    private JTextField State;
    private JTextField Phone;
    private JTextField Email;
    private JButton okButton;
    private JButton cancelButton;
    private String year = null;
    private JTextField DonationsT;
    private double tCostT = 0;
    private String NameEdU;
    private double tCostTOr = 0;
    private double mulchOr = 0;
    private double lpOr = 0;
    private double lgOr = 0;

    public AddCustomer(String cName) {
        year = CustomerReport.year;
        edit = true;
        initUI();
        String[] addr = new String[4];
        try {
            addr = getAddress(getAddr(cName).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String streetAdd = addr[3];
        String city = addr[0];
        String state = addr[1];
        String zip = addr[2];
        Address.setText(streetAdd);
        Town.setText(city);
        State.setText(state);
        ZipCode.setText(zip);
        Phone.setText(getPhone(cName));
        Paid.setSelected(Boolean.getBoolean(getPaid(cName)));
        Delivered.setSelected(Boolean.getBoolean(getDelivered(cName)));
        Email.setText(getEmail(cName));
        Name.setText(cName);
        fillTable(getOrderId(cName));
        NameEdU = cName;
        edit = true;
        table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == 0) {
                    if (e.getColumn() == 4) {

                        int row = e.getFirstRow();
                        int q = Integer.parseInt(table.getModel().getValueAt(row, 4).toString());
                        double tCost = q * Double.parseDouble(table.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));
                        tCostT = tCostT + tCost;
                        table.getModel().setValueAt(tCost, row, 5);
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

    public AddCustomer(String cName, String Year) {
        year = Year;
        edit = true;
        initUI();
        Address.setText(getAddr(cName).toString());
        Phone.setText(getPhone(cName));
        Paid.setSelected(Boolean.getBoolean(getPaid(cName)));
        Delivered.setSelected(Boolean.getBoolean(getDelivered(cName)));
        Email.setText(getEmail(cName));
        Name.setText(cName);
        fillTable(getOrderId(cName));
        NameEdU = cName;
        edit = true;
        table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == 0) {
                    if (e.getColumn() == 4) {

                        int row = e.getFirstRow();
                        int q = Integer.parseInt(table.getModel().getValueAt(row, 4).toString());
                        double tCost = q * Double.parseDouble(table.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));
                        tCostT = tCostT + tCost;
                        table.getModel().setValueAt(tCost, row, 5);
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
        table.getModel().addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if (e.getType() == 0) {
                    if (e.getColumn() == 4) {

                        int row = e.getFirstRow();
                        int q = Integer.parseInt(table.getModel().getValueAt(row, 4).toString());
                        double tCost = q * Double.parseDouble(table.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));
                        tCostT = tCostT + tCost;
                        table.getModel().setValueAt(tCost, row, 5);
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
     * Create the dialog.
     * Auto Fill information
     */


    private String getAddr(String name) {
        return DbInt.getCustInf(year, name, "ADDR");

    }

    private String getPhone(String name) {
        return DbInt.getCustInf(year, name, "PHONE");

    }

    private String getPaid(String name) {
        return DbInt.getCustInf(year, name, "PAID");

    }

    private String getDelivered(String name) {
        return DbInt.getCustInf(year, name, "DELIVERED");

    }

    private String getEmail(String name) {
        return DbInt.getCustInf(year, name, "Email");

    }

    private String getOrderId(String name) {
        return DbInt.getCustInf(year, name, "ORDERID");
    }

    /**
     * Create the dialog.
     *
     * @return
     */
    private void initUI() {
        setSize(1100, 700);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        //contentPanel.setLayout(BorderLayout);
        {
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(0, 102, 857, 547);
            getContentPane().add(scrollPane);
            {
                table = new JTable();
                table.setCellSelectionEnabled(true);
                table.setColumnSelectionAllowed(true);
                if (!edit) {
                    fillTable();
                }

                table.setFillsViewportHeight(true);

                scrollPane.setViewportView(table);
            }
        }
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

    @SuppressWarnings("serial")
    private void fillTable() {

        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
        ArrayList<String> productIDs;
        ArrayList<String> productNames;
        ArrayList<String> Size;
        ArrayList<String> Unit;
        String toGet[] = {"ID", "PNAME", "SIZE", "UNIT"};
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
        try {
            ResultSet rs = prep.executeQuery();
            for (int i = 0; i < 4; i++) {
                res.add(new ArrayList<String>());
                while (rs.next()) {

                    res.get(i).add(rs.getString(toGet[i]));

                }
                rs.beforeFirst();
                DbInt.pCon.commit();
                ////DbInt.pCon.close();

            }
            rs.close();
            rs = null;
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        productIDs = res.get(0);
        productNames = res.get(1);
        Size = res.get(2);
        Unit = res.get(3);
        Object[][] rows = new Object[productNames.size()][6];

        for (int i = 0; i < productNames.size(); i++) {
            rows[i][0] = productIDs.get(i);
            rows[i][1] = productNames.get(i);
            rows[i][2] = Size.get(i);
            rows[i][3] = Unit.get(i);
            rows[i][4] = 0;
            rows[i][5] = 0;

        }
        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};
        table.setModel(new DefaultTableModel(
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

    @SuppressWarnings("serial")
    private void fillTable(String OrderID) {

        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
        ArrayList<String> productIDs;
        ArrayList<String> productNames;
        ArrayList<String> Size;
        ArrayList<String> Unit;
        String toGet[] = {"ID", "PNAME", "SIZE", "UNIT"};
        ArrayList<ArrayList<String>> res = new ArrayList<ArrayList<String>>();
        PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");

        try {
            ResultSet rs = prep.executeQuery();
            for (int i = 0; i < 4; i++) {
                res.add(new ArrayList<String>());


                while (rs.next()) {

                    res.get(i).add(rs.getString(toGet[i]));


                }
                rs.beforeFirst();
                //DbInt.pCon.commit();
                ////DbInt.pCon.close();

            }

            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        productIDs = res.get(0);
        productNames = res.get(1);
        Size = res.get(2);
        Unit = res.get(3);
        Object[][] rows = new Object[productNames.size()][6];
        OldOrder = new Object[productNames.size()][3];
        ArrayList<String> Order = new ArrayList<String>();
        for (int i = 0; i < productNames.size(); i++) {

            int quant = 0;
            prep = DbInt.getPrep(year, "SELECT * FROM ORDERS WHERE ORDERID=?");
            try {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, OrderID);
                ResultSet rs = prep.executeQuery();

                while (rs.next()) {

                    Order.add(rs.getString(Integer.toString(i)));

                }
                ////DbInt.pCon.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            quant = Integer.parseInt(Order.get(Order.size() - 1));
            rows[i][0] = productIDs.get(i);
            rows[i][1] = productNames.get(i);
            rows[i][2] = Size.get(i);
            rows[i][3] = Unit.get(i);
            rows[i][4] = quant;
            rows[i][5] = quant * Double.parseDouble(Unit.get(i).replaceAll("\\$", ""));
            OldOrder[i][0] = i;
            OldOrder[i][1] = rows[i][4];
            OldOrder[i][2] = rows[i][5];
            tCostT = tCostT + Double.parseDouble(rows[i][5].toString());
            tCostTOr = tCostTOr + Double.parseDouble(rows[i][5].toString());
        }
        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};
        table.setModel(new DefaultTableModel(
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
        mulchOr = getMulchOrdered();
        lpOr = getLpOrdered();
        lgOr = getLgOrdered();

    }

    private void commitChanges() {
        /**
         * Insert Order
         * Get ID via Name
         * insert Customer INfo
         */
        try {
            String address = String.format("%s %s, %s", Address.getText().toString(), Town.getText().toString(), State.getText().toString());
            if (!edit) {
                String com = "INSERT INTO ORDERS(NAME";
                int cols = DbInt.getNoCol(year, "ORDERS");

                for (int i = 0; i <= cols - 3; i++) {
                    com = String.format("%s, \"%s\"", com, Integer.toString(i));

                }
                com = String.format("%s) VALUES(?", com);
                for (int i = 0; i < table.getRowCount(); i++) {
                    com = String.format("%s, %s", com, "?");//table.getModel().getValueAt(i, 4)
                }
                com = String.format("%s)", com);
                PreparedStatement writeOrd = DbInt.getPrep(year, com);
                writeOrd.setString(1, Name.getText().toString());
                for (int i = 0; i < table.getRowCount(); i++) {
                    writeOrd.setString(i + 2, table.getModel().getValueAt(i, 4).toString());//
                }
                System.out.println(writeOrd.executeUpdate());
                //////DbInt.pCon.close();

                ArrayList<String> Ids = new ArrayList<String>();
                PreparedStatement prep = DbInt.getPrep(year, "SELECT ORDERID FROM ORDERS WHERE NAME=?");

                prep.setString(1, Name.getText().toString());
                ResultSet rs = prep.executeQuery();
                while (rs.next()) {

                    Ids.add(rs.getString(1));

                }
                //////DbInt.pCon.close();

                String Id = Ids.get(Ids.size() - 1);
                //,,,,, Paid.isSelected(), , );
                PreparedStatement writeCust = DbInt.getPrep(year, "INSERT INTO CUSTOMERS(NAME,ADDR,PHONE, ORDERID , PAID,DELIVERED, EMAIL) VALUES (?,?,?,?,?,?,?)");
                writeCust.setString(1, Name.getText().toString());
                writeCust.setString(2, address);
                writeCust.setString(3, Phone.getText().toString());
                writeCust.setString(4, Id);
                writeCust.setString(5, Boolean.toString(Paid.isSelected()));
                writeCust.setString(6, Boolean.toString(Delivered.isSelected()));
                writeCust.setString(7, Email.getText().toString());

                writeCust.execute();
                //////DbInt.pCon.close();

                writeCust = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(ADDRESS, ORDERED, NI, NH) VALUES(?,'True','False','False')");
                writeCust.setString(1, address);
                writeCust.execute();
                //////DbInt.pCon.close();

            }
            if (edit) {

                PreparedStatement updateCust = DbInt.getPrep("Set", "UPDATE CUSTOMERS SET ADDRESS=?, ORDERED='True', NI='False', NH='False' WHERE ADDRESS=?");
                updateCust.setString(1, address);
                updateCust.setString(2, getAddr(NameEdU));
                updateCust.execute();
                //////DbInt.pCon.close();

                //Name.getText().toString(),address,Phone.getText().toString(), Paid.isSelected(), Delivered.isSelected(), Email.getText().toString(),NameEdU
                PreparedStatement CComU = DbInt.getPrep(year, "UPDATE CUSTOMERS SET NAME=?, ADDR=?,PHONE=?,PAID=?,DELIVERED=?, EMAIL=? WHERE NAME = ?");
                CComU.setString(1, Name.getText().toString());
                CComU.setString(2, address);
                CComU.setString(3, Phone.getText().toString());
                CComU.setString(4, Boolean.toString(Paid.isSelected()));
                CComU.setString(5, Boolean.toString(Delivered.isSelected()));
                CComU.setString(6, Email.getText().toString());
                CComU.setString(7, NameEdU);
                CComU.execute();
                //////DbInt.pCon.close();

                String OComU = "UPDATE ORDERS SET NAME=?";

                for (int i = 0; i < table.getRowCount(); i++) {
                    OComU = String.format("%s, \"%s\"=?", OComU, Integer.toString(i));//table.getModel().getValueAt(i, 4)
                }


                OComU = String.format("%s WHERE NAME = ?", OComU);
                PreparedStatement updateOrders = DbInt.getPrep(year, OComU);
                updateOrders.setString(1, Name.getText().toString());
                for (int i = 0; i < table.getRowCount(); i++) {
                    updateOrders.setString(i + 2, table.getModel().getValueAt(i, 4).toString());

                }
                updateOrders.setString(table.getRowCount() + 2, NameEdU);
                updateOrders.execute();
                //////DbInt.pCon.close();


                double donations = Double.parseDouble(getDonations()) + Double.parseDouble(DonationsT.getText().toString());
                double Lg = Double.parseDouble(getLG());
                double LP = Double.parseDouble(getLP());
                double Mulch = Double.parseDouble(getMulch()) + (getMulchOrdered() - mulchOr);
                double OT = Double.parseDouble(getOT()) + (tCostT - tCostTOr);
                double Customers = Double.parseDouble(getCustomers());
                double Commis = getCommission(OT);
                //donations,Lg,LP,Mulch,OT,Customers,Commis
/*                PreparedStatement writeTots = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS) VALUES(?,?,?,?,?,?,?)");
                writeTots.setString(1, Double.toString(donations));
                writeTots.setString(2, Double.toString(Lg));
                writeTots.setString(3, Double.toString(LP));
                writeTots.setString(4, Double.toString(Mulch));
                writeTots.setString(5, Double.toString(OT));
                writeTots.setString(6, Double.toString(Customers));
                writeTots.setString(7, Double.toString(Commis));
                writeTots.execute();*/
                //////DbInt.pCon.close();

			/*updatedouble newTot = Double.parseDouble(getOT()) + (tCostT - tCostTOr);  customers via name
             * update orders via id
			 * update totals by getting old totals
			 * subtracting from latest insert
			 * add new totals
			 * insert new row
			 */

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getZip(String zipCode) throws IOException {
        //String AddressF = Address.replace(" ","+");
        String url = String.format("http://open.mapquestapi.com/nominatim/v1/search.php?key=CCBtW1293lbtbxpRSnImGBoQopnvc4Mz&format=xml&q=%s&addressdetails=1&limit=1&accept-language=en-US", zipCode);

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

    private String getDonations() {
        return getTots("Donations");

    }

    private String getLG() {
        return getTots("LG");
    }

    private String getLP() {
        return getTots("LP");
    }

    private String getMulch() {
        return getTots("MULCH");
    }

    private String getOT() {
        return getTots("TOTAL");
    }

    private String getCustomers() {
        return getTots("CUSTOMERS");
    }

    private String getCommis() {
        return getTots("COMMISSIONS");
    }

    private double getMulchOrdered() {
        double quant = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getModel().getValueAt(i, 1).toString().contains("Mulch")) {
                if (table.getModel().getValueAt(i, 1).toString().contains("Bulk")) {
                    quant = quant + Double.parseDouble(table.getModel().getValueAt(i, 4).toString());
                }
            }
        }

        return quant;

    }

    private double getLpOrdered() {
        double lp = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getModel().getValueAt(i, 0).toString().contains("-P")) {
                lp = lp + Double.parseDouble(table.getModel().getValueAt(i, 4).toString());

            }
        }
        return lp;
    }

    private double getLgOrdered() {
        double lg = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getModel().getValueAt(i, 0).toString().contains("-L")) {
                lg = lg + Double.parseDouble(table.getModel().getValueAt(i, 4).toString());

            }
        }
        return lg;
    }

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

    private void updateTots() {
        /**
         * get current totals
         * add to them
         * update
         *
         */
        try {
            if (!edit) {
                Double donations = Double.parseDouble(getDonations()) + Double.parseDouble(DonationsT.getText().toString());
                Double Lg = Double.parseDouble(getLG()) + getLgOrdered();
                Double LP = Double.parseDouble(getLP()) + getLpOrdered();
                Double Mulch = Double.parseDouble(getMulch()) + getMulchOrdered();
                Double OT = Double.parseDouble(getOT()) + tCostT;
                Double Customers = Double.parseDouble(getCustomers()) + 1;
                Double Commis = getCommission(OT);
                PreparedStatement writeTots = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS) VALUES(?,?,?,?,?,?,?)");
                writeTots.setString(1, Double.toString(donations));
                writeTots.setString(2, Double.toString(Lg));
                writeTots.setString(3, Double.toString(LP));
                writeTots.setString(4, Double.toString(Mulch));
                writeTots.setString(5, Double.toString(OT));
                writeTots.setString(6, Double.toString(Customers));
                writeTots.setString(7, Double.toString(Commis));

                writeTots.execute();
                //////DbInt.pCon.close();

            } else if (edit) {
                Double donations = Double.parseDouble(getDonations()) + Double.parseDouble(DonationsT.getText().toString());
                Double Lg = Double.parseDouble(getLG()) + (getLgOrdered() - lgOr);
                Double LP = Double.parseDouble(getLP()) + (getLpOrdered() - lpOr);
                Double Mulch = Double.parseDouble(getMulch()) + (getMulchOrdered() - mulchOr);
                Double OT = Double.parseDouble(getOT()) + (tCostT - tCostTOr);
                Double Customers = Double.parseDouble(getCustomers());
                Double Commis = getCommission(OT);
                PreparedStatement writeTots = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS) VALUES(?,?,?,?,?,?,?)");
                writeTots.setString(1, Double.toString(donations));
                writeTots.setString(2, Double.toString(Lg));
                writeTots.setString(3, Double.toString(LP));
                writeTots.setString(4, Double.toString(Mulch));
                writeTots.setString(5, Double.toString(OT));
                writeTots.setString(6, Double.toString(Customers));
                writeTots.setString(7, Double.toString(Commis));
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
                    FullName = getZip(zip);
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
