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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.saxon.s9api.SaxonApiException;

import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
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
    private ComboBox<TreeItemPair<String, String>> cmbxUser;
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
    private ComboBox<TreeItemPair<String, ArrayList<Customer>>> cmbxCustomers;
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
    private HBox userPanel;

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



    @FXML
    private void reportTypeChange(ActionEvent actionEvent) {
        ComboBox comboBox = (ComboBox) actionEvent.getSource();

        Object selected = comboBox.getSelectionModel().getSelectedItem();
        reportTabPane.getTabs().get(1).setDisable(true);
        okButton.setDisable(true);
        if (selected != null) {
            nextButton.setDisable(false);

        }
    }

    @FXML
    private void selectedYearChanged(ActionEvent actionEvent) {

        //ComboBox comboBox = (ComboBox) actionEvent.getSource();
        if (cmbxReportType.getSelectionModel().getSelectedIndex() != 3) {
            userPanel.setDisable(false);
            String selected = cmbxYears.getSelectionModel().getSelectedItem().toString();
            User curUser = DbInt.getUser(selected);
            Iterable<String> uManage = curUser.getuManage();
            cmbxUser.getItems().clear();
            cmbxUser.getItems().addAll(new TreeItemPair<String, String>("All"), new TreeItemPair<String, String>("Yourself", curUser.getUserName()));
            for (String user : uManage) {
                cmbxUser.getItems().add(new TreeItemPair<String, String>(user, user));
            }
            cmbxUser.getSelectionModel().select(1);
        }
    }

    @FXML
    private void selectedUserChanged(ActionEvent actionEvent) {
        //ComboBox comboBox = (ComboBox) actionEvent.getSource();
        switch (cmbxReportType.getSelectionModel().getSelectedItem().toString()) {

            case "Year Totals":
                okButton.setDisable(cmbxUser.getSelectionModel().getSelectedItem() == null);
                break;
            case "Year Totals; Spilt by Customer":
                okButton.setDisable(cmbxUser.getSelectionModel().getSelectedItem() == null);
                break;
            case "Customer Year Totals":
                okButton.setDisable(true);
                break;
            case "Customer All-Time Totals":
                okButton.setDisable(true);
                break;
        }
        if (cmbxReportType.getSelectionModel().getSelectedIndex() != 3) {

            String selectedYear = cmbxYears.getSelectionModel().getSelectedItem().toString();
            String selectedUser = cmbxUser.getSelectionModel().getSelectedItem().getValue();
            cmbxCustomers.getItems().clear();

            if (cmbxReportType.getSelectionModel().getSelectedIndex() == 2) {
                if (!Objects.equals(selectedYear, "")) {
                    Year year;
                    if (Objects.equals(selectedUser, "")) {
                        year = new Year(selectedYear);
                    } else {
                        year = new Year(selectedYear, selectedUser);

                    }
                    Iterable<Customer> customersY = year.getCustomers();
                    cmbxCustomers.getItems().removeAll();
                    customersY.forEach(customer -> {
                        ArrayList custList = new ArrayList();
                        custList.add(customer);
                        cmbxCustomers.getItems().add(new TreeItemPair<String, ArrayList<Customer>>(customer.getName(), custList));
                    });
                    cmbxCustomers.setDisable(false);
                }
            }
            if (!Objects.equals(selectedYear, "")) {
                Year year;
                if (Objects.equals(selectedUser, "")) {
                    year = new Year(selectedYear);
                } else {
                    year = new Year(selectedYear, selectedUser);

                }
                cmbxCategory.getItems().clear();
                cmbxCategory.getItems().add("All");
                year.getCategories().forEach(category -> cmbxCategory.getItems().add(category.catName));
                cmbxCategory.getSelectionModel().selectFirst();
            }
        }
    }

    @FXML
    private void selectedCustomerChanged(ActionEvent actionEvent) {
        switch (cmbxReportType.getSelectionModel().getSelectedItem().toString()) {

            case "Year Totals":
                okButton.setDisable(true);
                break;
            case "Year Totals; Spilt by Customer":
                okButton.setDisable(true);
                break;
            case "Customer Year Totals":
                okButton.setDisable(cmbxCustomers.getSelectionModel().getSelectedItem() == null);
                break;
            case "Customer All-Time Totals":
                okButton.setDisable(cmbxCustomers.getSelectionModel().getSelectedItem() == null);
                break;
        }
    }
    @FXML
    private void selectedCategoryChanged(ActionEvent actionEvent) {

        includeHeader.setDisable(cmbxReportType.getSelectionModel().getSelectedIndex() == 4 || cmbxReportType.getSelectionModel().getSelectedIndex() == 0 || cmbxCategory.getSelectionModel().getSelectedItem().equals("All"));
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
        FileChooser.ExtensionFilter filter;
        String ending;
        if (cmbxReportType.getSelectionModel().getSelectedIndex() == 4) {
            filter = new FileChooser.ExtensionFilter("CSV files", "*.csv", "*.CSV");
            ending = ".csv";

        } else {
            filter = new FileChooser.ExtensionFilter("Portable Document files", "*.pdf", "*.PDF");
            ending = ".pdf";

        }
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        chooser.setInitialDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
        File pdf = chooser.showSaveDialog(reports);
        if (pdf != null) {
            String path = pdf.getAbsolutePath();
            if (!path.toLowerCase().endsWith(ending)) {
                path += ending;
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
        // okButton.setDisable(false);
        reportTabPane.getSelectionModel().select(reportTabPane.getSelectionModel().getSelectedIndex() + 1);
        reportTabPane.getTabs().get(1).setDisable(false);
    }

    @FXML
    public void submit(ActionEvent actionEvent) {
        String addrFormat = scoutTown.getText() + ' ' + scoutState.getText() + ", " + scoutZip.getText();
        switch (cmbxReportType.getSelectionModel().getSelectedIndex()) {
            case 0:
                repTitle = "Year of " + cmbxYears.getSelectionModel().getSelectedItem();
                Splitting = "";

                break;
            case 1:
                repTitle = "";
                Splitting = "";

                break;
            case 2:
                repTitle = cmbxCustomers.getSelectionModel().getSelectedItem() + " " + cmbxYears.getSelectionModel().getSelectedItem() + " Order";
                Splitting = "";

                break;
            case 3:
                repTitle = "All orders of " + cmbxCustomers.getSelectionModel().getSelectedItem();
                Splitting = "Year:";

                break;
            case 4:
                ProgressForm progDial = new ProgressForm();
                orderHistoryReportWorker reportsWorker = new orderHistoryReportWorker(pdfLoc.getText());

                progDial.activateProgressBar(reportsWorker);

                reportsWorker.setOnSucceeded(event -> {
                    progDial.getDialogStage().close();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Saved");
                    alert.setHeaderText("CSV saved");
                    alert.show();
                    close();
                });

                reportsWorker.setOnFailed(event -> {
                    progDial.getDialogStage().close();
                    Throwable e = reportsWorker.getException();


                    if (e instanceof SQLException) {
                        LogToFile.log((SQLException) e, Severity.SEVERE, CommonErrors.returnSqlMessage(((SQLException) reportsWorker.getException())));

                    }
                    if (e instanceof InterruptedException) {
                        if (reportsWorker.isCancelled()) {
                            LogToFile.log((InterruptedException) e, Severity.FINE, "Report generation process canceled.");

                        }
                    }
                    if (e instanceof Exception) {
                        LogToFile.log((Exception) e, Severity.WARNING, reportsWorker.getMessage());
                    }
                    if (e instanceof FileNotFoundException) {
                        LogToFile.log((FileNotFoundException) e, Severity.WARNING, "Error accessing CSV file. Please check if it is open in any other programs and close it.");
                    }
                    if (e instanceof FileSystemException) {
                        LogToFile.log((FileSystemException) e, Severity.WARNING, "Error opening file for writing. Ensure path is correct.");
                    }
                    if (e instanceof IOException) {
                        LogToFile.log((IOException) e, Severity.WARNING, reportsWorker.getMessage());
                    }

                });
                progDial.getDialogStage().show();
                new Thread(reportsWorker).start();
                return;


        }
        ProgressForm progDial = new ProgressForm();
        String selectedYear = (cmbxYears.getSelectionModel().getSelectedItem() != null) ? cmbxYears.getSelectionModel().getSelectedItem().toString() : "";
        String selectedUser = (cmbxUser.getSelectionModel().getSelectedItem() != null) ? cmbxUser.getSelectionModel().getSelectedItem().getValue() : "";
        String selectedCategory = (cmbxCategory.getSelectionModel().getSelectedItem() != null) ? cmbxCategory.getSelectionModel().getSelectedItem().toString() : "";
        ArrayList<Customer> selectedCustomers = (cmbxCustomers.getSelectionModel().getSelectedItem() != null) ? cmbxCustomers.getSelectionModel().getSelectedItem().getValue() : new ArrayList<Customer>();

        ReportsWorker reportsWorker = new ReportsWorker(cmbxReportType.getSelectionModel().getSelectedItem().toString(), selectedYear, scoutName.getText(), scoutStAddr.getText(), addrFormat, scoutRank.getText(), scoutPhone.getText(), logoLoc.getText(), selectedCategory, selectedUser, selectedCustomers, repTitle, Splitting, includeHeader.isSelected(), pdfLoc.getText());

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
                    LogToFile.log((InterruptedException) e, Severity.FINE, "Report Generation process canceled.");

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
        new Thread(reportsWorker).start();


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
        Iterable<String> years = DbInt.getUserYears();
        cmbxCategory.getItems().clear();
        cmbxUser.getItems().clear();
        cmbxCustomers.getItems().clear();
        cmbxYears.getItems().clear();
        switch (cmbxReportType.getSelectionModel().getSelectedItem().toString()) {

            case "Year Totals":
                userPanel.setDisable(false);
                yearPane.setDisable(false);
                customerPane.setDisable(true);
                cmbxYears.getItems().clear();
                //cmbxYears.getItems().add("");
                categoryPane.setDisable(false);
                includeHeader.setDisable(true);
                years.forEach(cmbxYears.getItems()::add);

                //cmbxYears.
                //cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
                break;
            case "Year Totals; Spilt by Customer":
                userPanel.setDisable(false);

                yearPane.setDisable(false);
                customerPane.setDisable(true);
                cmbxYears.getItems().clear();
                //cmbxYears.getItems().addAll(years);
                years.forEach(cmbxYears.getItems()::add);
                categoryPane.setDisable(false);
                includeHeader.setDisable(false);

                // cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
                break;
            case "Customer Year Totals":
                userPanel.setDisable(false);

                yearPane.setDisable(false);
                customerPane.setDisable(false);
                cmbxYears.getItems().removeAll();
                years.forEach(cmbxYears.getItems()::add);
                //    cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
                cmbxCustomers.setDisable(false);
                categoryPane.setDisable(false);
                includeHeader.setDisable(false);


                break;
            case "Customer All-Time Totals":
                userPanel.setDisable(true);
                categoryPane.setDisable(true);
                cmbxUser.getItems().add(new TreeItemPair<String, String>("Yourself", DbInt.getUserName()));
                cmbxUser.getSelectionModel().selectFirst();
                yearPane.setDisable(true);
                customerPane.setDisable(false);
                cmbxCustomers.getItems().removeAll();
                Iterable<Customer> customers = DbInt.getAllCustomers();

                HashMap<String, ArrayList<Customer>> curCustomers = new HashMap<>();
                customers.forEach(customer -> {
                    if (curCustomers.containsKey(customer.getName())) {
                        curCustomers.get(customer.getName()).add(customer);
                    } else {
                        ArrayList<Customer> customerArrayList = new ArrayList<>();
                        customerArrayList.add(customer);
                        curCustomers.put(customer.getName(), customerArrayList);
                    }
                });
                curCustomers.forEach((name, customersList) -> {
                    cmbxCustomers.getItems().add(new TreeItemPair<String, ArrayList<Customer>>(name, customersList));

                });
                includeHeader.setDisable(true);

                //      cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
                break;
            case "Address Order History":
                userPanel.setDisable(true);
                categoryPane.setDisable(true);
                yearPane.setDisable(true);
                customerPane.setDisable(true);
                includeHeader.setDisable(true);
                okButton.setDisable(false);
                if (!pdfLoc.getText().toLowerCase().endsWith(".csv")) {
                    pdfLoc.setText("");
                }
                //      cmbxYears.getSelectionModel().select(cmbxYears.getItems().size() - 1);
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