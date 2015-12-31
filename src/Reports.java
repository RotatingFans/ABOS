import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.codec.*;
import com.lowagie.text.pdf.codec.Base64;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;

import net.sf.saxon.s9api.*;


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.File;
import java.io.OutputStream;


import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

/**
 * Created by patrick on 12/24/15.
 */
public class Reports extends JDialog {
    private final JPanel contentPanel = new JPanel();
    JTabbedPane SteptabbedPane;
    private JTextField DbLoc;
    private JButton okButton;
    private JButton nextButton;
    private JPanel ReportInfo;
    private JButton cancelButton;
    private JComboBox cmbxReportType;
    private JComboBox cmbxYears = new JComboBox(new DefaultComboBoxModel<>());
    private JComboBox cmbxCustomers = new JComboBox(new DefaultComboBoxModel<>());
    private JTextField scoutName;
    private JTextField scoutStAddr;
    private JTextField scoutZip;
    private JTextField scoutTown;
    private JTextField scoutState;
    private JTextField scoutRank;
    private JTextField logoLoc;

    private String addrFormat;
    Object[][] rowDataF = new Object[0][];
    private double totL = 0;
    private double QuantL = 0;
    private String Splitting = "";
    private String repTitle = "";


    public Reports() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            new Reports();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SetBounds(X,Y,Width,Height)
    private void initUI() {
        setSize(450, 150);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());
        //North
        {
            SteptabbedPane = new JTabbedPane();
            //Report Type
            {
                JPanel ReportType = new JPanel(new FlowLayout());
                {
                    cmbxReportType = new JComboBox(new DefaultComboBoxModel<>());
                    cmbxReportType.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JComboBox comboBox = (JComboBox) actionEvent.getSource();

                            Object selected = comboBox.getSelectedItem();
                            if (cmbxReportType.getSelectedItem() != "") {
                                nextButton.setEnabled(true);
                            }

                        }
                    });
                    cmbxReportType.addItem("");
                    cmbxReportType.addItem("Year Totals");
                    cmbxReportType.addItem("Customer Year Totals");
                    cmbxReportType.addItem("Customer All-time Totals");

