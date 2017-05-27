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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

//import javax.swing.border.EmptyBorder;
//import java.awt.*;

/**
 * Created by patrick on 12/24/15.
 */
public class SettingsController{
    //private final JPanel contentPanel = new JPanel();
    //private JTabbedPane north;
    //General
    @FXML private TextField DbLoc;
    @FXML private CheckBox CreateDb;
    //Add Customer
    @FXML private CheckBox Delivered;
    @FXML private CheckBox Paid;
    @FXML private TextField Name;
    @FXML private TextField Address;
    @FXML private TextField ZipCode;
    @FXML private TextField Town;
    @FXML private TextField State;
    @FXML private TextField Phone;
    @FXML private TextField Email;
    @FXML private TextField DonationsT;
    //Report
    @FXML private ComboBox<Object> cmbxReportType;
    @FXML private TextField scoutName;
    @FXML private TextField scoutStAddr;
    @FXML private TextField scoutZip;
    @FXML private TextField scoutTown;
    @FXML private TextField scoutState;
    @FXML private TextField scoutPhone;
    @FXML private TextField scoutRank;
    @FXML private TextField logoLoc;
    @FXML private TextField pdfLoc;
    private Settings settings;


    public SettingsController() {

    }
/*

    public static void main(String... args) {
        try {
            new Settings();

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
*/

   /* private static void open(URI uri) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) { *//* TODO: error handling *//* }
        } else { *//* TODO: error handling *//* }
    }*/

    @FXML public void promptDB(ActionEvent event) {
        //Creates a JFileChooser to select a directory to store the Databases
        DirectoryChooser chooser = new DirectoryChooser();
        DbLoc.setText(chooser.showDialog(settings).getAbsolutePath());
    }

