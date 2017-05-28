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

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.*;
public class AddYearController {

    @FXML
    private TextField yearText;
    @FXML
    private TableView<Product.formattedProductProps> ProductTable;
    @FXML
    private TextField itemTb;
    @FXML
    private TextField sizeTb;
    @FXML
    private TextField rateTb;
    @FXML
    private TextField idTb;
    //private final JDialog parent;
    private Collection<String[]> rowsCats = new ArrayList<>();
    @FXML
    private CheckBox chkboxCreateDatabase;
    private ObservableList<String> categoriesTb = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> categoriesCmbx;
    //private DefaultTableModel tableModel;
    private boolean newYear = false;
    private ObservableList<Product.formattedProductProps> data = FXCollections.observableArrayList();
    private Window parentWindow;

    public AddYearController() {}

    private static Iterable<String[]> getCategories(String year) {
        Collection<String[]> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM Categories");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(new String[]{rs.getString("NAME"), rs.getString("DATE")});

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


        return ret;
    }

    /**
     * Create the dialog.
     */

    @FXML
    private void tableFrmXML(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files", "*.xml", "*.XML");
        chooser.getExtensionFilters().add(filter);

        chooser.setSelectedExtensionFilter(filter);
//        logoLoc.setText(chooser.showOpenDialog(settings).getAbsolutePath());
        File xmlFile = chooser.showOpenDialog(parentWindow);
        if (xmlFile != null) {
            String path = xmlFile.getAbsolutePath();
            createTable(path);
        }
    }

    private void convert(String csvLoc, String xmlLoc) {
        List<String> headers = new ArrayList<>(5);


        File file = new File(csvLoc);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

            Document newDoc = domBuilder.newDocument();
            // Root element
            Element rootElement = newDoc.createElement("LawnGarden");
            newDoc.appendChild(rootElement);

            int line = 0;

            String text;
            while ((text = reader.readLine()) != null) {

                StringTokenizer st = new StringTokenizer(text, ";", false);
                String[] rowValues = new String[st.countTokens()];
                int index = 0;
                while (st.hasMoreTokens()) {

                    String next = st.nextToken();
                    rowValues[index] = next;
                    index++;

                }

                //String[] rowValues = text.split(",");

                if (line == 0) { // Header row
                    Collections.addAll(headers, rowValues);
                } else { // Data row
                    Element rowElement = newDoc.createElement("Products");
                    rootElement.appendChild(rowElement);
                    Attr attr = newDoc.createAttribute("id");
                    attr.setValue(Integer.toString(line - 1));
                    rowElement.setAttributeNode(attr);
                    for (int col = 0; col < headers.size(); col++) {
                        String header = headers.get(col);
                        String value;

                        if (col < rowValues.length) {
                            value = rowValues[col].trim();
                        } else {
                            // ?? Default value
                            value = "";
                        }

                        Element curElement = newDoc.createElement(header);
                        curElement.appendChild(newDoc.createTextNode(value));
                        rowElement.appendChild(curElement);
                    }
                }
                line++;
            }

            OutputStreamWriter osw = null;

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                osw = new OutputStreamWriter(baos);

                TransformerFactory tranFactory = TransformerFactory.newInstance();
                Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                //aTransformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                Source src = new DOMSource(newDoc);
                Result result = new StreamResult(osw);
                aTransformer.transform(src, result);

                osw.flush();
                //System.out.println(new String(baos.toByteArray()));

                try (OutputStream outStream = new FileOutputStream(xmlLoc)) {// writing bytes in to byte output stream

                    baos.writeTo(outStream);
                } catch (IOException e) {
                    LogToFile.log(e, Severity.SEVERE, "Error writing XML file. Please try again.");
                }


            } catch (Exception exp) {
                LogToFile.log(exp, Severity.SEVERE, "Error writing XML file. Please try again.");
            } finally {
                try {
                    osw.close();
                } catch (IOException e) {
                    LogToFile.log(e, Severity.SEVERE, "Error closing file. Please try again.");
                }

            }
        } catch (Exception e) {
            LogToFile.log(e, Severity.SEVERE, "Error reading CSV file. Ensure the path exists, and the software has permission to read it.");
        }
    }

    @FXML
    private void csvToXml(ActionEvent event) {
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("CSV to XML conversion");

// Set the button types.
        ButtonType convertButtonType = new ButtonType("Convert", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(convertButtonType, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField csvLoc = new TextField();
        csvLoc.setPromptText("CSV file Location");
        TextField xmlLoc = new TextField();
        xmlLoc.setPromptText("XML Location");
        Button getCsvLoc = new Button("...");
        getCsvLoc.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("CSV files", "*.csv", "*.CSV");
            chooser.getExtensionFilters().add(filter);
            chooser.setSelectedExtensionFilter(filter);
            File csv = chooser.showOpenDialog(grid.getScene().getWindow());
            if (csv != null) {
                String path = csv.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".csv")) {
                    path += ".csv";
                }
                csvLoc.setText(path);
            }
        });
        Button getXmlLoc = new Button("...");
        getXmlLoc.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files", "*.xml", "*.XML");
            chooser.getExtensionFilters().add(filter);
            chooser.setSelectedExtensionFilter(filter);
            File XML = chooser.showSaveDialog(grid.getScene().getWindow());
            if (XML != null) {
                String path = XML.getAbsolutePath();
                if (!path.toLowerCase().endsWith(".xml")) {
                    path += ".xml";
                }
                xmlLoc.setText(path);
            }
        });
        grid.add(new Label("CSV file Location:"), 0, 0);
        grid.add(csvLoc, 1, 0);
        grid.add(getCsvLoc, 2, 0);
        grid.add(new Label("XML Location:"), 0, 1);
        grid.add(xmlLoc, 1, 1);
        grid.add(getXmlLoc, 2, 1);


