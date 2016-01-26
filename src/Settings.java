import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by patrick on 12/24/15.
 */
class Settings extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private JTabbedPane north;
    //General
    private JTextField DbLoc;
    private JCheckBox CreateDb;
    //Add Customer
    private JCheckBox Delivered;
    private JCheckBox Paid;
    private JTextField Name;
    private JTextField Address;
    private JTextField ZipCode;
    private JTextField Town;
    private JTextField State;
    private JTextField Phone;
    private JTextField Email;
    private JTextField DonationsT;
    //Report
    private JComboBox<Object> cmbxReportType;
    private JTextField scoutName;
    private JTextField scoutStAddr;
    private JTextField scoutZip;
    private JTextField scoutTown;
    private JTextField scoutState;
    private JTextField scoutPhone;
    private JTextField scoutRank;
    private JTextField logoLoc;

    public Settings() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String... args) {
        try {
            new Settings();

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //SetBounds(X,Y,Width,Height)
    private void initUI() {
        setSize(600, 400);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());
        FlowLayout flow = new FlowLayout(FlowLayout.LEADING);
        String eol = System.getProperty("line.separator");
        //Main Content
        {
            north = new JTabbedPane();

            //General Options
            JPanel general = new JPanel(flow);
            {
                //DB stuff
                {
                    //Choose DB location

                    //DB Location Lbael TextField Button
                    {
                        JLabel DbLocL = new JLabel("Database Location:");
                        DbLoc = new JTextField(Config.getDbLoc());
                        DbLoc.setColumns(20);
                        JButton dbButton = new JButton("...");
                        dbButton.addActionListener(e -> {
                            //Creates a JFileChooser to select a directory to store the Databases
                            JFileChooser chooser = new JFileChooser();
                            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            int returnVal = chooser.showOpenDialog(this);
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                DbLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                            }
                        });
                        general.add(DbLocL);
                        general.add(DbLoc);
                        general.add(dbButton);
                    }

                    //Create Database?
                    {
                        CreateDb = new JCheckBox("Create Database");
                        if (!Config.doesConfExist()) {
                            CreateDb.setSelected(false);
                        }
                        CreateDb.addActionListener(e -> {
                            String message = "<html><head><style>" +
                                    "h3 {text-align:center;}" +
                                    "h4 {text-align:center;}" +
                                    "</style></head>" +
                                    "<body><h3>WARNING!</h3>" +
                                    "<h3>SELECTING THIS WILL DELETE ALL DATA AT THE SPECIFIED LOCATION!</h3>" +
                                    "<h4>Would you like to continue?</h4>" +
                                    "</body>" +
                                    "</html>";
                            int cont = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                            if (cont == 1) {
                                CreateDb.setSelected(false);
                            }
                        });
                    }
                    general.add(CreateDb);

                }
                //Select UI theme
                {

                }
                //Export DB
                {
                    //Future options of alternate export types(csv, excel, xml?)
                }

            }
            north.addTab("General", general);

            //Add Customer Options
            JPanel AddCustomer = new JPanel();
            {

                //Set default options for add customer form
                AddCustomer.setLayout(flow);
                //CustomerName
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
                    AddCustomer.add(name);
                }
                //Customer Street Address
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
                    AddCustomer.add(name);
                }
                //Customer Zipcode
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
                    AddCustomer.add(name);
                }
                //Customer Town
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
                    AddCustomer.add(name);
                }
                //Customer State
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

                    AddCustomer.add(name);
                }
                //Customer Phone Number
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
                    AddCustomer.add(name);
                }
                //Customer Email Address
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
                    AddCustomer.add(name);
                }
                //Paid Checkbox
                {
                    Paid = new JCheckBox("Paid");
                    Paid.setSelected(Boolean.valueOf(Config.getProp("CustomerPaid")));
                    //Paid.setBounds(385, 62, 55, 23);
                    AddCustomer.add(Paid);
                }
                //Delivered Checkbox
                {
                    Delivered = new JCheckBox("Delivered");
                    Paid.setSelected(Boolean.valueOf(Config.getProp("CustomerDelivered")));
                    //Delivered.setBounds(473, 62, 83, 23);
                    AddCustomer.add(Delivered);
                }
                //Donations
                {
                    JPanel name = new JPanel(flow);
                    //Donation Label
                    {
                        JLabel lblNewLabel_3 = new JLabel("Donations");
                        //lblNewLabel_3.setBounds(568, 66, 76, 14);
                        name.add(lblNewLabel_3);
                    }
                    //Donation Text
                    {
                        DonationsT = new JTextField(Config.getProp("CustomerDonations"));
                        DonationsT.setColumns(4);
                        //DonationsT.setBounds(654, 59, 173, 28);
                        //autofill with 0.0 iff setting isn't set
                        if (Config.getProp("CustomerDonations") == null) {
                            DonationsT.setText("0.0");
                        }
                        name.add(DonationsT);
                    }
                    AddCustomer.add(name);
                }
            }
            north.addTab("Add Customer", AddCustomer);

            //Map Options
            JPanel MapOptions = new JPanel(flow);
            {
                //Area to Display
                //Default zoom

            }
            north.addTab("Map", MapOptions);

            //Report Options
            JPanel ReportInfo = new JPanel(flow);
            {
                //Default Options for Reports
                //ComboBox Type
                {

                    cmbxReportType = new JComboBox<>(new DefaultComboBoxModel<>());

                    cmbxReportType.addItem("");
                    cmbxReportType.addItem("Year Totals");
                    cmbxReportType.addItem("Customer Year Totals");
                    cmbxReportType.addItem("Customer All-time Totals");
                    ReportInfo.add(cmbxReportType);
                }
                //Info
                {
                    JLabel scoutNameL = new JLabel("Scout Name:");
                    JLabel scoutStAddrL = new JLabel("Scout Street Address:");
                    JLabel scoutZipL = new JLabel("Scout Zip:");
                    JLabel scoutTownL = new JLabel("Scout Town:");
                    JLabel scoutStateL = new JLabel("Scout State:");
                    JLabel scoutPhoneL = new JLabel("Scout Phone #:");

                    JLabel scoutRankL = new JLabel("Scout Rank");
                    JLabel logoLocL = new JLabel("Logo Location:");

                    scoutName = new JTextField(Config.getProp("ScoutName"), 20);
                    scoutStAddr = new JTextField(Config.getProp("ScoutAddress"), 20);
                    scoutZip = new JTextField(Config.getProp("ScoutZip"), 5);
                    scoutTown = new JTextField(Config.getProp("ScoutTown"), 20);
                    scoutState = new JTextField(Config.getProp("ScoutState"), 20);
                    scoutPhone = new JTextField(Config.getProp("ScoutPhone"), 10);

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
                }


            }
            north.addTab("Reports", ReportInfo);

            //Liscence
            JPanel Licesnce = new JPanel(flow);
            {
                //Display Liscence info


            }
            north.addTab("Licence", Licesnce);
            contentPanel.add(north, BorderLayout.CENTER);
        }

        //Button Pane
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            //OKButton
            JButton okButton;
            {
                okButton = new JButton("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            //CancelButton
            JButton cancelButton;
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            //OKButton Action
            okButton.addActionListener(e -> {
                saveData();

                dispose();
            });
            okButton.setActionCommand("OK");

            //CancelButton Action
            cancelButton.addActionListener(e -> dispose());
            cancelButton.setActionCommand("Cancel");
        }
    }

    private void saveData() {
        //General
        //If firstRun Create DB, if not, update Db Location
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream("./LGconfig.properties");
            //Add DB setting
            if (Config.doesConfExist()) {
                prop.setProperty("databaseLocation", DbLoc.getText());
            } else {
                prop.setProperty("databaseLocation", DbLoc.getText());
                DbInt.createDb("Set");

                try (PreparedStatement prep = DbInt.getPrep("Set", "CREATE TABLE Customers(CustomerID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), Address VARChAR(255), Ordered VARChAR(255), NI VARChAR(255), NH VARChAR(255))")) {
                    prep.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try (PreparedStatement prep = DbInt.getPrep("Set", "CREATE TABLE YEARS(ID int PRIMARY KEY NOT NULL, YEARS varchar(4))")) {
                    prep.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            //AddCustomer
            {
                prop.setProperty("CustomerName", Name.getText());
                prop.setProperty("CustomerAddress", Address.getText());
                prop.setProperty("CustomerZipCode", ZipCode.getText());
                prop.setProperty("CustomerTown", Town.getText());
                prop.setProperty("CustomerState", State.getText());
                prop.setProperty("CustomerPhone", Phone.getText());
                prop.setProperty("CustomerEmail", Email.getText());
                prop.setProperty("CustomerPaid", Boolean.toString(Paid.isSelected()));
                prop.setProperty("CustomerDelivered", Boolean.toString(Delivered.isSelected()));
                prop.setProperty("CustomerDonation", DonationsT.getText());
            }
            //Maps
            //Reports
            {
                prop.setProperty("ReportType", cmbxReportType.getSelectedItem().toString());
                prop.setProperty("ScoutName", scoutName.getText());
                prop.setProperty("ScoutAddress", scoutStAddr.getText());
                prop.setProperty("ScoutZip", scoutZip.getText());
                prop.setProperty("ScoutTown", scoutTown.getText());
                prop.setProperty("ScoutState", scoutState.getText());
                prop.setProperty("ScoutRank", scoutRank.getText());
                prop.setProperty("logoLoc", logoLoc.getText());
            }
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }
    private String getCityState(String zipCode) throws IOException {
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
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            //Fill String buffer with response
            while ((inputLine = in.readLine()) != null) {
                //inputLine = StringEscapeUtils.escapeHtml4(inputLine);
                //inputLine = StringEscapeUtils.escapeXml11(inputLine);
                response.append(inputLine);
            }


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


                    if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;


                        city = eElement.getElementsByTagName("city").item(0).getTextContent();
                        State = eElement.getElementsByTagName("state").item(0).getTextContent();


                        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Formats City and state into one string to return
        String fullName = city + '&';
        fullName += State;
        //print result
        //	return parseCoords(response.toString());
        return fullName;
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
                    FullName = getCityState(zip);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                String[] StateTown = FullName.split("&");
                String state = StateTown[1];
                String town = StateTown[0];
                if (north.getSelectedIndex() == 1) {
                    Town.setText(town);
                    State.setText(state);
                } else if (north.getSelectedIndex() == 3) {
                    scoutTown.setText(town);
                    scoutState.setText(state);
                }
            }
        }
    }
}
