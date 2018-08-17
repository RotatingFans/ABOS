package abos.server

import com.itextpdf.text.BadElementException
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.tool.xml.XMLWorker
import com.itextpdf.tool.xml.XMLWorkerHelper
import com.itextpdf.tool.xml.css.CssFile
import com.itextpdf.tool.xml.html.Tags
import com.itextpdf.tool.xml.parser.XMLParser
import com.itextpdf.tool.xml.pipeline.css.CSSResolver

//import Utilities.*

import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext
import net.sf.saxon.s9api.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.tidy.Tidy

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.*
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import java.text.SimpleDateFormat

import static grails.gorm.multitenancy.Tenants.withId

class ReportGenerator {
    private final String reportType
    private final String selectedYear
    private final String scoutName
    private final String scoutStAddr
    private final String addrFormat
    private final String scoutRank
    private final String scoutPhone
    private final String logoLoc
    private final String category
    private final String user
    private final ArrayList<Customers> customers
    private final String repTitle
    private final String Splitting
    private final Boolean includeHeader
    private final String pdfLoc

    private Document doc = null
    private File xmlTempFile = null
    private Double prog = 0.0
    // --Commented out by Inspection (7/27/16 3:02 PM):private File[] xmlTempFileA = null

    /**
     * Creates an instance of the worker
     *
     * @param reportType the type of report
     * @param selectedYear the selected year
     * @param scoutName name of the scout to put in header
     * @param scoutStAddr address  of the scout to put in header
     * @param addrFormat formatted address
     * @param scoutRank rank  of the scout to put in header
     * @param scoutPhone phone #  of the scout to put in header
     * @param logoLoc Location on disk of the logo
     * @param category Category to generate report for
     * @param user Utilities.User to create Report as
     * @param customers Name of the customer
     * @param repTitle Title of the report
     * @param splitting Split the report in any way?
     * @param includeHeader Include a header?
     * @param pdfLoc1 Location to save the pdf
     */
    //              java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, ,mjbnjava.util.ArrayList, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String
    ReportGenerator(String reportType, String selectedYear, String scoutName, String scoutStAddr, String addrFormat, String scoutRank, String scoutPhone, String logoLoc, String category, String user, ArrayList<Customers> customers, String repTitle, String splitting, Boolean includeHeader, String pdfLoc1) {

        this.reportType = reportType
        this.selectedYear = selectedYear
        this.scoutName = scoutName
        this.scoutStAddr = scoutStAddr
        this.addrFormat = addrFormat
        this.scoutRank = scoutRank
        this.scoutPhone = scoutPhone
        this.logoLoc = logoLoc
        this.category = category
        this.user = user
        this.customers = customers
        this.repTitle = repTitle
        Splitting = splitting
        this.includeHeader = includeHeader
        pdfLoc = pdfLoc1
    }

// --Commented out by Inspection START (7/27/16 3:02 PM):
//    private static void failIfInterrupted() throws InterruptedException {
//        if (Thread.currentThread().isInterrupted()) {
//            throw new InterruptedException("Interrupted while searching files")
//        }
//    }
// --Commented out by Inspection STOP (7/27/16 3:02 PM)

