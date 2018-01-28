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

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("WeakerAccess")

public class MapController implements Initializable {

    public HBox custOrders;
    public Label custAddress;
    public Label custPhone;
    public Label custName;
    @FXML
    private Button button;

    @FXML
    private GoogleMapView googleMapView;

    private MainController mainCont;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    protected void configureMap() {
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(new LatLong(47.6097, -122.3331))
                .mapType(MapTypeIdEnum.ROADMAP).zoom(9);
        GoogleMap map = googleMapView.createMap(mapOptions, false);


        //initMap();
        final double[] totCoords = {0, 0, 0};
        double totLat = 0;
        final double[] totLon = {0};
/*        List<String> Addr = getAllCustomersInfo("ADDRESS");
        List<String> Town = getAllCustomersInfo("TOWN");
        List<String> State = getAllCustomersInfo("STATE");

        List<String> latL = getAllCustomersInfo("Lat");
        List<String> lonL = getAllCustomersInfo("Lon");
        List<String> Ord = getAllCustomersInfo("Ordered");
        List<String> NI = getAllCustomersInfo("NI");
        List<String> NH = getAllCustomersInfo("NH");*/
        //cPoints = new Object[Addr.size()];
        Iterable<String> years = DbInt.getYears();
        List<Customer> customers = new ArrayList<Customer>();
        years.forEach(year -> {
            List<String> ret = new ArrayList<>();

            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT * FROM customerview", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                 ResultSet rs = prep.executeQuery()) {
                while (rs.next()) {
                    Customer cust;
                    try {
                        cust = new Customer(rs.getInt("idcustomers"), year);
                        if (!customers.contains(cust)) {
                            customers.add(cust);
                            double lat = rs.getDouble("Lat");
                            double lon = rs.getDouble("Lon");
                            totCoords[0] += lat;
                            totCoords[1] += lon;
                            MarkerOptions opts = new MarkerOptions();
                            String address = rs.getString("streetAddress");
                            String cName = rs.getString("Name");

                            opts.title(cName + " " + address + " " + rs.getString("City") + ", " + rs.getString("State"));
                            opts.position(new LatLong(lat, lon));
                            Marker m = new Marker(opts);

                            // cPoints[i] = new cPoint(lat, lon, Addr.get(i), Town.get(i), State.get(i));
                            //Determine color of dot
                            //Green = orderd
                            //Cyan = Not Interested
                            //Magenta = not home
            /*if (Ord.get(Ord.size() - 1).equals("True")) {
                m.setBackColor(Color.GREEN);
            }
            if (NI.get(NI.size() - 1).equals("True")) {
                m.setBackColor(Color.CYAN);
            }
            if (NH.get(NH.size() - 1).equals("True")) {
                m.setBackColor(Color.MAGENTA);
            }*/
                            //String id = getCustInfo("Set", "CUSTOMERID", Addr.get(i)).get(0);
                            map.addMarker(m);
                            map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> markerClicked(cust));
                            totCoords[2]++;
                        }

                    } catch (Exception e) {
                        LogToFile.log(e, Severity.WARNING, "Error adding mappoint. Please try again or contact support.");
                    }
                }
                ////DbInt.pCon.close()

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        });

        map.setCenter(new LatLong(totCoords[0] / totCoords[2], totCoords[1] / totCoords[2]));


    }

    public void initMap(MainController mainController) {
        mainCont = mainController;
        googleMapView.addMapInializedListener(() -> configureMap());


        //new MapController(map(), this);


    }

    private void markerClicked(Customer customer) {

        //System.out.println(mapMarker + " is clicked");
        //System.out.println(cP.getAddress());
        //String address = marker.getTitle();

        //custAddress.setText(address);
        //Customer cust = new Customer()

        custOrders.getChildren().removeAll();
            // m.OrderStat.setText("Has Ordered");
                                        /*Get info about customer that has clicked
                                        Display name Phone  Order status
                                        Creates a button for each ordered year to view more information
                                        */
            Iterable<String> yearsD;
            yearsD = DbInt.getYears();
        String Name;
        final String[] Phone = new String[1];
        final String[] Address = new String[1];

            yearsD.forEach(year -> {
                Customer cust = new Customer(customer.getName(), year);


                String PhoneD = cust.getPhone();
                Phone[0] = PhoneD;
                Address[0] = cust.getAddr();
/*                                                if (m.infoPanel.getComponentCount() > 8) {
                                                    m.infoPanel.remove(m.infoPanel.getComponentCount() - 1);

                                                }*/
                    Button b = new Button(year);
                    b.setOnAction(e1 -> {
                        Pane newPane = null;
                        FXMLLoader loader;
                        String tabTitle;
                        loader = new FXMLLoader(getClass().getResource("UI/Customer.fxml"));
                        try {
                            newPane = loader.load();
                        } catch (IOException e) {
                            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                        }
                        CustomerController customerCont = loader.getController();

                        customerCont.initCustomer(year, cust.getName(), mainCont);
                        tabTitle = ("Customer View - " + cust.getName() + " - " + year);
                        mainCont.addTab(newPane, tabTitle);
                    });
                    custOrders.getChildren().add(b);

            });
        custName.setText(customer.getName());
        custPhone.setText(customer.getPhone());
        custAddress.setText(customer.getAddr());

                   /* if (NI.get(NI.size() - 1).equals("True")) {
                        m.OrderStat.setText("Not interested");
                    }
                    if (NH.get(NH.size() - 1).equals("True")) {
                        m.OrderStat.setText("Not home");
                    }*/


    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    private static Coordinate c(double lat, double lon) {
//        return new Coordinate(lat, lon);
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

// --Commented out by Inspection START (2/1/16 5:28 PM):
//    public void main(String... args) {
//        Map window = new Map();
//        window.setVisible(true);
//        map().setDisplayToFitMapElements(true, false, false);
//
//
//    }
// --Commented out by Inspection STOP (2/1/16 5:28 PM)

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Create the application.
//     */
///*	public Map() {
//        initialize();
//		try {
////			Object[][] coords = GetCoords("1833 Rowland Rd Abington, PA");
////			System.out.println(coords[0][0]);
////			System.out.println(coords[0][1]);
//		}
//			catch(java.lang.Exception e) {
//					System.out.println(e.toString());
//			}
//		}*/
//    public Object[] getCPoints() {
//        return cPoints;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    /**
     * Getsx the requested info on customers
     *
     * @param info Info to retrieve
     * @return The info requested in ArrayList form
     */
    private List<String> getAllCustomersInfo(String info) {
        List<String> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection("Set");
             PreparedStatement prep = con.prepareStatement("SELECT * FROM Customers", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {

                ret.add(rs.getString(info));

            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    private List<String> getCustInfo(String Db, String info, String Address) {
        List<String> ret = new ArrayList<>();


        try (Connection con = DbInt.getConnection(Db);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM Customers WHERE ADDRESS=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, Address);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret.add(rs.getString(info));

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }


}