import net.sf.saxon.s9api.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Searches the text files under the given directory and counts the number of instances a given word is found
 * in these file.
 *
 * @author Albert Attard
 */
public class ReportsWorker extends SwingWorker<Integer, String> {

    private final String reportType;
    private final String selectedYear;
    private final String scoutName;
    private final String scoutStAddr;
    private final String addrFormat;
    private final String scoutRank;
    private final String scoutPhone;
    private final String logoLoc;
    private final String category;
    private final String customerName;
    private final String repTitle;
    private final String Splitting;
    private final Boolean includeHeader;
    private final JLabel statusLbl;
    private final String pdfLoc;
    private Document doc = null;
    private File xmlTempFile = null;
    // --Commented out by Inspection (7/27/16 3:02 PM):private File[] xmlTempFileA = null;

    /**
     * Creates an instance of the worker
     *
     * @param reportType
     * @param selectedYear
     * @param scoutName
     * @param scoutStAddr
     * @param addrFormat
     * @param scoutRank
     * @param scoutPhone
     * @param logoLoc
     * @param category
     * @param customerName
     * @param repTitle
     * @param splitting
     * @param includeHeader
     * @param statusLbl
     * @param pdfLoc1
     */
    public ReportsWorker(String reportType, String selectedYear, String scoutName, String scoutStAddr, String addrFormat, String scoutRank, String scoutPhone, String logoLoc, String category, String customerName, String repTitle, String splitting, Boolean includeHeader, JLabel statusLbl, String pdfLoc1) {

        this.reportType = reportType;
        this.selectedYear = selectedYear;
        this.scoutName = scoutName;
        this.scoutStAddr = scoutStAddr;
        this.addrFormat = addrFormat;
        this.scoutRank = scoutRank;
        this.scoutPhone = scoutPhone;
        this.logoLoc = logoLoc;
        this.category = category;
        this.customerName = customerName;
        this.repTitle = repTitle;
        Splitting = splitting;
        this.includeHeader = includeHeader;
        this.statusLbl = statusLbl;
        pdfLoc = pdfLoc1;
    }

// --Commented out by Inspection START (7/27/16 3:02 PM):
//    private static void failIfInterrupted() throws InterruptedException {
//        if (Thread.currentThread().isInterrupted()) {
//            throw new InterruptedException("Interrupted while searching files");
//        }
//    }
// --Commented out by Inspection STOP (7/27/16 3:02 PM)

