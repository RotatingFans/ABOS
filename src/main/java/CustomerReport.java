/*
 * Copyright (c) Patrick Magauran 2017.
 * Licensed under the AGPLv3. All conditions of said license apply.
 *     This file is part of LawnAndGarden.
 *
 *     LawnAndGarden is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     LawnAndGarden is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with LawnAndGarden.  If not, see <http://www.gnu.org/licenses/>.
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class CustomerReport extends JDialog {
    public static String year = "2017";
    //public String year;
    private final String name;
    //TODO Add Active search with only results shown
    private JFrame frame;
    private JTable table;
    private JTextField textField;
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

            QuantityL = new JLabel("0");
            QuantityL.setFont(Fonts.plainFont);
            QuantityL.setBounds(20, 413, 253, 37);
            west.add(QuantityL);

            JLabel lblTotalOrder = new JLabel("Total Order");
            lblTotalOrder.setFont(Fonts.LargeFont);
            lblTotalOrder.setBounds(20, 453, 96, 20);
            west.add(lblTotalOrder);

            TotL = new JLabel("$0.00");
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

            JButton btnNewButton_3 = new JButton("Delete");
            btnNewButton_3.addActionListener(e -> deleteCustomer(name));
            //btnNewButton_1.setBounds(193, 0, 120, 42);
            north.add(btnNewButton_3);

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
        Object[][] rows = new Object[order.orderData.length][6];
        int i = 0;
        for (Product.formattedProduct productOrder : order.orderData) {
            rows[i][0] = productOrder.productID;
            rows[i][1] = productOrder.productName;
            rows[i][2] = productOrder.productSize;
            rows[i][3] = productOrder.productUnitPrice;
            rows[i][4] = productOrder.orderedQuantity;
            rows[i][5] = productOrder.extendedCost;
            i++;
        }

        QuantityL.setText(Integer.toString(order.totalQuantity));
        TotL.setText("$" + order.totalCost.toPlainString());

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

    private void deleteCustomer(String name) {
        String message = "<html><head><style>" +
                "h3 {text-align:center;}" +
                "h4 {text-align:center;}" +
                "</style></head>" +
                "<body><h3>WARNING!</h3>" +
                "<h3>BY CONTINUING YOU ARE PERMANENTLY REMOVING A CUSTOMER! ALL DATA MUST BE REENTERED!</h3>" +
                "<h4>Would you like to continue?</h4>" +
                "</body>" +
                "</html>";
        int cont = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (cont == 0) {
            fillTable();
            BigDecimal preEditOrderCost = BigDecimal.ZERO;
            Order.orderArray order = new Order().createOrderArray(year, customerInfo.getName(), false);
            for (Product.formattedProduct productOrder : order.orderData) {
                preEditOrderCost = preEditOrderCost.add(productOrder.extendedCost);
            }
            Year yearInfo = new Year(year);
            BigDecimal donations = yearInfo.getDonations().subtract(customerInfo.getDontation());
            int Lg = yearInfo.getLG() - getNoLawnProductsOrdered();
            int LP = yearInfo.getLP() - getNoLivePlantsOrdered();
            int Mulch = yearInfo.getMulch() - getNoMulchOrdered();
            BigDecimal OT = yearInfo.getOT().subtract(preEditOrderCost);
            int Customers = (yearInfo.getNoCustomers() - 1);
            BigDecimal GTot = yearInfo.getGTot().subtract(preEditOrderCost.add(customerInfo.getDontation()));
            BigDecimal Commis = getCommission(GTot);
            try (PreparedStatement totalInsertString = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(?,?,?,?,?,?,?,?)")) {
                totalInsertString.setBigDecimal(1, (donations.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                totalInsertString.setInt(2, Lg);
                totalInsertString.setInt(3, (LP));
                totalInsertString.setInt(4, (Mulch));
                totalInsertString.setBigDecimal(5, (OT.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                totalInsertString.setInt(6, (Customers));
                totalInsertString.setBigDecimal(7, (Commis.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                totalInsertString.setBigDecimal(8, (GTot.setScale(2, BigDecimal.ROUND_HALF_EVEN)));
                totalInsertString.execute();

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Could not update year totals. Please delete and recreate the order.");
            }
            try (PreparedStatement prep = DbInt.getPrep(year, "DELETE FROM ORDERS WHERE NAME=?")) {

                prep.setString(1, name);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Error deleting customer. Try again or contact support.");
            }
            try (PreparedStatement prep = DbInt.getPrep(year, "DELETE FROM Customers WHERE NAME=?")) {

                prep.setString(1, name);
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, "Error deleting customer. Try again or contact support.");
            }

        }
    }

    /**
     * Loops through Table to get total amount of Bulk Mulch ordered.
     *
     * @return The amount of Bulk mulch ordered
     */
    private int getNoMulchOrdered() {
        int quantMulchOrdered = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((table.getModel().getValueAt(i, 1).toString().contains("Mulch")) && (table.getModel().getValueAt(i, 1).toString().contains("Bulk"))) {
                quantMulchOrdered += Integer.parseInt(table.getModel().getValueAt(i, 4).toString());
            }
        }
        return quantMulchOrdered;
    }

    /**
     * Loops through Table to get total amount of Lawn and Garden Products ordered.
     *
     * @return The amount of Lawn and Garden Products ordered
     */
    private int getNoLivePlantsOrdered() {
        int livePlantsOrdered = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getModel().getValueAt(i, 0).toString().contains("-P") || table.getModel().getValueAt(i, 0).toString().contains("-FV")) {
                livePlantsOrdered += Integer.parseInt((table.getModel().getValueAt(i, 4).toString()));
            }
        }
        return livePlantsOrdered;
    }

    /**
     * Loops through Table to get total amount of Lawn Products ordered.
     *
     * @return The amount of Live Plants ordered
     */
    private int getNoLawnProductsOrdered() {
        int lawnProductsOrdered = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getModel().getValueAt(i, 0).toString().contains("-L")) {
                lawnProductsOrdered += Integer.parseInt(table.getModel().getValueAt(i, 4).toString());
            }
        }
        return lawnProductsOrdered;
    }

    /**
     * Calculates the amount of commission to be earned.
     *
     * @param totalCost the Sub total for all orders
     * @return Commission to be earned
     */
    private BigDecimal getCommission(BigDecimal totalCost) {
        BigDecimal commision = BigDecimal.ZERO;
        if ((totalCost.compareTo(new BigDecimal("299.99")) == 1) && (totalCost.compareTo(new BigDecimal("500.01")) == -1)) {
            commision = totalCost.multiply(new BigDecimal("0.05"));
        } else if ((totalCost.compareTo(new BigDecimal("500.01")) == 1) && (totalCost.compareTo(new BigDecimal("1000.99")) == -1)) {
            commision = totalCost.multiply(new BigDecimal("0.1"));
        } else if (totalCost.compareTo(new BigDecimal("1000")) >= 0) {
            commision = totalCost.multiply(new BigDecimal("0.15"));
        }
        return commision;
    }
    private static class MyDefaultTableModel extends DefaultTableModel {

        final boolean[] columnEditables;

        public MyDefaultTableModel(Object[][] rowDataF) {
            super(rowDataF, new String[]{
                    "ID", "Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
            });
            columnEditables = new boolean[]{
                    false, false, false, false, false, false
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
