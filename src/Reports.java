import com.lowagie.text.DocumentException;
import net.sf.saxon.s9api.*;
import net.sf.saxon.s9api.Serializer.Property;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by patrick on 12/24/15.
 */
class Reports extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private final JComboBox cmbxYears = new JComboBox(new DefaultComboBoxModel<>());
    private final JComboBox cmbxCustomers = new JComboBox(new DefaultComboBoxModel<>());
    private JTabbedPane SteptabbedPane;
    private Object[][] rowDataF = new Object[0][];
    private JButton nextButton;
    private JPanel ReportInfo;
    private JComboBox<Object> cmbxReportType;
    private JTextField scoutName;
    private JTextField scoutStAddr;
    private JTextField scoutZip;
    private JTextField scoutTown;
    private JTextField scoutState;
    private JTextField scoutRank;
    private JTextField logoLoc;
    private String addrFormat = null;
    private double totL = 0.0;
    private double QuantL = 0.0;
    private String Splitting = "";
    private String repTitle = "";
    private File xmlTempFile = null;

    private Reports() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String... args) {
        try {
            new Reports();

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get info on a product
     *
     * @param info the info to be retrieved
     * @param PID  The ID of the product to get info for
     * @return The info of the product specified
     */
    private static List<String> GetProductInfo(String info, String PID, String year) {
        List<String> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS WHERE PID=?")) {


            prep.setString(1, PID);

            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret.add(rs.getString(info));

                }
            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static Iterable<String> getYears() {
        Collection<String> ret = new ArrayList<>();
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT YEARS FROM Years");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("YEARS"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    private static Iterable<String> getCustomers(String year) {
        Collection<String> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("NAME"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    private static Iterable<String> getCustomers() {
        Collection<String> ret = new ArrayList<>();
        Iterable<String> years = getYears();
        for (String year : years) {

            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers");
                 ResultSet rs = prep.executeQuery()
            ) {


                while (rs.next()) {
                    String name = rs.getString("NAME");
                    if (!ret.contains(name)) {
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

    private static String getCityState(String zipCode) throws IOException {
        //String AddressF = Address.replace(" ","+");
        //The URL for the MapquestAPI
        String url = String.format("http://open.mapquestapi.com/nominatim/v1/search.php?key=CCBtW1293lbtbxpRSnImGBoQopnvc4Mz&format=xml&q=%s&addressdetails=1&limit=1&accept-language=en-US", zipCode);

        //Defines connection
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        String city = "";
        String State = "";
        //Creates Response buffer for Web response
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            //Fill String buffer with response
            while ((inputLine = in.readLine()) != null) {
                //inputLine = StringEscapeUtils.escapeHtml4(inputLine);
                //inputLine = StringEscapeUtils.escapeXml11(inputLine);
                response.append(inputLine);
            }


            //Parses XML response and fills City and State Variables
            InputSource is = new InputSource(new StringReader(response.toString()));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("place");


            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


                    city = eElement.getElementsByTagName("city").item(0).getTextContent();
                    State = eElement.getElementsByTagName("state").item(0).getTextContent();


                    //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                }
            }
        } catch (ParserConfigurationException | IOException | SAXException | RuntimeException e) {
            e.printStackTrace();
        }

        //Formats City and state into one string to return
        String fullName = city + '&';
        fullName += State;
        //print result
        //	return parseCoords(response.toString());
        return fullName;
    }

    //SetBounds(X,Y,Width,Height)
    private void initUI() {
        setSize(700, 500);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());
        FlowLayout flow = new FlowLayout(FlowLayout.LEADING);

        //North
        {
            SteptabbedPane = new JTabbedPane();
            //Report Type
            {
                JPanel ReportType = new JPanel(new FlowLayout());
                {
                    cmbxReportType = new JComboBox<>(new DefaultComboBoxModel<>());
                    cmbxReportType.addActionListener(actionEvent -> {
                        JComboBox comboBox = (JComboBox) actionEvent.getSource();

                        Object selected = comboBox.getSelectedItem();
                        if (cmbxReportType.getSelectedItem() != "") {
                            nextButton.setEnabled(true);
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
                ReportInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
                {
                    cmbxYears.addActionListener(actionEvent -> {
                        JComboBox comboBox = (JComboBox) actionEvent.getSource();

                        Object selected = comboBox.getSelectedItem();
                        if (cmbxReportType.getSelectedIndex() == 2) {
                            if (cmbxYears.getSelectedItem() != "") {
                                Iterable<String> customersY = getCustomers(cmbxYears.getSelectedItem().toString());
                                cmbxCustomers.removeAllItems();
                                cmbxCustomers.addItem("");
                                cmbxCustomers.setSelectedItem("");
                                customersY.forEach(cmbxCustomers::addItem);
                                cmbxCustomers.setEnabled(true);
                            }
                        } else if (cmbxYears.getSelectedItem() != "") {
                            nextButton.setEnabled(true);
                        }

                    });

                    cmbxCustomers.addActionListener(actionEvent -> {
                        JComboBox comboBox = (JComboBox) actionEvent.getSource();

                        Object selected = comboBox.getSelectedItem();
                        if (cmbxCustomers.getSelectedItem() != "") {
                            nextButton.setEnabled(true);
                        }

                    });
                    JLabel scoutNameL = new JLabel("Scout Name:");
                    JLabel scoutStAddrL = new JLabel("Scout Street Address:");
                    JLabel scoutZipL = new JLabel("Scout Zip:");
                    JLabel scoutTownL = new JLabel("Scout Town:");
                    JLabel scoutStateL = new JLabel("Scout State:");
                    JLabel scoutRankL = new JLabel("Scout Rank");
                    JLabel logoLocL = new JLabel("Logo Location:");

                    scoutName = new JTextField(Config.getProp("ScoutName"), 20);
                    scoutStAddr = new JTextField(Config.getProp("ScoutAddress"), 20);
                    scoutZip = new JTextField(Config.getProp("ScoutZip"), 5);
                    scoutTown = new JTextField(Config.getProp("ScoutTown"), 20);
                    scoutState = new JTextField(Config.getProp("ScoutState"), 20);
                    scoutRank = new JTextField(Config.getProp("ScoutRank"), 20);
                    logoLoc = new JTextField(Config.getProp("logoLoc"), 25);
                    scoutZip.addActionListener(new MyTextActionListener());
                    scoutZip.getDocument().addDocumentListener(new MyDocumentListener());
                    JButton logoButton = new JButton("...");
                    logoButton.addActionListener(e -> {
                        //Creates a JFileChooser to select a directory to store the Databases
                        JFileChooser chooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
                        chooser.setFileFilter(filter);
                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        int returnVal = chooser.showOpenDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            logoLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                        }

                    });


                    {
                        JPanel group = new JPanel(flow);
                        ReportInfo.add(scoutNameL);
                        ReportInfo.add(scoutName);
                        ReportInfo.add(group);
                    }
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutStAddrL);
                        group.add(scoutStAddr);
                        ReportInfo.add(group);
                    }
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutZipL);
                        group.add(scoutZip);
                        ReportInfo.add(group);
                    }
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutTownL);
                        group.add(scoutTown);
                        ReportInfo.add(group);
                    }
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutStateL);
                        group.add(scoutState);
                        ReportInfo.add(group);
                    }
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutRankL);
                        group.add(scoutRank);
                        ReportInfo.add(group);
                    }
                    {
                        JPanel group = new JPanel(flow);
                        group.add(logoLocL);
                        group.add(logoLoc);
                        group.add(logoButton);
                        ReportInfo.add(group);
                    }
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
            JButton okButton;
            {
                okButton = new JButton("OK");
                okButton.setEnabled(false);
                buttonPane.add(okButton);
            }
            JButton cancelButton;
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            okButton.addActionListener(e -> dispose());
            nextButton.addActionListener(e -> {

                switch (SteptabbedPane.getSelectedIndex()) {
                    case 0:
                        updateCombos();
                        break;
                    case 1:
                        switch (cmbxReportType.getSelectedIndex()) {
                            case 1:
                                repTitle = "Year of " + cmbxYears.getSelectedItem();
                                Splitting = "Year:";
                                break;
                            case 2:
                                repTitle = cmbxCustomers.getSelectedItem() + " " + cmbxYears.getSelectedItem();
                                Splitting = "";
                                break;
                            case 3:
                                repTitle = "All orders of " + cmbxCustomers.getSelectedItem();
                                Splitting = "Year:";
                                break;

                        }
                        addrFormat = scoutTown.getText() + ' ' + scoutState.getText() + ", " + scoutZip.getText();
                        convert();
                        break;
                    case 2:
                        break;


                }
                SteptabbedPane.setSelectedIndex(SteptabbedPane.getSelectedIndex() + 1);

            });
            okButton.setActionCommand("OK");

            cancelButton.addActionListener(e -> dispose());
            cancelButton.setActionCommand("Cancel");
        }
    }

    private void convert() {


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
                    logo.appendChild(doc.createTextNode("file:///" + logoLoc.getText().replace("\\", "/")));
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
                String[] Columns = {"ID", "Name", "Unit Size", "Unit Cost", "Quantity", "Extended Price"};
                for (String Column : Columns) {
                    //Column
                    {
                        Element columnName = doc.createElement("column");
                        Element cName = doc.createElement("name");
                        cName.appendChild(doc.createTextNode(Column));
                        columnName.appendChild(cName);
                        columns.appendChild(columnName);
                    }
                }
            }
            switch (cmbxReportType.getSelectedItem().toString()) {
//TODO Add Split by customer option
                //TODO Fix GUI Sizing
                //TODO Fix total cost box

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
                    double tCost = 0.0;

                    for (Object[] aRowDataF : rowDataF) {
                        {
                            Element Product = doc.createElement("Product");
                            products.appendChild(Product);
                            {
                                Element ID = doc.createElement("ID");
                                ID.appendChild(doc.createTextNode(aRowDataF[0].toString()));
                                Product.appendChild(ID);
                            }
                            {
                                Element Name = doc.createElement("Name");
                                Name.appendChild(doc.createTextNode(aRowDataF[1].toString()));
                                Product.appendChild(Name);
                            }
                            {
                                Element Size = doc.createElement("Size");
                                Size.appendChild(doc.createTextNode(aRowDataF[2].toString()));
                                Product.appendChild(Size);
                            }
                            {
                                Element UnitCost = doc.createElement("UnitCost");
                                UnitCost.appendChild(doc.createTextNode(aRowDataF[3].toString()));
                                Product.appendChild(UnitCost);
                            }
                            {
                                Element Quantity = doc.createElement("Quantity");
                                Quantity.appendChild(doc.createTextNode(aRowDataF[4].toString()));
                                Product.appendChild(Quantity);
                            }
                            {
                                Element TotalCost = doc.createElement("TotalCost");
                                TotalCost.appendChild(doc.createTextNode(aRowDataF[5].toString()));
                                tCost += Double.parseDouble(aRowDataF[5].toString());
                                Product.appendChild(TotalCost);
                            }
                        }
                    }
                    {
                        Element tCostE = doc.createElement("totalCost");
                        tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                        products.appendChild(tCostE);
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
                    double tCost = 0.0;

                    for (Object[] aRowDataF : rowDataF) {
                        {
                            Element Product = doc.createElement("Product");
                            products.appendChild(Product);
                            {
                                Element ID = doc.createElement("ID");
                                ID.appendChild(doc.createTextNode(aRowDataF[0].toString()));
                                Product.appendChild(ID);
                            }
                            {
                                Element Name = doc.createElement("Name");
                                Name.appendChild(doc.createTextNode(aRowDataF[1].toString()));
                                Product.appendChild(Name);
                            }
                            {
                                Element Size = doc.createElement("Size");
                                Size.appendChild(doc.createTextNode(aRowDataF[2].toString()));
                                Product.appendChild(Size);
                            }
                            {
                                Element UnitCost = doc.createElement("UnitCost");
                                UnitCost.appendChild(doc.createTextNode(aRowDataF[3].toString()));
                                Product.appendChild(UnitCost);
                            }
                            {
                                Element Quantity = doc.createElement("Quantity");
                                Quantity.appendChild(doc.createTextNode(aRowDataF[4].toString()));
                                Product.appendChild(Quantity);
                            }
                            {
                                Element TotalCost = doc.createElement("TotalCost");
                                TotalCost.appendChild(doc.createTextNode(aRowDataF[5].toString()));
                                tCost += Double.parseDouble(aRowDataF[5].toString());
                                Product.appendChild(TotalCost);
                            }
                        }
                    }
                    {
                        Element tCostE = doc.createElement("totalCost");
                        tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                        products.appendChild(tCostE);
                    }
                }
                break;
                case "Customer All-time Totals":
                    String Qname = cmbxCustomers.getSelectedItem().toString();
                    Collection<String> customerYears = new ArrayList<>();
                    Iterable<String> years = getYears();
                    for (String year : years) {
                        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers WHERE NAME=?")) {

                            prep.setString(1, Qname);
                            try (ResultSet rs = prep.executeQuery()) {

                                while (rs.next()) {
                                    customerYears.add(year);
                                    //Product Elements
                                    Element products = doc.createElement("customerYear");
                                    rootElement.appendChild(products);
                                    //YearTitle
                                    {
                                        Element title = doc.createElement("title");
                                        title.appendChild(doc.createTextNode(year));
                                        products.appendChild(title);
                                    }
                                    fillTable(year, cmbxCustomers.getSelectedItem().toString());
                                    double tCost = 0.0;
                                    for (Object[] aRowDataF : rowDataF) {
                                        Element Product = doc.createElement("Product");
                                        products.appendChild(Product);
                                        {
                                            Element ID = doc.createElement("ID");
                                            ID.appendChild(doc.createTextNode(aRowDataF[0].toString()));
                                            Product.appendChild(ID);
                                        }
                                        {
                                            Element Name = doc.createElement("Name");
                                            Name.appendChild(doc.createTextNode(aRowDataF[1].toString()));
                                            Product.appendChild(Name);
                                        }
                                        {
                                            Element Size = doc.createElement("Size");
                                            Size.appendChild(doc.createTextNode(aRowDataF[2].toString()));
                                            Product.appendChild(Size);
                                        }
                                        {
                                            Element UnitCost = doc.createElement("UnitCost");
                                            UnitCost.appendChild(doc.createTextNode(aRowDataF[3].toString()));
                                            Product.appendChild(UnitCost);
                                        }
                                        {
                                            Element Quantity = doc.createElement("Quantity");
                                            Quantity.appendChild(doc.createTextNode(aRowDataF[4].toString()));
                                            Product.appendChild(Quantity);
                                        }
                                        {
                                            Element TotalCost = doc.createElement("TotalCost");
                                            TotalCost.appendChild(doc.createTextNode(aRowDataF[5].toString()));
                                            tCost += Double.parseDouble(aRowDataF[5].toString());
                                            Product.appendChild(TotalCost);
                                        }
                                    }
                                    Element tCostE = doc.createElement("totalCost");
                                    tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                                    products.appendChild(tCostE);

                                }
                            }
                            ////DbInt.pCon.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

            OutputStreamWriter osw = null;

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
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

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String tmpDirectoryOp = System.getProperty("java.io.tmpdir");
                File tmpDirectory = new File(tmpDirectoryOp);
                xmlTempFile = File.createTempFile("LGReport" + timeStamp, ".xml", tmpDirectory);
                xmlTempFile.deleteOnExit();

                try (OutputStream outStream = new FileOutputStream(xmlTempFile)) {// writing bytes in to byte output stream

                    baos.writeTo(outStream);
                    //fstream.deleteOnExit();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (Exception exp) {
                exp.printStackTrace();
            } finally {
                try {
                    if (osw != null) {
                        osw.close();
                    }
                } catch (IOException | RuntimeException e) {
                    e.printStackTrace();
                }

            }
            transf();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fillTable(String year, String name) {

        //Variables for inserting info into table
        String[] toGet = {"ID", "PNAME", "SIZE", "UNIT"};
        List<ArrayList<String>> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray

            for (int i = 0; i < 4; i++) {
                ProductInfoArray.add(new ArrayList<>());
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
        Object[][] rows = new Object[ProductInfoArray.get(2).size()][6];


        String OrderID = DbInt.getCustInf(year, name, "ORDERID");
        //Defines Arraylist of order quanitities
        List<String> OrderQuantities = new ArrayList<>();
        int noVRows = 0;
        //Fills OrderQuantities Array
        for (int i = 0; i < ProductInfoArray.get(2).size(); i++) {

            int quant;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ORDERS WHERE ORDERID=?")) {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, OrderID);
                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {
                        DbInt.pCon.close();
                    }
                }

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
                rows[noVRows][5] = (double) quant * Double.parseDouble(ProductInfoArray.get(3).get(i).replaceAll("\\$", ""));
                totL += ((double) quant * Double.parseDouble(ProductInfoArray.get(3).get(i).replaceAll("\\$", "")));
                QuantL += (double) quant;

                noVRows++;

            }
        }
        //Re create rows to remove blank rows
        rowDataF = new Object[noVRows][6];

        for (int i = 0; i <= (noVRows - 1); i++) {
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

        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", Config.getDbLoc(), year);
        System.setProperty("derby.system.home",
                Config.getDbLoc());

        int colO = DbInt.getNoCol(year, "ORDERS") - 2;
        Object[][] rowData = new Object[colO][6];

        int noRows = 0;
        int productsNamed = 0;
        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet Order = st.executeQuery("SELECT * FROM ORDERS")
        ) {


            ResultSetMetaData rsmd = Order.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            while (Order.next()) {
                if (productsNamed == 0) {
                    //loop through columns
                    for (int c = 3; c <= columnsNumber; c++) {
                        //Get ID of product
                        List<String> IDL = GetProductInfo("ID", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String ID = IDL.get(IDL.size() - 1);
                        //Get Name of product
                        List<String> productL = GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String product = productL.get(productL.size() - 1);
                        //Get Name of product
                        List<String> sizeL = GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String size = sizeL.get(sizeL.size() - 1);
                        //Get unit cost of product
                        List<String> UnitL = GetProductInfo("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String Unit = UnitL.get(productL.size() - 1);
                        //Get Quantity ordered
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);
                        //Calculate total price and overall Total
                        double TPrice = UnitD * quantityD;
                        totL += TPrice;
                        QuantL += quantityD;

                        rowData[noRows][0] = ID;
                        rowData[noRows][1] = product;
                        rowData[noRows][2] = size;
                        rowData[noRows][3] = Unit;
                        rowData[noRows][4] = quantity;
                        rowData[noRows][5] = TPrice;

                        noRows += 1;


                    }
                    productsNamed += 1;
                } else {
                    noRows = 0;
                    for (int c = 3; c <= columnsNumber; c++) {

                        //Get Name of product
                        List<String> productL = GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        //Get unit cost of product
                        java.util.List<String> UnitL = GetProductInfo("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String Unit = UnitL.get(productL.size() - 1);
                        //Get Quantity ordered
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);
                        //Calculate total price and overall Total
                        double TPrice = UnitD * quantityD;
                        totL += TPrice;
                        QuantL += quantityD;


                        rowData[noRows][4] = Double.parseDouble(rowData[noRows][4].toString()) + quantityD;
                        rowData[noRows][5] = Double.parseDouble(rowData[noRows][5].toString()) + TPrice;
                        noRows += 1;

                    }
                }
            }
            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if ((ex.getErrorCode() == 50000)
                    && "XJ015".equals(ex.getSQLState())) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }

        //Limit array to only rows that have ordered stuff
        Object[][] rowDataExclude0 = new Object[noRows][6];
        int NumNonEmptyRows = 0;
        for (int i = 0; i <= (noRows - 1); i++) {
            if (Double.parseDouble(rowData[i][4].toString()) > 0.0) {
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
        for (int i = 0; i <= (NumNonEmptyRows - 1); i++) {

            rowDataF[i][0] = rowDataExclude0[i][0];//ID
            rowDataF[i][1] = rowDataExclude0[i][1];//Name
            rowDataF[i][2] = rowDataExclude0[i][2];//UnitSize
            rowDataF[i][3] = rowDataExclude0[i][3];//UnitCost
            rowDataF[i][4] = rowDataExclude0[i][4];//Quantity
            rowDataF[i][5] = rowDataExclude0[i][5];//Tcost


        }

    }

    private void updateCombos() {
        Iterable<String> years = getYears();

        switch (cmbxReportType.getSelectedItem().toString()) {

            case "Year Totals":
                ReportInfo.remove(cmbxYears);
                ReportInfo.remove(cmbxCustomers);

                ReportInfo.add(cmbxYears);
                cmbxYears.removeAllItems();

                years.forEach(cmbxYears::addItem);
                cmbxYears.setSelectedItem(cmbxYears.getItemAt(cmbxYears.getItemCount() - 1));
                break;
            case "Customer Year Totals":
                ReportInfo.remove(cmbxYears);
                ReportInfo.remove(cmbxCustomers);
                ReportInfo.add(cmbxYears);
                ReportInfo.add(cmbxCustomers);
                cmbxCustomers.setEnabled(false);
                cmbxYears.removeAllItems();
                years.forEach(cmbxYears::addItem);
                cmbxYears.setSelectedItem(cmbxYears.getItemAt(cmbxYears.getItemCount() - 1));

                break;
            case "Customer All-time Totals":
                ReportInfo.remove(cmbxYears);
                ReportInfo.remove(cmbxCustomers);
                ReportInfo.add(cmbxCustomers);
                cmbxCustomers.removeAllItems();
                cmbxCustomers.addItem("");
                cmbxCustomers.setSelectedItem("");
                Iterable<String> customers = getCustomers();
                customers.forEach(cmbxCustomers::addItem);
                break;
        }
    }

    private void transf() throws SaxonApiException {
        OutputStream os = new ByteArrayOutputStream();

        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();
        XsltExecutable exp = comp.compile(new StreamSource(new File("Report.xsl")));
        XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlTempFile));
        Serializer out = proc.newSerializer(os);
        out.setOutputProperty(Property.METHOD, "html");
        out.setOutputProperty(Property.INDENT, "yes");
        XsltTransformer trans = exp.load();
        trans.setInitialContextNode(source);
        trans.setDestination(out);
        trans.transform();
        ByteArrayOutputStream baos;
        baos = (ByteArrayOutputStream) os;

        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Tidy tidy = new Tidy(); // obtain a new Tidy instance
        // set desired config options using tidy setters
        OutputStream osT = new ByteArrayOutputStream();
        tidy.setQuiet(true);
        tidy.setIndentContent(true);
        tidy.setDocType("loose");
        tidy.setFixBackslash(true);
        tidy.setFixUri(true);
        tidy.setShowWarnings(false);
        tidy.setEscapeCdata(true);
        tidy.setXHTML(true);
        tidy.setInputEncoding("utf8");
        tidy.setOutputEncoding("utf8");

        FileOutputStream fos;
        String fileNameWithPath = "PDF-XhtmlRendered.pdf";
        try {
            fos = new FileOutputStream(fileNameWithPath);

            tidy.parse(is, osT); // run tidy, providing an input and output streamp
            ByteArrayOutputStream baosT;
            baosT = (ByteArrayOutputStream) osT;
            try (InputStream isT = new ByteArrayInputStream(baosT.toByteArray())) {
                Document document = XMLResource.load(isT).getDocument();

                //preview.setDocument(document);

                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocument(document, null);

                renderer.layout();


                renderer.createPDF(fos);
                fos.close();
            }

        } catch (RuntimeException | IOException | DocumentException e) {
            e.printStackTrace();
        }

        /*
          Compile and execute a simple transformation that applies a stylesheet to an input file,
          and serializing the result to an output file
         */


    }

    static class MyDocumentListener implements DocumentListener {
        // --Commented out by Inspection (12/31/15 1:42 PM):final String newline = "\n";

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
            String zip = scoutZip.getText();
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
}