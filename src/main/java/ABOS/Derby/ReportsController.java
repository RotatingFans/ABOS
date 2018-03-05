/*
 * Copyright (c) Patrick Magauran 2018.
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

package ABOS.Derby;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.saxon.s9api.SaxonApiException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.CancellationException;

//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;

/**
 * Created by patrick on 12/24/15.
 */
@SuppressWarnings("WeakerAccess")

public class ReportsController {
    @FXML
    private ComboBox<Object> cmbxReportType;
    @FXML
    private TextField scoutName;
    @FXML
    private TextField scoutStAddr;
    @FXML
    private TextField scoutZip;
    @FXML
    private TextField scoutTown;
    @FXML
    private TextField scoutState;
    @FXML
    private TextField scoutPhone;
    @FXML
    private TextField scoutRank;
    @FXML
    private TextField logoLoc;
    @FXML
    private TextField pdfLoc;
    @FXML
    private ComboBox<Object> cmbxYears;
    @FXML
    private ComboBox<Object> cmbxCustomers;
    //private Label includeHeaderL;
    @FXML
    private TabPane reportTabPane;
    // --Commented out by Inspection (7/27/16 3:02 PM):private Object[][] rowDataF = new Object[0][];
    @FXML
    private Button nextButton;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private HBox categoryPane;
    @FXML
    private HBox customerPane;
    @FXML
    private HBox yearPane;

    @FXML
    private ComboBox<Object> cmbxCategory;
    @FXML
    private CheckBox includeHeader;

    private Reports reports;

    // --Commented out by Inspection (7/27/16 3:02 PM):private double totL = 0.0;
    // --Commented out by Inspection (7/27/16 3:02 PM):private double QuantL = 0.0;
    private String Splitting = "";
    private String repTitle = "";
    // --Commented out by Inspection (7/27/16 3:02 PM):private File xmlTempFile = null;
    // --Commented out by Inspection (7/27/16 3:02 PM):private File[] xmlTempFileA = null;
    // private ReportsWorker reportsWorker = null;



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

    /*private static Iterable<String> getYears() {
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
*/
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

