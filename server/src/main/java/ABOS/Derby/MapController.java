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

package ABOS.Derby;

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
        double totLat = 0;
        double totLon = 0;
        List<String> Addr = getAllCustomersInfo("ADDRESS");
        List<String> Town = getAllCustomersInfo("TOWN");
        List<String> State = getAllCustomersInfo("STATE");

        List<String> latL = getAllCustomersInfo("Lat");
        List<String> lonL = getAllCustomersInfo("Lon");
        List<String> Ord = getAllCustomersInfo("Ordered");
        List<String> NI = getAllCustomersInfo("NI");
        List<String> NH = getAllCustomersInfo("NH");
        //cPoints = new Object[Addr.size()];
        for (int i = 0; i < Addr.size(); i++) {
            try {
                double lat = Double.valueOf(latL.get(i));
                double lon = Double.valueOf(lonL.get(i));
                totLat += lat;
                totLon += lon;
                MarkerOptions opts = new MarkerOptions();
                opts.title(Addr.get(i) + " " + Town.get(i) + ", " + State.get(i));
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
                map.addMarker(m);
                int finalI = i;
                map.addUIEventHandler(m, UIEventType.click, (JSObject obj) -> markerClicked(Addr.get(finalI)));
            } catch (Exception e) {
                LogToFile.log(e, Severity.WARNING, "Error adding mappoint. Please try again or contact support.");
            }

        }
        map.setCenter(new LatLong(totLat / Addr.size(), totLon / Addr.size()));


    }

    public void initMap(MainController mainController) {
        mainCont = mainController;
        googleMapView.addMapInializedListener(() -> configureMap());


        //new Controllers.MapController(map(), this);


    }

    private void markerClicked(String address) {

        //System.out.println(mapMarker + " is clicked");
        //System.out.println(cP.getAddress());
        //String address = marker.getTitle();

        custAddress.setText(address);
        //Utilities.Customer cust = new Utilities.Customer()
        List<String> o = getCustInfo("Set", "ORDERED", address);
        // m.Orders.setText(o.get(o.size() - 1).toString());
        List<String> NI = getCustInfo("Set", "NI", address);
        //m.Orders.setText(NI.get(NI.size() - 1).toString());
        List<String> NH = getCustInfo("Set", "NH", address);
        custOrders.getChildren().removeAll();
        if (o.get(o.size() - 1).equals("True")) {
            // m.OrderStat.setText("Has Ordered");
                                        /*Get info about customer that has clicked
                                        Display name Phone  Utilities.Order status
                                        Creates a button for each ordered year to view more information
                                        */
            Iterable<String> yearsD;
            yearsD = DbInt.getYears();
            List<String> Name = new ArrayList<>();
            List<String> Phone = new ArrayList<>();

            yearsD.forEach(year -> {


                List<String> NameD = getCustInfo(year, "NAME", address);
                if (!NameD.isEmpty()) {
                    Name.add(NameD.get(NameD.size() - 1));
                    List<String> PhoneD = getCustInfo(year, "PHONE", address);
                    Phone.add(PhoneD.get(PhoneD.size() - 1));
/*                                                if (m.infoPanel.getComponentCount() > 8) {
                                                    m.infoPanel.remove(m.infoPanel.getComponentCount() - 1);

                                                }*/
                    Button b = new Button(year);
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

                        customerCont.initCustomer(year, NameD.get(NameD.size() - 1), mainCont);
                        tabTitle = ("Customer View - " + NameD.get(NameD.size() - 1) + " - " + year);
                        mainCont.addTab(newPane, tabTitle);
                    });
                    custOrders.getChildren().add(b);
                }
            });
            custName.setText(Name.get(Name.size() - 1));
            custPhone.setText(Phone.get(Phone.size() - 1));
        }
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

        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT * FROM customers");
             ResultSet rs = prep.executeQuery()
        ) {


            while (rs.next()) {

                ret.add(rs.getString(info));

            }
            ////Utilities.Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

    private List<String> getCustInfo(String Db, String info, String Address) {
        List<String> ret = new ArrayList<>();


        try (PreparedStatement prep = DbInt.getPrep(Db, "SELECT * FROM customers WHERE ADDRESS=?")) {


            prep.setString(1, Address);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret.add(rs.getString(info));

                }
            }
            ////Utilities.Utilities.DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }


}