/*******************************************************************************
 * ABOS
 * Copyright (C) 2018 Patrick Magauran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package ABOS.Derby;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import javafx.concurrent.Task;
import net.sf.saxon.s9api.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.tidy.Tidy;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;

//import javax.swing.*;

//import com.itextpdf.text.Document;

/**
 * Searches the text files under the given directory and counts the number of instances a given word is found
 * in these file.
 *
 * @author Albert Attard
 */
class ReportsWorker extends Task<Integer> {

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
    private final String pdfLoc;
    private Document doc = null;
    private File xmlTempFile = null;
    // --Commented out by Inspection (7/27/16 3:02 PM):private File[] xmlTempFileA = null;

    /**
     * Creates an instance of the worker
     *
     * @param reportType    the type of report
     * @param selectedYear  the selected year
     * @param scoutName     name of the scout to put in header
     * @param scoutStAddr   address  of the scout to put in header
     * @param addrFormat    formatted address
     * @param scoutRank     rank  of the scout to put in header
     * @param scoutPhone    phone #  of the scout to put in header
     * @param logoLoc       Location on disk of the logo
     * @param category      Category to generate report for
     * @param customerName  Name of the customer
     * @param repTitle      Title of the report
     * @param splitting     Split the report in any way?
     * @param includeHeader Include a header?
     * @param pdfLoc1       Location to save the pdf
     */
    public ReportsWorker(String reportType, String selectedYear, String scoutName, String scoutStAddr, String addrFormat, String scoutRank, String scoutPhone, String logoLoc, String category, String customerName, String repTitle, String splitting, Boolean includeHeader, String pdfLoc1) {

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
    protected Integer call() throws Exception {
        updateMessage("Generating Report");
        if (Objects.equals(reportType, "Year Totals; Spilt by Customer")) {
            Year year = new Year(selectedYear);
            Iterable<String> customers = year.getCustomerNames();
            // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder;
            domBuilder = domFactory.newDocumentBuilder();


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
                Customer cust = new Customer(customer, selectedYear);

                // Root element
                Order.orderArray orderArray;
                Order order = new Order();
                if (Objects.equals(category, "All")) {
                    orderArray = order.createOrderArray(selectedYear, customer, true);

                } else {
                    orderArray = order.createOrderArray(selectedYear, customer, true, category);
                }
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
                        StreetAddress.appendChild(doc.createTextNode(cust.getAddr()));
                        products.appendChild(StreetAddress);
                    }
                    // City elements
                    {
                        Element city = doc.createElement("city");
                        String addr = cust.getTown() + ' ' + cust.getState() + ", " + cust.getZip();
                        city.appendChild(doc.createTextNode(addr));
                        products.appendChild(city);
                    }

                    // phone elements
                    {
                        Element phone = doc.createElement("PhoneNumber");
                        phone.appendChild(doc.createTextNode(cust.getPhone()));
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
                        if (includeHeader && !Objects.equals(category, "All")) {
                            Element title = doc.createElement("specialInfo");
                            {
                                Element text = doc.createElement("text");
                                String notice = "*Notice: These products will be delivered to your house on " + DbInt.getCategoryDate(category, selectedYear) + ". Please have the total payment listed below ready and be present on that date.";
                                text.appendChild(doc.createTextNode(notice));
                                title.appendChild(text);
                            }
                            products.appendChild(title);
                        }
                    }
                    setProgress(getProgress() + (custProgressIncValue / 10));
                    BigDecimal tCost = BigDecimal.ZERO;

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
                                        tCost = tCost.add(aRowDataF.extendedCost);
                                        Product.appendChild(TotalCost);
                                    }
                                }
                            }
                            setProgress(getProgress() + productProgressIncValue);

                        }
                        //Total Cost for this Utilities.Year
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
                        TotalCost.appendChild(doc.createTextNode(curYear.getGTot().toPlainString()));
                        info.appendChild(TotalCost);
                    }
                    // OverallTotalQuantity elements
                    {
                        Element TotalQuantity = doc.createElement("totalQuantity");
                        TotalQuantity.appendChild(doc.createTextNode(Integer.toString(curYear.getQuant())));
                        info.appendChild(TotalQuantity);
                    }
                    String donation = cust.getDontation().toPlainString();
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
                            text.appendChild(doc.createTextNode(tCost.add(new BigDecimal(donation)).toPlainString()));
                            products.appendChild(text);
                        }

                    }
                    rootElement.appendChild(products);

                }

            });
        } else {

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder;
            domBuilder = domFactory.newDocumentBuilder();


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
                            if (includeHeader && !Objects.equals(category, "All")) {
                                Element title = doc.createElement("specialInfo");
                                {
                                    Element text = doc.createElement("text");
                                    String notice = "*Notice: These products will be delivered to your house on " + DbInt.getCategoryDate(category, selectedYear) + ". Please Have the total payment listed below ready and be present on that date.";
                                    text.appendChild(doc.createTextNode(notice));
                                    title.appendChild(text);
                                }
                                info.appendChild(title);
                            }

                        }
                        setProgress(10);
                        BigDecimal tCost = BigDecimal.ZERO;
                        orderArray.totalCost = BigDecimal.ZERO;
                        orderArray.totalQuantity = 0;
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
                                        orderArray.totalQuantity = orderArray.totalQuantity + aRowDataF.orderedQuantity;
                                        Quantity.appendChild(doc.createTextNode(String.valueOf(aRowDataF.orderedQuantity)));
                                        Product.appendChild(Quantity);
                                    }
                                    //TotalCost
                                    {
                                        Element TotalCost = doc.createElement("TotalCost");
                                        orderArray.totalCost = orderArray.totalCost.add(aRowDataF.extendedCost);

                                        TotalCost.appendChild(doc.createTextNode(String.valueOf(aRowDataF.extendedCost)));
                                        tCost = tCost.add(aRowDataF.extendedCost);
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
                            TotalCost.appendChild(doc.createTextNode((orderArray.totalCost).toPlainString()));
                            info.appendChild(TotalCost);
                        }
                        // OverallTotalQuantity elements
                        {
                            Element TotalQuantity = doc.createElement("totalQuantity");
                            TotalQuantity.appendChild(doc.createTextNode(Integer.toString(orderArray.totalQuantity)));
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
                            if (includeHeader && !Objects.equals(category, "All")) {
                                Element title = doc.createElement("specialInfo");
                                {
                                    Element text = doc.createElement("text");
                                    String notice = "*Notice: These products will be delivered to your house on " + DbInt.getCategoryDate(category, selectedYear) + ". Please Have the total payment listed below ready and be present on that date.";
                                    text.appendChild(doc.createTextNode(notice));
                                    title.appendChild(text);
                                }
                                info.appendChild(title);
                            }
                        }
                        setProgress(5);
                        BigDecimal tCost = BigDecimal.ZERO;
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
                                        tCost = tCost.add(aRowDataF.extendedCost);
                                        Product.appendChild(TotalCost);
                                    }
                                }
                            }
                            setProgress(getProgress() + productIncValue);
                        }
                        //Total Cost for this Utilities.Year
                        {
                            Element tCostE = doc.createElement("totalCost");
                            tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                            products.appendChild(tCostE);
                        }
                        // OverallTotalCost elements
                        {
                            Element TotalCost = doc.createElement("TotalCost");
                            TotalCost.appendChild(doc.createTextNode(orderArray.totalCost.toPlainString()));
                            info.appendChild(TotalCost);
                        }
                        // OverallTotalQuantity elements
                        {
                            Element TotalQuantity = doc.createElement("totalQuantity");
                            TotalQuantity.appendChild(doc.createTextNode(Integer.toString(orderArray.totalQuantity)));
                            info.appendChild(TotalQuantity);
                        }
                    }
                    setProgress(100);
                }
                break;

                case "Customer All-time Totals": {
                    // Collection<String> customerYears = new ArrayList<>();
                    Iterable<String> years = DbInt.getYears();
                    String headerS = "true";
                    BigDecimal overallTotalCost = BigDecimal.ZERO;
                    int overallTotalQuantity = 0;
                    int yearProgressInc = 95 / ((years instanceof Collection<?>) ? ((Collection<?>) years).size() : 1);
                    //For Each Utilities.Year
                    for (String year : years) {
                        //Get Utilities.Customer with name ?
                        Year yearObj = new Year(year);

                        //    customerYears.add(year);
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
                        BigDecimal tCost = BigDecimal.ZERO;
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
                                tCost = tCost.add(orderedProduct.extendedCost);
                                Product.appendChild(TotalCost);
                            }
                        }
                        //Total for current customerName
                        {
                            Element tCostE = doc.createElement("totalCost");
                            tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)));
                            products.appendChild(tCostE);
                        }


                        ////Utilities.DbInt.pCon.close();


                        setProgress(getProgress() + yearProgressInc);
                    }
                    // OverallTotalCost elements
                    {
                        Element TotalCost = doc.createElement("TotalCost");
                        TotalCost.appendChild(doc.createTextNode((overallTotalCost.toPlainString())));
                        info.appendChild(TotalCost);
                    }
                    // OverallTotalQuantity elements
                    {
                        Element TotalQuantity = doc.createElement("totalQuantity");
                        TotalQuantity.appendChild(doc.createTextNode(Integer.toString(overallTotalQuantity)));
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
                updateMessage("Error writing temporary XML. Please try again.");
                throw e;
            }


        } catch (Exception exp) {
            updateMessage("Error while genertating tempory XML. Please try again or contact support.");
            throw exp;
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException | RuntimeException e) {
                LogToFile.log(e, Severity.FINE, "Error closing temporary XML file.");
            }

        }
        convertXSLToPDF();
        updateMessage("Done");


        // Return the number of matches found
        return 1;
    }

    private void convertXSLToPDF() throws Exception {
        OutputStream os = new ByteArrayOutputStream();

        Processor proc = new Processor(false);
        XsltCompiler comp = proc.newXsltCompiler();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream("/Report.xsl")) {


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
                String cssText = "  .LBordered {\n" +
                        "                border-left: 1px solid black;\n" +
                        "                border-bottom: 1px solid black;\n" +

                        "                border-collapse: collapse;\n" +
                        "                }\n" +
                        "                .Bordered {\n" +
                        "                border: 1px solid black;\n" +
                        "                border-collapse: collapse;\n" +
                        "                }\n" +
                        "                .UBordered {\n" +
                        "                border: 0px solid black;\n" +
                        "                border-collapse: collapse;\n" +
                        "                }\n" +
                        "                .splitTitle {display:inline;}\n" +
                        "                h4{\n" +
                        "                margin:1px;\n" +
                        "                padding:1px;\n" +
                        "                }\n" +
                        "                table {\n" +
                        "                width:100%;\n" +
                        "                margin-bottom: 0.4pt;\n" +
                        "                margin-top: 0;\n" +
                        "                margin-left: 0;\n" +
                        "                margin-right: 0;\n" +
                        "                text-indent: 0;\n" +
                        "                }\n" +
                        "                tr {\n" +
                        "                vertical-align: inherit;\n" +
                        "                }\n" +
                        "                table > tr {\n" +
                        "                vertical-align: middle;\n" +
                        "                }\n" +
                        "                table, td {\n" +
                        "                background-color:#FFF;\n" +
                        "                font-size:10pt;\n" +
                        "                padding: 50px;\n" +
                        "                border-spacing: 50px;\n" +

                        "                text-align: inherit;\n" +
                        "                vertical-align: inherit;\n" +
                        "                }\n" +
                        "                th {\n" +
                        "                background-color: #FFF;\n" +
                        "                font-size:10pt;\n" +
                        "                color:#000;\n" +
                        "                display: table-cell;\n" +
                        "                font-weight: bold;\n" +
                        "                padding: 1px;\n" +
                        "                vertical-align: inherit;\n" +
                        "                }";
                //fstream.deleteOnExit();
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();

                // step 2
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                writer.setInitialLeading(12.5f);
                writer.setTagged();
                // step 3
                document.open();

                // step 4

                // CSS
                CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
                CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(cssText.getBytes()));
                cssResolver.addCss(cssFile);
                // HTML
                HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
                htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
                htmlContext.autoBookmark(false);

                // Pipelines
                PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer);
                HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
                CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);

                // XML Worker
                XMLWorker worker = new XMLWorker(css, true);
                XMLParser p = new XMLParser(worker);
                p.parse(new FileInputStream(xhtml));

                // step 5
                document.close();
                /*com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                // step 2
                PdfWriter writer = PdfWriter.getInstance(document, fos);
                // step 3
                document.open();
                // step 4
                XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                        new FileInputStream(xhtml));
                // step 5
                document.close();*/


            } catch (Exception e) {
                updateMessage("Error ocurred while converting temporary XML to pdf. Try again or contact support.");
                throw e;
            }
        } catch (IOException e) {
            updateMessage("Error writing PDF file. Please try again.");
            throw e;
        }


        /*
          Compile and execute a simple transformation that applies a stylesheet to an input file,
          and serializing the result to an output file
         */


    }

    private void setProgress(double progress) {
        updateProgress(progress, 100.0);
    }
}