    @FXML
    private void reportTypeChange(ActionEvent actionEvent) {
        ComboBox comboBox = (ComboBox) actionEvent.getSource();

        Object selected = comboBox.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nextButton.setDisable(false);
        }
    }

    @FXML
    private void selectedYearChanged(ActionEvent actionEvent) {
        //ComboBox comboBox = (ComboBox) actionEvent.getSource();

        Object selected = cmbxYears.getSelectionModel().getSelectedItem();
        if (cmbxReportType.getSelectionModel().getSelectedIndex() == 2) {
            if (selected != "") {
                Year year = new Year(selected.toString());
                Iterable<String> customersY = year.getCustomerNames();
                cmbxCustomers.getItems().removeAll();
                cmbxCustomers.getItems().add("");
                cmbxCustomers.getSelectionModel().select("");
                customersY.forEach(cmbxCustomers.getItems()::add);
                cmbxCustomers.setDisable(false);
                year.getCategories().forEach(category -> cmbxCategory.getItems().add(category.catName));

            }
        }
    }

    @FXML
    private void selectedCategoryChanged(ActionEvent actionEvent) {
        if (cmbxCategory.getSelectionModel().getSelectedItem().equals("All")) {
            includeHeader.setDisable(false);
            //includeHeaderL.setVisible(true);

        } else {
            includeHeader.setDisable(true);
            //includeHeaderL.setVisible(false);

        }
    }

    @FXML
    public void promptLogo(ActionEvent event) {
        //Creates a JFileChooser to select a directory to store the Databases
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.gif", "*.png", "*.bmp");
        chooser.getExtensionFilters().add(filter);

        chooser.setSelectedExtensionFilter(filter);
//        logoLoc.setText(chooser.showOpenDialog(settings).getAbsolutePath());
        File image = chooser.showOpenDialog(reports);
        if (image != null) {
            String path = image.getAbsolutePath();
            logoLoc.setText(path);
        }
    }

    @FXML
    public void promptPDF(ActionEvent event) {
        //Creates a JFileChooser to select save location of XML file
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Portable Document files", "*.pdf", "*.PDF");
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        File pdf = chooser.showSaveDialog(reports);
        if (pdf != null) {
            String path = pdf.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".pdf")) {
                path += ".pdf";
            }
            pdfLoc.setText(path);
        }
    }

    @FXML
    public void cancel(ActionEvent event) {
        close();
    }

    @FXML
    public void next(ActionEvent event) {
        updateCombos();
        nextButton.setDisable(true);
        okButton.setDisable(false);
        reportTabPane.getSelectionModel().select(reportTabPane.getSelectionModel().getSelectedIndex() + 1);
    }

    @FXML
    public void submit(ActionEvent actionEvent) {
        String addrFormat = scoutTown.getText() + ' ' + scoutState.getText() + ", " + scoutZip.getText();
        switch (cmbxReportType.getSelectionModel().getSelectedIndex()) {
            case 1:
                repTitle = "Year of " + cmbxYears.getSelectionModel().getSelectedItem();
                Splitting = "Year:";

                break;
            case 2:
                repTitle = cmbxCustomers.getSelectionModel().getSelectedItem() + " " + cmbxYears.getSelectionModel().getSelectedItem();
                Splitting = "";

                break;
            case 3:
                repTitle = cmbxCustomers.getSelectionModel().getSelectedItem() + " " + cmbxYears.getSelectionModel().getSelectedItem();
                Splitting = "";

                break;
            case 4:
                repTitle = "All orders of " + cmbxCustomers.getSelectionModel().getSelectedItem();
                Splitting = "Year:";

                break;

        }
        ProgressForm progDial = new ProgressForm();
        String selectedYear = (cmbxYears.getSelectionModel().getSelectedItem() != null) ? cmbxYears.getSelectionModel().getSelectedItem().toString() : "";
        String selectedCustomer = (cmbxCustomers.getSelectionModel().getSelectedItem() != null) ? cmbxCustomers.getSelectionModel().getSelectedItem().toString() : "";

        ReportsWorker reportsWorker = new ReportsWorker(cmbxReportType.getSelectionModel().getSelectedItem().toString(), selectedYear, scoutName.getText(), scoutStAddr.getText(), addrFormat, scoutRank.getText(), scoutPhone.getText(), logoLoc.getText(), cmbxCategory.getSelectionModel().getSelectedItem().toString(), selectedCustomer, repTitle, Splitting, includeHeader.isSelected(), pdfLoc.getText());

        progDial.activateProgressBar(reportsWorker);

        reportsWorker.setOnSucceeded(event -> {
            progDial.getDialogStage().close();

            try {
                if (Desktop.isDesktopSupported()) {
                    new Thread(() -> {
                        try {
                            File myFile = new File(pdfLoc.getText());
                            Desktop.getDesktop().open(myFile);
                        } catch (IOException ex) {
                            LogToFile.log(ex, Severity.SEVERE, "Error writing pdf file. Please try again or contacting support.");
                        }
                    }).start();

                }
            } catch (CancellationException e1) {
                LogToFile.log(e1, Severity.INFO, "The process was cancelled.");

            } catch (Exception e1) {
                LogToFile.log(e1, Severity.SEVERE, "The process failed.");

            }
            close();
        });

        reportsWorker.setOnFailed(event -> {
            progDial.getDialogStage().close();
            Throwable e = reportsWorker.getException();

            if (e instanceof ParserConfigurationException) {
                LogToFile.log((ParserConfigurationException) e, Severity.WARNING, "Error configuring parser. Please reinstall or contact support.");

            }
            if (e instanceof SQLException) {
                LogToFile.log((SQLException) e, Severity.SEVERE, CommonErrors.returnSqlMessage(((SQLException) reportsWorker.getException())));

            }
            if (e instanceof InterruptedException) {
                if (reportsWorker.isCancelled()) {
                    LogToFile.log((InterruptedException) e, Severity.FINE, "Add Customer process canceled.");

                }
            }
            if (e instanceof Exception) {
                LogToFile.log((Exception) e, Severity.WARNING, reportsWorker.getMessage());
            }
            if (e instanceof FileNotFoundException) {
                LogToFile.log((FileNotFoundException) e, Severity.WARNING, "Error accessing PDF file. Please check if it is open in any other programs and close it.");
            }
            if (e instanceof IOException) {
                LogToFile.log((IOException) e, Severity.WARNING, reportsWorker.getMessage());
            }
            if (e instanceof SaxonApiException) {
                LogToFile.log((SaxonApiException) e, Severity.SEVERE, "Error converting temporary XML to pdf. Try again or contact support.");
            }
        });
        progDial.getDialogStage().show();
        reportsWorker.run();


    }

    private void close() {
        Stage stage = (Stage) pdfLoc.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    //SetBounds(X,Y,Width,Height)
    public void initUI(Reports reps) {
        reports = reps;

        cmbxReportType.getSelectionModel().select(Config.getProp("ReportType"));
        // includeHeaderL.setVisible(false);
        scoutName.setText(Config.getProp("ScoutName"));
        scoutStAddr.setText(Config.getProp("ScoutAddress"));
        scoutZip.setText(Config.getProp("ScoutZip"));
        scoutTown.setText(Config.getProp("ScoutTown"));
        scoutState.setText(Config.getProp("ScoutState"));
        scoutPhone.setText(Config.getProp("ScoutPhone"));
        scoutRank.setText(Config.getProp("ScoutRank"));
        logoLoc.setText(Config.getProp("logoLoc"));
        pdfLoc.setText(Config.getProp("pdfLoc"));

        scoutZip.setOnKeyTyped(keyEvent -> {
            if (scoutZip.getCharacters().length() >= 4) {
                String zip = scoutZip.getText() + keyEvent.getCharacter();

                String cityAndState;
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


    }

    private void updateCombos() {
        Iterable<String> years = DbInt.getYears();

        switch (cmbxReportType.getSelectionModel().getSelectedItem().toString()) {

            case "Year Totals":
                yearPane.setDisable(false);
                customerPane.setDisable(true);
                cmbxYears.getItems().clear();
                years.forEach(cmbxYears.getItems()::add);
                //cmbxYears.

                cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
                break;
            case "Year Totals; Spilt by Customer":
                yearPane.setDisable(false);
                customerPane.setDisable(true);
                cmbxYears.getItems().clear();
                //cmbxYears.getItems().addAll(years);
                years.forEach(cmbxYears.getItems()::add);
                cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
                break;
            case "Customer Year Totals":
                yearPane.setDisable(false);
                customerPane.setDisable(false);
                cmbxYears.getItems().removeAll();
                years.forEach(cmbxYears.getItems()::add);
                cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
                cmbxCustomers.setDisable(false);


                break;
            case "Customer All-time Totals":
                yearPane.setDisable(true);
                customerPane.setDisable(false);
                cmbxCustomers.getItems().removeAll();
                Iterable<String> customers = DbInt.getAllCustomers();
                cmbxCustomers.getItems().add("");
                cmbxCustomers.getSelectionModel().select("");
                customers.forEach(cmbxCustomers.getItems()::add);
                cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
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


}