// Enable/Disable login button depending on whether a username was entered.
        javafx.scene.Node convertButton = dialog.getDialogPane().lookupButton(convertButtonType);
        convertButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        csvLoc.textProperty().addListener((observable, oldValue, newValue) -> {
            convertButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> csvLoc.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == convertButtonType) {
                return new Pair<>(csvLoc.getText(), xmlLoc.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(fileLocations -> {
            convert(fileLocations.getKey(), fileLocations.getValue());
            createTable(fileLocations.getValue());
        });




/*        CSV2XML csv = new CSV2XML(parent);
        String xmlFile = csv.getXML();
        if (!xmlFile.isEmpty()) {
            createTable(xmlFile);
        }*/
    }

    @FXML
    private void catCmbxChanged(ActionEvent event) {
        if (Objects.equals(categoriesCmbx.getSelectionModel().getSelectedItem(), "Add Category")) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Add new category");

// Set the button types.
            ButtonType addCat = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addCat, ButtonType.CANCEL);

// Create the username and password labels and fields.
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField catName = new TextField();
            catName.setPromptText("Category Name");
            DatePicker catDate = new DatePicker(LocalDate.now());
            catDate.setPromptText("Category Due Date");

            grid.add(new Label("Category Name:"), 0, 0);
            grid.add(catName, 1, 0);
            grid.add(new Label("Category Due Date:"), 0, 1);
            grid.add(catDate, 1, 1);


// Enable/Disable login button depending on whether a username was entered.
            javafx.scene.Node addCatButton = dialog.getDialogPane().lookupButton(addCat);
            addCatButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
            catName.textProperty().addListener((observable, oldValue, newValue) -> {
                addCatButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
            Platform.runLater(() -> catName.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addCat) {
                    return new Pair<String, String>(catName.getText(), catDate.getValue().toString());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(category -> {
                rowsCats.add(new String[]{category.getKey(), category.getValue()});
                refreshCmbx();

            });



        }
    }

    @FXML
    private void addBtnPressed(ActionEvent event) {
        int count = ProductTable.getItems().size() + 1;
        data.add(new Product.formattedProductProps(idTb.getText(), itemTb.getText(), sizeTb.getText(), rateTb.getText(), categoriesCmbx.getSelectionModel().getSelectedItem(), 0, BigDecimal.ZERO));
        ProductTable.setItems(data);
    }

    @FXML
    private void submit(ActionEvent event) {
        if (chkboxCreateDatabase.isSelected()) {
            CreateDb();
        } else if (newYear) {
            addYear();
            updateDb(yearText.getText());
        } else {
            updateDb(yearText.getText());
        }

        close();
    }

    @FXML
    private void cancel(ActionEvent event) {
        close();
    }

    private void close() {
        Stage stage = (Stage) yearText.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    public void initAddYear(Window parWindow) {
        parentWindow = parWindow;
        newYear = true;

        yearText.setText(Integer.toString(Calendar.getInstance().get(Calendar.YEAR)));
        categoriesTb.addAll("", "Add Category");
        categoriesCmbx.getItems().setAll(categoriesTb);
        String[][] columnNames = {{"ID", "productID"}, {"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}};
        for (String[] column : columnNames) {
            javafx.scene.control.TableColumn<Product.formattedProductProps, String> tbCol = new javafx.scene.control.TableColumn<>(column[0]);
            tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
            tbCol.setCellFactory(TextFieldTableCell.forTableColumn());
            ProductTable.getColumns().add(tbCol);
        }
        javafx.scene.control.TableColumn<Product.formattedProductProps, String> categoryColumn = new javafx.scene.control.TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));

        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categoriesTb));

        categoryColumn.setOnEditCommit(t -> {
            t.getRowValue().productCategory.set(t.getNewValue());
            data.get(t.getTablePosition().getRow()).productCategory.set(t.getNewValue());
            if (Objects.equals(categoriesCmbx.getSelectionModel().getSelectedItem(), "Add Category")) {
                AddCategory addCat = new AddCategory();
                String[] cat = addCat.showDialog();
                if (!Objects.equals(cat[0], "") && cat[0] != null) {
                    rowsCats.add(new String[]{cat[0], cat[1]});
                    refreshCmbx();
                }


            }
        });
        ProductTable.getColumns().add(categoryColumn);
        //{"Category", "productCategory"}


        //categoryColumn.setCellEditor(new DefaultCellEditor(categoriesTb));


    }

    /**
     * Create the dialog.
     */
    public void initAddYear(String year, Window parWindow) {
        newYear = false;
        parentWindow = parWindow;

        yearText.setText(year);
        yearText.setEditable(false);

        categoriesCmbx.getItems().clear();
        categoriesTb.clear();
        categoriesTb.add("");
        String browse = "Add Category";
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Categories")) {
            prep.execute();
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    categoriesTb.add(rs.getString(1));
                }
                ////DbInt.pCon.close();
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        categoriesTb.add(browse);
        categoriesCmbx.getItems().setAll(categoriesTb);
        String[][] columnNames = {{"ID", "productID"}, {"Item", "productName"}, {"Size", "productSize"}, {"Price/Item", "productUnitPrice"}};
        for (String[] column : columnNames) {
            javafx.scene.control.TableColumn<Product.formattedProductProps, String> tbCol = new javafx.scene.control.TableColumn<>(column[0]);
            tbCol.setCellValueFactory(new PropertyValueFactory<>(column[1]));
            tbCol.setCellFactory(TextFieldTableCell.forTableColumn());
            ProductTable.getColumns().add(tbCol);
        }
        javafx.scene.control.TableColumn<Product.formattedProductProps, String> categoryColumn = new javafx.scene.control.TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));

        categoryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categoriesTb));

        categoryColumn.setOnEditCommit(t -> {
            t.getRowValue().productCategory.set(t.getNewValue());
            data.get(t.getTablePosition().getRow()).productCategory.set(t.getNewValue());
            if (Objects.equals(categoriesCmbx.getSelectionModel().getSelectedItem(), "Add Category")) {
                AddCategory addCat = new AddCategory();
                String[] cat = addCat.showDialog();
                if (!Objects.equals(cat[0], "") && cat[0] != null) {
                    rowsCats.add(new String[]{cat[0], cat[1]});
                    refreshCmbx();
                }


            }
        });
        ProductTable.getColumns().add(categoryColumn);
        // boolean updateDb = true;
        fillTable();

    }

    /*    public static void main(String... args) {
            try {

                AddYear dialog = new AddYear();
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                dialog.setVisible(true);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }*/
    private void refreshCmbx() {
        categoriesCmbx.getItems().clear();
        categoriesTb.clear();
        categoriesTb.add("");
        String browse = "Add Category";

        rowsCats.forEach(cat -> {
            categoriesTb.add(cat[0]);
        });


        categoriesTb.add(browse);
        categoriesCmbx.getItems().setAll(categoriesTb);

    }

    /**
     * Creates Database for the year specified.
     */
    private void CreateDb() {
        String year = yearText.getText();
        if (DbInt.createDb(year)) {
            //Create Tables
            //Create Customers Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE CUSTOMERS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),NAME varchar(255),ADDRESS varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), PHONE varchar(255), ORDERID varchar(255), PAID varchar(255),DELIVERED varchar(255), EMAIL varchar(255), DONATION VARCHAR(255))")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Products Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255), Category VARCHAR(255))")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Create Totals Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE TOTALS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),DONATIONS DECIMAL(7,2),LG INTEGER,LP INTEGER,MULCH INTEGER,TOTAL DECIMAL(7,2),CUSTOMERS INTEGER,COMMISSIONS DECIMAL(7,2),GRANDTOTAL DECIMAL(7,2))")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }

/*            //Create Residence Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE Residence(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Address varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), Action varchar(255))")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }*/

            //Create Categories Table
            try (PreparedStatement prep = DbInt.getPrep(year, "CREATE TABLE Categories(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Name varchar(255), Date DATE)")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Insert products into Product table
            //Insert products into Product table
            String col = "";
            for (int i = 0; i < ProductTable.getItems().size(); i++) {
                Product.formattedProductProps curRow = ProductTable.getItems().get(i);
                String cat = (curRow.getProductCategory() != null) ? curRow.getProductCategory() : "";
                col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
                try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO PRODUCTS(ID, PName, Unit, Size, Category) VALUES (?,?,?,?,?)")) {
                    prep.setString(1, curRow.getProductID());
                    prep.setString(2, curRow.getProductName());
                    prep.setString(3, curRow.getProductUnitPrice());
                    prep.setString(4, curRow.getProductSize());
                    prep.setString(5, cat);
                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
            }
            //Add Categories
            rowsCats.forEach(cat -> {
                try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO Categories(Name, Date) VALUES (?,?)")) {
                    prep.setString(1, cat[0]);
                    prep.setDate(2, Date.valueOf(cat[1]));

                    prep.execute();
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
            });

            //ORDers Table
            try (PreparedStatement prep = DbInt.getPrep(year, String.format("CREATE TABLE ORDERS(OrderID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), NAME VARChAR(255) %s)", col))) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Add default values to TOTALS
            try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(0,0,0,0,0,0,0,0)")) {
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //ADD to Year
            addYear();
        } else {
            LogToFile.log(null, Severity.WARNING, "Year already exists: Please rename the year you are adding or delete the Year you are trying to overwrite.");
        }

    }

    private void updateDb(String year) {
        //Delete Year Customer table

        try (PreparedStatement addCol = DbInt.getPrep(year, "DROP TABLE \"PRODUCTS\"")) {
            addCol.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        //Recreate Year Customer table

        try (PreparedStatement addCol = DbInt.getPrep(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255), Category VARCHAR(255))")) {
            addCol.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        //Insert products into Product table
        String col = "";
        for (int i = 0; i < ProductTable.getItems().size(); i++) {
            Product.formattedProductProps curRow = ProductTable.getItems().get(i);
            String cat = (curRow.getProductCategory() != null) ? curRow.getProductCategory() : "";
            col = String.format("%s, \"%s\" VARCHAR(255)", col, Integer.toString(i));
            try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO PRODUCTS(ID, PName, Unit, Size, Category) VALUES (?,?,?,?,?)")) {
                prep.setString(1, curRow.getProductID());
                prep.setString(2, curRow.getProductName());
                prep.setString(3, curRow.getProductUnitPrice());
                prep.setString(4, curRow.getProductSize());
                prep.setString(5, cat);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        }
    }

    private void addYear() {
        try (PreparedStatement prep = DbInt.getPrep("Set", "INSERT INTO YEARS(YEARS) VALUES(?)")) {
            prep.setString(1, yearText.getText());
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
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

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nListCats = doc.getElementsByTagName("Categories");

            // Collection<String[]> rowsCatsL = new ArrayList<>();

            for (int temp = 0; temp < nListCats.getLength(); temp++) {

                Node nNode = nListCats.item(temp);


                if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    rowsCats.add(new String[]{eElement.getElementsByTagName("CategoryName").item(0).getTextContent(), eElement.getElementsByTagName("CategoryDate").item(0).getTextContent()});
                }
            }
            //rowsCats = rowsCatsL;
            NodeList nList = doc.getElementsByTagName("Products");

            Object[][] rows = new Object[nList.getLength()][5];

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


                    //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
                    Product.formattedProductProps prodProps = new Product.formattedProductProps(eElement.getElementsByTagName(
                            "ProductID").item(0).getTextContent(),
                            eElement.getElementsByTagName("ProductName").item(0).getTextContent(),
                            eElement.getElementsByTagName("Size").item(0).getTextContent(),
                            eElement.getElementsByTagName("UnitCost").item(0).getTextContent(),
                            (eElement.getElementsByTagName("Category").item(0) != null) ? eElement.getElementsByTagName("Category").item(0).getTextContent() : "",
                            0,
                            BigDecimal.ZERO
                    );
                    data.add(prodProps);
                    ProductTable.setItems(data);

                }


            }
        } catch (Exception e) {
            LogToFile.log(e, Severity.SEVERE, "Error Converting XML file to table. Please try again or contact support.");
        }
        refreshCmbx();
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
            Iterable<String[]> caters;
            if (!newYear) {
                caters = getCategories(yearText.getText());
            } else {
                caters = rowsCats;
            }
            int[] i = {0};
            caters = getCategories(yearText.getText());
            caters.forEach(cat -> {
                        Element cats = doc.createElement("Categories");
                        rootElement.appendChild(cats);
                        Attr attr = doc.createAttribute("id");
                        attr.setValue(Integer.toString(i[0]));
                        cats.setAttributeNode(attr);


                        //CateName elements
                        Element ProductID = doc.createElement("CategoryName");
                        ProductID.appendChild(doc.createTextNode(cat[0]));
                        cats.appendChild(ProductID);

                        //CatDate elements
                        Element ProductName = doc.createElement("CategoryDate");
                        ProductName.appendChild(doc.createTextNode(cat[1]));
                        cats.appendChild(ProductName);
                        i[0]++;
                    }
            );

            // staff elements


            // set attribute to staff element
            for (int i2 = 0; i2 < ProductTable.getItems().size(); i2++) {

                Element staff = doc.createElement("Products");
                rootElement.appendChild(staff);
                Attr attr = doc.createAttribute("id");
                attr.setValue(Integer.toString(i2));
                staff.setAttributeNode(attr);

                //ProductID elements
                Element ProductID = doc.createElement("ProductID");
                ProductID.appendChild(doc.createTextNode(ProductTable.getItems().get(i2).getProductID()));
                staff.appendChild(ProductID);

                // Prodcut Name elements
                Element ProductName = doc.createElement("ProductName");
                ProductName.appendChild(doc.createTextNode(ProductTable.getItems().get(i2).getProductName()));
                staff.appendChild(ProductName);

                // Unit COst elements
                Element UnitCost = doc.createElement("UnitCost");
                UnitCost.appendChild(doc.createTextNode(ProductTable.getItems().get(i2).getProductUnitPrice()));
                staff.appendChild(UnitCost);

                // Size elements
                Element Size = doc.createElement("Size");
                Size.appendChild(doc.createTextNode(ProductTable.getItems().get(i2).getProductSize()));
                staff.appendChild(Size);

                // Category elements

                String cat = (ProductTable.getItems().get(i2).getProductCategory() != null) ? ProductTable.getItems().get(i2).getProductCategory() : "";
                Element category = doc.createElement("Category");
                category.appendChild(doc.createTextNode(cat));
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

            //System.out.println("File saved!");
        } catch (ParserConfigurationException e) {
            LogToFile.log(e, Severity.SEVERE, "Error creating XML file: Parser error. Contact support.");
        } catch (TransformerException e) {
            LogToFile.log(e, Severity.SEVERE, "Error creating XML file: Parser Error. Contact support.");
        } catch (FileNotFoundException e) {
            LogToFile.log(e, Severity.SEVERE, "Error creating XML file: Error writing to file. Make sure the directory is readable by the software.");
        }
    }

    /**
     * Fills the table from a DB table
     */
    private void fillTable() {
        String year = yearText.getText();
        Year yearInfo = new Year(year);

        Product.formattedProduct[] productArray = yearInfo.getAllProducts();
        Object[][] rows = new Object[productArray.length][6];
        // data = FXCollections.observableArrayList();

        int i = 0;
        for (Product.formattedProduct productOrder : productArray) {
            //String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, BigDecimal extendedCost
            Product.formattedProductProps prodProps = new Product.formattedProductProps(productOrder.productID, productOrder.productName, productOrder.productSize, productOrder.productUnitPrice, productOrder.productCategory, productOrder.orderedQuantity, productOrder.extendedCost);
            data.add(prodProps);
            i++;
        }

        ProductTable.setItems(data);

    }

    @FXML
    private void tablefromDb(ActionEvent event) {

        fillTable();
    }

    @FXML
    private void xmlFromTable(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files", "*.xml", "*.XML");
        chooser.getExtensionFilters().add(filter);
        chooser.setSelectedExtensionFilter(filter);
        File XML = chooser.showSaveDialog(parentWindow);
        if (XML != null) {
            String path = XML.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xml")) {
                path += ".xml";
            }
            createXML(path);
        }
    }


}