import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class AddYear extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private final JButton genXmlFrmTbl;
    private final JButton fillTblFrmDb;
    private final JButton openCSV;
    private final JCheckBox chkboxCreateDatabase;
    private JTextField textField;
    private JTable table;
    private JTextField itemTb;
    private JTextField sizeTb;
    private JTextField rateTb;
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
                    textField = new JTextField();
                    textField.setHorizontalAlignment(SwingConstants.CENTER);
                    textField.setFont(new Font("Tahoma", Font.PLAIN, 26));
                    //textField.setBounds(73, 0, 77, 32);
                    northNorth.add(textField);
                    textField.setColumns(4);
                    textField.setText(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
                }

                {
                    fillTblFrmDb = new JButton("Fill table from pre-existing Database");
                    fillTblFrmDb.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            tablefromDb();
                        }
                    });
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


                genXmlFrmTbl = new JButton("Generate XML file from Table below");
                genXmlFrmTbl.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        xmlFromTable();
                    }
                });
                //chckbxNewCheckBox.setBounds(149, 57, 235, 23);
                northSouth.add(genXmlFrmTbl);

                openCSV = new JButton("Generate XML and fill table from CSV");
                openCSV.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        CSV2XML csv = new CSV2XML(parent);
                        String xmlFile = csv.getXML();
                        createTable(xmlFile);
                    }
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
                    JButton btnNewButton = new JButton("Add");
                    btnNewButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            int count = tableModel.getRowCount() + 1;
                            tableModel.addRow(new Object[]{idTb.getText(), itemTb.getText(), sizeTb.getText(), rateTb.getText()});
                        }
                    });
                    //btnNewButton.setBounds(484, 105, 57, 19);
                    CenterNorth.add(btnNewButton);

                    JButton btnGenerateTableFrom = new JButton("Generate table from XML");
                    btnGenerateTableFrom.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {

                            FileDialog f = new FileDialog((Frame) getParent(), "Open", FileDialog.LOAD);
                            f.setVisible(true);
                            if (!(f.getFile() == null)) {
                                createTable(f.getDirectory() + f.getFile());
                            }

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

                table = new JTable();
                table.setFillsViewportHeight(true);
                table.setColumnSelectionAllowed(true);
                table.setCellSelectionEnabled(true);
                tableModel = new DefaultTableModel(new Object[]{"ID", "Item", "Size", "Price/Item"}, 0);
                table.setModel(tableModel);
                table.getColumnModel().getColumn(0).setPreferredWidth(15);
                table.getColumnModel().getColumn(0).setMinWidth(10);
                scrollPane.setViewportView(table);
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
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (chkboxCreateDatabase.isSelected()) {
                            CreateDb();
                        } else {
                            addYear();

                        }

                        dispose();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {

            AddYear dialog = new AddYear();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CreateDb() {
        String year = textField.getText();
        DbInt.createDb(year);

        DbInt.writeData(year, "CREATE TABLE CUSTOMERS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),NAME varchar(255),ADDR varchar(255),PHONE varchar(255), ORDERID varchar(255), PAID varchar(255),DELIVERED varchar(255), EMAIL varchar(255), DONATION VARCHAR(255))");
        DbInt.writeData(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255))");
        DbInt.writeData(year, "CREATE TABLE TOTALS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),DONATIONS varchar(255),LG varchar(255),LP varchar(255),MULCH varchar(255),TOTAL varchar(255),CUSTOMERS varchar(255),COMMISSIONS varchar(255),GRANDTOTAL varchar(255))");
        DbInt.writeData(year, "CREATE TABLE Residence(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Address varchar(255),Action varchar(255))");

        String col = "";
        for (int i = 0; i < table.getRowCount(); i++) {
            col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
            DbInt.writeData(year, String.format("INSERT INTO PRODUCTS(ID, PName, Unit, Size) VALUES ('%s','%s','%s','%s')", table.getModel().getValueAt(i, 0).toString().replaceAll("'", "''"), table.getModel().getValueAt(i, 1).toString().replaceAll("'", "''"), table.getModel().getValueAt(i, 3).toString().replaceAll("'", "''"), table.getModel().getValueAt(i, 2).toString().replaceAll("'", "''")));
        }
        DbInt.writeData(year, String.format("CREATE TABLE ORDERS(OrderID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NAME VARChAR(255) %s)", col));
        DbInt.writeData(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES('0','0','0','0','0','0','0','0')");
        DbInt.writeData("Set", String.format("INSERT INTO YEARS VALUES(%s, '%s')", year, year));

    }

    private void addYear() {
        DbInt.writeData("Set", String.format("INSERT INTO YEARS(ID, YEARS) VALUES('%s', '%s')", textField.getText(), textField.getText()));
    }

    @SuppressWarnings("serial")
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


                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


                    rows[temp][0] = eElement.getElementsByTagName("ProductID").item(0).getTextContent();
                    rows[temp][1] = eElement.getElementsByTagName("ProductName").item(0).getTextContent();
                    rows[temp][2] = eElement.getElementsByTagName("Size").item(0).getTextContent();
                    rows[temp][3] = eElement.getElementsByTagName("UnitCost").item(0).getTextContent();


                    //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};
                    tableModel = new DefaultTableModel(
                            rows,
                            new String[]{
                                    "ID", "Item", "Size", "Price/Item"
                            }
                    ) {

                        boolean[] columnEditables = new boolean[]{
                                false, false, false, true, false
                        };

                        public boolean isCellEditable(int row, int column) {
                            return columnEditables[column];
                        }
                    };
                    table.setModel(tableModel);


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            for (int i = 0; i < table.getRowCount(); i++) {

                Element staff = doc.createElement("Products");
                rootElement.appendChild(staff);
                Attr attr = doc.createAttribute("id");
                attr.setValue(Integer.toString(i));
                staff.setAttributeNode(attr);

                Element firstname = doc.createElement("ProductID");
                firstname.appendChild(doc.createTextNode(table.getModel().getValueAt(i, 0).toString()));
                staff.appendChild(firstname);

                // lastname elements
                Element lastname = doc.createElement("ProductName");
                lastname.appendChild(doc.createTextNode(table.getModel().getValueAt(i, 1).toString()));
                staff.appendChild(lastname);

                // nickname elements
                Element nickname = doc.createElement("UnitCost");
                nickname.appendChild(doc.createTextNode(table.getModel().getValueAt(i, 3).toString()));
                staff.appendChild(nickname);

                // salary elements
                Element salary = doc.createElement("Size");
                salary.appendChild(doc.createTextNode(table.getModel().getValueAt(i, 2).toString()));
                staff.appendChild(salary);
            }


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileOutputStream(SavePath));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");
        } catch (ParserConfigurationException e) {

            e.printStackTrace();

        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
    }

    @SuppressWarnings("serial")
    private void fillTable() {
        String year = textField.getText().toString();
        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
        ArrayList<String> productID;
        ArrayList<String> productNames;
        ArrayList<String> Size;
        ArrayList<String> Unit;
        String toGet[] = {"ID", "PNAME", "SIZE", "UNIT"};
        String ret[][] = new String[4][];
        ArrayList<String> res = new ArrayList<String>();

        PreparedStatement prep = DbInt.getPrep(year, "SELECT ? FROM PRODUCTS");
        try {
            for (int i = 0; i < 4; i++) {
                prep.setString(1, toGet[i]);
                ResultSet rs = prep.executeQuery();

                while (rs.next()) {

                    res.add(rs.getString(1));

                }
                ret[i] = (String[]) res.toArray();
                ////DbInt.pCon.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        productID = new ArrayList(Arrays.asList(ret[0]));
        productNames = new ArrayList(Arrays.asList(ret[1]));
        Size = new ArrayList(Arrays.asList(ret[2]));
        Unit = new ArrayList(Arrays.asList(ret[3]));
        Object[][] rows = new Object[productNames.size()][4];

        for (int i = 0; i < productNames.size(); i++) {
            rows[i][0] = productID.get(i);
            rows[i][1] = productNames.get(i);
            rows[i][2] = Size.get(i);
            rows[i][3] = Unit.get(i);


        }
        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};
        tableModel = new DefaultTableModel(
                rows,
                new String[]{
                        "ID", "Item", "Size", "Price/Item"
                }
        ) {

            boolean[] columnEditables = new boolean[]{
                    false, false, false, true, false
            };

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        };
        table.setModel(tableModel);
    }

    private void tablefromDb() {

        fillTable();
    }

    private void xmlFromTable() {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(AddYear.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile().getName().endsWith(".xml")) {
                createXML(chooser.getSelectedFile().getAbsolutePath() + ".xml");
            } else {
                createXML(chooser.getSelectedFile().getAbsolutePath() + ".xml");

            }
        }

    }

}