    String generate() throws Exception {
        updateMessage("Generating Report")
        if (Objects.equals(reportType, "customers_split")) {
            withId(user, {
                def customers = Customers.findAllByYear(Year.get(selectedYear))

                // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance()
                DocumentBuilder domBuilder
                domBuilder = domFactory.newDocumentBuilder()


                doc = domBuilder.newDocument()
                Element rootElement = doc.createElement("LawnGardenReports")
                doc.appendChild(rootElement)
                //Info Elements
                Element info = doc.createElement("info")
                rootElement.appendChild(info)

                l:
                {
                    // Scoutname elements
                    scoutName:
                    {
                        Element ScoutName = doc.createElement("name")
                        ScoutName.appendChild(doc.createTextNode(scoutName))
                        info.appendChild(ScoutName)
                    }
                    // StreetAddress elements
                    streetAddress:
                    {
                        Element StreetAddress = doc.createElement("streetAddress")
                        StreetAddress.appendChild(doc.createTextNode(scoutStAddr))
                        info.appendChild(StreetAddress)
                    }
                    // City elements
                    l:
                    {
                        Element city = doc.createElement("city")
                        city.appendChild(doc.createTextNode(addrFormat))
                        info.appendChild(city)
                    }
                    // Rank elements
                    l:
                    {
                        Element rank = doc.createElement("rank")
                        rank.appendChild(doc.createTextNode(scoutRank))
                        info.appendChild(rank)
                    }
                    // phone elements
                    l:
                    {
                        Element rank = doc.createElement("PhoneNumber")
                        rank.appendChild(doc.createTextNode(scoutPhone))
                        info.appendChild(rank)
                    }
                    // Logo elements
                    l:
                    {
                        Element logo = doc.createElement("logo")
                        logo.appendChild(doc.createTextNode(logoLoc))
                        info.appendChild(logo)
                    }


                }
                //Column Elements
                l:
                {
                    Element columns = doc.createElement("columns")
                    rootElement.appendChild(columns)
                    String[] Columns = ["ID", "Name", "Unit Size", "Unit Cost", "Quantity", "Extended Price"]
                    for (String Column : Columns) {
                        //Column
                        l:
                        {
                            Element columnName = doc.createElement("column")
                            Element cName = doc.createElement("name")
                            cName.appendChild(doc.createTextNode(Column))
                            columnName.appendChild(cName)
                            columns.appendChild(columnName)
                        }
                    }
                }
                setProgress(10)
                int custProgressIncValue = 90 / ((customers instanceof Collection<?> && !((Collection) customers).isEmpty()) ? ((Collection<?>) customers).size() : 1)

                customers.forEach { cust ->
                    BigDecimal paid = cust.order.getAmountPaid()
                    int custId = cust.getId()
                    String customer = cust.getCustomerName()
                    // Root element
                    Set<Ordered_products> orderArray
                    if (Objects.equals(category, "All")) {
                        orderArray = cust.orderedProducts.sort { it.products.id }

                    } else {
                        orderArray = Ordered_products.where {
                            customer == cust && year == cust.year && products.category == Categories.findBycategoryNameAndYear(category, cust.year)
                        }.list().sort { it.products.id }
                    }
                    if (orderArray.quantity.sum() > 0) {
                        //Set Items
                        l:
                        {
                            //Product Elements
                            Element products = doc.createElement("customerYear")
                            //YearTitle
                            l:
                            {
                                Element custAddr = doc.createElement("custAddr")
                                custAddr.appendChild(doc.createTextNode("true"))
                                products.appendChild(custAddr)
                            }
                            // customername elements
                            l:
                            {
                                Element custName = doc.createElement("name")
                                custName.appendChild(doc.createTextNode(customer))
                                products.appendChild(custName)
                            }
                            // StreetAddress elements
                            l:
                            {
                                Element StreetAddress = doc.createElement("streetAddress")
                                StreetAddress.appendChild(doc.createTextNode(cust.getStreetAddress()))
                                products.appendChild(StreetAddress)
                            }
                            // City elements
                            l:
                            {
                                Element city = doc.createElement("city")
                                String addr = cust.getCity() + ' ' + cust.getState() + ", " + cust.getZipCode()
                                city.appendChild(doc.createTextNode(addr))
                                products.appendChild(city)
                            }

                            // phone elements
                            l:
                            {
                                Element phone = doc.createElement("PhoneNumber")
                                phone.appendChild(doc.createTextNode(cust.getPhone()))
                                products.appendChild(phone)
                            }
                            l:
                            {
                                Element header = doc.createElement("header")
                                header.appendChild(doc.createTextNode("true"))
                                products.appendChild(header)
                            }
                            l:
                            {
                                Element title = doc.createElement("title")
                                title.appendChild(doc.createTextNode(customer + ' ' + selectedYear + " Order"))
                                products.appendChild(title)
                            }
                            l:
                            {
                                if (includeHeader && !Objects.equals(category, "All")) {
                                    Element title = doc.createElement("specialInfo")
                                    l:
                                    {
                                        Element text = doc.createElement("text")
                                        String notice = "*Notice: These products will be delivered to your house on " + Categories.findBycategoryNameAndYear(category, cust.year).deliveryDate.toString() + ('. Total paid to date: $' + paid.toPlainString())
                                        text.appendChild(doc.createTextNode(notice))
                                        title.appendChild(text)
                                    }
                                    products.appendChild(title)
                                }
                            }
                            setProgress(getProg() + (custProgressIncValue / 10))
                            BigDecimal tCost = BigDecimal.ZERO

                            if (orderArray.quantity.sum() > 0) {
                                Element prodTable = doc.createElement("prodTable")
                                prodTable.appendChild(doc.createTextNode("true"))
                                products.appendChild(prodTable)
                                int productProgressIncValue = ((custProgressIncValue / 10) * 9) / orderArray.size()
                                //For each product ordered, enter info
                                for (Ordered_products aRowDataF : orderArray) {
                                    if (Objects.equals(aRowDataF.products.category ? aRowDataF.products.category.categoryName : "", category) || (Objects.equals(category, "All"))) {

                                        l:
                                        {
                                            Element Product = doc.createElement("Product")
                                            products.appendChild(Product)
                                            //ID
                                            l:
                                            {
                                                Element ID = doc.createElement("ID")
                                                ID.appendChild(doc.createTextNode(aRowDataF.products.humanProductId))
                                                Product.appendChild(ID)
                                            }
                                            //Name
                                            l:
                                            {
                                                Element Name = doc.createElement("Name")
                                                Name.appendChild(doc.createTextNode(aRowDataF.products.productName))
                                                Product.appendChild(Name)
                                            }
                                            //Size
                                            l:
                                            {
                                                Element Size = doc.createElement("Size")
                                                Size.appendChild(doc.createTextNode(aRowDataF.products.unitSize))
                                                Product.appendChild(Size)
                                            }
                                            //UnitCost
                                            l:
                                            {
                                                Element UnitCost = doc.createElement("UnitCost")
                                                UnitCost.appendChild(doc.createTextNode(aRowDataF.products.unitCost.toPlainString()))
                                                Product.appendChild(UnitCost)
                                            }
                                            //Quantity
                                            l:
                                            {
                                                Element Quantity = doc.createElement("Quantity")
                                                Quantity.appendChild(doc.createTextNode(String.valueOf(aRowDataF.quantity)))
                                                Product.appendChild(Quantity)
                                            }
                                            //TotalCost
                                            l:
                                            {
                                                Element TotalCost = doc.createElement("TotalCost")
                                                TotalCost.appendChild(doc.createTextNode(String.valueOf(aRowDataF.extendedCost)))
                                                tCost = tCost.add(aRowDataF.extendedCost)
                                                Product.appendChild(TotalCost)
                                            }
                                        }
                                    }
                                    setProgress(getProg() + productProgressIncValue)

                                }
                                //Total Cost for this Utilities.Year
                                l:
                                {
                                    Element tCostE = doc.createElement("totalCost")
                                    tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)))
                                    products.appendChild(tCostE)
                                }


                            }
                            /*Utilities.Year cYear
                            if (user == null) {
                                cYear = new Utilities.Year(selectedYear)
                            } else {
                                cYear = new Utilities.Year(selectedYear, user)

                            }
                            // OverallTotalCost elements
                            l:
                            {
                                Element TotalCost = doc.createElement("TotalCost")
                                TotalCost.appendChild(doc.createTextNode(cYear.getGTot().toPlainString()))
                                info.appendChild(TotalCost)
                            }
                            // OverallTotalQuantity elements
                            l:
                            {
                                Element TotalQuantity = doc.createElement("totalQuantity")
                                TotalQuantity.appendChild(doc.createTextNode(Integer.toString(cYear.getQuant())))
                                info.appendChild(TotalQuantity)
                            }*/
                            BigDecimal donationBD = cust.getDonation() ?: BigDecimal.ZERO

                            String donation = donationBD.toPlainString()
                            if (!(donationBD.compareTo(BigDecimal.ZERO) <= 0)) {
                                Element title = doc.createElement("DonationThanks")
                                l:
                                {
                                    Element text = doc.createElement("text")
                                    String notice = 'Thank you for your $' + donation + " donation "
                                    text.appendChild(doc.createTextNode(notice))
                                    title.appendChild(text)
                                }
                                products.appendChild(title)

                                l:
                                {
                                    Element prodTable = doc.createElement("includeDonation")
                                    prodTable.appendChild(doc.createTextNode("true"))
                                    products.appendChild(prodTable)
                                }
                                l:
                                {
                                    Element text = doc.createElement("Donation")
                                    text.appendChild(doc.createTextNode(donation))
                                    products.appendChild(text)
                                }
                                l:
                                {
                                    Element text = doc.createElement("GrandTotal")
                                    text.appendChild(doc.createTextNode(tCost.add(new BigDecimal(donation)).toPlainString()))
                                    products.appendChild(text)
                                }

                            }
                            rootElement.appendChild(products)

                        }
                    }

                }
            })
        } else {
            withId(user, {

                DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance()
                DocumentBuilder domBuilder
                domBuilder = domFactory.newDocumentBuilder()


                doc = domBuilder.newDocument()
                // Root element
                Element rootElement = doc.createElement("LawnGardenReports")
                doc.appendChild(rootElement)
                //Info Elements
                Element info = doc.createElement("info")
                rootElement.appendChild(info)

                l:
                {
                    // Scoutname elements
                    l:
                    {
                        Element ScoutName = doc.createElement("name")
                        ScoutName.appendChild(doc.createTextNode(scoutName))
                        info.appendChild(ScoutName)
                    }
                    // StreetAddress elements
                    l:
                    {
                        Element StreetAddress = doc.createElement("streetAddress")
                        StreetAddress.appendChild(doc.createTextNode(scoutStAddr))
                        info.appendChild(StreetAddress)
                    }
                    // City elements
                    l:
                    {
                        Element city = doc.createElement("city")
                        city.appendChild(doc.createTextNode(addrFormat))
                        info.appendChild(city)
                    }
                    // Rank elements
                    l:
                    {
                        Element rank = doc.createElement("rank")
                        rank.appendChild(doc.createTextNode(scoutRank))
                        info.appendChild(rank)
                    }
                    // phone elements
                    l:
                    {
                        Element rank = doc.createElement("PhoneNumber")
                        rank.appendChild(doc.createTextNode(scoutPhone))
                        info.appendChild(rank)
                    }
                    // Logo elements
                    // Logo elements
                    l:
                    {
                        Element logo = doc.createElement("logo")
                        logo.appendChild(doc.createTextNode(logoLoc))
                        info.appendChild(logo)
                    }
                    // ReportTitle elements
                    l:
                    {
                        Element reportTitle = doc.createElement("reportTitle")
                        reportTitle.appendChild(doc.createTextNode(repTitle))
                        info.appendChild(reportTitle)
                    }
                    // Splitter elements
                    l:
                    {
                        if (!Splitting.isEmpty()) {
                            Element splitting = doc.createElement("splitting")

                            splitting.appendChild(doc.createTextNode(Splitting))
                            info.appendChild(splitting)
                        }
                    }


                }
                //Column Elements
                l:
                {
                    Element columns = doc.createElement("columns")
                    rootElement.appendChild(columns)
                    String[] Columns = ["ID", "Name", "Unit Size", "Unit Cost", "Quantity", "Extended Price"]
                    for (String Column : Columns) {
                        //Column
                        l:
                        {
                            Element columnName = doc.createElement("column")
                            Element cName = doc.createElement("name")
                            cName.appendChild(doc.createTextNode(Column))
                            columnName.appendChild(cName)
                            columns.appendChild(columnName)
                        }
                    }
                }
                setProgress(5)
                switch (reportType) {

                    case "Year Totals":
                        Set<Ordered_products> orderArray
                        if (user == null) {
                            orderArray = Year.findByYear(selectedYear).orderedProducts.groupBy { it.products }.collect {
                                [products: it.key, quantity: it.value.quantity.sum(), extendedCost: it.value.extendedCost.sum()]
                            }.sort { it.products.id }
                        } else {
                            orderArray = User.findByUsername(user).orderedProducts.findAll {
                                it.getYearId() == Integer.decode(selectedYear)
                            }.groupBy { it.products }.collect {
                                [products: it.key, quantity: it.value.quantity.sum(), extendedCost: it.value.extendedCost.sum()]
                            }.sort { it.products.id }

                        }
                        //Products for year
                        l:
                        {
                            //Product Elements
                            Element products = doc.createElement("customerYear")

                            rootElement.appendChild(products)
                            l:
                            {
                                Element header = doc.createElement("header")
                                header.appendChild(doc.createTextNode("true"))
                                products.appendChild(header)
                            }
                            l:
                            {
                                Element prodTable = doc.createElement("prodTable")
                                prodTable.appendChild(doc.createTextNode("true"))
                                products.appendChild(prodTable)
                            }

                            /*            if (includeHeader && !Objects.equals(category, "All")) {
                                        Element title = doc.createElement("specialInfo")
                                                {
                                                    Element text = doc.createElement("text")

                                                    String notice = "*Notice: These products will be delivered to your house on " + Utilities.DbInt.getCategoryDate(category, selectedYear) + (paid ? "Please be available for delivery. Thank you for your advance payment." : ". Please Have the total payment listed below ready and be present on that date.")
                                                    text.appendChild(doc.createTextNode(notice))
                                                    title.appendChild(text)
                                                }
                                        info.appendChild(title)
                                    }*/


                            setProgress(10)
                            BigDecimal tCost = BigDecimal.ZERO
                            int productIncValue = 1
                            for (Ordered_products aRowDataF : orderArray) {
                                if (Objects.equals(aRowDataF.products.category ? aRowDataF.products.category.categoryName : "", category) || (Objects.equals(category, "All"))) {

                                    l:
                                    {
                                        Element Product = doc.createElement("Product")
                                        products.appendChild(Product)
                                        //ID
                                        l:
                                        {
                                            Element ID = doc.createElement("ID")
                                            ID.appendChild(doc.createTextNode(aRowDataF.products.humanProductId))
                                            Product.appendChild(ID)
                                        }
                                        //Name
                                        l:
                                        {
                                            Element Name = doc.createElement("Name")
                                            Name.appendChild(doc.createTextNode(aRowDataF.products.productName))
                                            Product.appendChild(Name)
                                        }
                                        //Size
                                        l:
                                        {
                                            Element Size = doc.createElement("Size")
                                            Size.appendChild(doc.createTextNode(aRowDataF.products.unitSize))
                                            Product.appendChild(Size)
                                        }
                                        //UnitCost
                                        l:
                                        {
                                            Element UnitCost = doc.createElement("UnitCost")
                                            UnitCost.appendChild(doc.createTextNode(aRowDataF.products.unitCost.toPlainString()))
                                            Product.appendChild(UnitCost)
                                        }
                                        //Quantity
                                        l:
                                        {
                                            Element Quantity = doc.createElement("Quantity")
                                            Quantity.appendChild(doc.createTextNode(String.valueOf(aRowDataF.quantity)))
                                            Product.appendChild(Quantity)
                                        }
                                        //TotalCost
                                        l:
                                        {
                                            Element TotalCost = doc.createElement("TotalCost")
                                            TotalCost.appendChild(doc.createTextNode(String.valueOf(aRowDataF.extendedCost)))
                                            tCost = tCost.add(aRowDataF.extendedCost)
                                            Product.appendChild(TotalCost)
                                        }
                                    }
                                }
                                setProgress(getProg() + productIncValue)
                            }
                            //Total Cost for list
                            l:
                            {
                                Element tCostE = doc.createElement("totalCost")
                                tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)))
                                products.appendChild(tCostE)
                            }
                            // OverallTotalCost elements
                            l:
                            {
                                Element TotalCost = doc.createElement("TotalCost")
                                TotalCost.appendChild(doc.createTextNode((orderArray.extendedCost.sum().toString())))
                                info.appendChild(TotalCost)
                            }
                            // OverallTotalQuantity elements
                            l:
                            {
                                Element TotalQuantity = doc.createElement("totalQuantity")
                                TotalQuantity.appendChild(doc.createTextNode(orderArray.quantity.sum().toString()))
                                info.appendChild(TotalQuantity)
                            }

                        }
                        setProgress(100)

                        break
                    case "Customer Year Totals":
                        Customers cust = customers.get(0)
                        BigDecimal paid = cust.order.getAmountPaid()
                        int custId = customers.get(0).getId()
                        String customer = cust.getCustomerName()
                        // Root element
                        Set<Ordered_products> orderArray
                        //if (Objects.equals(category, "All")) {
                        orderArray = cust.orderedProducts.sort { it.products.id }

                        /* } else {
                        Categories cat = Categories.findByCategoryNameAndYear(category, cust.year)
                        *//*orderArray = cust.orderedProducts.findAll{
                            it.getProducts().getCategoryId() == cat.getId()}*//*

                        orderArray = Ordered_products.where {
                            customer == cust && year == cust.year && products.category.id == cat.getId()
                        }.list()
                    }*/

                        //Set Items
                        l:
                        {
                            Element products = doc.createElement("customerYear")

                            l:
                            {
                                Element custAddr = doc.createElement("custAddr")
                                custAddr.appendChild(doc.createTextNode("true"))
                                products.appendChild(custAddr)
                            }
                            // customername elements
                            l:
                            {
                                Element custName = doc.createElement("name")
                                custName.appendChild(doc.createTextNode(cust.getCustomerName()))
                                products.appendChild(custName)
                            }
                            l: // StreetAddress elements
                            {
                                Element StreetAddress = doc.createElement("streetAddress")
                                StreetAddress.appendChild(doc.createTextNode(cust.getStreetAddress()))
                                products.appendChild(StreetAddress)
                            }
                            // City elements
                            l:
                            {
                                Element city = doc.createElement("city")
                                String addr = cust.getCity() + ' ' + cust.getState() + ", " + cust.getZipCode()
                                city.appendChild(doc.createTextNode(addr))
                                products.appendChild(city)
                            }

                            // phone elements
                            l:
                            {
                                Element phone = doc.createElement("PhoneNumber")
                                phone.appendChild(doc.createTextNode(cust.getPhone()))
                                products.appendChild(phone)
                            }
                            l:
                            {
                                Element header = doc.createElement("header")
                                header.appendChild(doc.createTextNode("true"))
                                products.appendChild(header)
                            }
                            l:
                            {
                                Element title = doc.createElement("title")
                                title.appendChild(doc.createTextNode(cust.getCustomerName() + ' ' + selectedYear + " Order"))
                                products.appendChild(title)
                            }
                            /* l:
                        {
                            if (includeHeader && !Objects.equals(category, "All")) {
                                Element title = doc.createElement("specialInfo")
                                l:
                                {
                                    Element text = doc.createElement("text")
                                    String notice = "*Notice: These products will be delivered to your house on " + DbInt.getCategoryDate(category, selectedYear) + ('. Total paid to date: $' + cust.getPaid().toPlainString())
                                    text.appendChild(doc.createTextNode(notice))
                                    title.appendChild(text)
                                }
                                products.appendChild(title)
                            }
                        }*/

                            //Product Elements
                            rootElement.appendChild(products)

                            l:
                            {
                                Element prodTable = doc.createElement("prodTable")
                                prodTable.appendChild(doc.createTextNode("true"))
                                products.appendChild(prodTable)
                            }

                            setProgress(5)
                            BigDecimal tCost = BigDecimal.ZERO
                            int productIncValue = 1
                            for (Ordered_products aRowDataF : orderArray) {
                                if (Objects.equals(aRowDataF.products.category ? aRowDataF.products.category.categoryName : "", category) || (Objects.equals(category, "All"))) {

                                    l:
                                    {
                                        Element Product = doc.createElement("Product")
                                        products.appendChild(Product)
                                        //ID
                                        l:
                                        {
                                            Element ID = doc.createElement("ID")
                                            ID.appendChild(doc.createTextNode(aRowDataF.products.humanProductId))
                                            Product.appendChild(ID)
                                        }
                                        //Name
                                        l:
                                        {
                                            Element Name = doc.createElement("Name")
                                            Name.appendChild(doc.createTextNode(aRowDataF.products.productName))
                                            Product.appendChild(Name)
                                        }
                                        //Size
                                        l:
                                        {
                                            Element Size = doc.createElement("Size")
                                            Size.appendChild(doc.createTextNode(aRowDataF.products.unitSize))
                                            Product.appendChild(Size)
                                        }
                                        //UnitCost
                                        l:
                                        {
                                            Element UnitCost = doc.createElement("UnitCost")
                                            UnitCost.appendChild(doc.createTextNode(aRowDataF.products.unitCost.toPlainString()))
                                            Product.appendChild(UnitCost)
                                        }
                                        //Quantity
                                        l:
                                        {
                                            Element Quantity = doc.createElement("Quantity")
                                            Quantity.appendChild(doc.createTextNode(String.valueOf(aRowDataF.quantity)))
                                            Product.appendChild(Quantity)
                                        }
                                        //TotalCost
                                        l:
                                        {
                                            Element TotalCost = doc.createElement("TotalCost")
                                            TotalCost.appendChild(doc.createTextNode(String.valueOf(aRowDataF.extendedCost)))
                                            tCost = tCost.add(aRowDataF.extendedCost)
                                            Product.appendChild(TotalCost)
                                        }
                                    }
                                }
                                setProgress(getProg() + productIncValue)
                            }
                            //Total Cost for this Utilities.Year
                            l:
                            {
                                Element tCostE = doc.createElement("totalCost")
                                tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)))
                                products.appendChild(tCostE)
                            }
                            // OverallTotalCost elements
                            l:
                            {
                                Element TotalCost = doc.createElement("TotalCost")
                                TotalCost.appendChild(doc.createTextNode((orderArray.extendedCost.sum().toString())))
                                info.appendChild(TotalCost)
                            }
                            // OverallTotalQuantity elements
                            l:
                            {
                                Element TotalQuantity = doc.createElement("totalQuantity")
                                TotalQuantity.appendChild(doc.createTextNode(orderArray.quantity.sum().toString()))
                                info.appendChild(TotalQuantity)
                            }
                        }
                        setProgress(100)

                        break

                    case "Customer All-Time Totals":
                        // Collection<String> customerYears = new ArrayList<>()
                        final String[] headerS = ["true"]
                        final BigDecimal[] overallTotalCost = [BigDecimal.ZERO]
                        final int[] overallTotalQuantity = [0]
                        int yearProgressInc = 1
                        //For Each Utilities.Year
                        Customers.findAllByCustomerName(customers.get(0).customerName).forEach { cust ->

                            //    customerYears.add(year)
                            //Product Elements
                            Element products = doc.createElement("customerYear")
                            rootElement.appendChild(products)
                            l:
                            {
                                Element header = doc.createElement("header")
                                header.appendChild(doc.createTextNode(headerS[0]))
                                headerS[0] = "false"
                                products.appendChild(header)
                            }
                            l:
                            {
                                Element prodTable = doc.createElement("prodTable")
                                prodTable.appendChild(doc.createTextNode("true"))
                                products.appendChild(prodTable)
                            }
                            l:
                            {
                                Element custAddr = doc.createElement("custAddr")
                                custAddr.appendChild(doc.createTextNode("true"))
                                products.appendChild(custAddr)
                            }
                            // customername elements
                            l:
                            {
                                Element custName = doc.createElement("name")
                                custName.appendChild(doc.createTextNode(cust.getCustomerName()))
                                products.appendChild(custName)
                            }
                            // StreetAddress elements
                            l:
                            {
                                Element StreetAddress = doc.createElement("streetAddress")
                                StreetAddress.appendChild(doc.createTextNode(cust.getStreetAddress()))
                                products.appendChild(StreetAddress)
                            }
                            // City elements
                            l:
                            {
                                Element city = doc.createElement("city")
                                String addr = cust.getCity() + ' ' + cust.getState() + ", " + cust.getZipCode()
                                city.appendChild(doc.createTextNode(addr))
                                products.appendChild(city)
                            }

                            // phone elements
                            l:
                            {
                                Element phone = doc.createElement("PhoneNumber")
                                phone.appendChild(doc.createTextNode(cust.getPhone()))
                                products.appendChild(phone)
                            }

                            //YearTitle
                            l:
                            {
                                Element title = doc.createElement("title")
                                title.appendChild(doc.createTextNode(cust.getYear().year))
                                products.appendChild(title)
                            }

                            Set<Ordered_products> orderArray = cust.orderedProducts.sort { it.products.id }
                            BigDecimal tCost = BigDecimal.ZERO
                            //For each product in the table set the data
                            int productIncValue = 1
                            for (Ordered_products aRowDataF : orderArray) {

                                l:
                                {
                                    Element Product = doc.createElement("Product")
                                    products.appendChild(Product)
                                    //ID
                                    l:
                                    {
                                        Element ID = doc.createElement("ID")
                                        ID.appendChild(doc.createTextNode(aRowDataF.products.humanProductId))
                                        Product.appendChild(ID)
                                    }
                                    //Name
                                    l:
                                    {
                                        Element Name = doc.createElement("Name")
                                        Name.appendChild(doc.createTextNode(aRowDataF.products.productName))
                                        Product.appendChild(Name)
                                    }
                                    //Size
                                    l:
                                    {
                                        Element Size = doc.createElement("Size")
                                        Size.appendChild(doc.createTextNode(aRowDataF.products.unitSize))
                                        Product.appendChild(Size)
                                    }
                                    //UnitCost
                                    l:
                                    {
                                        Element UnitCost = doc.createElement("UnitCost")
                                        UnitCost.appendChild(doc.createTextNode(aRowDataF.products.unitCost.toPlainString()))
                                        Product.appendChild(UnitCost)
                                    }
                                    //Quantity
                                    l:
                                    {
                                        Element Quantity = doc.createElement("Quantity")
                                        Quantity.appendChild(doc.createTextNode(String.valueOf(aRowDataF.quantity)))
                                        Product.appendChild(Quantity)
                                    }
                                    //TotalCost
                                    l:
                                    {
                                        Element TotalCost = doc.createElement("TotalCost")
                                        TotalCost.appendChild(doc.createTextNode(String.valueOf(aRowDataF.extendedCost)))
                                        tCost = tCost.add(aRowDataF.extendedCost)
                                        Product.appendChild(TotalCost)
                                    }
                                }
                            }
                            //Total for current customers
                            l:
                            {
                                Element tCostE = doc.createElement("totalCost")
                                tCostE.appendChild(doc.createTextNode(String.valueOf(tCost)))
                                products.appendChild(tCostE)
                            }

                            ////Utilities.DbInt.pCon.close()


                            setProgress(getProg() + yearProgressInc)
                        }

                        // OverallTotalCost elements
                        l:
                        {
                            Element TotalCost = doc.createElement("TotalCost")
                            TotalCost.appendChild(doc.createTextNode((overallTotalCost[0].toPlainString())))
                            info.appendChild(TotalCost)
                        }
                        // OverallTotalQuantity elements
                        l:
                        {
                            Element TotalQuantity = doc.createElement("totalQuantity")
                            TotalQuantity.appendChild(doc.createTextNode(Integer.toString(overallTotalQuantity[0])))
                            info.appendChild(TotalQuantity)
                        }

                        setProgress(100)


                        break
                }
            })
        }
        OutputStreamWriter osw = null
        try {
            new ByteArrayOutputStream().withStream { baos ->
                osw = new OutputStreamWriter(baos)

                TransformerFactory tranFactory = TransformerFactory.newInstance()
                Transformer aTransformer = tranFactory.newTransformer()
                aTransformer.setOutputProperty(OutputKeys.ENCODING, osw.getEncoding())
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes")
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml")
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")

                Source src = new DOMSource(doc)
                Result result = new StreamResult(osw)
                aTransformer.transform(src, result)

                osw.flush()

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
                String tmpDirectoryOp = System.getProperty("java.io.tmpdir")
                File tmpDirectory = new File(tmpDirectoryOp)
                xmlTempFile = File.createTempFile("LGReport" + timeStamp, ".xml", tmpDirectory)
                xmlTempFile.deleteOnExit()

                new FileOutputStream(xmlTempFile).withStream {
                    baos.writeTo(it)
                }// writing bytes in to byte output stream

                /* } catch (IOException e) {
                    updateMessage("Error writing temporary XML. Please try again.")
                    throw e
                }*/

            }
        } catch (Exception exp) {
            updateMessage("Error while genertating tempory XML. Please try again or contact support.")
            throw exp
        } finally {
            try {
                if (osw != null) {
                    osw.close()
                }
            } catch (IOException | RuntimeException e) {
                updateMessage("Error closing temporary XML file.")

                //LogToFile.log(e, Severity.FINE, "Error closing temporary XML file.")
            }

        }
        String ret = convertXSLToPDF()
        updateMessage("Done")

        // Return the number of matches found
        return ret
    }

    private String convertXSLToPDF() throws Exception {
        String fileLoc = ""
        OutputStream os = new ByteArrayOutputStream()

        Processor proc = new Processor(false)
        XsltCompiler comp = proc.newXsltCompiler()

        try {
            getClass().getResourceAsStream("/Report.xsl").withStream { templateStream ->
                XsltExecutable exp = comp.compile(new StreamSource(templateStream))
                XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlTempFile))
                Serializer out = proc.newSerializer(os)
                out.setOutputProperty(Serializer.Property.METHOD, "html")
                out.setOutputProperty(Serializer.Property.INDENT, "yes")
                XsltTransformer trans = exp.load()
                trans.setInitialContextNode(source)
                trans.setDestination(out)
                trans.transform()
                ByteArrayOutputStream baos
                baos = (ByteArrayOutputStream) os

                InputStream is = new ByteArrayInputStream(baos.toByteArray())

                Tidy tidy = new Tidy() // obtain a new Tidy instance
                // set desired config options using tidy setters
                OutputStream osT = new ByteArrayOutputStream()
                tidy.setQuiet(true)
                tidy.setIndentContent(true)
                tidy.setDocType("loose")
                tidy.setFixBackslash(true)
                tidy.setFixUri(true)
                tidy.setShowWarnings(false)
                tidy.setEscapeCdata(true)
                tidy.setXHTML(true)
                tidy.setInputEncoding("utf8")
                tidy.setOutputEncoding("utf8")

                File xhtml
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
                String tmpDirectoryOp = System.getProperty("java.io.tmpdir")
                File tmpDirectory = new File(tmpDirectoryOp)
                xhtml = File.createTempFile("LGReportXhtml" + timeStamp, ".xhtml", tmpDirectory)
                File pdfF = File.createTempFile("LGReportPDF" + timeStamp, ".pdf", tmpDirectory)
                fileLoc = pdfF.absolutePath
                xhtml.deleteOnExit()
                try {
                    new FileOutputStream(xhtml).withStream {
                        tidy.parse(is, osT) // run tidy, providing an input and output streamp
                        ByteArrayOutputStream baosT
                        baosT = (ByteArrayOutputStream) osT

                        baosT.writeTo(it)
                    }

                    new FileOutputStream(pdfF).withStream { fos ->
                        String cssText = "* {\n" +
                                "                margin-top: 0px\n" +
                                "                margin-bottom: 0px\n" +
                                "                } " +
                                " .LBordered {\n" +
                                "                border-left: 1px solid black\n" +
                                "                border-bottom: 1px solid black\n" +

                                "                border-collapse: collapse\n" +
                                "                }\n" +
                                "                .Bordered {\n" +
                                "                border: 1px solid black\n" +
                                "                border-collapse: collapse\n" +
                                "                }\n" +
                                "                .UBordered {\n" +
                                "                border: 0px solid black\n" +
                                "                border-collapse: collapse\n" +
                                "                }\n" +
                                "                .splitTitle {display:inline}\n" +
                                "                h4{\n" +
                                "                margin:1px\n" +
                                "                padding:1px\n" +
                                "                }\n" +
                                "                table {\n" +
                                "                width:100%\n" +
                                "                margin-bottom: 0.4pt\n" +
                                "                margin-top: 0\n" +
                                "                margin-left: 0\n" +
                                "                margin-right: 0\n" +
                                "                text-indent: 0\n" +
                                "                }\n" +
                                "                tr {\n" +
                                "                vertical-align: inherit\n" +
                                "                }\n" +
                                "                table > tr {\n" +
                                "                vertical-align: middle\n" +
                                "                }\n" +
                                "                table, td {\n" +
                                "                background-color:#FFF\n" +
                                "                font-size:10pt\n" +
                                "                padding: 50px\n" +
                                "                border-spacing: 50px\n" +

                                "                text-align: inherit\n" +
                                "                vertical-align: inherit\n" +
                                "                }\n" +
                                "                th {\n" +
                                "                background-color: #FFF\n" +
                                "                font-size:10pt\n" +
                                "                color:#000\n" +
                                "                display: table-cell\n" +
                                "                font-weight: bold\n" +
                                "                padding: 1px\n" +
                                "                vertical-align: inherit\n" +
                                "                }"
                        //fstream.deleteOnExit()
                        com.itextpdf.text.Document document = new com.itextpdf.text.Document()

                        // step 2
                        PdfWriter writer = PdfWriter.getInstance(document, fos)
                        writer.setInitialLeading(12.5f)
                        writer.setTagged()
                        // step 3
                        document.open()

                        // step 4

                        // CSS
                        CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true)
                        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(cssText.getBytes()))
                        cssResolver.addCss(cssFile)
                        // HTML
                        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null)
                        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory())
                        htmlContext.autoBookmark(false)
                        htmlContext.setImageProvider(new Base64ImageProvider())

                        // Pipelines
                        PdfWriterPipeline pdf = new PdfWriterPipeline(document, writer)
                        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf)
                        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html)

                        // XML Worker
                        XMLWorker worker = new XMLWorker(css, true)
                        XMLParser p = new XMLParser(worker)
                        p.parse(new FileInputStream(xhtml))

                        // step 5
                        document.close()
                    }

                    /*com.itextpdf.text.Document document = new com.itextpdf.text.Document()
                    // step 2
                    PdfWriter writer = PdfWriter.getInstance(document, fos)
                    // step 3
                    document.open()
                    // step 4
                    XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                            new FileInputStream(xhtml))
                    // step 5
                    document.close()*/


                } catch (Exception e) {
                    updateMessage("Error ocurred while converting temporary XML to pdf. Try again or contact support.")
                    throw e
                }
            }

            /*
          Compile and execute a simple transformation that applies a stylesheet to an input file,
          and serializing the result to an output file
         */


        }
        catch (IOException e) {
            updateMessage("Error writing PDF file. Please try again.")
            throw e
        }
        return fileLoc
    }

    private double getProg() {
        return prog
    }

    private void setProgress(double progress) {
        prog += progress

    }

    private static void updateMessage(String message) {
        println message
    }

    class Base64ImageProvider extends AbstractImageProvider {

        @Override
        Image retrieve(String src) {
            int pos = src.indexOf("base64,")
            try {
                if (src.startsWith("data") && pos > 0) {
                    byte[] img = (src.substring(pos + 7)).decodeBase64()
                    return Image.getInstance(img)
                } else {
                    return Image.getInstance(src)
                }
            } catch (BadElementException ex) {
                return null
            } catch (IOException ex) {
                return null
            }
        }

        @Override
        String getImageRootPath() {
            return null
        }
    }
}
