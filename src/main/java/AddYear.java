import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class AddYear extends JDialog {

    private JCheckBox chkboxCreateDatabase;
    private JTextField yearText;
    private JTable ProductTable;
    private JTextField itemTb;
    private JTextField sizeTb;
    private JTextField rateTb;
    private JComboBox categoriesTb;
    private JComboBox categoriesCmbx;
    private boolean updateDb = false;
    private DefaultTableModel tableModel;
    private JTextField idTb;
    private JDialog parent;

    /**
     * Create the dialog.
     */
    public AddYear() {
        parent = this;
        setSize(700, 500);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());

        //North
        {
            JPanel north = new JPanel(new BorderLayout());
            //North
            {
                JPanel northNorth = new JPanel(new FlowLayout());
                {
                    JLabel lblYear = new JLabel("Year:");
                    lblYear.setFont(new Font("Tahoma", Font.PLAIN, 16));
                    //lblYear.setBounds(10, 0, 89, 40);
                    northNorth.add(lblYear);
                }
                {
                    yearText = new JTextField();
                    yearText.setHorizontalAlignment(SwingConstants.CENTER);
                    yearText.setFont(new Font("Tahoma", Font.PLAIN, 26));
                    //yearText.setBounds(73, 0, 77, 32);
                    northNorth.add(yearText);
                    yearText.setColumns(4);
                    yearText.setText(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
                }

                {
                    JButton fillTblFrmDb = new JButton("Fill table from pre-existing Database");
                    fillTblFrmDb.addActionListener(e -> tablefromDb());
                    //chckbxGenerateXmlFile.setBounds(149, 21, 259, 23);
                    northNorth.add(fillTblFrmDb);

                }
                {
                    chkboxCreateDatabase = new JCheckBox("Create Database");
                    //chckbxCreateDatabase.setBounds(10, 57, 120, 23);
                    chkboxCreateDatabase.setSelected(true);
                    northNorth.add(chkboxCreateDatabase);

                }
                north.add(northNorth, BorderLayout.NORTH);

            }
            //South
            {
                JPanel northSouth = new JPanel(new FlowLayout());


                JButton genXmlFrmTbl = new JButton("Generate XML file from Table below");
                genXmlFrmTbl.addActionListener(e -> xmlFromTable());
                //chckbxNewCheckBox.setBounds(149, 57, 235, 23);
                northSouth.add(genXmlFrmTbl);

                JButton openCSV = new JButton("Generate XML and fill table from CSV");
                openCSV.addActionListener(e -> {
                    CSV2XML csv = new CSV2XML(parent);
                    String xmlFile = csv.getXML();
                    createTable(xmlFile);
                });
                //chckbxNewCheckBox.setBounds(149, 57, 235, 23);
                northSouth.add(openCSV);

                north.add(northSouth, BorderLayout.SOUTH);

            }
            contentPanel.add(north, BorderLayout.NORTH);
        }
        //Center
        {
            JPanel center = new JPanel(new BorderLayout());
            //North
            {
                JPanel CenterNorth = new JPanel(new FlowLayout());
                //North
                {
                    //ID
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblId = new JLabel("ID");
                        //lblId.setBounds(20, 87, 46, 14);
                        ID.add(lblId);
                        idTb = new JTextField();
                        //	idTb.setBounds(10, 104, 68, 19);
                        ID.add(idTb);
                        idTb.setColumns(4);
                        CenterNorth.add(ID);
                    }
                    //Item
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblNewLabel = new JLabel("Item");
                        //lblNewLabel.setBounds(104, 87, 46, 14);
                        ID.add(lblNewLabel);
                        itemTb = new JTextField();
                        //itemTb.setBounds(88, 104, 141, 19);
                        ID.add(itemTb);
                        itemTb.setColumns(10);
                        CenterNorth.add(ID);
                    }
                    //Size
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblNewLabel_1 = new JLabel("Size");
                        //	lblNewLabel_1.setBounds(252, 87, 46, 14);
                        ID.add(lblNewLabel_1);
                        sizeTb = new JTextField();
                        //sizeTb.setBounds(239, 104, 138, 19);
                        ID.add(sizeTb);
                        sizeTb.setColumns(10);
                        CenterNorth.add(ID);
                    }
                    //Rate
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblNewLabel_2 = new JLabel("Price/Item");
                        //	lblNewLabel_2.setBounds(390, 87, 70, 14);
                        ID.add(lblNewLabel_2);
                        rateTb = new JTextField();
                        //rateTb.setBounds(387, 104, 97, 19);
                        ID.add(rateTb);
                        rateTb.setColumns(4);
                        CenterNorth.add(ID);
                    }
                    //Category
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblNewLabel_2 = new JLabel("Category");
                        //	lblNewLabel_2.setBounds(390, 87, 70, 14);
                        ID.add(lblNewLabel_2);
                        categoriesCmbx = new JComboBox();
                        categoriesTb = new JComboBox();
                        categoriesTb.insertItemAt("",0);
                        categoriesCmbx.insertItemAt("",0);
                        String browse = "Add Category";
                        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT NAME FROM Categories")) {
                            prep.execute();
                            try (ResultSet rs = prep.executeQuery()) {

                                while (rs.next()) {

                                    categoriesTb.addItem(rs.getString(1));
                                    categoriesCmbx.addItem(rs.getString(1));
                                }
                                ////DbInt.pCon.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        categoriesCmbx.addItem(browse);
                        categoriesCmbx.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                if ((e.getStateChange() == ItemEvent.SELECTED) && browse.equals(e.getItem())) {
                                    new AddCategory();
                                }
                            }
                        });
                        //rateTb.setBounds(387, 104, 97, 19);
                        ID.add(categoriesCmbx);
                        CenterNorth.add(ID);
                    }
                    JButton btnNewButton = new JButton("Add");
                    btnNewButton.addActionListener(e -> {
                        int count = tableModel.getRowCount() + 1;
                        tableModel.addRow(new Object[]{idTb.getText(), itemTb.getText(), sizeTb.getText(), rateTb.getText()});
                    });
                    //btnNewButton.setBounds(484, 105, 57, 19);
                    CenterNorth.add(btnNewButton);

                    JButton btnGenerateTableFrom = new JButton("Generate table from XML");
                    btnGenerateTableFrom.addActionListener(e -> {

                        FileDialog f = new FileDialog((Frame) getParent(), "Open", FileDialog.LOAD);
                        f.setVisible(true);
                        if (f.getFile() != null) {
                            createTable(f.getDirectory() + f.getFile());
                        }

                    });
                    //btnGenerateTableFrom.setBounds(387, 57, 154, 23);
                    CenterNorth.add(btnGenerateTableFrom);

                }

                center.add(CenterNorth, BorderLayout.NORTH);
                //contentPanel.add(center, BorderLayout.CENTER);
            }
            //Center
            {
                JScrollPane scrollPane = new JScrollPane();
                //scrollPane.setBounds(0, 125, 541, 315);
                center.add(scrollPane, BorderLayout.CENTER);

                ProductTable = new JTable();
                ProductTable.setFillsViewportHeight(true);
                ProductTable.setColumnSelectionAllowed(true);
                ProductTable.setCellSelectionEnabled(true);
                tableModel = new DefaultTableModel(new Object[]{"ID", "Item", "Size", "Price/Item", "Category"}, 0);
                ProductTable.setModel(tableModel);
                ProductTable.getColumnModel().getColumn(0).setPreferredWidth(15);
                ProductTable.getColumnModel().getColumn(0).setMinWidth(10);
                scrollPane.setViewportView(ProductTable);
            }
            contentPanel.add(center, BorderLayout.CENTER);
        }
        //South
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(e -> {
                    if (chkboxCreateDatabase.isSelected()) {
                        CreateDb();
                    } else {
                        addYear();

                    }

                    dispose();
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(e -> dispose());
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Create the dialog.
     */
    public AddYear(String year) {
        parent = this;
        setSize(700, 500);
        getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());

        //North
        {
            JPanel north = new JPanel(new BorderLayout());

            //South
            {
                JPanel northSouth = new JPanel(new FlowLayout());
                {
                    JLabel lblYear = new JLabel("Year:");
                    lblYear.setFont(new Font("Tahoma", Font.PLAIN, 16));
                    //lblYear.setBounds(10, 0, 89, 40);
                    northSouth.add(lblYear);
                }
                {
                    yearText = new JTextField();
                    yearText.setHorizontalAlignment(SwingConstants.CENTER);
                    yearText.setFont(new Font("Tahoma", Font.PLAIN, 26));
                    //yearText.setBounds(73, 0, 77, 32);
                    northSouth.add(yearText);
                    yearText.setColumns(4);
                    yearText.setText(year);
                    yearText.setEditable(false);
                }

                JButton genXmlFrmTbl = new JButton("Generate XML file from Table below");
                genXmlFrmTbl.addActionListener(e -> xmlFromTable());
                //chckbxNewCheckBox.setBounds(149, 57, 235, 23);
                northSouth.add(genXmlFrmTbl);


                north.add(northSouth, BorderLayout.SOUTH);

            }
            contentPanel.add(north, BorderLayout.NORTH);
        }
        //Center
        {
            JPanel center = new JPanel(new BorderLayout());
            //North
            {
                JPanel CenterNorth = new JPanel(new FlowLayout());
                //North
                {
                    //ID
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblId = new JLabel("ID");
                        //lblId.setBounds(20, 87, 46, 14);
                        ID.add(lblId);
                        idTb = new JTextField();
                        //	idTb.setBounds(10, 104, 68, 19);
                        ID.add(idTb);
                        idTb.setColumns(4);
                        CenterNorth.add(ID);
                    }
                    //Item
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblNewLabel = new JLabel("Item");
                        //lblNewLabel.setBounds(104, 87, 46, 14);
                        ID.add(lblNewLabel);
                        itemTb = new JTextField();
                        //itemTb.setBounds(88, 104, 141, 19);
                        ID.add(itemTb);
                        itemTb.setColumns(10);
                        CenterNorth.add(ID);
                    }
                    //Size
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblNewLabel_1 = new JLabel("Size");
                        //	lblNewLabel_1.setBounds(252, 87, 46, 14);
                        ID.add(lblNewLabel_1);
                        sizeTb = new JTextField();
                        //sizeTb.setBounds(239, 104, 138, 19);
                        ID.add(sizeTb);
                        sizeTb.setColumns(10);
                        CenterNorth.add(ID);
                    }
                    //Rate
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblNewLabel_2 = new JLabel("Price/Item");
                        //	lblNewLabel_2.setBounds(390, 87, 70, 14);
                        ID.add(lblNewLabel_2);
                        rateTb = new JTextField();
                        //rateTb.setBounds(387, 104, 97, 19);
                        ID.add(rateTb);
                        rateTb.setColumns(4);
                        CenterNorth.add(ID);
                    }
                    //Category
                    {
                        JPanel ID = new JPanel();
                        ID.setLayout(new BoxLayout(ID, BoxLayout.PAGE_AXIS));
                        JLabel lblNewLabel_2 = new JLabel("Category");
                        //	lblNewLabel_2.setBounds(390, 87, 70, 14);
                        ID.add(lblNewLabel_2);
                        categoriesCmbx = new JComboBox();
                        categoriesTb = new JComboBox();
                        categoriesTb.insertItemAt("",0);
                        categoriesCmbx.insertItemAt("",0);
                        String browse = "Add Category";
                        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT NAME FROM Categories")) {
                            prep.execute();
                            try (ResultSet rs = prep.executeQuery()) {

                                while (rs.next()) {

                                    categoriesTb.addItem(rs.getString(1));
                                    categoriesCmbx.addItem(rs.getString(1));
                                }
                                ////DbInt.pCon.close();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        categoriesCmbx.addItem(browse);
                        categoriesCmbx.addItemListener(e -> {
                            if ((e.getStateChange() == ItemEvent.SELECTED) && browse.equals(e.getItem())) {
                                new AddCategory();
                            }
                        });
                        //rateTb.setBounds(387, 104, 97, 19);
                        ID.add(categoriesCmbx);
                        CenterNorth.add(ID);
                    }
                    JButton btnNewButton = new JButton("Add");
                    btnNewButton.addActionListener(e -> {
                        int count = tableModel.getRowCount() + 1;
                        tableModel.addRow(new Object[]{idTb.getText(), itemTb.getText(), sizeTb.getText(), rateTb.getText()});
                    });
                    //btnNewButton.setBounds(484, 105, 57, 19);
                    CenterNorth.add(btnNewButton);

                    JButton btnGenerateTableFrom = new JButton("Generate table from XML");
                    btnGenerateTableFrom.addActionListener(e -> {

                        FileDialog f = new FileDialog((Frame) getParent(), "Open", FileDialog.LOAD);
                        f.setVisible(true);
                        if (f.getFile() != null) {
                            createTable(f.getDirectory() + f.getFile());
                        }

                    });
                    //btnGenerateTableFrom.setBounds(387, 57, 154, 23);
                    CenterNorth.add(btnGenerateTableFrom);

                }

                center.add(CenterNorth, BorderLayout.NORTH);
                //contentPanel.add(center, BorderLayout.CENTER);
            }
            //Center
            {
                JScrollPane scrollPane = new JScrollPane();
                //scrollPane.setBounds(0, 125, 541, 315);
                center.add(scrollPane, BorderLayout.CENTER);

                ProductTable = new JTable();
                ProductTable.setFillsViewportHeight(true);
                ProductTable.setColumnSelectionAllowed(true);
                ProductTable.setCellSelectionEnabled(true);
                tableModel = new DefaultTableModel(new Object[]{"ID", "Item", "Size", "Price/Item", "Category"}, 0);
                ProductTable.setModel(tableModel);
                ProductTable.getColumnModel().getColumn(0).setPreferredWidth(15);
                ProductTable.getColumnModel().getColumn(0).setMinWidth(10);
                TableColumn categoryColumn = ProductTable.getColumnModel().getColumn(4);
                categoryColumn.setCellEditor(new DefaultCellEditor(categoriesCmbx));


                scrollPane.setViewportView(ProductTable);
            }
            contentPanel.add(center, BorderLayout.CENTER);
        }
        //South
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(e -> {
                        updateDb(year);


                    dispose();
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(e -> dispose());
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        updateDb = true;
        fillTable();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Launch the application.
     */
    public static void main(String... args) {
        try {

            AddYear dialog = new AddYear();
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates Database for the year specified.
     */
    private void CreateDb() {
        String year = yearText.getText();
        DbInt.createDb(year);
        //Create Tables
        DbInt.writeData(year, "CREATE TABLE CUSTOMERS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),NAME varchar(255),ADDRESS varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), PHONE varchar(255), ORDERID varchar(255), PAID varchar(255),DELIVERED varchar(255), EMAIL varchar(255), DONATION VARCHAR(255))");
        DbInt.writeData(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255), Category VARCHAR(255))");
        DbInt.writeData(year, "CREATE TABLE TOTALS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),DONATIONS varchar(255),LG varchar(255),LP varchar(255),MULCH varchar(255),TOTAL varchar(255),CUSTOMERS varchar(255),COMMISSIONS varchar(255),GRANDTOTAL varchar(255))");
        DbInt.writeData(year, "CREATE TABLE Residence(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Address varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), Action varchar(255))");

        //Insert products into Product table
        String col = "";
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
            DbInt.writeData(year, String.format("INSERT INTO PRODUCTS(ID, PName, Unit, Size, Category) VALUES ('%s','%s','%s','%s', '%s')", ProductTable.getModel().getValueAt(i, 0).toString().replaceAll("'", "''"), ProductTable.getModel().getValueAt(i, 1).toString().replaceAll("'", "''"), ProductTable.getModel().getValueAt(i, 3).toString().replaceAll("'", "''"), ProductTable.getModel().getValueAt(i, 2).toString().replaceAll("'", "''"), ProductTable.getModel().getValueAt(i, 4).toString().replaceAll("'", "''")));
        }
        DbInt.writeData(year, String.format("CREATE TABLE ORDERS(OrderID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NAME VARChAR(255) %s)", col));
        DbInt.writeData(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES('0','0','0','0','0','0','0','0')");
        DbInt.writeData("Set", String.format("INSERT INTO YEARS VALUES(%s, '%s')", year, year));

    }

    private void updateDb(String year) {
        //Delete Year Customer table

        try (PreparedStatement addCol = DbInt.getPrep(year, "DROP TABLE \"PRODUCTS\"")) {
            addCol.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Recreate Year Customer table

        try (PreparedStatement addCol = DbInt.getPrep(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255), Category VARCHAR(255))")) {
            addCol.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Insert products into Product table
        String col = "";
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
            DbInt.writeData(year, String.format("INSERT INTO PRODUCTS(ID, PName, Unit, Size, Category) VALUES ('%s','%s','%s','%s', '%s')", ProductTable.getModel().getValueAt(i, 0).toString().replaceAll("'", "''"), ProductTable.getModel().getValueAt(i, 1).toString().replaceAll("'", "''"), ProductTable.getModel().getValueAt(i, 3).toString().replaceAll("'", "''"), ProductTable.getModel().getValueAt(i, 2).toString().replaceAll("'", "''"), ProductTable.getModel().getValueAt(i, 4).toString().replaceAll("'", "''")));
        }
    }

    private void addYear() {
        DbInt.writeData("Set", String.format("INSERT INTO YEARS(ID, YEARS) VALUES('%s', '%s')", yearText.getText(), yearText.getText()));
    }

    /**
     * Parses XML file to insert into products table on screen
     *
     * @param FLoc the location of the XML file
     */
    private void createTable(String FLoc) {
        try {

            File fXmlFile = new File(FLoc);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("Products");

            Object[][] rows = new Object[nList.getLength()][4];

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


                    rows[temp][0] = eElement.getElementsByTagName("ProductID").item(0).getTextContent();
                    rows[temp][1] = eElement.getElementsByTagName("ProductName").item(0).getTextContent();
                    rows[temp][2] = eElement.getElementsByTagName("Size").item(0).getTextContent();
                    rows[temp][3] = eElement.getElementsByTagName("UnitCost").item(0).getTextContent();
                    rows[temp][4] = eElement.getElementsByTagName("Category").item(0).getTextContent();
                    //TODO add category to DB if not present

                    //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};
                    tableModel = new DefaultTableModel(
                            rows,
                            new String[]{
                                    "ID", "Item", "Size", "Price/Item", "Category"
                            }
                    ) {

                        boolean[] columnEditables = new boolean[]{
                                false, false, false, true, false, true
                        };

                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return columnEditables[column];
                        }
                    };
                    ProductTable.setModel(tableModel);


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates an XML file from the table
     *
     * @param SavePath Path to save the created XML file
     */
    private void createXML(String SavePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;

            docBuilder = docFactory.newDocumentBuilder();


            // root elements
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("LawnGarden");
            doc.appendChild(rootElement);

            // staff elements


            // set attribute to staff element
            for (int i = 0; i < ProductTable.getRowCount(); i++) {

                Element staff = doc.createElement("Products");
                rootElement.appendChild(staff);
                Attr attr = doc.createAttribute("id");
                attr.setValue(Integer.toString(i));
                staff.setAttributeNode(attr);

                //ProductID elements
                Element ProductID = doc.createElement("ProductID");
                ProductID.appendChild(doc.createTextNode(ProductTable.getModel().getValueAt(i, 0).toString()));
                staff.appendChild(ProductID);

                // Prodcut Name elements
                Element ProductName = doc.createElement("ProductName");
                ProductName.appendChild(doc.createTextNode(ProductTable.getModel().getValueAt(i, 1).toString()));
                staff.appendChild(ProductName);

                // Unit COst elements
                Element UnitCost = doc.createElement("UnitCost");
                UnitCost.appendChild(doc.createTextNode(ProductTable.getModel().getValueAt(i, 3).toString()));
                staff.appendChild(UnitCost);

                // Size elements
                Element Size = doc.createElement("Size");
                Size.appendChild(doc.createTextNode(ProductTable.getModel().getValueAt(i, 2).toString()));
                staff.appendChild(Size);
                // Category elements
                Element category = doc.createElement("Category");
                category.appendChild(doc.createTextNode(ProductTable.getModel().getValueAt(i, 4).toString()));
                staff.appendChild(category);
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            Source source = new DOMSource(doc);
            Result result = new StreamResult(new FileOutputStream(SavePath));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");
        } catch (ParserConfigurationException | TransformerException | FileNotFoundException e) {

            e.printStackTrace();

        }
    }

    /**
     * Fills the table from a DB table
     */
    private void fillTable() {
        String year = yearText.getText();
        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"

        //Variables for inserting info into table

        String[] toGet = {"ID", "PNAME", "SIZE", "UNIT", "Category"};
        List<ArrayList<String>> ProductInfoArray = new ArrayList<ArrayList<String>>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()
        ) {
            //Run through Data set and add info to ProductInfoArray
            for (int i = 0; i < 5; i++) {
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
            rows[i][4] = ProductInfoArray.get(4).get(i);


        }
        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};

        //Define table properties
        ProductTable.setModel(new MyDefaultTableModel(rows));
        TableColumn categoryColumn = ProductTable.getColumnModel().getColumn(4);
        categoryColumn.setCellEditor(new DefaultCellEditor(categoriesTb));
    }

    private void tablefromDb() {

        fillTable();
    }

    private void xmlFromTable() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().getName().endsWith(".xml")) {
                createXML(chooser.getSelectedFile().getAbsolutePath());
            } else {
                createXML(chooser.getSelectedFile().getAbsolutePath() + ".xml");

            }
        }

    }

    private static class MyDefaultTableModel extends DefaultTableModel {

        boolean[] columnEditables;

        public MyDefaultTableModel(Object[][] rows) {
            super(rows, new String[]{
                    "ID", "Product Name", "Size", "Price/Item", "Category"
            });
            columnEditables = new boolean[]{
                    true, true, true, true, true
            };
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return columnEditables[column];
        }
    }
}