    @FXML public void createDbChecked(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("WARNING!");
        alert.setHeaderText("SELECTING THIS WILL DELETE ALL DATA AT THE SPECIFIED LOCATION!");
        alert.setContentText("Would you like to continue?");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.CANCEL) {
            CreateDb.setSelected(false);
        }

    }
    @FXML public void promptLogo(ActionEvent event){
        //Creates a JFileChooser to select a directory to store the Databases
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.gif","*.png", "*.bmp");
        chooser.getExtensionFilters().add(filter);

        chooser.setSelectedExtensionFilter(filter);
//        logoLoc.setText(chooser.showOpenDialog(settings).getAbsolutePath());
        File image = chooser.showOpenDialog(settings);
        if (image != null) {
            String path = image.getAbsolutePath();
            logoLoc.setText(path);
        }
    }

    @FXML public void promptPDF(ActionEvent event){
        //Creates a JFileChooser to select save location of XML file
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Portable Document files", "*.pdf", "*.PDF");
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        File pdf = chooser.showSaveDialog(settings);
        if (pdf != null) {
            String path = pdf.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }
            pdfLoc.setText(path);
        }
    }

    @FXML public void submit(ActionEvent event) {
        saveData();
        // get a handle to the stage

        Stage stage = (Stage) pdfLoc.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML public void cancel(ActionEvent event) {
        Stage stage = (Stage) pdfLoc.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    //SetBounds(X,Y,Width,Height)
    public void initUI(Settings settingsWindow) {
        settings = settingsWindow;
        if (!Config.doesConfExist()) {
            Config.createConfigFile();
        }
        //Main Content


        DbLoc.setText(Config.getDbLoc());



        CreateDb.setSelected(!Config.doesConfExist());




        Name.setText(Config.getProp("CustomerName"));

        Address.setText(Config.getProp("CustomerAddress"));

        ZipCode.setText(Config.getProp("CustomerZipCode"));

        ZipCode.setOnKeyTyped(keyEvent -> {
            if (ZipCode.getCharacters().length() >= 4) {
                String zip = ZipCode.getText() + keyEvent.getCharacter();

                String cityAndState = "";
                try {
                    cityAndState = Geolocation.getCityState(zip);
                } catch (IOException e1) {
                    LogToFile.log(e1, Severity.WARNING, "Couldn't contact geolocation service. Please try again or enter the adress manually and contact suport.");
                }
                String[] StateTown = cityAndState.split("&");
                String state = StateTown[1];
                String town = StateTown[0];
                Town.setText(town);
                State.setText(state);
            }
        });
        Town.setText(Config.getProp("CustomerTown"));

        State.setText(Config.getProp("CustomerState"));


        Phone.setText(Config.getProp("CustomerPhone"));

        Email.setText(Config.getProp("CustomerEmail"));

        Paid.setSelected(Boolean.valueOf(Config.getProp("CustomerPaid")));

        Delivered.setSelected(Boolean.valueOf(Config.getProp("CustomerDelivered")));

        DonationsT.setText(Config.getProp("CustomerDonations"));

        if (Config.getProp("CustomerDonations") == null) {
            DonationsT.setText("0.0");
        }


        scoutName.setText(Config.getProp("ScoutName"));
        scoutStAddr.setText(Config.getProp("ScoutAddress"));
        scoutZip.setText(Config.getProp("ScoutZip"));
        scoutTown.setText(Config.getProp("ScoutTown"));
        scoutState.setText(Config.getProp("ScoutState"));
        scoutPhone.setText(Config.getProp("ScoutPhone"));

        scoutRank.setText(Config.getProp("ScoutRank"));
        logoLoc.setText(Config.getProp("logoLoc"));
        scoutZip.setOnKeyTyped(keyEvent -> {
            if (scoutZip.getCharacters().length() >= 4) {
                String zip = scoutZip.getText() + keyEvent.getCharacter();

                String cityAndState = "";
                try {
                    cityAndState = Geolocation.getCityState(zip);
                    String[] StateTown = cityAndState.split("&");
                    String state = StateTown[1];
                    String town = StateTown[0];
                    scoutTown.setText(town);
                    scoutState.setText(state);
                } catch (IOException e1) {
                    LogToFile.log(e1, Severity.WARNING, "Couldn't contact geolocation service. Please try again or enter the adress manually and contact suport.");
                }

            }
        });
        pdfLoc.setText(Config.getProp("pdfLoc"));



/*                final URI uri;
                try {
                    uri = new URI("https://www.gnu.org/licenses/agpl.html");

                    class OpenUrlAction implements ActionListener {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            open(uri);
                        }
                    }
                    JButton button = new JButton();
                    button.setText("<HTML><h2>This software is released under the AGPLv3 license.</h2> Click <FONT size=14px color=\"#000099\"><U>Here</U></FONT>"
                            + " to access the AGPLv3 license.</HTML>");
                    button.setHorizontalAlignment(SwingConstants.LEFT);
                    button.setBorderPainted(false);
                    button.setOpaque(false);
                    button.setBackground(Color.WHITE);
                    button.setToolTipText(uri.toString());
                    button.addActionListener(new OpenUrlAction());
                    License.add(button);

                } catch (URISyntaxException ignored) {

                }
                JLabel libs = new JLabel("<HTML><h2>Included Libraries:</h2>" +
                        "<ul>" +
                        "<li>jDatePicker Version 1.3.4</li>" +
                        "<li>Apache Derby Version 10.11</li>" +
                        "<li>iText Version 5.5.10</li>" +
                        "<li>JMapViewer Version 1.0.0</li>" +
                        "<li>JTidy Version 938</li>" +
                        "<li>Saxon Version 9</li>" +
                        "</ul></HTML>");
                License.add(libs);
            }
            north.addTab("License", License);
            contentPanel.add(north, BorderLayout.CENTER);
        */


    }

    private void saveData() {
        //General
        //If firstRun Create DB, if not, update Db Location
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream("./LGconfig.properties");

            //Add DB setting
            if (Config.doesConfExist() && !CreateDb.isSelected()) {
                prop.setProperty("databaseLocation", DbLoc.getText());
            } else if (!Config.doesConfExist() || CreateDb.isSelected()) {
                prop.setProperty("databaseLocation", DbLoc.getText());
                prop.store(output, null);
                prop = new Properties();

                DbInt.createDb("Set");

                try (PreparedStatement prep = DbInt.getPrep("Set", "CREATE TABLE Customers(CustomerID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), Address varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), Ordered VARChAR(255), NI VARChAR(255), NH VARChAR(255))")) {
                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
                try (PreparedStatement prep = DbInt.getPrep("Set", "CREATE TABLE YEARS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), YEARS varchar(255))")) {
                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
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
                prop.setProperty("ReportType", cmbxReportType.getSelectionModel().getSelectedIndex() >= 0 ? cmbxReportType.getSelectionModel().getSelectedItem().toString() : "");
                prop.setProperty("ScoutName", scoutName.getText());
                prop.setProperty("ScoutAddress", scoutStAddr.getText());
                prop.setProperty("ScoutZip", scoutZip.getText());
                prop.setProperty("ScoutTown", scoutTown.getText());
                prop.setProperty("ScoutState", scoutState.getText());
                prop.setProperty("ScoutPhone", scoutPhone.getText());

                prop.setProperty("ScoutRank", scoutRank.getText());
                prop.setProperty("logoLoc", logoLoc.getText());
                prop.setProperty("pdfLoc", pdfLoc.getText());

            }
            prop.store(output, null);

        } catch (IOException io) {
            LogToFile.log(io, Severity.SEVERE, "Error writing settings file. Please try again.");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    LogToFile.log(e, Severity.SEVERE, "Error closing settings file. Please try again.");
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
                LogToFile.log(e, Severity.SEVERE, "Error parsing geolocation response. Please try again later or contacting support.");
            }
        }
        //Formats City and state into one string to return
        String fullName = city + '&';
        fullName += State;
        //print result
        //	return parseCoords(response.toString());
        return fullName;
    }




}
