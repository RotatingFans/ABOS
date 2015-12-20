import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

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
public class MapController implements MouseListener {

    protected JMapViewer map;
    protected Map m;
    private Point lastDragPoint;

    private boolean isMoving = false;

    private boolean movementEnabled = true;

    private int movementMouseButton = MouseEvent.BUTTON3;
    private int movementMouseButtonMask = MouseEvent.BUTTON3_DOWN_MASK;

    private boolean wheelZoomEnabled = true;
    private boolean doubleClickZoomEnabled = true;

    public MapController(JMapViewer map, Map m) {
        this.map = map;
        this.m = m;
        if (this instanceof MouseListener)
            map.addMouseListener(this);
        if (this instanceof MouseWheelListener)
            map.addMouseWheelListener((MouseWheelListener) this);
        if (this instanceof MouseMotionListener)
            map.addMouseMotionListener((MouseMotionListener) this);
    }

    public void mouseClicked(MouseEvent e) {
        //determine if a dot was clicked
        if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {

            Point p = e.getPoint();
            int X = p.x + 3;
            int Y = p.y + 3;
            java.util.List<MapMarker> ar = map.getMapMarkerList();
            Iterator<MapMarker> i = ar.iterator();
            while (i.hasNext()) {

                MapMarker mapMarker = i.next();

                Point MarkerPosition = map.getMapPosition(mapMarker.getLat(), mapMarker.getLon());
                if (MarkerPosition != null) {

                    int centerX = MarkerPosition.x;
                    int centerY = MarkerPosition.y;

                    // calculate the radius from the touch to the center of the dot
                    double radCircle = Math.sqrt((((centerX - X) * (centerX - X)) + (centerY - Y) * (centerY - Y)));

                    if (radCircle < 8) {

                        Object[] cPoints = m.cPoints;
                        for (int c = 0; c < cPoints.length; c++) {
                            cPoint cP = (cPoint) cPoints[c];
                            if (cP.getLat() == mapMarker.getLat()) {
                                if (cP.getLon() == mapMarker.getLon()) {
                                    System.out.println(mapMarker.toString() + " is clicked");
                                    System.out.println(cP.getAddress());
                                    m.Address.setText(cP.getAddress());

                                    ArrayList<String> o = getCustInfo("Set", "ORDERED", "ADDRESS", cP.getAddress());
                                    // m.Orders.setText(o.get(o.size() - 1).toString());
                                    ArrayList<String> NI = getCustInfo("Set", "NI", "ADDRESS", cP.getAddress());
                                    //m.Orders.setText(NI.get(NI.size() - 1).toString());
                                    ArrayList<String> NH = getCustInfo("Set", "NH", "ADDRESS", cP.getAddress());

                                    if (o.get(o.size() - 1).toString().equals("True")) {
                                        m.OrderStat.setText("Has Ordered");
                                        /*Get info about customer that has clicked
                                        Display name Phone  Order status
                                        Creates a button for each ordered year to view more information
                                        */
                                        final ArrayList<String> yearsD = DbInt.getData("Set", "SELECT YEARS From YEARS");
                                        ArrayList<String> Name = new ArrayList<String>();
                                        ArrayList<String> Phone = new ArrayList<String>();
                                        ArrayList<String> Years = new ArrayList<String>();
                                        for (int i1 = 0; i1 < yearsD.size(); i1++) {
                                            final ArrayList<String> NameD = getCustInfo(yearsD.get(i1), "NAME", "ADDRESS", cP.getAddress());
                                            if (NameD.size() > 0) {
                                                Name.add(NameD.get(NameD.size() - 1));
                                                ArrayList<String> PhoneD = getCustInfo(yearsD.get(i1), "PHONE", "ADDRESS", cP.getAddress());
                                                Phone.add(PhoneD.get(PhoneD.size() - 1));
                                                Years.add(yearsD.get(i1));
                                                if (m.infoPanel.getComponentCount() > 8) {
                                                    m.infoPanel.remove(m.infoPanel.getComponentCount() - 1);

                                                }
                                                JButton b = new JButton(yearsD.get(i1));
                                                final int finalI = i1;
                                                b.addActionListener(new ActionListener() {
                                                    public void actionPerformed(ActionEvent e) {
                                                        new CustomerReport(NameD.get(NameD.size() - 1), yearsD.get(finalI));
                                                    }
                                                });
                                                m.infoPanel.add(b);
                                            }
                                        }
                                        m.name.setText(Name.get(Name.size() - 1));
                                        m.Phone.setText(Phone.get(Phone.size() - 1));
                                    }
                                    if (NI.get(NI.size() - 1).toString().equals("True")) {
                                        m.OrderStat.setText("Not interested");
                                    }
                                    if (NH.get(NH.size() - 1).toString().equals("True")) {
                                        m.OrderStat.setText("Not home");
                                    }

                                }
                            }
                        }
                    }

                }
            }
        } else if (doubleClickZoomEnabled && e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
            map.zoomIn(e.getPoint());
        }
    }

    private ArrayList<String> getCustInfo(String Db, String info, String where, String Address) {
        ArrayList<String> ret = new ArrayList<String>();

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
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
}
