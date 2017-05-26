/*
 * Copyright (c) Patrick Magauran 2017.
 *   Licensed under the AGPLv3. All conditions of said license apply.
 *       This file is part of ABOS.
 *
 *       ABOS is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Affero General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       ABOS is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Affero General Public License for more details.
 *
 *       You should have received a copy of the GNU Affero General Public License
 *       along with ABOS.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;

/**
 * Created by patrick on 12/24/15.
 */
class Reports extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private final JComboBox<Object> cmbxYears = new JComboBox<>(new DefaultComboBoxModel<>());
    private final JComboBox<Object> cmbxCustomers = new JComboBox<>(new DefaultComboBoxModel<>());
    private JLabel includeHeaderL;
    private JTabbedPane SteptabbedPane;
    // --Commented out by Inspection (7/27/16 3:02 PM):private Object[][] rowDataF = new Object[0][];
    private JButton nextButton;
    private JPanel ReportInfo;
    private JComboBox<Object> cmbxReportType;
    private JTextField scoutName;
    private JTextField scoutStAddr;
    private JTextField scoutZip;
    private JTextField scoutTown;
    private JTextField scoutState;
    private JTextField scoutPhone;
    private JTextField scoutRank;
    private JTextField logoLoc;
    private JTextField pdfLoc;
    private JComboBox<Object> cmbxCategory;
    private JCheckBox includeHeader;


    private String addrFormat = null;
    // --Commented out by Inspection (7/27/16 3:02 PM):private double totL = 0.0;
    // --Commented out by Inspection (7/27/16 3:02 PM):private double QuantL = 0.0;
    private String Splitting = "";
    private String repTitle = "";
    // --Commented out by Inspection (7/27/16 3:02 PM):private File xmlTempFile = null;
    // --Commented out by Inspection (7/27/16 3:02 PM):private File[] xmlTempFileA = null;
    private ReportsWorker reportsWorker = null;


    public Reports() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
