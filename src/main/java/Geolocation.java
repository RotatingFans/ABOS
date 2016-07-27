import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by patrick on 7/26/16.
 */
public class Geolocation {
    public String getCityState(String zipCode) throws IOException {
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

    public Object[][] GetCoords(String Address) throws IOException {
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

    public Object[][] parseCoords(String xml) {
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
