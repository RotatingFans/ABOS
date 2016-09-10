import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;


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
    private Customer customerInfo;

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
        customerInfo = new Customer(name, year);
        frame = new JFrame();
        frame.setBounds(100, 100, 826, 595);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        //West
        {
            JPanel west = new JPanel();
            west.setLayout(new BoxLayout(west, BoxLayout.PAGE_AXIS));

            JLabel lblName = new JLabel("Name");
            lblName.setFont(Fonts.LargeFont);
            lblName.setBounds(20, 28, 66, 14);
            west.add(lblName);

            JLabel NameL = new JLabel(name);
            NameL.setFont(Fonts.plainFont);
            NameL.setBounds(20, 42, 253, 46);
            west.add(NameL);

            JLabel lblNewLabel = new JLabel("Address");
            lblNewLabel.setFont(Fonts.LargeFont);
            lblNewLabel.setBounds(20, 82, 120, 20);
            west.add(lblNewLabel);

            JLabel AddrL = new JLabel(customerInfo.getAddr());
            AddrL.setFont(Fonts.plainFont);
            AddrL.setBounds(20, 100, 253, 45);
            west.add(AddrL);

            JLabel lblPhone = new JLabel("Phone #");
            lblPhone.setFont(Fonts.LargeFont);
            lblPhone.setBounds(20, 144, 96, 20);
            west.add(lblPhone);

            JLabel PhoneL = new JLabel(customerInfo.getPhone());
            PhoneL.setFont(Fonts.plainFont);
            PhoneL.setBounds(20, 173, 253, 21);
            west.add(PhoneL);

            JLabel lblNewLabel_1 = new JLabel("Email");
            lblNewLabel_1.setFont(Fonts.LargeFont);
            lblNewLabel_1.setBounds(20, 205, 83, 20);
            west.add(lblNewLabel_1);

            JLabel lblNewLabel_2 = new JLabel(customerInfo.getEmail());
            lblNewLabel_2.setFont(Fonts.plainFont);
            lblNewLabel_2.setBounds(20, 224, 253, 35);
            west.add(lblNewLabel_2);

            JLabel lblPaid = new JLabel("Paid");
            lblPaid.setFont(Fonts.LargeFont);
            lblPaid.setBounds(20, 265, 57, 20);
            west.add(lblPaid);

            JLabel PaidL = new JLabel(customerInfo.getPaid());
            PaidL.setFont(Fonts.plainFont);
            PaidL.setBounds(20, 283, 253, 37);
            west.add(PaidL);

            JLabel lblDelivered = new JLabel("Delivered");
            lblDelivered.setFont(Fonts.LargeFont);
            lblDelivered.setBounds(20, 328, 83, 20);
            west.add(lblDelivered);

            JLabel DeliveredL = new JLabel(customerInfo.getDelivered());
            DeliveredL.setFont(Fonts.plainFont);
            DeliveredL.setBounds(20, 347, 253, 37);
            west.add(DeliveredL);

            JLabel lblTotalQuantity = new JLabel("Total Quantity");
            lblTotalQuantity.setFont(Fonts.LargeFont);
            lblTotalQuantity.setBounds(20, 395, 120, 20);
            west.add(lblTotalQuantity);

            QuantityL = new JLabel(Double.toString(QuantL));
            QuantityL.setFont(Fonts.plainFont);
            QuantityL.setBounds(20, 413, 253, 37);
            west.add(QuantityL);

            JLabel lblTotalOrder = new JLabel("Total Order");
            lblTotalOrder.setFont(Fonts.LargeFont);
            lblTotalOrder.setBounds(20, 453, 96, 20);
            west.add(lblTotalOrder);

            TotL = new JLabel(Double.toString(totL));
            TotL.setFont(Fonts.plainFont);
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

                    if (table.getModel().getValueAt(i, 0).toString().contains(textField.getText())) {
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


    private void fillTable() {
        Order.orderArray order = new Order().createOrderArray(year, name, true);
        Object[][] rows = new Object[order.orderData.length][5];
        int i = 0;
        for (Product.formattedProduct productOrder : order.orderData) {
            rows[i][0] = productOrder.productName;
            rows[i][1] = productOrder.productSize;
            rows[i][2] = productOrder.productUnitPrice;
            rows[i][3] = productOrder.orderedQuantity;
            rows[i][4] = productOrder.extendedCost;
            i++;
        }

        QuantityL.setText(Double.toString(order.totalQuantity));
        TotL.setText(Double.toString(order.totalCost));

        //final Object[] columnNames = {"Product Name", "Size", "Price/Item", "Quantity", "Total Cost"};
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(false);

        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setModel(new MyDefaultTableModel(rows));
        table.setFillsViewportHeight(true);


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
