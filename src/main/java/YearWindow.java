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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


class YearWindow extends JDialog {

    public static String year = "2017";
    private JFrame frame;
    private JTable table;


    /**
     * Create the application.
     *
     * @param Years The year to display
     */
    public YearWindow(String Years) {
        year = Years;
        //System.out.print(year);
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
        Year yearInfo = new Year(year);
        frame = new JFrame();
        frame.setBounds(100, 100, 750, 600);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ABOS-LOGO.png")));
        this.setTitle("ABOS - Year Overview - " + year);

        //West
        {
            JPanel East = new JPanel();
            East.setLayout(new BoxLayout(East, BoxLayout.PAGE_AXIS));


            JLabel lblNewLabel = new JLabel("Donations");
            Font l = new Font("Tahoma", Font.PLAIN, 24);
            lblNewLabel.setFont(l);
            //lblNewLabel.setBounds(16, 11, 97, 14);
            East.add(lblNewLabel);

            JLabel DonationsR = new JLabel(yearInfo.getDonations().toPlainString());
            DonationsR.setFont(l);
            //DonationsR.setBounds(193, 11, 65, 14);
            East.add(DonationsR);

            JLabel lblLawnAndGarden = new JLabel("Lawn and Garden Products");
            lblLawnAndGarden.setFont(l);
            //lblLawnAndGarden.setBounds(16, 36, 142, 14);
            East.add(lblLawnAndGarden);

            JLabel LGR = new JLabel(Integer.toString(yearInfo.getLG()));
            LGR.setFont(l);
            //LGR.setBounds(193, 36, 65, 14);
            East.add(LGR);

            JLabel lblLivePlantProducts = new JLabel("Live Plant Products");
            lblLivePlantProducts.setFont(l);
            //lblLivePlantProducts.setBounds(16, 61, 113, 14);
            East.add(lblLivePlantProducts);

            JLabel LPR = new JLabel(Integer.toString(yearInfo.getLP()));
            LPR.setFont(l);
            //LPR.setBounds(193, 61, 65, 14);
            East.add(LPR);

            JLabel lblF = new JLabel("Mulch");
            lblF.setFont(l);
            //	lblF.setBounds(16, 86, 113, 14);
            East.add(lblF);

            JLabel MulchR = new JLabel(Integer.toString(yearInfo.getMulch()));
            MulchR.setFont(l);
            //MulchR.setBounds(193, 86, 65, 14);
            East.add(MulchR);

            JLabel lblCustomers = new JLabel("Order Total");
            lblCustomers.setFont(l);
            //lblCustomers.setBounds(16, 111, 113, 14);
            East.add(lblCustomers);

            JLabel OR = new JLabel(yearInfo.getOT().toPlainString());
            OR.setFont(l);
            //OR.setBounds(193, 111, 65, 14);
            East.add(OR);


            JLabel lblGTot = new JLabel("Grand Total: ");
            lblGTot.setFont(l);
            //lblCommision.setBounds(16, 160, 83, 14);
            East.add(lblGTot);

            JLabel GTotR = new JLabel(yearInfo.getGTot().toPlainString());
            //CommissionR.setBounds(193, 161, 65, 14);
            GTotR.setFont(l);
            East.add(GTotR);
            JLabel lblCommision = new JLabel("Commission ");
            lblCommision.setFont(l);
            //lblCommision.setBounds(16, 160, 83, 14);
            East.add(lblCommision);

            JLabel CommissionR = new JLabel(yearInfo.getCommis().toPlainString());
            //CommissionR.setBounds(193, 161, 65, 14);
            CommissionR.setFont(l);
            East.add(CommissionR);
            JLabel lblCustomers_1 = new JLabel("Customers");
            lblCustomers_1.setFont(l);
            //lblCustomers_1.setBounds(16, 136, 142, 14);
            East.add(lblCustomers_1);

            JLabel CustomersR = new JLabel(Integer.toString(yearInfo.getNoCustomers()));
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
            {
                JButton btnNewButton_1 = new JButton("Customers");
                //btnNewButton_1.setBounds(276, 232, 212, 247);
                btnNewButton_1.addActionListener(arg0 -> new CustomerView(year));
                South.add(btnNewButton_1);
            }
            {
                JButton btnNewButton_1 = new JButton("Edit Year");
                //btnNewButton_1.setBounds(276, 232, 212, 247);
                btnNewButton_1.addActionListener(arg0 -> new AddYear(year));
                South.add(btnNewButton_1);
            }
            {
                JButton btnNewButton_1 = new JButton("Delete Year");
                //btnNewButton_1.setBounds(276, 232, 212, 247);
                btnNewButton_1.addActionListener(e -> {
                    String message = "<html><head><style>" +
                            "h3 {text-align:center;}" +
                            "h4 {text-align:center;}" +
                            "</style></head>" +
                            "<body><h3>WARNING !</h3>" +
                            "<h3>You are about to delete an entire Year.</h3>" +
                            "<h3>This action is irreversible.</h3>" +
                            "<h4>Would you like to continue with the deletion?</h4>" +
                            "</body>" +
                            "</html>";
                    int cont = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (cont == 0) {
                        DbInt.deleteDb(year);
                        try (PreparedStatement prep = DbInt.getPrep("Set", "DELETE FROM YEARS WHERE YEARS=?")) {
                            prep.setString(1, year);
                            prep.execute();
                        } catch (SQLException Se) {
                            LogToFile.log(Se, Severity.SEVERE, CommonErrors.returnSqlMessage(Se));
                        }

                    }
                    message = "<html><head><style>" +
                            "h3 {text-align:center;}" +
                            "h4 {text-align:center;}" +
                            "</style></head>" +
                            "<body><h3>WARNING !</h3>" +
                            "<h3>The application must now manuallly restart</h3>" +
                            "<h4>Please re-open the application once it closes.</h4>" +
                            "</body>" +
                            "</html>";
                    JOptionPane.showConfirmDialog(null, message, "", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
                    System.exit(0);
                });
                South.add(btnNewButton_1);
            }
            {
                JButton btnRefresh = new JButton("Refresh");
                btnRefresh.addActionListener(e -> {
                    frame.setVisible(false);
                    initialize();
                    frame.setVisible(true);


                });

                //btnRefresh.setBounds(628, 157, 89, 23);
                South.add(btnRefresh);
            }
            frame.getContentPane().add(South, BorderLayout.SOUTH);
        }


    }



    /**
     * Fills the Table of order amounts
     */
    private void fillTable() {
        Order.orderArray order = new Order().createOrderArray(year);

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
        Object[] columnNames = {"Item", "Size", "Price/Item", "Quantity", "Price"};

        table = new JTable();
        table.setLocation(172, 44);
        table.setSize(548, 490);
        table.setFillsViewportHeight(true);
        table.setModel(new DefaultTableModel(rows, columnNames));
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
    @SuppressWarnings("unused")
    private List<String> GetProductInfo(String info, String PID) {
        List<String> ret = new ArrayList<>();

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
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return ret;
    }
}