/*
    public static void main(String... args) {
        try {
            new Reports();

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Get info on a product
     *
     * @return The info of the product specified
     */

    private static Iterable<String> getYears() {
        Collection<String> ret = new ArrayList<>();
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT YEARS FROM Years");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("YEARS"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
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
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
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

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

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
        } catch (ParserConfigurationException | SAXException e) {
            LogToFile.log(e, Severity.SEVERE, "Error Parsing geolaction data. Please try again or contact support.");
        } catch (IOException e) {
            LogToFile.log(e, Severity.SEVERE, "Error Reading geolaction data. Please try again or contact support.");
        } catch (RuntimeException e) {
            LogToFile.log(e, Severity.SEVERE, "Unknown error. Please contact support.");
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
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ABOS-LOGO.png")));
        this.setTitle("ABOS - Reports");
        //Main
        {
            SteptabbedPane = new JTabbedPane();
            //Report Type
            {
                JPanel ReportType = new JPanel(new FlowLayout());
                {
                    cmbxReportType = new JComboBox<>(new DefaultComboBoxModel<>());
                    cmbxReportType.setSelectedItem(Config.getProp("ReportType"));
                    cmbxReportType.addActionListener(actionEvent -> {
                        JComboBox comboBox = (JComboBox) actionEvent.getSource();

                        Object selected = comboBox.getSelectedItem();
                        if (cmbxReportType.getSelectedItem() != "") {
                            nextButton.setEnabled(true);
                        }

                    });
                    cmbxReportType.addItem("");
                    cmbxReportType.addItem("Year Totals");
                    cmbxReportType.addItem("Year Totals; Spilt by Customer");

                    cmbxReportType.addItem("Customer Year Totals");
                    cmbxReportType.addItem("Customer All-time Totals");

                    ReportType.add(cmbxReportType);
                }
                SteptabbedPane.addTab("Report Type", ReportType);

            }
            //Report Info
            {
                ReportInfo = new JPanel(new FlowLayout(FlowLayout.LEADING));
                //Content
                {
                    cmbxYears.addActionListener(actionEvent -> {
                        JComboBox comboBox = (JComboBox) actionEvent.getSource();

                        Object selected = comboBox.getSelectedItem();
                        if (cmbxReportType.getSelectedIndex() == 2) {
                            if (cmbxYears.getSelectedItem() != "") {
                                Year year = new Year(cmbxYears.getSelectedItem().toString());
                                Iterable<String> customersY = year.getCustomerNames();
                                cmbxCustomers.removeAllItems();
                                cmbxCustomers.addItem("");
                                cmbxCustomers.setSelectedItem("");
                                customersY.forEach(cmbxCustomers::addItem);
                                cmbxCustomers.setEnabled(true);
                                try (PreparedStatement prep = DbInt.getPrep(cmbxYears.getSelectedItem().toString(), "SELECT NAME FROM Categories")) {
                                    prep.execute();
                                    try (ResultSet rs = prep.executeQuery()) {

                                        while (rs.next()) {

                                            cmbxCategory.addItem(rs.getString(1));

                                        }
                                        ////DbInt.pCon.close();
                                    }
                                } catch (SQLException e) {
                                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                                }
                            }
                        }

                    });


                    cmbxCategory = new JComboBox<>();
                    cmbxCategory.addItem("All");


                    cmbxCategory.setSelectedIndex(0);
                    cmbxCategory.addItemListener(e -> {
                        if ((e.getStateChange() == ItemEvent.SELECTED) && !e.getItem().equals("All")) {
                            includeHeader.setVisible(true);
                            includeHeaderL.setVisible(true);

                        } else {
                            includeHeader.setVisible(false);
                            includeHeaderL.setVisible(false);

                        }
                    });
                    JLabel scoutNameL = new JLabel("Scout Name:");
                    JLabel scoutStAddrL = new JLabel("Scout Street Address:");
                    JLabel scoutZipL = new JLabel("Scout Zip:");
                    JLabel scoutTownL = new JLabel("Scout Town:");
                    JLabel scoutStateL = new JLabel("Scout State:");
                    JLabel scoutPhoneL = new JLabel("Scout Phone #:");
                    JLabel scoutRankL = new JLabel("Scout Rank");
                    JLabel logoLocL = new JLabel("Logo Location:");
                    JLabel pdfLocL = new JLabel("PDF Save location:");
                    JLabel categoryL = new JLabel("Category:");
                    includeHeaderL = new JLabel("Include Due header");

                    includeHeaderL.setVisible(false);
                    scoutName = new JTextField(Config.getProp("ScoutName"), 20);
                    scoutStAddr = new JTextField(Config.getProp("ScoutAddress"), 20);
                    scoutZip = new JTextField(Config.getProp("ScoutZip"), 5);
                    scoutTown = new JTextField(Config.getProp("ScoutTown"), 20);
                    scoutState = new JTextField(Config.getProp("ScoutState"), 20);
                    scoutPhone = new JTextField(Config.getProp("ScoutPhone"), 10);
                    scoutRank = new JTextField(Config.getProp("ScoutRank"), 20);
                    logoLoc = new JTextField(Config.getProp("logoLoc"), 25);
                    includeHeader = new JCheckBox();
                    includeHeader.setVisible(false);
                    scoutZip.addActionListener(new MyTextActionListener());
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
                    pdfLoc = new JTextField(Config.getProp("pdfLoc"), 25);
                    JButton pdfButton = new JButton("...");
                    pdfButton.addActionListener(e -> {
                        //Creates a JFileChooser to select save location of XML file
                        JFileChooser chooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("Portable Document Formant", "pdf");
                        chooser.setFileFilter(filter);
                        int returnVal = chooser.showSaveDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            if (chooser.getSelectedFile().getName().endsWith(".pdf")) {
                                pdfLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                            } else {
                                pdfLoc.setText(chooser.getSelectedFile().getAbsolutePath() + ".pdf");

                            }

                        }
                    });

                    //ScoutName
                    {
                        JPanel group = new JPanel(flow);
                        ReportInfo.add(scoutNameL);
                        ReportInfo.add(scoutName);
                        ReportInfo.add(group);
                    }
                    //ScoutStAddress
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutStAddrL);
                        group.add(scoutStAddr);
                        ReportInfo.add(group);
                    }
                    //ScoutZip
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutZipL);
                        group.add(scoutZip);
                        ReportInfo.add(group);
                    }
                    //ScoutTown
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutTownL);
                        group.add(scoutTown);
                        ReportInfo.add(group);
                    }
                    //ScoutState
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutStateL);
                        group.add(scoutState);
                        ReportInfo.add(group);
                    }
                    //ScoutPhone
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutPhoneL);
                        group.add(scoutPhone);
                        ReportInfo.add(group);
                    }
                    //ScoutRank
                    {
                        JPanel group = new JPanel(flow);
                        group.add(scoutRankL);
                        group.add(scoutRank);
                        ReportInfo.add(group);
                    }
                    //LogoLocation
                    {
                        JPanel group = new JPanel(flow);
                        group.add(logoLocL);
                        group.add(logoLoc);
                        group.add(logoButton);
                        ReportInfo.add(group);
                    }
                    //PDF Location
                    {
                        JPanel group = new JPanel(flow);
                        group.add(pdfLocL);
                        group.add(pdfLoc);
                        group.add(pdfButton);
                        ReportInfo.add(group);
                    }
                    //Category Choice
                    {
                        JPanel group = new JPanel(flow);
                        group.add(categoryL);
                        group.add(cmbxCategory);
                        ReportInfo.add(group);
                    }
                    //Add info header
                    {
                        JPanel group = new JPanel(flow);
                        group.add(includeHeaderL);
                        group.add(includeHeader);
                        ReportInfo.add(group);
                    }
                }
                SteptabbedPane.addTab("Report Info", ReportInfo);

            }


            contentPanel.add(SteptabbedPane, BorderLayout.CENTER);
        }

        //Buttons
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            //Next
            {
                nextButton = new JButton("Next -->");
                nextButton.setEnabled(false);
                buttonPane.add(nextButton);
                getRootPane().setDefaultButton(nextButton);
            }
            //OK
            JButton okButton;
            {
                okButton = new JButton("OK");
                okButton.setEnabled(false);
                buttonPane.add(okButton);
            }
            //Cancel
            JButton cancelButton;
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            //OK Button Action
            okButton.addActionListener(e -> {
                addrFormat = scoutTown.getText() + ' ' + scoutState.getText() + ", " + scoutZip.getText();
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
                        repTitle = cmbxCustomers.getSelectedItem() + " " + cmbxYears.getSelectedItem();
                        Splitting = "";

                        break;
                    case 4:
                        repTitle = "All orders of " + cmbxCustomers.getSelectedItem();
                        Splitting = "Year:";

                        break;

                }
                ProgressDialog progDial = new ProgressDialog();
                String selectedYear = (cmbxYears.getSelectedItem() != null) ? cmbxYears.getSelectedItem().toString() : "";
                String selectedCustomer = (cmbxCustomers.getSelectedItem() != null) ? cmbxCustomers.getSelectedItem().toString() : "";

                reportsWorker = new ReportsWorker(cmbxReportType.getSelectedItem().toString(), selectedYear, scoutName.getText(), scoutStAddr.getText(), addrFormat, scoutRank.getText(), scoutPhone.getText(), logoLoc.getText(), cmbxCategory.getSelectedItem().toString(), selectedCustomer, repTitle, Splitting, includeHeader.isSelected(), progDial.statusLbl, pdfLoc.getText());
                reportsWorker.addPropertyChangeListener(event -> {
                    switch (event.getPropertyName()) {
                        case "progress":
                            progDial.progressBar.setIndeterminate(false);
                            progDial.progressBar.setValue((Integer) event.getNewValue());
                            break;
                        case "state":
                            switch ((SwingWorker.StateValue) event.getNewValue()) {
                                case DONE:
                                    try {
                                        if (Desktop.isDesktopSupported()) {
                                            try {
                                                File myFile = new File(pdfLoc.getText());
                                                Desktop.getDesktop().open(myFile);
                                            } catch (IOException ex) {
                                                LogToFile.log(ex, Severity.SEVERE, "Error writing pdf file. Please try again or contacting support.");
                                            }
                                        }
                                    } catch (CancellationException e1) {
                                        LogToFile.log(e1, Severity.INFO, "The process was cancelled.");

                                    } catch (Exception e1) {
                                        LogToFile.log(e1, Severity.SEVERE, "The process failed.");

                                    }

                                    reportsWorker = null;
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
                });
                reportsWorker.execute();

                dispose();
            });
            okButton.setActionCommand("OK");
            //NextButton Action
            nextButton.addActionListener(e -> {

                updateCombos();
                nextButton.setEnabled(false);
                okButton.setEnabled(true);
                SteptabbedPane.setSelectedIndex(SteptabbedPane.getSelectedIndex() + 1);


            });
            //Cancel Button Action
            cancelButton.addActionListener(e -> dispose());
            cancelButton.setActionCommand("Cancel");
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
            case "Year Totals; Spilt by Customer":
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


// --Commented out by Inspection START (7/27/16 3:02 PM):
//    private String getDate(String catName){
//        Date ret = null;
//        try (PreparedStatement prep = DbInt.getPrep("set", "SELECT Date FROM Categories WHERE Name=?")) {
//
//
//            prep.setString(1, catName);
//
//            try (ResultSet rs = prep.executeQuery()) {
//
//                while (rs.next()) {
//
//                    ret = rs.getDate(1);
//
//                }
//            }
//            ////DbInt.pCon.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        String output;
//        SimpleDateFormat formatter;
//        formatter = new SimpleDateFormat("MM/dd/yyyy");
//        output = formatter.format(ret);
//        return output;
//    }
// --Commented out by Inspection STOP (7/27/16 3:02 PM)



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
                    LogToFile.log(e1, Severity.WARNING, "Error getting geolocation info. Please try again later.");
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