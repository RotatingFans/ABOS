import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Map extends JFrame implements JMapViewerEventListener {
    private static final long serialVersionUID = 1L;
    private final String USER_AGENT = "Mozilla/5.0";
    public JLabel Address = new JLabel("");
    public JLabel OrderStat = new JLabel("");
    public JLabel name = new JLabel("");
    public JLabel Phone = new JLabel("");
    public JLabel Orders = new JLabel("");
    public Object[] cPoints;
    public JPanel infoPanel = new JPanel();
    //TODO FIx zoom on map markers and colors
    private JMapViewerTree treeMap = null;
    private JLabel zoomLabel = null;
    private JLabel zoomValue = null;
    private JLabel mperpLabelName = null;
    private JLabel mperpLabelValue = null;
    private JFrame frame;

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
    public Map() {
        super("JMapViewer Map");

        this.setSize(600, 400);
        this.treeMap = new JMapViewerTree("Zones");
        this.map().addJMVListener(this);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(3);
        this.setExtendedState(6);
        JPanel panel = new JPanel();
        JPanel panelTop = new JPanel();
        JPanel panelBottom = new JPanel();
        JPanel helpPanel = new JPanel();


        this.mperpLabelName = new JLabel("Meters/Pixels: ");
        this.mperpLabelValue = new JLabel(String.format("%s", new Object[]{Double.valueOf(this.map().getMeterPerPixel())}));
        this.zoomLabel = new JLabel("Zoom: ");
        this.zoomValue = new JLabel(String.format("%s", new Object[]{Integer.valueOf(this.map().getZoom())}));
        this.add(panel, "North");
        this.add(helpPanel, "South");
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
        this.add(infoPanel, "East");

        Map.this.map().setTileSource(new OsmTileSource.Mapnik());
        this.map().setTileLoader(new OsmTileLoader(this.map()));


        Map.this.map().setMapMarkerVisible(true);


        panelTop.add(this.zoomLabel);
        panelTop.add(this.zoomValue);
        panelTop.add(this.mperpLabelName);
        panelTop.add(this.mperpLabelValue);
        this.add(this.treeMap, "Center");

        //Add customers to map
        ArrayList<String> Addr = getCustInfo("ADDRESS");
        ArrayList<String> Ord = getCustInfo("Ordered");
        ArrayList<String> NI = getCustInfo("NI");
        ArrayList<String> NH = getCustInfo("NH");
        cPoints = new Object[Addr.size()];
        for (int i = 0; i < Addr.size(); i++) {
            try {
                Object[][] coords = GetCoords(Addr.get(i).toString());
                double lat = Double.valueOf(coords[0][0].toString());
                double lon = Double.valueOf(coords[0][1].toString());
                MapMarkerDot m = new MapMarkerDot(lat, lon);
                cPoints[i] = new cPoint(lat, lon, Addr.get(i).toString());
                //Determine color of dot
                //Green = orderd
                //Cyan = Not Interested
                //Magenta = not home
                if (Ord.get(Ord.size() - 1).toString().equals("True")) {
                    m.setBackColor(Color.GREEN);
                }
                if (NI.get(NI.size() - 1).toString().equals("True")) {
                    m.setBackColor(Color.CYAN);
                }
                if (NH.get(NH.size() - 1).toString().equals("True")) {
                    m.setBackColor(Color.MAGENTA);
                }
                this.map().addMapMarker(m);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        new MapController(this.map(), this);
        Map.this.map().setDisplayToFitMapMarkers();

    }

    private static Coordinate c(double lat, double lon) {
        return new Coordinate(lat, lon);
    }

    public static void main(String[] args) {
        Map window = new Map();
        window.setVisible(true);

    }

    /**
     * Create the application.
     */
/*	public Map() {
        initialize();
		try {
//			Object[][] coords = GetCoords("***REMOVED***");
//			System.out.println(coords[0][0]);
//			System.out.println(coords[0][1]);
		}
			catch(java.lang.Exception e) {
					System.out.println(e.toString());
			}
		}*/
    public Object[] getCPoints() {
        return cPoints;
    }

    /**
     * Getsx the requested info on customers
     *
     * @param info Info to retrieve
     * @return The info requested in ArrayList form
     */
    private ArrayList<String> getCustInfo(String info) {
        ArrayList<String> ret = new ArrayList<String>();

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

    private JMapViewer map() {
        return this.treeMap.getViewer();
    }

    private void updateZoomParameters() {
        if (this.mperpLabelValue != null) {
            this.mperpLabelValue.setText(String.format("%s", Double.valueOf(this.map().getMeterPerPixel())));
        }

        if (this.zoomValue != null) {
            this.zoomValue.setText(String.format("%s", Integer.valueOf(this.map().getZoom())));
        }

    }

    public void processCommand(JMVCommandEvent command) {
        if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) || command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
            this.updateZoomParameters();
        }

    }


    /**Gets coords of an address
     * @param Address Address to get coords of
     * @return Object[][] that holds the houses coordinates
     * @throws Exception
     */
    private Object[][] GetCoords(String Address) throws Exception {
        String AddressF = Address.replace(" ", "+");
        String url = String.format("http://open.mapquestapi.com/nominatim/v1/search.php?format=xml&q=%s&addressdetails=0&limit=1", AddressF);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return parseCoords(response.toString());

    }

    /**Gets coords of an address
     * @param xml THe XML to parse
     * @return Object[][] that holds the houses coordinates
     */
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


                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;


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
    /**
     * Initialize the contents of the frame.
     */
/*	private void initialize() {
        frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}*/

}
