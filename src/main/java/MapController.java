/*
 * Copyright (c) Patrick Magauran 2017.
 * Licensed under the AGPLv3. All conditions of said license apply.
 *     This file is part of LawnAndGarden.
 *
 *     LawnAndGarden is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     LawnAndGarden is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with LawnAndGarden.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract base class for all mouse controller implementations. For
 * implementing your own controller create a class that derives from this one
 * and implements one or more of the following interfaces:
 * <ul>
 * <li>{@link MouseListener}</li>
 * <li>{@link MouseMotionListener}</li>
 * <li>{@link MouseWheelListener}</li>
 * </ul>
 *
 * @author Jan Peter Stotz
 */
class MapController extends MouseAdapter {

    private final JMapViewer map;
    private final Map m;
    // --Commented out by Inspection (1/2/2016 12:01 PM):private Point lastDragPoint;

    // --Commented out by Inspection (1/2/2016 12:01 PM):private boolean isMoving = false;

    // --Commented out by Inspection (1/2/2016 12:01 PM):private boolean movementEnabled = true;

    // --Commented out by Inspection (1/2/2016 12:01 PM):private int movementMouseButton = MouseEvent.BUTTON3;
    // --Commented out by Inspection (1/2/2016 12:01 PM):private int movementMouseButtonMask = MouseEvent.BUTTON3_DOWN_MASK;

    // --Commented out by Inspection (1/2/2016 12:01 PM):private boolean wheelZoomEnabled = true;

    public MapController(JMapViewer map, Map m) {
        this.map = map;
        this.m = m;
        if (this instanceof MouseListener) {
            map.addMouseListener(this);
        }
        if (this instanceof MouseWheelListener) {
            map.addMouseWheelListener(this);
        }
        if (this instanceof MouseMotionListener) {
            map.addMouseMotionListener(this);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //determine if a dot was clicked
        boolean doubleClickZoomEnabled = true;
        if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)) {

            Point p = e.getPoint();
            int X = p.x + 3;
            int Y = p.y + 3;
            java.util.List<MapMarker> ar = map.getMapMarkerList();
            for (MapMarker mapMarker : ar) {

                Point MarkerPosition = map.getMapPosition(mapMarker.getLat(), mapMarker.getLon());
                if (MarkerPosition != null) {

                    int centerX = MarkerPosition.x;
                    int centerY = MarkerPosition.y;

                    // calculate the radius from the touch to the center of the dot
                    double radCircle = Math.sqrt((double) (((centerX - X) * (centerX - X)) + ((centerY - Y) * (centerY - Y))));

                    if (radCircle < 10.0) {

                        Object[] cPoints = m.cPoints;
                        for (Object cPoint : cPoints) {
                            cPoint cP = (cPoint) cPoint;
                            if (cP.getLat() == mapMarker.getLat()) {
                                if (cP.getLon() == mapMarker.getLon()) {
                                    //System.out.println(mapMarker + " is clicked");
                                    //System.out.println(cP.getAddress());
                                    m.Address.setText(String.format("%s %s, %s", cP.getAddress(), cP.getCity(), cP.getState()));

                                    List<String> o = getCustInfo("Set", "ORDERED", cP.getAddress());
                                    // m.Orders.setText(o.get(o.size() - 1).toString());
                                    List<String> NI = getCustInfo("Set", "NI", cP.getAddress());
                                    //m.Orders.setText(NI.get(NI.size() - 1).toString());
                                    List<String> NH = getCustInfo("Set", "NH", cP.getAddress());
                                    m.buttonPanel.removeAll();
                                    if (o.get(o.size() - 1).equals("True")) {
                                        m.OrderStat.setText("Has Ordered");
                                        /*Get info about customer that has clicked
                                        Display name Phone  Order status
                                        Creates a button for each ordered year to view more information
                                        */
                                        Collection<String> yearsD = new ArrayList<>();
                                        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT Years.YEARS FROM Years");
                                             ResultSet rs = prep.executeQuery()
                                        ) {
                                            while (rs.next()) {

                                                yearsD.add(rs.getString(1));

                                            }

                                            rs.close();
                                            if (DbInt.pCon != null) {
                                                DbInt.pCon = null;
                                            }
                                        } catch (SQLException Se) {
                                            LogToFile.log(Se, Severity.SEVERE, CommonErrors.returnSqlMessage(Se));
                                        }
                                        List<String> Name = new ArrayList<>();
                                        List<String> Phone = new ArrayList<>();

                                        yearsD.forEach(year -> {


                                            List<String> NameD = getCustInfo(year, "NAME", cP.getAddress());
                                            if (!NameD.isEmpty()) {
                                                Name.add(NameD.get(NameD.size() - 1));
                                                List<String> PhoneD = getCustInfo(year, "PHONE", cP.getAddress());
                                                Phone.add(PhoneD.get(PhoneD.size() - 1));
/*                                                if (m.infoPanel.getComponentCount() > 8) {
                                                    m.infoPanel.remove(m.infoPanel.getComponentCount() - 1);

                                                }*/
                                                JButton b = new JButton(year);
                                                b.addActionListener(e1 -> new CustomerReport(NameD.get(NameD.size() - 1), year));
                                                m.buttonPanel.add(b);
                                            }
                                        });
                                        m.name.setText(Name.get(Name.size() - 1));
                                        m.Phone.setText(Phone.get(Phone.size() - 1));
                                    }
                                    if (NI.get(NI.size() - 1).equals("True")) {
                                        m.OrderStat.setText("Not interested");
                                    }
                                    if (NH.get(NH.size() - 1).equals("True")) {
                                        m.OrderStat.setText("Not home");
                                    }

                                }
                            }
                        }
                    }

                }
            }
        } else if (doubleClickZoomEnabled && (e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
            map.zoomIn(e.getPoint());
        }
    }

    private List<String> getCustInfo(String Db, String info, String Address) {
        List<String> ret = new ArrayList<>();


        try (PreparedStatement prep = DbInt.getPrep(Db, "SELECT * FROM Customers WHERE ADDRESS=?")) {


            prep.setString(1, Address);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret.add(rs.getString(info));

                }
            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }

}
