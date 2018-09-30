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

package Utilities;/*
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

import Exceptions.addressException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * Created by patrick on 7/26/16.
 */
public class Geolocation {

    /**
     * Takes a zipcode and returns the city and state of the customer.
     *
     * @param zipCode The Zipcode of the customer
     * @return The City and state of the customer
     * @throws IOException If it fails to retrieve the city and state
     */
    public static String getCityState(String zipCode) throws IOException {
        //String AddressF = Address.replace(" ","+");
        //The URL for the MapquestAPI

        String url = String.format("https://api.opencagedata.com/geocode/v1/xml?key=4745cb28cf7744d7b43f1dd482b83d5d&countrycode=us&min_confidence=6&no_annotations=1&q=%s", zipCode);

        //Defines connection
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        String city = "";
        String State = "";
        Integer prevConfidence = 0;
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

                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                NodeList nList = doc.getElementsByTagName("result");


                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);


                    if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        if (Objects.equals(eElement.getElementsByTagName("_type").item(0).getTextContent(), "city") && Integer.valueOf(eElement.getElementsByTagName("confidence").item(0).getTextContent()) > prevConfidence) {

                            city = eElement.getElementsByTagName("city").item(0).getTextContent();
                            State = eElement.getElementsByTagName("state").item(0).getTextContent();
                        }

                        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                    }
                }
            } catch (Exception e) {
                LogToFile.log(e, Severity.WARNING, "Error parsing geolocation server response. Please Check the address and try again");
            }
        }
        //Formats City and state into one string to return
        String fullName = city + '&';
        fullName += State;
        //print result
        //	return parseCoords(response.toString());
        return fullName;
    }

    public static Coords GetCoords(String Address) throws IOException {
        String AddressF = Address.replace(" ", "%20");
        String url = String.format("https://api.opencagedata.com/geocode/v1/xml?key=4745cb28cf7744d7b43f1dd482b83d5d&countrycode=us&min_confidence=6&no_annotations=1&q=%s", AddressF);
        Integer prevConfidence = 0;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        String USER_AGENT = "Mozilla/5.0";
        con.setRequestProperty("User-Agent", USER_AGENT);

        //int responseCode = con.getResponseCode();
        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }


            //print result
            Coords coords = new Coords();
            try {
                InputSource is = new InputSource(new StringReader(response.toString()));

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(is);

                //optional, but recommended
                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                NodeList nList = doc.getElementsByTagName("result");

                if (nList.getLength() < 1) {
                    throw new addressException();
                }


                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);


                    if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNode;
                        if (Objects.equals(eElement.getElementsByTagName("_type").item(0).getTextContent(), "building") && Integer.valueOf(eElement.getElementsByTagName("confidence").item(0).getTextContent()) > prevConfidence) {
                            Element geometry = (Element) eElement.getElementsByTagName("geometry").item(0);


                            coords.setLat(Double.valueOf(geometry.getElementsByTagName("lat").item(0).getTextContent()));
                            coords.setLon(Double.valueOf(geometry.getElementsByTagName("lng").item(0).getTextContent()));
                        }

                        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                    }
                }
            } catch (IOException | ParserConfigurationException | SAXException e) {
                LogToFile.log(e, Severity.WARNING, "Error parsing geolocation server response. Please try again or contact support.");
            }
            if (coords.getLat() <= -190 && coords.getLon() <= -190) {
                throw new addressException();

            } else {


                return coords;
            }
        } catch (Exception e) {
            throw new addressException();
        }
    }

    private static Object[][] parseCoords(String xml) throws addressException {
        Object[][] coords = new Object[1][2];
        try {
            InputSource is = new InputSource(new StringReader(xml));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("place");

            if (nList.getLength() < 1) {
                throw new addressException();
            }
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);


                if ((int) nNode.getNodeType() == (int) Node.ELEMENT_NODE) {


                    coords[0][0] = ((Element) nNode).getAttributeNode("lat").getValue();
                    coords[0][1] = ((Element) nNode).getAttributeNode("lon").getValue();


                    //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LogToFile.log(e, Severity.WARNING, "Error parsing geolocation server response. Please try again or contact support.");
        }
        return coords;
    }


}
