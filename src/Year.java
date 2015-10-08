import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Year extends JDialog {

    public static String year = "2015";
    private JFrame frame;
    private JTable table;
    private double QuantL = 0;
    private double totL = 0;

    /**
     * Create the application.
     *
     * @param Years
     */
    public Year(String Years) {
        year = Years;
        System.out.print(year);
        initialize();
        this.frame.setVisible(true);


    }

    /**
     * Launch the application.
     */
    public static void main(final String Years, String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    year = Years;
                    Year window = new Year(Years);
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 750, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());


        //West
        {
            Font l = new Font("Tahoma", Font.PLAIN, 24);
            JPanel East = new JPanel();
            East.setLayout(new BoxLayout(East, BoxLayout.PAGE_AXIS));


            JLabel lblNewLabel = new JLabel("Donations");
            lblNewLabel.setFont(l);
            //lblNewLabel.setBounds(16, 11, 97, 14);
            East.add(lblNewLabel);

            JLabel DonationsR = new JLabel(getDonations());
            DonationsR.setFont(l);
            //DonationsR.setBounds(193, 11, 65, 14);
            East.add(DonationsR);

            JLabel lblLawnAndGarden = new JLabel("Lawn and Garden Products");
            lblLawnAndGarden.setFont(l);
            //lblLawnAndGarden.setBounds(16, 36, 142, 14);
            East.add(lblLawnAndGarden);

            JLabel LGR = new JLabel(getLG());
            LGR.setFont(l);
            //LGR.setBounds(193, 36, 65, 14);
            East.add(LGR);

            JLabel lblLivePlantProducts = new JLabel("Live Plant Products");
            lblLivePlantProducts.setFont(l);
            //lblLivePlantProducts.setBounds(16, 61, 113, 14);
            East.add(lblLivePlantProducts);

            JLabel LPR = new JLabel(getLP());
            LPR.setFont(l);
            //LPR.setBounds(193, 61, 65, 14);
            East.add(LPR);

            JLabel lblF = new JLabel("Mulch");
            lblF.setFont(l);
            //	lblF.setBounds(16, 86, 113, 14);
            East.add(lblF);

            JLabel MulchR = new JLabel(getMulch());
            MulchR.setFont(l);
            //MulchR.setBounds(193, 86, 65, 14);
            East.add(MulchR);

            JLabel lblCustomers = new JLabel("Order Total");
            lblCustomers.setFont(l);
            //lblCustomers.setBounds(16, 111, 113, 14);
            East.add(lblCustomers);

            JLabel OR = new JLabel(getOT());
            OR.setFont(l);
            //OR.setBounds(193, 111, 65, 14);
            East.add(OR);

            JLabel lblCustomers_1 = new JLabel("Customers");
            lblCustomers_1.setFont(l);
            //lblCustomers_1.setBounds(16, 136, 142, 14);
            East.add(lblCustomers_1);

            JLabel CustomersR = new JLabel(getCustomers());
            CustomersR.setFont(l);
            //CustomersR.setBounds(193, 136, 65, 14);
            East.add(CustomersR);

            JLabel lblCommision = new JLabel("Commission ");
            lblCommision.setFont(l);
            //lblCommision.setBounds(16, 160, 83, 14);
            East.add(lblCommision);

            JLabel CommissionR = new JLabel(getCommis());
            //CommissionR.setBounds(193, 161, 65, 14);
            CommissionR.setFont(l);
            East.add(CommissionR);
            frame.getContentPane().add(East, BorderLayout.WEST);
        }
        //East
        {

//			JPanel North = new JPanel(new BorderLayout());
//			JLabel YearR = new JLabel(year);
//			YearR.setFont(new Font("Tahoma", Font.PLAIN, 48));
//			//YearR.setBounds(318, 0, 420, 139);
//			North.add(YearR);
//			frame.getContentPane().add(North, BorderLayout.EAST);
            frame.setTitle(year);
        }
        //CENTER
        {
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(219, 67, 527, 514);
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
            fillTable();
            scrollPane.setViewportView(table);
        }

        {

            JPanel South = new JPanel(new GridLayout(1, 3));
            South.setSize(frame.getSize().width, frame.getSize().height / 3);
            JButton btnNewButton_1 = new JButton("Customers");
            //btnNewButton_1.setBounds(276, 232, 212, 247);
            btnNewButton_1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    new CustomerView();
                }
            });
            South.add(btnNewButton_1);

