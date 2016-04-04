import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


class Year extends JDialog {

    public static String year = "2015";
    private JFrame frame;
    private JTable table;
    private double QuantL = 0.0;
    private double totL = 0.0;

    /**
     * Create the application.
     *
     * @param Years The year to display
     */
    public Year(String Years) {
        year = Years;
        System.out.print(year);
        initialize();
        frame.setVisible(true);


    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Launch the application.
//     */
//    public static void main(String Years, String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    year = Years;
//                    Year window = new Year(Years);
//                    window.frame.setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 750, 600);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());


        //West
        {
            JPanel East = new JPanel();
            East.setLayout(new BoxLayout(East, BoxLayout.PAGE_AXIS));


            JLabel lblNewLabel = new JLabel("Donations");
            Font l = new Font("Tahoma", Font.PLAIN, 24);
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


            JLabel lblGTot = new JLabel("Grand Total: ");
            lblGTot.setFont(l);
            //lblCommision.setBounds(16, 160, 83, 14);
            East.add(lblGTot);

            JLabel GTotR = new JLabel(getGTot());
            //CommissionR.setBounds(193, 161, 65, 14);
            GTotR.setFont(l);
            East.add(GTotR);
            JLabel lblCommision = new JLabel("Commission ");
            lblCommision.setFont(l);
            //lblCommision.setBounds(16, 160, 83, 14);
            East.add(lblCommision);

            JLabel CommissionR = new JLabel(getCommis());
            //CommissionR.setBounds(193, 161, 65, 14);
            CommissionR.setFont(l);
            East.add(CommissionR);
            JLabel lblCustomers_1 = new JLabel("Customers");
            lblCustomers_1.setFont(l);
            //lblCustomers_1.setBounds(16, 136, 142, 14);
            East.add(lblCustomers_1);

            JLabel CustomersR = new JLabel(getCustomers());
            CustomersR.setFont(l);
            //CustomersR.setBounds(193, 136, 65, 14);
            East.add(CustomersR);
            frame.getContentPane().add(East, BorderLayout.WEST);
        }
        //East
        {


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
            btnNewButton_1.addActionListener(arg0 -> new CustomerView());
            South.add(btnNewButton_1);


            JButton btnRefresh = new JButton("Refresh");
            btnRefresh.addActionListener(e -> {
                frame.setVisible(false);
                new Year(year).setVisible(true);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

            });
            //btnRefresh.setBounds(628, 157, 89, 23);
            South.add(btnRefresh);
            frame.getContentPane().add(South, BorderLayout.SOUTH);
        }


    }

    /**
     * Gets a piece of info from the totals table
     *
     * @param info The info to be pulled
     * @return The Result of the query
     */
    private String getTots(String info) {
        String ret = "";

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM TOTALS");
             ResultSet rs = prep.executeQuery()
        ) {

            //prep.setString(1, info);


            while (rs.next()) {

                ret = rs.getString(info);

            }
            ////DbInt.pCon.close();

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

    private String getGTot() {
        return getTots("GRANDTOTAL");
    }


    /**
     * Fills the Table of order amounts
     */
    private void fillTable() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", Config.getDbLoc(), year);
        System.setProperty("derby.system.home",
                Config.getDbLoc());

        int colO = DbInt.getNoCol(year, "ORDERS") - 2;
        Object[][] rowData = new Object[colO][4];
        Object[] columnNames = {"Item", "Price/Item", "Quantity", "Price"};

        int noRows = 0;

        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet Order = st.executeQuery("SELECT * FROM ORDERS")) {


            ResultSetMetaData rsmd = Order.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            int productsNamed = 0;
            while (Order.next()) {
                if (productsNamed == 0) {
                    //loop through columns
                    for (int c = 3; c <= columnsNumber; c++) {
                        //Get Name of product
                        List<String> productL = GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        String product = productL.get(productL.size() - 1);
                        //Get unit cost of product
                        List<String> UnitL = GetProductInfo("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        String Unit = UnitL.get(productL.size() - 1);
                        //Get Quantity ordered
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);
                        //Calculate total price and overall Total
                        double TPrice = UnitD * quantityD;
                        totL += TPrice;
                        QuantL += quantityD;

                        rowData[noRows][0] = product;
                        rowData[noRows][1] = Unit;
                        rowData[noRows][2] = quantity;
                        rowData[noRows][3] = TPrice;
                        noRows += 1;


                    }
                    productsNamed += 1;
                } else {
                    noRows = 0;
                    for (int c = 3; c <= columnsNumber; c++) {

                        //Get Name of product
                        List<String> productL = GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        //Get unit cost of product
                        java.util.List<String> UnitL = GetProductInfo("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1));
                        String Unit = UnitL.get(productL.size() - 1);
                        //Get Quantity ordered
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);
                        //Calculate total price and overall Total
                        double TPrice = UnitD * quantityD;
                        totL += TPrice;
                        QuantL += quantityD;


                        rowData[noRows][2] = Double.parseDouble(rowData[noRows][2].toString()) + quantityD;
                        rowData[noRows][3] = Double.parseDouble(rowData[noRows][3].toString()) + TPrice;
                        noRows += 1;

                    }
                }
            }
            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(Year.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }

        //Limit array to only rows that have ordered stuff
        Object[][] rowDataExclude0 = new Object[noRows][4];
        int NumNonEmptyRows = 0;
        for (int i = 0; i <= (noRows - 1); i++) {
            if (Double.parseDouble(rowData[i][2].toString()) > 0.0) {
                rowDataExclude0[NumNonEmptyRows][0] = rowData[i][0];
                rowDataExclude0[NumNonEmptyRows][1] = rowData[i][1];
                rowDataExclude0[NumNonEmptyRows][2] = rowData[i][2];
                rowDataExclude0[NumNonEmptyRows][3] = rowData[i][3];
                NumNonEmptyRows++;
            }
        }
        //Only show non whitespace rows
        Object[][] rowDataFinal = new Object[noRows][4];
        for (int i = 0; i <= (NumNonEmptyRows - 1); i++) {

            rowDataFinal[i][0] = rowDataExclude0[i][0];
            rowDataFinal[i][1] = rowDataExclude0[i][1];
            rowDataFinal[i][2] = rowDataExclude0[i][2];
            rowDataFinal[i][3] = rowDataExclude0[i][3];


        }
        //Set up table
        table = new JTable();
        table.setLocation(172, 44);
        table.setSize(548, 490);
        table.setFillsViewportHeight(true);
        table.setModel(new DefaultTableModel(rowDataExclude0, columnNames));
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
    }

    /**
     * Get info on a product
     *
     * @param info the info to be retrieved
     * @param PID  The ID of the product to get info for
     * @return The info of the product specified
     */
    private List<String> GetProductInfo(String info, String PID) {
        List<String> ret = new ArrayList<String>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS WHERE PID=?")
        ) {


            prep.setString(1, PID);

            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret.add(rs.getString(info));

                }
            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }
}

