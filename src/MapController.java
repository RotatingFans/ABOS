import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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


    MapController(JMapViewer map, Map m) {
        this.map = map;
        this.m = m;
        if (this instanceof MouseListener) {
            map.addMouseListener(this);
        }
        if (this instanceof MouseWheelListener) {
            map.addMouseWheelListener((MouseWheelListener) this);
        }
        if (this instanceof MouseMotionListener) {
            map.addMouseMotionListener((MouseMotionListener) this);
        }
    }

    private static ArrayList<String> getCustInfo(String Db, String info, String Address) {
        ArrayList<String> ret = new ArrayList<>();

        PreparedStatement prep = DbInt.getPrep(Db, "SELECT * FROM Customers WHERE ADDRESS=?");
        try {


            prep.setString(1, Address);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret.add(rs.getString(info));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //determine if a dot was clicked
        boolean doubleClickZoomEnabled = true;
        if ((e.getClickCount() == 1) && (e.getButton() == MouseEvent.BUTTON1)) {

            Point p = e.getPoint();
            int X = p.x + 3;
            int Y = p.y + 3;
            List<MapMarker> ar = map.getMapMarkerList();
            for (MapMarker mapMarker : ar) {

                Point MarkerPosition = map.getMapPosition(mapMarker.getLat(), mapMarker.getLon());
                if (MarkerPosition != null) {

                    int centerX = MarkerPosition.x;
                    int centerY = MarkerPosition.y;

                    // calculate the radius from the touch to the center of the dot
                    double radCircle = Math.sqrt(((centerX - X) * (centerX - X)) + ((centerY - Y) * (centerY - Y)));

                    if (radCircle < 8) {

                        Object[] cPoints = m.cPoints;
                        for (Object cPoint : cPoints) {
                            cPoint cP = (cPoint) cPoint;
                            if (cP.getLat() == mapMarker.getLat()) {
                                if (cP.getLon() == mapMarker.getLon()) {
                                    System.out.println(mapMarker + " is clicked");
                                    System.out.println(cP.getAddress());
                                    m.Address.setText(cP.getAddress());

                                    ArrayList<String> o = getCustInfo("Set", "ORDERED", cP.getAddress());
                                    // m.Orders.setText(o.get(o.size() - 1).toString());
                                    ArrayList<String> NI = getCustInfo("Set", "NI", cP.getAddress());
                                    //m.Orders.setText(NI.get(NI.size() - 1).toString());
                                    ArrayList<String> NH = getCustInfo("Set", "NH", cP.getAddress());

                                    if ("True".equals(o.get(o.size() - 1))) {
                                        m.OrderStat.setText("Has Ordered");
                                        /*Get info about customer that has clicked
                                        Display name Phone  Order status
                                        Creates a button for each ordered year to view more information
                                        */
                                        ArrayList<String> yearsD = DbInt.getData("Set", "SELECT YEARS From YEARS");
                                        ArrayList<String> Name = new ArrayList<>();
                                        ArrayList<String> Phone = new ArrayList<>();
                                        ArrayList<String> Years = new ArrayList<>();
                                        for (int i1 = 0; i1 < yearsD.size(); i1++) {
                                            ArrayList<String> NameD = getCustInfo(yearsD.get(i1), "NAME", cP.getAddress());
                                            if (!NameD.isEmpty()) {
                                                Name.add(NameD.get(NameD.size() - 1));
                                                ArrayList<String> PhoneD = getCustInfo(yearsD.get(i1), "PHONE", cP.getAddress());
                                                Phone.add(PhoneD.get(PhoneD.size() - 1));
                                                Years.add(yearsD.get(i1));
                                                if (m.infoPanel.getComponentCount() > 8) {
                                                    m.infoPanel.remove(m.infoPanel.getComponentCount() - 1);

                                                }
                                                JButton b = new JButton(yearsD.get(i1));
                                                int finalI = i1;
                                                b.addActionListener(e1 -> new CustomerReport(NameD.get(NameD.size() - 1), yearsD.get(finalI)));
                                                m.infoPanel.add(b);
                                            }
                                        }
                                        m.name.setText(Name.get(Name.size() - 1));
                                        m.Phone.setText(Phone.get(Phone.size() - 1));
                                    }
                                    if ("True".equals(NI.get(NI.size() - 1))) {
                                        m.OrderStat.setText("Not interested");
                                    }
                                    if ("True".equals(NH.get(NH.size() - 1))) {
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

}