//			JButton btnNewButton_2 = new JButton("Total Orders");
//			btnNewButton_2.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					new OrderTotals();
//				}
//			});
//			//btnNewButton_2.setBounds(525, 232, 212, 247);
//			South.add(btnNewButton_2);
            JButton btnRefresh = new JButton("Refresh");
            btnRefresh.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(false);
                    new Year(year).setVisible(true);
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

                }
            });
            //btnRefresh.setBounds(628, 157, 89, 23);
            South.add(btnRefresh);
            frame.getContentPane().add(South, BorderLayout.SOUTH);
        }


    }

    private String getTots(String info) {
        String ret = "";

        PreparedStatement prep = DbInt.getPrep(year, "SELECT ? FROM TOTALS");
        try {

            prep.setString(1, info);

            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret = rs.getString(1);

            }
            DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private String getDonations() {
        return getTots("Donations");

    }

    private String getLG() {
        return getTots("LG");
    }

    private String getLP() {
        return getTots("LP");
    }

    private String getMulch() {
        return getTots("MULCH");
    }

    private String getOT() {
        return getTots("TOTAL");
    }

    private String getCustomers() {
        return getTots("CUSTOMERS");
    }

    private String getCommis() {
        return getTots("COMMISSIONS");
    }

    private void fillTable() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }
        Connection con = null;
        Statement st = null;
        ResultSet Order = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", new Config().getDbLoc(), year);
        System.setProperty("derby.system.home",
                new Config().getDbLoc());
        ArrayList<String> res = new ArrayList<String>();
        DefaultTableModel model;
        int colO = DbInt.getNoCol(year, "ORDERS") - 2;
        final Object[][] rowData = new Object[colO][4];
        final Object[] columnNames = {"Item", "Price/Item", "Quantity", "Price"};

        int noVRows = 0;
        int n = 0;
        try {


            con = DriverManager.getConnection(url);
            st = con.createStatement();
            Order = st.executeQuery("SELECT * FROM ORDERS");


            ResultSetMetaData rsmd = Order.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            while (Order.next()) {
                if (n == 0) {
                    for (int c = 3; c <= columnsNumber; c++) {

                        ArrayList<String> productL = getOrders("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        String product = productL.get(productL.size() - 1);
                        ArrayList<String> UnitL = getOrders("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        String Unit = UnitL.get(productL.size() - 1);
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);

                        double TPrice = UnitD * quantityD;
                        totL = totL + TPrice;
                        QuantL = QuantL + quantityD;

                        rowData[noVRows][0] = product;
                        rowData[noVRows][1] = Unit;
                        rowData[noVRows][2] = quantity;
                        rowData[noVRows][3] = TPrice;
                        noVRows = noVRows + 1;

                    }
                    n = n + 1;
                } else {
                    noVRows = 0;
                    for (int c = 3; c <= columnsNumber; c++) {

                        ArrayList<String> productL = getOrders("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        String product = productL.get(productL.size() - 1);
                        ArrayList<String> UnitL = getOrders("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        String Unit = UnitL.get(productL.size() - 1);
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);

                        double TPrice = UnitD * quantityD;
                        totL = totL + TPrice;
                        QuantL = QuantL + quantityD;


                        rowData[noVRows][2] = Double.parseDouble(rowData[noVRows][2].toString()) + quantityD;
                        rowData[noVRows][3] = Double.parseDouble(rowData[noVRows][3].toString()) + TPrice;
                        noVRows = noVRows + 1;

                    }
                }
            }
            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        } finally {

            try {
                if (Order != null) {
                    Order.close();
                    Order = null;
                }
                if (st != null) {
                    st.close();
                    st = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DbInt.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }


        final Object[][] rowDataF = new Object[noVRows][4];
        for (int i = 0; i <= noVRows - 1; i++) {
            rowDataF[i][0] = rowData[i][0];
            rowDataF[i][1] = rowData[i][1];
            rowDataF[i][2] = rowData[i][2];
            rowDataF[i][3] = rowData[i][3];
        }
        table = new JTable();
        table.setLocation(172, 44);
        table.setSize(548, 490);
        table.setFillsViewportHeight(true);
        table.setModel(new DefaultTableModel(rowDataF, columnNames));
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
    }

    private ArrayList<String> getOrders(String info, String PID) {
        ArrayList<String> ret = new ArrayList<String>();

        PreparedStatement prep = DbInt.getPrep(year, "SELECT ? FROM PRODUCTS WHERE PID=?");
        try {

            prep.setString(1, info);
            prep.setString(2, PID);

            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret.add(rs.getString(1));

            }
            DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
}

