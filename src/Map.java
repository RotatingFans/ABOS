import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent.COMMAND;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource.Mapnik;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class Map extends JFrame implements JMapViewerEventListener {
    // --Commented out by Inspection (12/31/15 1:42 PM):private static final long serialVersionUID = 1L;
    public final JLabel Address = new JLabel("");
    public final JLabel OrderStat = new JLabel("");
    public final JLabel name = new JLabel("");
    public final JLabel Phone = new JLabel("");
    public final Object[] cPoints;
    public final JPanel infoPanel = new JPanel();
    private final JLabel Orders = new JLabel("");
    //TODO FIx zoom on map markers and colors
    private final JMapViewerTree treeMap;
    private final JLabel zoomLabel;
    private final JLabel zoomValue;
    private final JLabel mperpLabelName;
    private final JLabel mperpLabelValue;
    // --Commented out by Inspection (12/31/15 1:42 PM):private JFrame frame;

    /**
     * Launch the application.
     */

	/*public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Map window = new Map();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/
    Map() {
        super("JMapViewer Map");

        setSize(600, 400);
        treeMap = new JMapViewerTree("Zones");
        map().addJMVListener(this);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(3);
        setExtendedState(6);
        JPanel panel = new JPanel();
        JPanel panelTop = new JPanel();
        JPanel panelBottom = new JPanel();
        JPanel helpPanel = new JPanel();


        mperpLabelName = new JLabel("Meters/Pixels: ");
        mperpLabelValue = new JLabel(String.format("%s", new Object[]{Double.valueOf(map().getMeterPerPixel())}));
        zoomLabel = new JLabel("Zoom: ");
        zoomValue = new JLabel(String.format("%s", new Object[]{Integer.valueOf(map().getZoom())}));
        add(panel, "North");
        add(helpPanel, "South");
        panel.setLayout(new BorderLayout());
        panel.add(panelTop, "North");
        panel.add(panelBottom, "South");
        JLabel helpLabel = new JLabel("Use right mouse button to move,\n left double click or mouse wheel to zoom.");
        helpPanel.add(helpLabel);
        JLabel AddressL = new JLabel("Address:");
        JLabel OrderStatL = new JLabel("Order Status:");
        JLabel nameL = new JLabel("Name:");
        JLabel PhoneL = new JLabel("Phone:");
        JLabel OrdersL = new JLabel("Orders:");


        infoPanel.add(AddressL);
        infoPanel.add(Address);
        Address.setBorder(new EmptyBorder(0, 0, 15, 0));
        infoPanel.add(OrderStatL);
        infoPanel.add(OrderStat);
        OrderStat.setBorder(new EmptyBorder(0, 0, 15, 0));
        infoPanel.add(nameL);
        infoPanel.add(name);
        name.setBorder(new EmptyBorder(0, 0, 15, 0));
        infoPanel.add(PhoneL);
        infoPanel.add(Phone);
        Phone.setBorder(new EmptyBorder(0, 0, 15, 0));
        infoPanel.add(OrdersL);
        infoPanel.add(Orders);
        Orders.setBorder(new EmptyBorder(0, 0, 15, 0));


        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        //infoPanel.setLayout(new GridLayout(10, 1, 3, 50));
        //infoPanel.setLayout(new FlowLayout());
        add(infoPanel, "East");

        map().setTileSource(new Mapnik());
        map().setTileLoader(new OsmTileLoader(map()));


        map().setMapMarkerVisible(true);


        panelTop.add(zoomLabel);
        panelTop.add(zoomValue);
        panelTop.add(mperpLabelName);
        panelTop.add(mperpLabelValue);
        add(treeMap, "Center");

        //Add customers to map
        ArrayList<String> Addr = getCustInfo("ADDRESS");
        ArrayList<String> Ord = getCustInfo("Ordered");
        ArrayList<String> NI = getCustInfo("NI");
        ArrayList<String> NH = getCustInfo("NH");
        cPoints = new Object[Addr.size()];
        for (int i = 0; i < Addr.size(); i++) {
            try {
                Object[][] coords = GetCoords(Addr.get(i));
                double lat = Double.valueOf(coords[0][0].toString());
                double lon = Double.valueOf(coords[0][1].toString());
                MapMarkerDot m = new MapMarkerDot(lat, lon);
                cPoints[i] = new cPoint(lat, lon, Addr.get(i));
                //Determine color of dot
                //Green = orderd
                //Cyan = Not Interested
                //Magenta = not home
                if ("True".equals(Ord.get(Ord.size() - 1))) {
                    m.setBackColor(Color.GREEN);
                }
                if ("True".equals(NI.get(NI.size() - 1))) {
                    m.setBackColor(Color.CYAN);
                }
                if ("True".equals(NH.get(NH.size() - 1))) {
                    m.setBackColor(Color.MAGENTA);
                }
                map().addMapMarker(m);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        }
        new MapController(map(), this);
        map().setDisplayToFitMapMarkers();

    }

// --Commented out by Inspection START (12/31/15 1:42 PM):
//    private static Coordinate c(double lat, double lon) {
//        return new Coordinate(lat, lon);
//    }
// --Commented out by Inspection STOP (12/31/15 1:42 PM)

    public static void main(String[] args) {
        Map window = new Map();
        window.setVisible(true);

    }

// --Commented out by Inspection START (12/31/15 1:42 PM):
//    /**
//     * Create the application.
//     */
///*	public Map() {
//        initialize();
//		try {
////			Object[][] coords = GetCoords("***REMOVED***");
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
// --Commented out by Inspection STOP (12/31/15 1:42 PM)

    /**
     * Getsx the requested info on customers
     *
     * @param info Info to retrieve
     * @return The info requested in ArrayList form
     */
    private static ArrayList<String> getCustInfo(String info) {
        ArrayList<String> ret = new ArrayList<>();

        PreparedStatement prep = DbInt.getPrep("Set", "SELECT * FROM Customers");
        try {

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

    /**
     * Gets coords of an address
     *
     * @param Address Address to get coords of
     * @return Object[][] that holds the houses coordinates
     * @throws IOException
     */
    private static Object[][] GetCoords(String Address) throws IOException {
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

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return parseCoords(response.toString());

    }

    /**
     * Gets coords of an address
     *
     * @param xml THe XML to parse
     * @return Object[][] that holds the houses coordinates
     */
    private static Object[][] parseCoords(String xml) {
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


                if (nNode.getNodeType() == Node.ELEMENT_NODE) {


                    coords[0][0] = ((Element) nNode).getAttributeNode("lat").getValue();
                    coords[0][1] = ((Element) nNode).getAttributeNode("lon").getValue();


                    //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};


                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return coords;
    }

    private JMapViewer map() {
        return treeMap.getViewer();
    }

    private void updateZoomParameters() {
        if (mperpLabelValue != null) {
            mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
        }

        if (zoomValue != null) {
            zoomValue.setText(String.format("%s", map().getZoom()));
        }

    }

    @Override
    public void processCommand(JMVCommandEvent command) {
        if ((command.getCommand() == COMMAND.ZOOM) || (command.getCommand() == COMMAND.MOVE)) {
            updateZoomParameters();
        }

    }
    /*
      Initialize the contents of the frame.
     */
/*	private void initialize() {
        frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}*/

}