    @Override
    protected Integer doInBackground() {
        publish("Generating Report");
        if (Objects.equals(reportType, "Year Totals; Spilt by Customer")) {
            Year year = new Year(selectedYear);
            Iterable<String> customers = year.getCustomerNames();
            // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = null;
            try {
                domBuilder = domFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                LogToFile.log(e, Severity.WARNING, "Error configuring parser. Please reinstall or contact support.");
            }


            doc = domBuilder.newDocument();
            Element rootElement = doc.createElement("LawnGardenReports");
            doc.appendChild(rootElement);
            //Info Elements
            Element info = doc.createElement("info");
            rootElement.appendChild(info);

            {
                // Scoutname elements
                {
                    Element ScoutName = doc.createElement("name");
                    ScoutName.appendChild(doc.createTextNode(scoutName));
                    info.appendChild(ScoutName);
                }
                // StreetAddress elements
                {
                    Element StreetAddress = doc.createElement("streetAddress");
                    StreetAddress.appendChild(doc.createTextNode(scoutStAddr));
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
                    rank.appendChild(doc.createTextNode(scoutRank));
                    info.appendChild(rank);
                }
                // phone elements
                {
                    Element rank = doc.createElement("PhoneNumber");
                    rank.appendChild(doc.createTextNode(scoutPhone));
                    info.appendChild(rank);
                }
                // Logo elements
                {
                    Element logo = doc.createElement("logo");
                    logo.appendChild(doc.createTextNode("file:///" + logoLoc.replace("\\", "/")));
                    info.appendChild(logo);
                }


            }
            //Column Elements
            {
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
            setProgress(10);
            int custProgressIncValue = 90 / ((customers instanceof Collection<?>) ? ((Collection<?>) customers).size() : 1);

            customers.forEach(customer -> {


                // Root element

                Order order = new Order();
                Order.orderArray orderArray = order.createOrderArray(selectedYear, customer, true);
                //Set Items
                {
                    //Product Elements
                    Element products = doc.createElement("customerYear");
                    //YearTitle
                    {
                        Element custAddr = doc.createElement("custAddr");
                        custAddr.appendChild(doc.createTextNode("true"));
                        products.appendChild(custAddr);
                    }
                    // customername elements
                    {
                        Element custName = doc.createElement("name");
                        custName.appendChild(doc.createTextNode(customer));
                        products.appendChild(custName);
                    }
                    // StreetAddress elements
                    {
                        Element StreetAddress = doc.createElement("streetAddress");
                        StreetAddress.appendChild(doc.createTextNode(DbInt.getCustInf(selectedYear, customer, "ADDRESS")));
                        products.appendChild(StreetAddress);
                    }
                    // City elements
                    {
                        Element city = doc.createElement("city");
                        String addr = DbInt.getCustInf(selectedYear, customer, "TOWN") + ' ' + DbInt.getCustInf(selectedYear, customer, "STATE") + ", " + DbInt.getCustInf(selectedYear, customer, "ZIPCODE");
                        city.appendChild(doc.createTextNode(addr));
                        products.appendChild(city);
                    }

                    // phone elements
                    {
                        Element phone = doc.createElement("PhoneNumber");
                        phone.appendChild(doc.createTextNode(DbInt.getCustInf(selectedYear, customer, "PHONE")));
                        products.appendChild(phone);
                    }
                    {
                        Element header = doc.createElement("header");
                        header.appendChild(doc.createTextNode("true"));
                        products.appendChild(header);
                    }
                    {
                        Element title = doc.createElement("title");
                        title.appendChild(doc.createTextNode(customer + ' ' + selectedYear + " Order"));
                        products.appendChild(title);
                    }
                    {
                        if (includeHeader) {
                            Element title = doc.createElement("specialInfo");
                            {
                                Element text = doc.createElement("text");
                                String notice = "*Notice: These products will be delivered to your house on " + DbInt.getCategoryDate(category) + ". Please Have the total payment listed below ready and be present on that date.";
                                text.appendChild(doc.createTextNode(notice));
                                title.appendChild(text);
                            }
                            products.appendChild(title);
                        }
                    }
                    setProgress(getProgress() + (custProgressIncValue / 10));
                    double tCost = 0.0;

                    if (orderArray.totalQuantity > 0) {
                        Element prodTable = doc.createElement("prodTable");
                        prodTable.appendChild(doc.createTextNode("true"));
                        products.appendChild(prodTable);
                        int productProgressIncValue = ((custProgressIncValue / 10) * 9) / orderArray.orderData.length;
                        //For each product ordered, enter info
                        for (Product.formattedProduct aRowDataF : orderArray.orderData) {
                            if (Objects.equals(aRowDataF.productCategory, category) || (Objects.equals(category, "All"))) {

                                {
                                    Element Product = doc.createElement("Product");
                                    products.appendChild(Product);
                                    //ID
                                    {
                                        Element ID = doc.createElement("ID");
                                        ID.appendChild(doc.createTextNode(aRowDataF.productID));
                                        Product.appendChild(ID);
                                    }
                                    //Name
                                    {
                                        Element Name = doc.createElement("Name");
                                        Name.appendChild(doc.createTextNode(aRowDataF.productName));
                                        Product.appendChild(Name);
                                    }
                                    //Size
                                    {
                                        Element Size = doc.createElement("Size");
                                        Size.appendChild(doc.createTextNode(aRowDataF.productSize));
                                        Product.appendChild(Size);
                                    }
                                    //UnitCost
                                    {
                                        Element UnitCost = doc.createElement("UnitCost");
                                        UnitCost.appendChild(doc.createTextNode(aRowDataF.productUnitPrice));
                                        Product.appendChild(UnitCost);
                                    }
                                    //Quantity
                                    {
                                        Element Quantity = doc.createElement("Quantity");
                                        Quantity.appendChild(doc.createTextNode(String.valueOf(aRowDataF.orderedQuantity)));
                                        Product.appendChild(Quantity);
                                    }
                                    //TotalCost
                                    {
                                        Element TotalCost = doc.createElement("TotalCost");
                                        TotalCost.appendChild(doc.createTextNode(String.valueOf(aRowDataF.extendedCost)));
                                        tCost += aRowDataF.extendedCost;
                                        Product.appendChild(TotalCost);
                                    }
                                }
                            }
                            setProgress(getProgress() + productProgressIncValue);

                        }
                        //Total Cost for this Year
                        {
                            Element tCostE = doc.createElement("totalCost");
                            tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                            products.appendChild(tCostE);
                        }


                    }
                    // OverallTotalCost elements
                    Year curYear = new Year(selectedYear);
                    {
                        Element TotalCost = doc.createElement("TotalCost");
                        TotalCost.appendChild(doc.createTextNode(curYear.getGTot()));
                        info.appendChild(TotalCost);
                    }
                    // OverallTotalQuantity elements
                    {
                        Element TotalQuantity = doc.createElement("totalQuantity");
                        TotalQuantity.appendChild(doc.createTextNode(curYear.getQuant()));
                        info.appendChild(TotalQuantity);
                    }
                    String donation = DbInt.getCustInf(selectedYear, customer, "DONATION");
                    if (!Objects.equals(donation, "0.0") && !Objects.equals(donation, "0")) {
                        Element title = doc.createElement("DonationThanks");
                        {
                            Element text = doc.createElement("text");
                            String notice = "Thank you for your $" + donation + " donation ";
                            text.appendChild(doc.createTextNode(notice));
                            title.appendChild(text);
                        }
                        products.appendChild(title);

                        {
                            Element prodTable = doc.createElement("includeDonation");
                            prodTable.appendChild(doc.createTextNode("true"));
                            products.appendChild(prodTable);
                        }
                        {
                            Element text = doc.createElement("Donation");
                            text.appendChild(doc.createTextNode(donation));
                            products.appendChild(text);
                        }
                        {
                            Element text = doc.createElement("GrandTotal");
                            text.appendChild(doc.createTextNode(Double.toString(tCost + Double.parseDouble(donation))));
                            products.appendChild(text);
                        }

                    }
                    rootElement.appendChild(products);

                }

            });
        } else {

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = null;
            try {
                domBuilder = domFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                LogToFile.log(e, Severity.WARNING, "Error configuring parser. Please reinstall or contact support.");
            }

            doc = domBuilder.newDocument();
            // Root element
            Element rootElement = doc.createElement("LawnGardenReports");
            doc.appendChild(rootElement);
            //Info Elements
            Element info = doc.createElement("info");
            rootElement.appendChild(info);

            {
                // Scoutname elements
                {
                    Element ScoutName = doc.createElement("name");
                    ScoutName.appendChild(doc.createTextNode(scoutName));
                    info.appendChild(ScoutName);
                }
                // StreetAddress elements
                {
                    Element StreetAddress = doc.createElement("streetAddress");
                    StreetAddress.appendChild(doc.createTextNode(scoutStAddr));
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
                    rank.appendChild(doc.createTextNode(scoutRank));
                    info.appendChild(rank);
                }
                // phone elements
                {
                    Element rank = doc.createElement("PhoneNumber");
                    rank.appendChild(doc.createTextNode(scoutPhone));
                    info.appendChild(rank);
                }
                // Logo elements
                {
                    Element logo = doc.createElement("logo");
                    logo.appendChild(doc.createTextNode("file:///" + logoLoc.replace("\\", "/")));
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


            }
            //Column Elements
            {
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
            setProgress(5);
            switch (reportType) {

                case "Year Totals": {
                    Order order = new Order();
                    Order.orderArray orderArray = order.createOrderArray(selectedYear);
                    //Products for year
                    {
                        //Product Elements
                        Element products = doc.createElement("customerYear");
                        rootElement.appendChild(products);
                        {
                            Element header = doc.createElement("header");
                            header.appendChild(doc.createTextNode("true"));
                            products.appendChild(header);
                        }
                        {
                            Element prodTable = doc.createElement("prodTable");
                            prodTable.appendChild(doc.createTextNode("true"));
                            products.appendChild(prodTable);
                        }
                        //YearTitle
                        {
                            Element title = doc.createElement("title");
                            title.appendChild(doc.createTextNode(selectedYear));
                            products.appendChild(title);
                        }
                        {
                            if (includeHeader) {
                                Element title = doc.createElement("specialInfo");
                                {
                                    Element text = doc.createElement("text");
                                    String notice = "*Notice: These products will be delivered to your house on " + DbInt.getCategoryDate(category) + ". Please Have the total payment listed below ready and be present on that date.";
                                    text.appendChild(doc.createTextNode(notice));
                                    title.appendChild(text);
                                }
                                info.appendChild(title);
                            }

                        }
                        setProgress(10);
                        double tCost = 0.0;
                        orderArray.totalCost = 0.0;
                        orderArray.totalQuantity = 0.0;
                        int productIncValue = 90 / orderArray.orderData.length;
                        for (Product.formattedProduct aRowDataF : orderArray.orderData) {
                            if (Objects.equals(aRowDataF.productCategory, category) || (Objects.equals(category, "All"))) {

                                {
                                    Element Product = doc.createElement("Product");
                                    products.appendChild(Product);
                                    //ID
                                    {
                                        Element ID = doc.createElement("ID");
                                        ID.appendChild(doc.createTextNode(aRowDataF.productID));
                                        Product.appendChild(ID);
                                    }
                                    //Name
                                    {
                                        Element Name = doc.createElement("Name");
                                        Name.appendChild(doc.createTextNode(aRowDataF.productName));
                                        Product.appendChild(Name);
                                    }
                                    //Size
                                    {
                                        Element Size = doc.createElement("Size");
                                        Size.appendChild(doc.createTextNode(aRowDataF.productSize));
                                        Product.appendChild(Size);
                                    }
                                    //UnitCost
                                    {
                                        Element UnitCost = doc.createElement("UnitCost");
                                        UnitCost.appendChild(doc.createTextNode(aRowDataF.productUnitPrice));
                                        Product.appendChild(UnitCost);
                                    }
                                    //Quantity
                                    {
                                        Element Quantity = doc.createElement("Quantity");
                                        orderArray.totalQuantity += (double) aRowDataF.orderedQuantity;
                                        Quantity.appendChild(doc.createTextNode(String.valueOf(aRowDataF.orderedQuantity)));
                                        Product.appendChild(Quantity);
                                    }
                                    //TotalCost
                                    {
                                        Element TotalCost = doc.createElement("TotalCost");
                                        orderArray.totalCost += aRowDataF.extendedCost;

                                        TotalCost.appendChild(doc.createTextNode(String.valueOf(aRowDataF.extendedCost)));
                                        tCost += aRowDataF.extendedCost;
                                        Product.appendChild(TotalCost);
                                    }
                                }
                            }
                            setProgress(getProgress() + productIncValue);
                        }
                        //Total Cost for list
                        {
                            Element tCostE = doc.createElement("totalCost");
                            tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                            products.appendChild(tCostE);
                        }
                        // OverallTotalCost elements
                        {
                            Element TotalCost = doc.createElement("TotalCost");
                            TotalCost.appendChild(doc.createTextNode(Double.toString(orderArray.totalCost)));
                            info.appendChild(TotalCost);
                        }
                        // OverallTotalQuantity elements
                        {
                            Element TotalQuantity = doc.createElement("totalQuantity");
                            TotalQuantity.appendChild(doc.createTextNode(Double.toString(orderArray.totalQuantity)));
                            info.appendChild(TotalQuantity);
                        }

                    }
                    setProgress(100);
                }
                break;
                case "Customer Year Totals": {
                    Order order = new Order();
                    Order.orderArray orderArray = order.createOrderArray(selectedYear, customerName, true);
                    //Set Items
                    {
                        //Product Elements
                        Element products = doc.createElement("customerYear");
                        rootElement.appendChild(products);
                        {
                            Element header = doc.createElement("header");
                            header.appendChild(doc.createTextNode("true"));
                            products.appendChild(header);
                        }
                        {
                            Element prodTable = doc.createElement("prodTable");
                            prodTable.appendChild(doc.createTextNode("true"));
                            products.appendChild(prodTable);
                        }
                        //YearTitle
                        {
                            Element title = doc.createElement("title");
                            title.appendChild(doc.createTextNode(selectedYear));
                            products.appendChild(title);
                        }
                        {
                            if (includeHeader) {
                                Element title = doc.createElement("specialInfo");
                                {
                                    Element text = doc.createElement("text");
                                    String notice = "*Notice: These products will be delivered to your house on " + DbInt.getCategoryDate(category) + ". Please Have the total payment listed below ready and be present on that date.";
                                    text.appendChild(doc.createTextNode(notice));
                                    title.appendChild(text);
                                }
                                info.appendChild(title);
                            }
                        }
                        setProgress(5);
                        double tCost = 0.0;
                        int productIncValue = 90 / orderArray.orderData.length;

                        //For each product ordered, enter info
                        for (Product.formattedProduct aRowDataF : orderArray.orderData) {
                            if (Objects.equals(aRowDataF.productCategory, category) || (Objects.equals(category, "All"))) {

                                {
                                    Element Product = doc.createElement("Product");
                                    products.appendChild(Product);
                                    //ID
                                    {
                                        Element ID = doc.createElement("ID");
                                        ID.appendChild(doc.createTextNode(aRowDataF.productID));
                                        Product.appendChild(ID);
                                    }
                                    //Name
                                    {
                                        Element Name = doc.createElement("Name");
                                        Name.appendChild(doc.createTextNode(aRowDataF.productName));
                                        Product.appendChild(Name);
                                    }
                                    //Size
                                    {
                                        Element Size = doc.createElement("Size");
                                        Size.appendChild(doc.createTextNode(aRowDataF.productSize));
                                        Product.appendChild(Size);
                                    }
                                    //UnitCost
                                    {
                                        Element UnitCost = doc.createElement("UnitCost");
                                        UnitCost.appendChild(doc.createTextNode(aRowDataF.productUnitPrice));
                                        Product.appendChild(UnitCost);
                                    }
                                    //Quantity
                                    {
                                        Element Quantity = doc.createElement("Quantity");
                                        Quantity.appendChild(doc.createTextNode(String.valueOf(aRowDataF.orderedQuantity)));
                                        Product.appendChild(Quantity);
                                    }
                                    //TotalCost
                                    {
                                        Element TotalCost = doc.createElement("TotalCost");
                                        TotalCost.appendChild(doc.createTextNode(String.valueOf(aRowDataF.extendedCost)));
                                        tCost += aRowDataF.extendedCost;
                                        Product.appendChild(TotalCost);
                                    }
                                }
                            }
                            setProgress(getProgress() + productIncValue);
                        }
                        //Total Cost for this Year
                        {
                            Element tCostE = doc.createElement("totalCost");
                            tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                            products.appendChild(tCostE);
                        }
                        // OverallTotalCost elements
                        {
                            Element TotalCost = doc.createElement("TotalCost");
                            TotalCost.appendChild(doc.createTextNode(Double.toString(orderArray.totalCost)));
                            info.appendChild(TotalCost);
                        }
                        // OverallTotalQuantity elements
                        {
                            Element TotalQuantity = doc.createElement("totalQuantity");
                            TotalQuantity.appendChild(doc.createTextNode(Double.toString(orderArray.totalQuantity)));
                            info.appendChild(TotalQuantity);
                        }
                    }
                    setProgress(100);
                }
                break;

                case "Customer All-time Totals": {
                    Collection<String> customerYears = new ArrayList<>();
                    Iterable<String> years = DbInt.getYears();
                    String headerS = "true";
                    Double overallTotalCost = 0.0;
                    Double overallTotalQuantity = 0.0;
                    int yearProgressInc = 95 / ((years instanceof Collection<?>) ? ((Collection<?>) years).size() : 1);
                    //For Each Year
                    for (String year : years) {
                        //Get Customer with name ?
                        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers WHERE NAME=?")) {

                            prep.setString(1, customerName);
                            try (ResultSet rs = prep.executeQuery()) {

                                //Loop through customers
                                while (rs.next()) {
                                    customerYears.add(year);
                                    //Product Elements
                                    Element products = doc.createElement("customerYear");
                                    rootElement.appendChild(products);
                                    {
                                        Element header = doc.createElement("header");
                                        header.appendChild(doc.createTextNode(headerS));
                                        headerS = "false";
                                        products.appendChild(header);
                                    }
                                    {
                                        Element prodTable = doc.createElement("prodTable");
                                        prodTable.appendChild(doc.createTextNode("true"));
                                        products.appendChild(prodTable);
                                    }
                                    //YearTitle
                                    {
                                        Element title = doc.createElement("title");
                                        title.appendChild(doc.createTextNode(year));
                                        products.appendChild(title);
                                    }
                                    Order order = new Order();
                                    Order.orderArray orderArray = order.createOrderArray(year, customerName, true);
                                    double tCost = 0.0;
                                    overallTotalCost = orderArray.totalCost;
                                    overallTotalQuantity = orderArray.totalQuantity;
                                    //For each product in the table set the data
                                    for (Product.formattedProduct orderedProduct : orderArray.orderData) {
                                        Element Product = doc.createElement("Product");
                                        products.appendChild(Product);
                                        //ID
                                        {
                                            Element ID = doc.createElement("ID");
                                            ID.appendChild(doc.createTextNode(orderedProduct.productID));
                                            Product.appendChild(ID);
                                        }
                                        //Name
                                        {
                                            Element Name = doc.createElement("Name");
                                            Name.appendChild(doc.createTextNode(orderedProduct.productName));
                                            Product.appendChild(Name);
                                        }
                                        //Size
                                        {
                                            Element Size = doc.createElement("Size");
                                            Size.appendChild(doc.createTextNode(orderedProduct.productSize));
                                            Product.appendChild(Size);
                                        }
                                        //UnitCost
                                        {
                                            Element UnitCost = doc.createElement("UnitCost");
                                            UnitCost.appendChild(doc.createTextNode(orderedProduct.productUnitPrice));
                                            Product.appendChild(UnitCost);
                                        }
                                        //Quantity
                                        {
                                            Element Quantity = doc.createElement("Quantity");
                                            Quantity.appendChild(doc.createTextNode(String.valueOf(orderedProduct.orderedQuantity)));
                                            Product.appendChild(Quantity);
                                        }
                                        //TotalCost
                                        {
                                            Element TotalCost = doc.createElement("TotalCost");
                                            TotalCost.appendChild(doc.createTextNode(String.valueOf(orderedProduct.extendedCost)));
                                            tCost += orderedProduct.extendedCost;
                                            Product.appendChild(TotalCost);
                                        }
                                    }
                                    //Total for current customerName
                                    {
                                        Element tCostE = doc.createElement("totalCost");
                                        tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                                        products.appendChild(tCostE);
                                    }

                                }

                            }
                            ////DbInt.pCon.close();

                        } catch (SQLException e) {
                            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                        }
                        setProgress(getProgress() + yearProgressInc);
                    }
                    // OverallTotalCost elements
                    {
                        Element TotalCost = doc.createElement("TotalCost");
                        TotalCost.appendChild(doc.createTextNode(Double.toString(overallTotalCost)));
                        info.appendChild(TotalCost);
                    }
                    // OverallTotalQuantity elements
                    {
                        Element TotalQuantity = doc.createElement("totalQuantity");
                        TotalQuantity.appendChild(doc.createTextNode(Double.toString(overallTotalQuantity)));
                        info.appendChild(TotalQuantity);
                    }
                    setProgress(100);
                }
                break;
            }
        }
        OutputStreamWriter osw = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            osw = new OutputStreamWriter(baos);

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.ENCODING, osw.getEncoding());
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
                LogToFile.log(e, Severity.SEVERE, "Error writing temporary XML. Please try again.");
            }


        } catch (Exception exp) {
            LogToFile.log(exp, Severity.WARNING, "Error while genertating tempory XML. Please try again or contact support.");
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException | RuntimeException e) {
                LogToFile.log(e, Severity.FINE, "Error closing temporary XML file.");
            }

        }
        try {
            convertXSLToPDF();
        } catch (SaxonApiException e) {
            LogToFile.log(e, Severity.SEVERE, "Error converting temporary XML to pdf. Try again or contact support.");
        }
        publish("Done");


        // Return the number of matches found
        return 1;
    }

    @Override
    protected void process(List<String> chunks) {
        // Updates the messages text area
        chunks.forEach(statusLbl::setText);
    }

    private void convertXSLToPDF() throws SaxonApiException {
        OutputStream os = new ByteArrayOutputStream();

        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("Report.xsl")) {


            XsltExecutable exp = comp.compile(new StreamSource(in));
            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlTempFile));
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

            File xhtml;
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            String tmpDirectoryOp = System.getProperty("java.io.tmpdir");
            File tmpDirectory = new File(tmpDirectoryOp);
            xhtml = File.createTempFile("LGReportXhtml" + timeStamp, ".xhtml", tmpDirectory);
            xhtml.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(pdfLoc);
                 FileOutputStream xhtmlfos = new FileOutputStream(xhtml)) {


                tidy.parse(is, osT); // run tidy, providing an input and output streamp
                ByteArrayOutputStream baosT;
                baosT = (ByteArrayOutputStream) osT;

                baosT.writeTo(xhtmlfos);
                //fstream.deleteOnExit();


                try (InputStream isT = new FileInputStream(xhtml)) {
                    Document document = XMLResource.load(isT).getDocument();

                    //preview.setDocument(document);

                    ITextRenderer renderer = new ITextRenderer();
                    renderer.setDocument(document, null);

                    renderer.layout();


                    renderer.createPDF(fos);
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                LogToFile.log(e, Severity.WARNING, "Temporary xml file not found.");
            } catch (Exception e) {
                LogToFile.log(e, Severity.SEVERE, "Error ocurred while converting temporary XML to pdf. Try again or contact support.");
            }
        } catch (IOException e) {
            LogToFile.log(e, Severity.SEVERE, "Error writing PDF file. Please try again.");
        }


        /*
          Compile and execute a simple transformation that applies a stylesheet to an input file,
          and serializing the result to an output file
         */


    }
}