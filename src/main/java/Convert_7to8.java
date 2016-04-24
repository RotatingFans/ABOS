import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by patrick on 1/26/16.
 */
public class Convert_7to8 {
    private LogToFile MyLogger = new LogToFile();

    private Convert_7to8() {
        System.out.println("Starting");
        Iterable<String> years = getYears();
        Object[][] yearData; //this is generic can use String[] directly
        for (String year : years) {
            int noOfCust = 1;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM Customers");
                 ResultSet rs = prep.executeQuery()) {
                while (!rs.isLast()) {
                    rs.next();
                }
                noOfCust = rs.getRow();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            yearData = new Object[15][noOfCust + 1];
            System.out.println("Starting");

            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM Customers");
                 ResultSet rs = prep.executeQuery()) {

                Object rsArray;

                while (rs.next()) {
                    //ID
                    rsArray = rs.getObject(1);
                    yearData[0][rs.getRow()] = rsArray;
                    //NAME
                    rsArray = rs.getObject(2);
                    yearData[1][rs.getRow()] = rsArray;
                    //ADDR
                    rsArray = rs.getObject(3);
                    yearData[2][rs.getRow()] = rsArray;
                    //PHONE
                    rsArray = rs.getObject(4);
                    yearData[3][rs.getRow()] = rsArray;
                    //ORDERID
                    rsArray = rs.getObject(5);
                    yearData[4][rs.getRow()] = rsArray;
                    //PAID
                    rsArray = rs.getObject(6);
                    yearData[5][rs.getRow()] = rsArray;
                    //Delivered
                    rsArray = rs.getObject(7);
                    yearData[6][rs.getRow()] = rsArray;
                    //EMail
                    rsArray = rs.getObject(8);
                    yearData[7][rs.getRow()] = rsArray;
                    //DONATION
                    rsArray = 0;
                    yearData[13][rs.getRow()] = rsArray;
                }
                ////DbInt.pCon.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            //puts all the customer data into an array
            for (int i = 1; i < yearData[2].length; i++) {
                Object addr = yearData[2][i];

                double lat = 0.0;
                double lon = 0.0;
                String[] address = new String[4];
                try {
                    Object[][] coords = GetCoords(addr.toString());
                    lat = Double.valueOf(coords[0][0].toString());
                    lon = Double.valueOf(coords[0][1].toString());
                    address = GetAddress(addr.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String streetAdd = address[3];
                String city = address[0];
                String state = address[1];
                String zip = address[2];
                //LAT
                yearData[8][i] = lat;
                //LONG
                yearData[9][i] = lon;
                //Street
                yearData[2][i] = streetAdd;
                //TOWN
                yearData[10][i] = city;
                //STATE
                yearData[11][i] = state;
                //ZIPCODE
                yearData[12][i] = zip;
            }
            //Store settings data

            //Delete Year Customer table

            try (PreparedStatement addCol = DbInt.getPrep(year, "DROP TABLE \"CUSTOMERS\"")) {
                addCol.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Recreate Year Customer table

            try (PreparedStatement addCol = DbInt.getPrep(year, "CREATE TABLE CUSTOMERS(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),NAME varchar(255),ADDRESS varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), PHONE varchar(255), ORDERID varchar(255), PAID varchar(255),DELIVERED varchar(255), EMAIL varchar(255), DONATION VARCHAR(255))")) {
                addCol.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //Write data back to year
            for (int i = 1; i < yearData[2].length; i++) {
                try (PreparedStatement addCol = DbInt.getPrep(year, "INSERT INTO Customers(NAME,ADDRESS, Town, STATE, ZIPCODE, Lat, Lon, PHONE, ORDERID, PAID,DELIVERED, EMAIL, DONATION) Values (?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                    addCol.setString(1, yearData[1][i].toString());
                    addCol.setString(2, yearData[2][i].toString());

                    addCol.setString(3, yearData[10][i].toString());
                    addCol.setString(4, yearData[11][i].toString());
                    addCol.setString(5, yearData[12][i].toString());
                    addCol.setDouble(6, (Double) yearData[8][i]);
                    addCol.setDouble(7, (Double) yearData[9][i]);
                    addCol.setString(8, yearData[3][i].toString());
                    addCol.setString(9, yearData[4][i].toString());
                    addCol.setString(10, yearData[5][i].toString());
                    addCol.setString(11, yearData[6][i].toString());
                    addCol.setString(12, yearData[7][i].toString());
                    addCol.setString(13, yearData[13][i].toString());

                    addCol.execute();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


        }

        int noOfCust = 1;
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT * FROM Customers");
             ResultSet rs = prep.executeQuery()) {
            while (!rs.isLast()) {
                rs.next();
            }
            noOfCust = rs.getRow();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Object[][] setData = new Object[15][noOfCust + 1]; //this is generic can use String[] directly
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT * FROM Customers");
             ResultSet rs = prep.executeQuery()) {

            Object rsArray;


            while (rs.next()) {
                //ID
                rsArray = rs.getObject(1);
                setData[0][rs.getRow()] = rsArray;
                //Address
                rsArray = rs.getObject(2);
                setData[1][rs.getRow()] = rsArray;
                //Ordered
                rsArray = rs.getObject(3);
                setData[2][rs.getRow()] = rsArray;
                //NI
                rsArray = rs.getObject(4);
                setData[3][rs.getRow()] = rsArray;
                //NH
                rsArray = rs.getObject(5);
                setData[4][rs.getRow()] = rsArray;
            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        //puts all the customer data into an array
        for (int i = 1; i < setData[2].length; i++) {
            Object addr = setData[1][i];

            double lat = 0.0;
            double lon = 0.0;
            String[] address = new String[4];
            try {
                Object[][] coords = GetCoords(addr.toString());
                lat = Double.valueOf(coords[0][0].toString());
                lon = Double.valueOf(coords[0][1].toString());
                address = GetAddress(addr.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            String streetAdd = address[3];
            String city = address[0];
            String state = address[1];
            String zip = address[2];
            //LAT
            setData[5][i] = lat;
            //LONG
            setData[6][i] = lon;
            //Street
            setData[1][i] = streetAdd;
            //TOWN
            setData[7][i] = city;
            //STATE
            setData[8][i] = state;
            //ZIPCODE
            setData[9][i] = zip;
        }
        //Delete Set Customer table

        try (PreparedStatement addCol = DbInt.getPrep("Set", "DROP TABLE \"CUSTOMERS\"")) {
            addCol.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Recreate Settings Customer table
        try (PreparedStatement addCol = DbInt.getPrep("Set", "CREATE TABLE Customers(CustomerID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), Address varchar(255), Town VARCHAR(255), STATE VARCHAR(255), ZIPCODE VARCHAR(6), Lat float(15), Lon float(15), Ordered VARChAR(255), NI VARChAR(255), NH VARChAR(255))")) {
            addCol.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Write data back to Set
        for (int i = 1; i < setData[2].length; i++) {
            try (PreparedStatement addCol = DbInt.getPrep("Set", "INSERT INTO Customers(Address, Town, STATE, ZIPCODE, Lat, Lon, Ordered, NI, NH) Values (?,?,?,?,?,?,?,?,?)")) {

                addCol.setString(1, setData[1][i].toString());
                addCol.setString(2, setData[7][i].toString());
                addCol.setString(3, setData[8][i].toString());
                addCol.setString(4, setData[9][i].toString());
                addCol.setDouble(5, (Double) setData[5][i]);
                addCol.setDouble(6, (Double) setData[6][i]);
                addCol.setString(7, setData[2][i].toString());
                addCol.setString(8, setData[3][i].toString());
                addCol.setString(9, setData[4][i].toString());


                addCol.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Success");
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> {
            try {
                Convert_7to8 window = new Convert_7to8();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
    }

    private static Iterable<String> getYears() {
        Collection<String> ret = new ArrayList<>();
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT YEARS FROM Years");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("YEARS"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

// --Commented out by Inspection START (2/1/16 5:28 PM):
//    private static Iterable<String> getCustomers(String year) {
//        Collection<String> ret = new ArrayList<>();
//
//        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers");
//             ResultSet rs = prep.executeQuery()) {
//
//
//            while (rs.next()) {
//
//                ret.add(rs.getString("NAME"));
//
//            }
//            ////DbInt.pCon.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//        return ret;
//    }
// --Commented out by Inspection STOP (2/1/16 5:28 PM)

    private String[] GetAddress(String Address) throws IOException {
        String AddressF = Address.replace(" ", "+");
        String url = String.format("http://open.mapquestapi.com/nominatim/v1/search.php?key=CCBtW1293lbtbxpRSnImGBoQopnvc4Mz&format=xml&q=%s&addressdetails=1&limit=1&accept-language=en-US", AddressF);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        String city = "";
        String State = "";
        String zipCode = "";
        String hN = "";
        String strt = "";
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                //inputLine = StringEscapeUtils.escapeHtml4(inputLine);
                //inputLine = StringEscapeUtils.escapeXml11(inputLine);
                response.append(inputLine);
            }


            //String city = "";
            try {
                InputSource is = new InputSource(new StringReader(response.toString()));

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                NodeList nList = doc.getElementsByTagName("place");


                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);


                    if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;


                        city = eElement.getElementsByTagName("city").item(0).getTextContent();
                        State = eElement.getElementsByTagName("state").item(0).getTextContent();
                        zipCode = eElement.getElementsByTagName("postcode").item(0).getTextContent();
                        hN = eElement.getElementsByTagName("house_number").item(0).getTextContent();
                        strt = eElement.getElementsByTagName("road").item(0).getTextContent();


                        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String[] address = new String[4];
        address[0] = city;
        address[1] = State;
        address[2] = zipCode;
        address[3] = hN + ' ' + strt;
        return address;
//        //print result
//        //	return parseCoords(response.toString());
    }

    private Object[][] GetCoords(String Address) throws IOException {
        String AddressF = Address.replace(" ", "+");
        String url = String.format("http://open.mapquestapi.com/nominatim/v1/search.php?key=CCBtW1293lbtbxpRSnImGBoQopnvc4Mz&format=xml&q=%s&addressdetails=0&limit=1", AddressF);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        String USER_AGENT = "Mozilla/5.0";
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }


            //print result
            return parseCoords(response.toString());
        }
    }

    private Object[][] parseCoords(String xml) {
        Object[][] coords = new Object[1][2];
        try {
            InputSource is = new InputSource(new StringReader(xml));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("place");


            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {


                    coords[0][0] = ((Element) nNode).getAttributeNode("lat").getValue();
                    coords[0][1] = ((Element) nNode).getAttributeNode("lon").getValue();


                    //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coords;
    }


}
