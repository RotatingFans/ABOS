import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class CustomerReport extends JDialog {

    //TODO Add Active search with only results shown

    //TODO FIND SPELLING errors

    //TODO ADD ERROR HANDLING
    public static String year;
    private JFrame frame;
    private JTable table;
    //public String year;
    private String name;
    private JTextField textField;
    private double QuantL = 0;
    private double totL = 0;

    /**
     * Create the application.
     */
    public CustomerReport(String Name, String Year) {
        year = Year;
        name = Name;
        initialize();
        this.frame.setVisible(true);
    }

    /**
     * Launch the application.
     */
    public static void main(final String Name, final String Year, String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    CustomerReport window = new CustomerReport(Name, Year);
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
    @SuppressWarnings("serial")
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 826, 595);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        //West
        {
            JPanel west = new JPanel();
            west.setLayout(new BoxLayout(west, BoxLayout.PAGE_AXIS));

            JLabel lblName = new JLabel("Name");
            lblName.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblName.setBounds(20, 28, 66, 14);
            west.add(lblName);

            JLabel NameL = new JLabel(name);
            NameL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            NameL.setBounds(20, 42, 253, 46);
            west.add(NameL);

            JLabel lblNewLabel = new JLabel("Address");
            lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblNewLabel.setBounds(20, 82, 120, 20);
            west.add(lblNewLabel);

            JLabel AddrL = new JLabel(getAddr());
            AddrL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            AddrL.setBounds(20, 100, 253, 45);
            west.add(AddrL);

            JLabel lblPhone = new JLabel("Phone #");
            lblPhone.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblPhone.setBounds(20, 144, 96, 20);
            west.add(lblPhone);

            JLabel PhoneL = new JLabel(getPhone());
            PhoneL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            PhoneL.setBounds(20, 173, 253, 21);
            west.add(PhoneL);

            JLabel lblNewLabel_1 = new JLabel("Email");
            lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblNewLabel_1.setBounds(20, 205, 83, 20);
            west.add(lblNewLabel_1);

            JLabel lblNewLabel_2 = new JLabel(getEmail());
            lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
            lblNewLabel_2.setBounds(20, 224, 253, 35);
            west.add(lblNewLabel_2);

            JLabel lblPaid = new JLabel("Paid");
            lblPaid.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblPaid.setBounds(20, 265, 57, 20);
            west.add(lblPaid);

            JLabel PaidL = new JLabel(getPaid());
            PaidL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            PaidL.setBounds(20, 283, 253, 37);
            west.add(PaidL);

            JLabel lblDelivered = new JLabel("Delivered");
            lblDelivered.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblDelivered.setBounds(20, 328, 83, 20);
            west.add(lblDelivered);

            JLabel DeliveredL = new JLabel(getDelivered());
            DeliveredL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            DeliveredL.setBounds(20, 347, 253, 37);
            west.add(DeliveredL);

            JLabel lblTotalQuantity = new JLabel("Total Quantity");
            lblTotalQuantity.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblTotalQuantity.setBounds(20, 395, 120, 20);
            west.add(lblTotalQuantity);

            JLabel QuantityL = new JLabel(Double.toString(QuantL));
            QuantityL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            QuantityL.setBounds(20, 413, 253, 37);
            west.add(QuantityL);

            JLabel lblTotalOrder = new JLabel("Total Order");
            lblTotalOrder.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblTotalOrder.setBounds(20, 453, 96, 20);
            west.add(lblTotalOrder);

            JLabel TotL = new JLabel(Double.toString(totL));
            TotL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            TotL.setBounds(20, 474, 253, 37);
            west.add(TotL);
            frame.getContentPane().add(west, BorderLayout.WEST);
        }
        //North
        {
            JPanel north = new JPanel(new FlowLayout());


            JButton btnNewButton_1 = new JButton("Edit");
            btnNewButton_1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new AddCustomer(name);
                }
            });
            //btnNewButton_1.setBounds(193, 0, 120, 42);
            north.add(btnNewButton_1);

            JButton btnNewButton_2 = new JButton("Refresh");
            btnNewButton_2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(false);
                    new CustomerReport(name, year).setVisible(true);
                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                }
            });
            //	btnNewButton_2.setBounds(340, 0, 73, 38);
            north.add(btnNewButton_2);

            textField = new JTextField();
            //textField.setBounds(471, 11, 190, 31);
            north.add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("Search");
            btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < table.getRowCount(); i++) {

                        if (textField.getText().toString().contains((CharSequence) table.getModel().getValueAt(i, 0))) {
                            table.setRowSelectionInterval(i, i);
                        }
                    }
                }
            });
            //btnNewButton.setBounds(671, 11, 108, 31);
            north.add(btnNewButton);
            frame.getContentPane().add(north, BorderLayout.NORTH);


        }
        //Center
        {
            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(283, 42, 527, 514);
            frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            fillTable();
            scrollPane.setViewportView(table);
        }
        //South/Button Pane
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.LEFT));
            frame.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                        //frame.dispose();
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                frame.getRootPane().setDefaultButton(okButton);
            }

        }


    }

    private String getAddr() {
        return DbInt.getCustInf(year, name, "ADDR");

    }

    private String getPhone() {
        return DbInt.getCustInf(year, name, "PHONE");

    }

    private String getPaid() {
        return DbInt.getCustInf(year, name, "PAID");

    }

    private String getDelivered() {
        return DbInt.getCustInf(year, name, "DELIVERED");

    }

    private String getEmail() {
        return DbInt.getCustInf(year, name, "Email");

    }

    private void fillTable() {

        //DefaultTableModel model;
        //"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
        ArrayList<String> productIDs;
        ArrayList<String> productNames;
        ArrayList<String> Size;
        ArrayList<String> Unit;
        String toGet[] = {"ID", "PNAME", "SIZE", "UNIT"};
        String ret[][] = new String[4][];
        ArrayList<String> res = new ArrayList<String>();

        PreparedStatement prep = DbInt.getPrep(year, "SELECT ? FROM PRODUCTS");
        try {
            for (int i = 0; i < 4; i++) {
                prep.setString(1, toGet[i]);
                ResultSet rs = prep.executeQuery();

                while (rs.next()) {

                    res.add(rs.getString(1));

                }
                ret[i] = (String[]) res.toArray();
                DbInt.pCon.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        productIDs = new ArrayList(Arrays.asList(ret[0]));
        productNames = new ArrayList(Arrays.asList(ret[1]));
        Size = new ArrayList(Arrays.asList(ret[2]));
        Unit = new ArrayList(Arrays.asList(ret[3]));
        Object[][] rows = new Object[productNames.size()][6];
        String OrderID = DbInt.getCustInf(year, name, "ORDERID");
        ArrayList<String> Order = new ArrayList<String>();
        int colO = DbInt.getNoCol(year, "ORDERS") - 2;
        final Object[][] rowData = new Object[colO][4];
        int noVRows = 0;
        for (int i = 0; i < productNames.size(); i++) {

            int quant = 0;
            prep = DbInt.getPrep(year, "SELECT ? FROM ORDERS WHERE ORDERID=?");
            try {

                prep.setString(1, Integer.toString(i));
                prep.setString(2, OrderID);
                ResultSet rs = prep.executeQuery();

                while (rs.next()) {

                    Order.add(rs.getString(1));

                }
                DbInt.pCon.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            quant = Integer.parseInt(Order.get(Order.size() - 1));
            if (quant > 0) {

                rows[i][0] = productNames.get(i);
                rows[i][1] = Size.get(i);
                rows[i][2] = Unit.get(i);
                rows[i][3] = quant;
                rows[i][4] = quant * Double.parseDouble(Unit.get(i).replaceAll("\\$", ""));
                noVRows = noVRows + 1;

            }
        }
        final Object[][] rowDataF = new Object[noVRows][4];
        for (int i = 0; i <= noVRows - 1; i++) {
            rowDataF[i][0] = rowData[i][0];
            rowDataF[i][1] = rowData[i][1];
            rowDataF[i][2] = rowData[i][2];
            rowDataF[i][3] = rowData[i][3];
        }
        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};
        table.setModel(new DefaultTableModel(
                rows,
                new String[]{
                        "Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
                }
        ) {

            boolean[] columnEditables = new boolean[]{
                    false, false, false, false, false
            };

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        table.setFillsViewportHeight(true);
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);

    }

	/*private void fillTable() {
        try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		Connection con = null;
	    Statement st = null;
	    ResultSet Order = null;
	    //String Db = String.format("L&G%3",year);
		String url = String.format("jdbc:derby:%s/%s",new Config().getDbLoc(), year);
		System.setProperty("derby.system.home",
				new Config().getDbLoc());
	    ArrayList<String> res = new ArrayList<String>();
		DefaultTableModel model;
		int colO = DbInt.getNoCol(year, "ORDERS") - 2;
	    final Object[][] rowData = new Object[colO][4];
	    final Object[] columnNames = {"Item", "Price/Item", "Quantity", "Price"};
		orderIdl = DbInt.getCustInf(year, name, "ORDERID");
		int noVRows = 0;
		for (int i=0; i<orderIdl.size(); i++ ) {
	    try {
	        

	        
	        con = DriverManager.getConnection(url);
	        st = con.createStatement();
	        Order = st.executeQuery(String.format("SELECT * FROM ORDERS WHERE ORDERID=%s",orderIdl);
	        

			ResultSetMetaData rsmd = Order.getMetaData();

			int columnsNumber = rsmd.getColumnCount();
			while (Order.next()) {
				for (int c=3; c<= columnsNumber; c++) {
				
					ArrayList<String> productL = DbInt.getData(year, String.format("SELECT PName FROM PRODUCTS WHERE PID=%d",Integer.parseInt(rsmd.getColumnName(c)) + 1));
					String product = productL.get(productL.size() - 1);
					ArrayList<String> UnitL = DbInt.getData(year, String.format("SELECT Unit FROM PRODUCTS WHERE PID=%d",Integer.parseInt(rsmd.getColumnName(c)) + 1));
					String Unit = UnitL.get(productL.size() - 1);
					String quantity = Order.getString(c);
					double UnitD = Double.parseDouble(Unit.replaceAll("\\$",""));
					double quantityD = Double.parseDouble(quantity);
					if (quantityD > 0) {
						double TPrice = UnitD * quantityD;
						totL = totL + TPrice;
						QuantL = QuantL + quantityD;
						
						rowData[noVRows][0] = product;
						rowData[noVRows][1] = Unit;
						rowData[noVRows][2] = quantity;
						rowData[noVRows][3] = TPrice;
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
		
	}
		 final Object[][] rowDataF = new Object[noVRows][4];
		for (int i=0; i <= noVRows - 1; i++) {
			rowDataF[i][0] = rowData[i][0];
			rowDataF[i][1] = rowData[i][1];
			rowDataF[i][2] = rowData[i][2];
			rowDataF[i][3] = rowData[i][3];
		}
		table = new JTable();
		table.setModel(new DefaultTableModel(rowDataF,columnNames));
		table.setFillsViewportHeight(true);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
	}*/
}
