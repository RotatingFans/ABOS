import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


class CustomerReport extends JDialog {

    //TODO Add Active search with only results shown


    //TODO ADD ERROR HANDLING
    public static String year = "2015";
    private JFrame frame;
    private JTable table;
    //public String year;
    private String name;
    private JTextField textField;
    private double QuantL = 0.0;
    private double totL = 0.0;
    private JLabel QuantityL;
    private JLabel TotL;

    /**
     * Create the application.
     */
    public CustomerReport(String Name, String Year) {
        year = Year;
        name = Name;
        initialize();
        frame.setVisible(true);
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Launch the application.
//     */
//    public static void main(String Name, String Year, String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    CustomerReport window = new CustomerReport(Name, Year);
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
        frame.setBounds(100, 100, 826, 595);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

            QuantityL = new JLabel(Double.toString(QuantL));
            QuantityL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            QuantityL.setBounds(20, 413, 253, 37);
            west.add(QuantityL);

            JLabel lblTotalOrder = new JLabel("Total Order");
            lblTotalOrder.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblTotalOrder.setBounds(20, 453, 96, 20);
            west.add(lblTotalOrder);

            TotL = new JLabel(Double.toString(totL));
            TotL.setFont(new Font("Tahoma", Font.PLAIN, 14));
            TotL.setBounds(20, 474, 253, 37);
            west.add(TotL);
            frame.getContentPane().add(west, BorderLayout.WEST);
        }
        //North
        {
            JPanel north = new JPanel(new FlowLayout());


            JButton btnNewButton_1 = new JButton("Edit");
            btnNewButton_1.addActionListener(e -> new AddCustomer(name));
            //btnNewButton_1.setBounds(193, 0, 120, 42);
            north.add(btnNewButton_1);

            JButton btnNewButton_2 = new JButton("Refresh");
            btnNewButton_2.addActionListener(e -> {
                frame.setVisible(false);
                new CustomerReport(name, year).setVisible(true);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            });
            //	btnNewButton_2.setBounds(340, 0, 73, 38);
            north.add(btnNewButton_2);

            textField = new JTextField();
            //textField.setBounds(471, 11, 190, 31);
            north.add(textField);
            textField.setColumns(10);

            JButton btnNewButton = new JButton("Search");
            btnNewButton.addActionListener(e -> {
                for (int i = 0; i < table.getRowCount(); i++) {

                    if (textField.getText().contains((CharSequence) table.getModel().getValueAt(i, 0))) {
                        table.setRowSelectionInterval(i, i);
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
                okButton.addActionListener(e -> {

                    frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                    //frame.dispose();
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                frame.getRootPane().setDefaultButton(okButton);
            }

        }


    }

    private String getAddr() {
        return DbInt.getCustInf(year, name, "ADDRESS");

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

        //Variables for inserting info into table
        String[] toGet = {"PNAME", "SIZE", "UNIT"};
        List<ArrayList<String>> ProductInfoArray = new ArrayList<ArrayList<String>>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()
        ) {
            //Run through Data set and add info to ProductInfoArray
            for (int i = 0; i < 3; i++) {
                ProductInfoArray.add(new ArrayList<String>());
                while (ProductInfoResultSet.next()) {

                    ProductInfoArray.get(i).add(ProductInfoResultSet.getString(toGet[i]));

                }
                ProductInfoResultSet.beforeFirst();
                DbInt.pCon.commit();
                ////DbInt.pCon.close();

            }

            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Table rows array
        Object[][] rows = new Object[ProductInfoArray.get(1).size()][6];



        String OrderID = DbInt.getCustInf(year, name, "ORDERID");
        //Defines Arraylist of order quanitities
        List<String> OrderQuantities = new ArrayList<String>();
        int noVRows = 0;
        //Fills OrderQuantities Array
        for (int i = 0; i < ProductInfoArray.get(1).size(); i++) {

            int quant;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ORDERS WHERE ORDERID=?")) {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, OrderID);
                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {

                        OrderQuantities.add(rs.getString(Integer.toString(i)));

                    }
                }
                ////DbInt.pCon.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Fills row array for table with info
            quant = Integer.parseInt(OrderQuantities.get(OrderQuantities.size() - 1));


            if (quant > 0) {


                rows[noVRows][0] = ProductInfoArray.get(0).get(i);
                rows[noVRows][1] = ProductInfoArray.get(1).get(i);
                rows[noVRows][2] = ProductInfoArray.get(2).get(i);
                rows[noVRows][3] = quant;
                rows[noVRows][4] = (double) quant * Double.parseDouble(ProductInfoArray.get(2).get(i).replaceAll("\\$", ""));
                QuantL = +QuantL;
                totL += ((double) quant * Double.parseDouble(ProductInfoArray.get(2).get(i).replaceAll("\\$", "")));
                noVRows++;

            }
        }
        //Re create rows to remove blank rows
        Object[][] rowDataF = new Object[noVRows][5];
        for (int i = 0; i <= (noVRows - 1); i++) {
            rowDataF[i][0] = rows[i][0];
            rowDataF[i][1] = rows[i][1];
            rowDataF[i][2] = rows[i][2];
            rowDataF[i][3] = rows[i][3];
            rowDataF[i][4] = rows[i][4];
        }
        QuantityL.setText(Double.toString(QuantL));
        TotL.setText(Double.toString(totL));

        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};
        table = new JTable();
        table.setModel(new MyDefaultTableModel(rowDataF));
        table.setFillsViewportHeight(true);
        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);

    }

    private static class MyDefaultTableModel extends DefaultTableModel {

        boolean[] columnEditables;

        public MyDefaultTableModel(Object[][] rowDataF) {
            super(rowDataF, new String[]{
                    "Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
            });
            columnEditables = new boolean[]{
                    false, false, false, false, false
            };
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return columnEditables[column];
        }
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    public void setTable(JTable table) {
//        this.table = table;
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)


}
