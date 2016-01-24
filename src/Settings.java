import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

/**
 * Created by patrick on 12/24/15.
 */
class Settings extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private JTextField DbLoc;
    private JButton DbButton;
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

    private Settings() {
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
        setSize(450, 150);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());
        //North
        {
            JTabbedPane north = new JTabbedPane();
            JPanel general = new JPanel(new FlowLayout());
            {
                //Choose DB location
                {
                    DbLoc = new JTextField();
                    DbButton = new JButton("...");
                    DbButton.addActionListener(e -> {
                        //Creates a JFileChooser to select a directory to store the Databases
                        JFileChooser chooser = new JFileChooser();
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int returnVal = chooser.showOpenDialog(this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            DbLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                        }
                    });
                }
                //Export DB
                {
                    //Future options of alternate export types(csv, excel, xml?)
                }

            }
            north.addTab("General", general);
            JPanel AddCustomer = new JPanel(new BorderLayout());
            {
                //Set default options for add customer form

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
                    AddCustomer.add(North, BorderLayout.NORTH);
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
                    AddCustomer.add(South, BorderLayout.SOUTH);

                }
            }
            north.addTab("Add Customer", AddCustomer);

            JPanel MapOptions = new JPanel(new FlowLayout());
            {
                //Area to Display
                //Default zoom

            }
            north.addTab("Map", MapOptions);

            JPanel Report = new JPanel(new FlowLayout());
            {
                //Default Options for Reports
                {
                    //Default Type
                }
                {

                }


            }
            north.addTab("Reports", Report);

            JPanel Licesnce = new JPanel(new FlowLayout());
            {
                //Display Liscence info


            }
            north.addTab("Licence", Licesnce);
            contentPanel.add(north, BorderLayout.CENTER);
        }


        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            JButton okButton;
            {
                okButton = new JButton("OK");

                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            JButton cancelButton;
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            okButton.addActionListener(e -> dispose());
            okButton.setActionCommand("OK");

            cancelButton.addActionListener(e -> dispose());
            cancelButton.setActionCommand("Cancel");
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
                Town.setText(town);
                State.setText(state);
            }
        }
    }
}
