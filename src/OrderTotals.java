import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class OrderTotals {

    public String year = Year.year;
    private JFrame frame;
    private JTable table;
    private double QuantL = 0;
    private double totL = 0;
    private String name;

    /**
     * Create the application.
     */
    public OrderTotals() {
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    OrderTotals window = new OrderTotals();
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
        frame.setBounds(100, 100, 762, 620);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(219, 67, 527, 514);
        frame.getContentPane().add(scrollPane);
        fillTable();
        scrollPane.setViewportView(table);

        JLabel label = new JLabel("Donations");
        label.setBounds(10, 50, 97, 14);
        frame.getContentPane().add(label);

        JLabel DonationsL = new JLabel(getDonations());
        DonationsL.setBounds(10, 75, 65, 14);
        frame.getContentPane().add(DonationsL);

        JLabel label_2 = new JLabel("Lawn and Garden Prducts");
        label_2.setBounds(10, 100, 142, 14);
        frame.getContentPane().add(label_2);

        JLabel LGL = new JLabel(getLG());
        LGL.setBounds(10, 125, 65, 14);
        frame.getContentPane().add(LGL);

        JLabel label_4 = new JLabel("Live Plant Products");
        label_4.setBounds(10, 150, 113, 14);
        frame.getContentPane().add(label_4);

        JLabel LPL = new JLabel(getLP());
        LPL.setBounds(10, 171, 65, 14);
        frame.getContentPane().add(LPL);

        JLabel label_6 = new JLabel("Muclh");
        label_6.setBounds(10, 196, 113, 14);
        frame.getContentPane().add(label_6);

        JLabel MulchL = new JLabel(getMulch());
        MulchL.setBounds(10, 221, 65, 14);
        frame.getContentPane().add(MulchL);

        JLabel label_8 = new JLabel("ORder Total");
        label_8.setBounds(10, 246, 113, 14);
        frame.getContentPane().add(label_8);

        JLabel OTL = new JLabel(getOT());
        OTL.setBounds(10, 271, 65, 14);
        frame.getContentPane().add(OTL);

        JLabel label_10 = new JLabel("Customers");
        label_10.setBounds(10, 306, 142, 14);
        frame.getContentPane().add(label_10);

        JLabel CustomerL = new JLabel(getCustomers());
        CustomerL.setBounds(10, 331, 65, 14);
        frame.getContentPane().add(CustomerL);

        JLabel label_12 = new JLabel("Commision ");
        label_12.setBounds(10, 366, 83, 14);
        frame.getContentPane().add(label_12);

        JLabel CommisL = new JLabel(getCommis());
        CommisL.setBounds(10, 391, 65, 14);
        frame.getContentPane().add(CommisL);

        JButton button = new JButton("Refresh");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initialize();
            }
        });
        button.setBounds(608, 21, 89, 23);
        frame.getContentPane().add(button);
        this.frame.setVisible(true);
    }

    private String getTots(String info) {
        String ret = "";

        PreparedStatement prep = DbInt.getPrep(year, "SELECT TOTALS.? FROM TOTALS");
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
                        double UnitD = Double.parseDouble(Unit);
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
                        ArrayList<String> UnitL = getOrders("UNIT", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        String Unit = UnitL.get(productL.size() - 1);
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit);
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
