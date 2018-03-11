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

package Controllers;

import Utilities.*;
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
import javafx.scene.layout.VBox;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("WeakerAccess")

public class MapController implements Initializable {

    //  public HBox custOrders;
    // public Label custAddress;
    //public Label custPhone;
    //public Label custName;
    @FXML
    private VBox infoPanel;
    @FXML
    private Button button;

    @FXML
    private GoogleMapView googleMapView;

    private MainController mainCont;
    private HashMap<LatLongComparable, ArrayList<Customer>> customersToClick = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    protected void configureMap() {
        try {
            MapOptions mapOptions = new MapOptions();

            mapOptions.center(new LatLong(47.6097, -122.3331))
                    .mapType(MapTypeIdEnum.ROADMAP).zoom(9);


            //initMap();
            final double[] totCoords = {0, 0, 0};
            double totLat = 0;
            final double[] totLon = {0};
            HashMap<Marker, LatLongComparable> markers = new HashMap<>();
/*        List<String> Addr = getAllCustomersInfo("ADDRESS");
        List<String> Town = getAllCustomersInfo("TOWN");
        List<String> State = getAllCustomersInfo("STATE");

        List<String> latL = getAllCustomersInfo("Lat");
        List<String> lonL = getAllCustomersInfo("Lon");
        List<String> Ord = getAllCustomersInfo("Ordered");
        List<String> NI = getAllCustomersInfo("NI");
        List<String> NH = getAllCustomersInfo("NH");*/
            //cPoints = new Object[Addr.size()];
            Iterable<String> years = DbInt.getUserYears();
            // List<LatLong> customers = new ArrayList<LatLong>();
            years.forEach(year -> {
                List<String> ret = new ArrayList<>();
                Year yearObj = new Year(year);
                yearObj.getCustomers().forEach(customer -> {
                    LatLongComparable custLatLong = new LatLongComparable(customer.getLat(), customer.getLon());
                    if (!customersToClick.containsKey(custLatLong)) {
                        double lat = customer.getLat();
                        double lon = customer.getLon();
                        totCoords[0] += lat;
                        totCoords[1] += lon;
                        MarkerOptions opts = new MarkerOptions();
                        String address = customer.getAddr();
                        String cName = customer.getName();

                        opts.title(cName + " " + address + " " + customer.getCustAddressFrmName()[0] + ", " + customer.getCustAddressFrmName()[1]);
                        opts.position(new LatLong(lat, lon));
                        Marker m = new Marker(opts);

                        // cPoints[i] = new Utilities.cPoint(lat, lon, Addr.get(i), Town.get(i), State.get(i));
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
                        markers.put(m, custLatLong);
                        totCoords[2]++;
                        ArrayList<Customer> customs = new ArrayList();
                        customs.add(customer);
                        customersToClick.put(custLatLong, customs);

                    } else {
                        customersToClick.get(custLatLong).add(customer);
                    }
                });

            });
            GoogleMap map = googleMapView.createMap(mapOptions, false);


            markers.forEach((m, latLon) -> {
                map.addMarker(m);
                map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> markerClicked(latLon));
            });

            map.setCenter(new LatLong(totCoords[0] / totCoords[2], totCoords[1] / totCoords[2]));


        } catch (Exception e) {
            LogToFile.log(e, Severity.WARNING, "Error loading map");
        }


    }

    public void initMap(MainController mainController) {
        mainCont = mainController;
        googleMapView.addMapInializedListener(() -> configureMap());


        //new Controllers.MapController(map(), this);


    }

    private void markerClicked(LatLongComparable latLong) {

        //System.out.println(mapMarker + " is clicked");
        //System.out.println(cP.getAddress());
        //String address = marker.getTitle();

        //custAddress.setText(address);
        //Utilities.Customer cust = new Utilities.Customer()

        infoPanel.getChildren().clear();
        // m.OrderStat.setText("Has Ordered");
                                        /*Get info about customer that has clicked
                                        Display name Phone  Utilities.Order status
                                        Creates a button for each ordered year to view more information
                                        */

        String Name;

        List<Customer> customers = customersToClick.get(latLong);
        HashMap<String, HBox> custNames = new HashMap<>();
        customers.forEach(customer -> {
            if (custNames.containsKey(customer.getName())) {
                String PhoneD = customer.getPhone();

/*                                                if (m.infoPanel.getComponentCount() > 8) {
                                                    m.infoPanel.remove(m.infoPanel.getComponentCount() - 1);

                                                }*/
                Button b = new Button(customer.getYear());
                b.setOnAction(e1 -> {
                    Pane newPane = null;
                    FXMLLoader loader;
                    String tabTitle;
                    loader = new FXMLLoader(getClass().getResource("/UI/Customer.fxml"));
                    try {
                        newPane = loader.load();
                    } catch (IOException e) {
                        LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                    }
                    CustomerController customerCont = loader.getController();

                    customerCont.initCustomer(customer, mainCont);
                    tabTitle = ("Customer View - " + customer.getName() + " - " + customer.getYear());
                    mainCont.addTab(newPane, tabTitle);
                });
                custNames.get(customer.getName()).getChildren().add(b);
            } else {
                HBox custOrders = new HBox();

                Label addrHeader = new Label("Customer address");
                Label nameHeader = new Label("Customer Name");
                Label phoneHeader = new Label("Customer Phone");
                Label orderHeader = new Label("Customer Orders");
                addrHeader.getStyleClass().add("Info-Header");
                nameHeader.getStyleClass().add("Info-Header");
                phoneHeader.getStyleClass().add("Info-Header");
                orderHeader.getStyleClass().add("Info-Header");

                Label custAddress = new Label(customer.getAddr());
                Label custPhone = new Label(customer.getPhone());
                Label custName = new Label(customer.getName());
                String PhoneD = customer.getPhone();
                custNames.put(customer.getName(), custOrders);
/*                                                if (m.infoPanel.getComponentCount() > 8) {
                                                    m.infoPanel.remove(m.infoPanel.getComponentCount() - 1);

                                                }*/
                Button b = new Button(customer.getYear());
                b.setOnAction(e1 -> {
                    Pane newPane = null;
                    FXMLLoader loader;
                    String tabTitle;
                    loader = new FXMLLoader(getClass().getResource("/UI/Customer.fxml"));
                    try {
                        newPane = loader.load();
                    } catch (IOException e) {
                        LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
                    }
                    CustomerController customerCont = loader.getController();

                    customerCont.initCustomer(customer, mainCont);
                    tabTitle = ("Customer View - " + customer.getName() + " - " + customer.getYear());
                    mainCont.addTab(newPane, tabTitle);
                });
                custOrders.getChildren().add(b);
                VBox info = new VBox(nameHeader, custName, phoneHeader, custPhone, addrHeader, custAddress, orderHeader, custOrders);
                info.getStyleClass().add("Map-Info-Box");
                infoPanel.getChildren().add(info);
            }


        });



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
            ////Utilities.DbInt.pCon.close()

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
            ////Utilities.DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    class LatLongComparable extends LatLong {

        public LatLongComparable(double latitude, double longitude) {
            super(latitude, longitude);
        }

        @Override
        public int hashCode() {
            return Double.hashCode(getLatitude()) + Double.hashCode(getLongitude());
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof LatLongComparable)) { return false; }
            if (o == this) { return true; }
            LatLongComparable compare = (LatLongComparable) o;
            return (this.getLatitude() == compare.getLatitude() && this.getLongitude() == compare.getLongitude());
        }
    }
}