                    ReportType.add(cmbxReportType);
                }
                SteptabbedPane.addTab("Report Type", ReportType);

            }
            //Report Info
            {
                ReportInfo = new JPanel(new FlowLayout());
                {
                    cmbxYears.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JComboBox comboBox = (JComboBox) actionEvent.getSource();

                            Object selected = comboBox.getSelectedItem();
                            if (cmbxReportType.getSelectedIndex() == 2) {
                                if (cmbxYears.getSelectedItem() != "") {
                                    ArrayList<String> customersY = getCustomers(cmbxYears.getSelectedItem().toString());
                                    cmbxCustomers.removeAllItems();
                                    cmbxCustomers.addItem("");
                                    cmbxCustomers.setSelectedItem("");
                                    for (int i = 0; i < customersY.size(); i++) {
                                        cmbxCustomers.addItem(customersY.get(i));
                                    }
                                    cmbxCustomers.setEnabled(true);
                                }
                            } else if (cmbxYears.getSelectedItem() != "") {
                                nextButton.setEnabled(true);
                            }

                        }
                    });

                    cmbxCustomers.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JComboBox comboBox = (JComboBox) actionEvent.getSource();

                            Object selected = comboBox.getSelectedItem();
                            if (cmbxCustomers.getSelectedItem() != "") {
                                nextButton.setEnabled(true);
                            }

                        }
                    });
                    JLabel scoutNameL = new JLabel("Scout Name:");
                    JLabel scoutStAddrL = new JLabel("Scout Street Address:");
                    JLabel scoutZipL = new JLabel("Scout Zip:");
                    JLabel scoutTownL = new JLabel("Scout Town:");
                    JLabel scoutStateL = new JLabel("Scout State:");
                    JLabel scoutRankL = new JLabel("Scout Rank");
                    JLabel logoLocL = new JLabel("Logo Location:");

                    scoutName = new JTextField(20);
                    scoutStAddr = new JTextField(20);
                    scoutZip = new JTextField(5);
                    scoutTown = new JTextField(20);
                    scoutState = new JTextField(20);
                    scoutRank = new JTextField(20);
                    logoLoc = new JTextField(50);
                    scoutZip.addActionListener(new MyTextActionListener());
                    scoutZip.getDocument().addDocumentListener(new MyDocumentListener());
                    JButton logoButton = new JButton("...");
                    logoButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            //Creates a JFileChooser to select a directory to store the Databases
                            JFileChooser chooser = new JFileChooser();
                            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
                            chooser.setFileFilter(filter);
                            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                            int returnVal = chooser.showOpenDialog(Reports.this);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                logoLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                            }

                        }
                    });


                    ReportInfo.add(cmbxYears);
                    ReportInfo.add(cmbxCustomers);
                    ReportInfo.add(scoutNameL);
                    ReportInfo.add(scoutName);
                    ReportInfo.add(scoutStAddrL);
                    ReportInfo.add(scoutStAddr);
                    ReportInfo.add(scoutZipL);
                    ReportInfo.add(scoutZip);
                    ReportInfo.add(scoutTownL);
                    ReportInfo.add(scoutTown);
                    ReportInfo.add(scoutStateL);
                    ReportInfo.add(scoutState);
                    ReportInfo.add(scoutRankL);
                    ReportInfo.add(scoutRank);
                    ReportInfo.add(logoLocL);
                    ReportInfo.add(logoLoc);
                    ReportInfo.add(logoButton);
                }
                SteptabbedPane.addTab("Report Info", ReportInfo);

            }
            //Report Preview
            {
                JPanel ReportPreview = new JPanel(new FlowLayout());
                {

                }
                SteptabbedPane.addTab("Report Type", ReportPreview);

            }

            contentPanel.add(SteptabbedPane, BorderLayout.CENTER);
        }


        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                nextButton = new JButton("Next -->");
                nextButton.setEnabled(false);
                buttonPane.add(nextButton);
                getRootPane().setDefaultButton(nextButton);
            }
            {
                okButton = new JButton("OK");
                okButton.setEnabled(false);
                buttonPane.add(okButton);
            }
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    dispose();
                }
            });
            nextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    switch (SteptabbedPane.getSelectedIndex()) {
                        case 0:
                            updateCombos();
                            break;
                        case 1:
                            switch (cmbxReportType.getSelectedIndex()) {
                                case 1:
                                    repTitle = "Year of " + cmbxYears.getSelectedItem().toString();
                                    Splitting = "Year:";
                                    break;
                                case 2:
                                    repTitle = cmbxCustomers.getSelectedItem().toString() + " " + cmbxYears.getSelectedItem().toString();
                                    Splitting = "";
                                    break;
                                case 3:
                                    repTitle = "All orders of " + cmbxCustomers.getSelectedItem().toString();
                                    Splitting = "Year:";
                                    break;

                            }
                            addrFormat = scoutTown.getText() + " " + scoutState.getText() + ", " + scoutZip.getText();
                            convert();
                            break;
                        case 2:
                            break;


                    }
                    SteptabbedPane.setSelectedIndex(SteptabbedPane.getSelectedIndex() + 1);

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
    }

    void convert() {
        java.util.List<String> headers = new ArrayList<String>(5);


        BufferedReader reader = null;

        try {

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

            Document doc = domBuilder.newDocument();
            // Root element
            Element rootElement = doc.createElement("LawnGardenReports");
            doc.appendChild(rootElement);
            {
                //Info Elements
                Element info = doc.createElement("info");
                rootElement.appendChild(info);
                // Scoutname elements
                {
                    Element ScoutName = doc.createElement("name");
                    ScoutName.appendChild(doc.createTextNode(scoutName.getText()));
                    info.appendChild(ScoutName);
                }
                // StreetAddress elements
                {
                    Element StreetAddress = doc.createElement("streetAddress");
                    StreetAddress.appendChild(doc.createTextNode(scoutStAddr.getText()));
                    info.appendChild(StreetAddress);
                }
                // City elements
                {
                    Element city = doc.createElement("city");
                    city.appendChild(doc.createTextNode(addrFormat));
                    info.appendChild(city);
                }
                // Rank elements
                {
                    Element rank = doc.createElement("rank");
                    rank.appendChild(doc.createTextNode(scoutRank.getText()));
                    info.appendChild(rank);
                }
                // Logo elements
                {
                    Element logo = doc.createElement("logo");
                    logo.appendChild(doc.createTextNode(logoLoc.getText()));
                    info.appendChild(logo);
                }
                // ReportTitle elements
                {
                    Element reportTitle = doc.createElement("reportTitle");
                    reportTitle.appendChild(doc.createTextNode(repTitle));
                    info.appendChild(reportTitle);
                }
                // Splitter elements
                {
                    Element splitting = doc.createElement("splitting");
                    splitting.appendChild(doc.createTextNode(Splitting));
                    info.appendChild(splitting);
                }
                // TotalCost elements
                {
                    Element TotalCost = doc.createElement("TotalCost");
                    TotalCost.appendChild(doc.createTextNode(Double.toString(totL)));
                    info.appendChild(TotalCost);
                }
                // TotalQuantity elements
                {
                    Element TotalQuantity = doc.createElement("totalQuantity");
                    TotalQuantity.appendChild(doc.createTextNode(Double.toString(QuantL)));
                    info.appendChild(TotalQuantity);
                }
            }
            {
                //Column Elements
                Element columns = doc.createElement("columns");
                rootElement.appendChild(columns);
                String[] Columns = {"ID","Name","Unit Size", "Unit Cost", "Quantity", "Extended Price"};
                for (int i=0; i<Columns.length; i++) {
                    //Column
                    {
                        Element columnName = doc.createElement("column");
                        Element cName = doc.createElement("name");
                        cName.appendChild(doc.createTextNode(Columns[i]));
                        columnName.appendChild(cName);
                        columns.appendChild(columnName);
                    }
                }
            }
            switch (cmbxReportType.getSelectedItem().toString()) {

                case "Year Totals":
                    fillTable(cmbxYears.getSelectedItem().toString());
                {
                    //Product Elements
                    Element products = doc.createElement("customerYear");
                    rootElement.appendChild(products);
                    //YearTitle
                    {
                        Element title = doc.createElement("title");
                        title.appendChild(doc.createTextNode(cmbxYears.getSelectedItem().toString()));
                        products.appendChild(title);
                    }

                    for (int p = 0; p < rowDataF.length; p++) {
                        {
                            Element Product = doc.createElement("Product");
                            products.appendChild(Product);
                            {
                                Element ID = doc.createElement("ID");
                                ID.appendChild(doc.createTextNode(rowDataF[p][0].toString()));
                                Product.appendChild(ID);
                            }
                            {
                                Element Name = doc.createElement("Name");
                                Name.appendChild(doc.createTextNode(rowDataF[p][1].toString()));
                                Product.appendChild(Name);
                            }
                            {
                                Element Size = doc.createElement("Size");
                                Size.appendChild(doc.createTextNode(rowDataF[p][2].toString()));
                                Product.appendChild(Size);
                            }
                            {
                                Element UnitCost = doc.createElement("UnitCost");
                                UnitCost.appendChild(doc.createTextNode(rowDataF[p][3].toString()));
                                Product.appendChild(UnitCost);
                            }
                            {
                                Element Quantity = doc.createElement("Quantity");
                                Quantity.appendChild(doc.createTextNode(rowDataF[p][4].toString()));
                                Product.appendChild(Quantity);
                            }
                            {
                                Element TotalCost = doc.createElement("TotalCost");
                                TotalCost.appendChild(doc.createTextNode(rowDataF[p][5].toString()));
                                Product.appendChild(TotalCost);
                            }
                        }
                    }
                }
                    break;
                case "Customer Year Totals":
                    fillTable(cmbxYears.getSelectedItem().toString(), cmbxCustomers.getSelectedItem().toString());
                {
                    //Product Elements
                    Element products = doc.createElement("customerYear");
                    rootElement.appendChild(products);
                    //YearTitle
                    {
                        Element title = doc.createElement("title");
                        title.appendChild(doc.createTextNode(cmbxYears.getSelectedItem().toString()));
                        products.appendChild(title);
                    }

                    for (int p = 0; p < rowDataF.length; p++) {
                        {
                            Element Product = doc.createElement("Product");
                            products.appendChild(Product);
                            {
                                Element ID = doc.createElement("ID");
                                ID.appendChild(doc.createTextNode(rowDataF[p][0].toString()));
                                Product.appendChild(ID);
                            }
                            {
                                Element Name = doc.createElement("Name");
                                Name.appendChild(doc.createTextNode(rowDataF[p][1].toString()));
                                Product.appendChild(Name);
                            }
                            {
                                Element Size = doc.createElement("Size");
                                Size.appendChild(doc.createTextNode(rowDataF[p][2].toString()));
                                Product.appendChild(Size);
                            }
                            {
                                Element UnitCost = doc.createElement("UnitCost");
                                UnitCost.appendChild(doc.createTextNode(rowDataF[p][3].toString()));
                                Product.appendChild(UnitCost);
                            }
                            {
                                Element Quantity = doc.createElement("Quantity");
                                Quantity.appendChild(doc.createTextNode(rowDataF[p][4].toString()));
                                Product.appendChild(Quantity);
                            }
                            {
                                Element TotalCost = doc.createElement("TotalCost");
                                TotalCost.appendChild(doc.createTextNode(rowDataF[p][5].toString()));
                                Product.appendChild(TotalCost);
                            }
                        }
                    }
                }
                    break;
                case "Customer All-time Totals":
                    String Qname = cmbxCustomers.getSelectedItem().toString();
                    ArrayList<String> customerYears = new ArrayList<>();
                    ArrayList<String> years = getYears();
                    for (int i = 0; i < years.size(); i++) {
                        PreparedStatement prep = DbInt.getPrep(years.get(i), "SELECT NAME FROM Customers WHERE NAME=?");
                        try {

                            prep.setString(1,Qname);
                            ResultSet rs = prep.executeQuery();

                            while (rs.next()) {
                                    customerYears.add(years.get(i).toString());
                                {
                                    //Product Elements
                                    Element products = doc.createElement("customerYear");
                                    rootElement.appendChild(products);
                                    //YearTitle
                                    {
                                        Element title = doc.createElement("title");
                                        title.appendChild(doc.createTextNode(years.get(i).toString()));
                                        products.appendChild(title);
                                    }
                                    fillTable(years.get(i).toString(), cmbxCustomers.getSelectedItem().toString());

                                    for (int p = 0; p < rowDataF.length; p++) {
                                        {
                                            Element Product = doc.createElement("Product");
                                            products.appendChild(Product);
                                            {
                                                Element ID = doc.createElement("ID");
                                                ID.appendChild(doc.createTextNode(rowDataF[p][0].toString()));
                                                Product.appendChild(ID);
                                            }
                                            {
                                                Element Name = doc.createElement("Name");
                                                Name.appendChild(doc.createTextNode(rowDataF[p][1].toString()));
                                                Product.appendChild(Name);
                                            }
                                            {
                                                Element Size = doc.createElement("Size");
                                                Size.appendChild(doc.createTextNode(rowDataF[p][2].toString()));
                                                Product.appendChild(Size);
                                            }
                                            {
                                                Element UnitCost = doc.createElement("UnitCost");
                                                UnitCost.appendChild(doc.createTextNode(rowDataF[p][3].toString()));
                                                Product.appendChild(UnitCost);
                                            }
                                            {
                                                Element Quantity = doc.createElement("Quantity");
                                                Quantity.appendChild(doc.createTextNode(rowDataF[p][4].toString()));
                                                Product.appendChild(Quantity);
                                            }
                                            {
                                                Element TotalCost = doc.createElement("TotalCost");
                                                TotalCost.appendChild(doc.createTextNode(rowDataF[p][5].toString()));
                                                Product.appendChild(TotalCost);
                                            }
                                        }
                                    }
                                }

                            }
                            ////DbInt.pCon.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            ByteArrayOutputStream baos = null;
            OutputStreamWriter osw = null;

            try {
                baos = new ByteArrayOutputStream();
                osw = new OutputStreamWriter(baos);

                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                Source src = new DOMSource(doc);
                Result result = new StreamResult(osw);
                aTransformer.transform(src, result);

                osw.flush();
                System.out.println(new String(baos.toByteArray()));
                OutputStream outStream = null;

                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                    String tmpDirectoryOp = System.getProperty("java.io.tmpdir");
                    File tmpDirectory = new File(tmpDirectoryOp);
                    File fstream = File.createTempFile("LGReport" + timeStamp, ".xml" , tmpDirectory);
                    outStream = new FileOutputStream(fstream);

                    // writing bytes in to byte output stream

                    baos.writeTo(outStream);
                    //fstream.deleteOnExit();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    outStream.close();
                }


            } catch (Exception exp) {
                exp.printStackTrace();
            } finally {
                try {
                    osw.close();
                } catch (Exception e) {
                }
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
            transf();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void fillTable(String year, String name) {

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
        Object[][] rows = new Object[ProductInfoArray.get(2).size()][6];



        String OrderID = DbInt.getCustInf(year, name, "ORDERID");
        //Defines Arraylist of order quanitities
        ArrayList<String> OrderQuantities = new ArrayList<String>();
        int noVRows = 0;
        //Fills OrderQuantities Array
        for (int i = 0; i < ProductInfoArray.get(2).size(); i++) {

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


            if (quant > 0) {

                rows[noVRows][0] = ProductInfoArray.get(0).get(i);
                rows[noVRows][1] = ProductInfoArray.get(1).get(i);
                rows[noVRows][2] = ProductInfoArray.get(2).get(i);
                rows[noVRows][3] = ProductInfoArray.get(3).get(i);
                rows[noVRows][4] = quant;
                rows[noVRows][5] = quant * Double.parseDouble(ProductInfoArray.get(3).get(i).replaceAll("\\$", ""));
                totL = totL + (quant * Double.parseDouble(ProductInfoArray.get(3).get(i).replaceAll("\\$", "")));
                QuantL = QuantL + quant;

                noVRows++;

            }
        }
        //Re create rows to remove blank rows
        rowDataF = new Object[noVRows][6];

        for (int i = 0; i <= noVRows - 1; i++) {
            rowDataF[i][0] = rows[i][0];//Product ID
            rowDataF[i][1] = rows[i][1];//Product Name
            rowDataF[i][2] = rows[i][2];//Unit Size
            rowDataF[i][3] = rows[i][3];//Unit Cost
            rowDataF[i][4] = rows[i][4];//Quantity
            rowDataF[i][5] = rows[i][5]; //cost
        }



    }
    private void fillTable(String year) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }
        Connection con = null;
        Statement st = null;
        ResultSet Order = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", new Config().getDbLoc(), year);
        System.setProperty("derby.system.home",
                new Config().getDbLoc());

        int colO = DbInt.getNoCol(year, "ORDERS") - 2;
        final Object[][] rowData = new Object[colO][6];

        int noRows = 0;
        int productsNamed = 0;
        try {


            con = DriverManager.getConnection(url);
            st = con.createStatement();
            Order = st.executeQuery("SELECT * FROM ORDERS");


            ResultSetMetaData rsmd = Order.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            while (Order.next()) {
                if (productsNamed == 0) {
                    //loop through columns
                    for (int c = 3; c <= columnsNumber; c++) {
                        //Get ID of product
                        ArrayList<String> IDL = GetProductInfo("ID", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String ID = IDL.get(IDL.size() - 1);
                        //Get Name of product
                        ArrayList<String> productL = GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String product = productL.get(productL.size() - 1);
                        //Get Name of product
                        ArrayList<String> sizeL = GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String size = sizeL.get(sizeL.size() - 1);
                        //Get unit cost of product
                        ArrayList<String> UnitL = GetProductInfo("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String Unit = UnitL.get(productL.size() - 1);
                        //Get Quantity ordered
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);
                        //Calculate total price and overall Total
                        double TPrice = UnitD * quantityD;
                        totL = totL + TPrice;
                        QuantL = QuantL + quantityD;

                        rowData[noRows][0] = ID;
                        rowData[noRows][1] = product;
                        rowData[noRows][2] = size;
                        rowData[noRows][3] = Unit;
                        rowData[noRows][4] = quantity;
                        rowData[noRows][5] = TPrice;

                        noRows = noRows + 1;


                    }
                    productsNamed = productsNamed + 1;
                } else {
                    noRows = 0;
                    for (int c = 3; c <= columnsNumber; c++) {

                        //Get Name of product
                        ArrayList<String> productL = GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        //Get unit cost of product
                        ArrayList<String> UnitL = GetProductInfo("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String Unit = UnitL.get(productL.size() - 1);
                        //Get Quantity ordered
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);
                        //Calculate total price and overall Total
                        double TPrice = UnitD * quantityD;
                        totL = totL + TPrice;
                        QuantL = QuantL + quantityD;


                        rowData[noRows][4] = Double.parseDouble(rowData[noRows][4].toString()) + quantityD;
                        rowData[noRows][5] = Double.parseDouble(rowData[noRows][5].toString()) + TPrice;
                        noRows = noRows + 1;

                    }
                }
            }
            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        } finally {

            try {
                if (Order != null) {
                    Order.close();
                    Order = null;
                }
                if (st != null) {
                    st.close();
                    st = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DbInt.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        //Limit array to only rows that have ordered stuff
        final Object[][] rowDataExclude0 = new Object[noRows][6];
        int NumNonEmptyRows = 0;
        for (int i = 0; i <= noRows - 1; i++) {
            if (Double.parseDouble(rowData[i][4].toString()) > 0) {
                rowDataExclude0[NumNonEmptyRows][0] = rowData[i][0];
                rowDataExclude0[NumNonEmptyRows][1] = rowData[i][1];
                rowDataExclude0[NumNonEmptyRows][2] = rowData[i][2];
                rowDataExclude0[NumNonEmptyRows][3] = rowData[i][3];
                rowDataExclude0[NumNonEmptyRows][4] = rowData[i][4];
                rowDataExclude0[NumNonEmptyRows][5] = rowData[i][5];

                NumNonEmptyRows++;
            }
        }
        //Only show non whitespace rows
        rowDataF = new Object[NumNonEmptyRows][6];
        for (int i = 0; i <= NumNonEmptyRows - 1; i++) {

            rowDataF[i][0] = rowDataExclude0[i][0];//ID
            rowDataF[i][1] = rowDataExclude0[i][1];//Name
            rowDataF[i][2] = rowDataExclude0[i][2];//UnitSize
            rowDataF[i][3] = rowDataExclude0[i][3];//UnitCost
            rowDataF[i][4] = rowDataExclude0[i][4];//Quantity
            rowDataF[i][5] = rowDataExclude0[i][5];//Tcost




        }

    }

    /**
     * Get info on a product
     *
     * @param info the info to be retrieved
     * @param PID  The ID of the product to get info for
     * @return The info of the product specified
     */
    private ArrayList<String> GetProductInfo(String info, String PID, String year) {
        ArrayList<String> ret = new ArrayList<String>();

        PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS WHERE PID=?");
        try {


            prep.setString(1, PID);

            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret.add(rs.getString(info));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
    private void updateCombos() {
        ArrayList<String> years = getYears();

        switch (cmbxReportType.getSelectedItem().toString()) {

            case "Year Totals":
                ReportInfo.remove(cmbxYears);
                ReportInfo.remove(cmbxCustomers);

                ReportInfo.add(cmbxYears);
                cmbxYears.removeAllItems();
                cmbxYears.addItem("");
                cmbxYears.setSelectedItem("");
                for (int i = 0; i < years.size(); i++) {

                    cmbxYears.addItem(years.get(i));
                }

                break;
            case "Customer Year Totals":
                ReportInfo.remove(cmbxYears);
                ReportInfo.remove(cmbxCustomers);
                ReportInfo.add(cmbxYears);
                ReportInfo.add(cmbxCustomers);
                cmbxCustomers.setEnabled(false);
                cmbxYears.removeAllItems();
                cmbxYears.addItem("");
                cmbxYears.setSelectedItem("");
                for (int i = 0; i < years.size(); i++) {
                    cmbxYears.addItem(years.get(i));
                }

                break;
            case "Customer All-time Totals":
                ReportInfo.remove(cmbxYears);
                ReportInfo.remove(cmbxCustomers);
                ReportInfo.add(cmbxCustomers);
                cmbxCustomers.removeAllItems();
                cmbxCustomers.addItem("");
                cmbxCustomers.setSelectedItem("");
                ArrayList<String> customers = getCustomers();
                for (int i = 0; i < customers.size(); i++) {
                    cmbxCustomers.addItem(customers.get(i));
                }
                break;
        }
    }

    private ArrayList<String> getYears() {
        ArrayList<String> ret = new ArrayList<>();
        PreparedStatement prep = DbInt.getPrep("Set", "SELECT YEARS FROM Years");
        try {


            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret.add(rs.getString("YEARS"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    private ArrayList<String> getCustomers(String year) {
        ArrayList<String> ret = new ArrayList<>();
        PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers");
        try {


            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret.add(rs.getString("NAME"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    private ArrayList<String> getCustomers() {
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<String> years = getYears();
        for (int i = 0; i < years.size(); i++) {
            PreparedStatement prep = DbInt.getPrep(years.get(i), "SELECT NAME FROM Customers");
            try {


                ResultSet rs = prep.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("NAME");
                    if (ret.contains(name)) {
                    } else {
                        ret.add(name);
                    }

                }
                ////DbInt.pCon.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return ret;
    }
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
    private void stuff(){

    }
    private void transf() throws SaxonApiException {
        OutputStream os = new ByteArrayOutputStream();

        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(new File("Report.xsl")));
        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(new File("test.xml")));
        Serializer out = proc.newSerializer(os);
        out.setOutputProperty(Serializer.Property.METHOD, "html");
        out.setOutputProperty(Serializer.Property.INDENT, "yes");
        XsltTransformer trans = exp.load();
        trans.setInitialContextNode(source);
        trans.setDestination(out);
        trans.transform();
        ByteArrayOutputStream baos;
        baos = (ByteArrayOutputStream) os;
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        // if you have html source in hand, use it to generate document object
        Document document = XMLResource.load(is).getDocument();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument( document, null );

        renderer.layout();

        String fileNameWithPath =  "PDF-XhtmlRendered.pdf";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream( fileNameWithPath );

        renderer.createPDF( fos );
        fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Output written to books.html");
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
            String zip = scoutZip.getText().toString();
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
                scoutTown.setText(town);
                scoutState.setText(state);
            }
        }
    }
    /**
     * Compile and execute a simple transformation that applies a stylesheet to an input file,
     * and serializing the result to an output file
     */


}

