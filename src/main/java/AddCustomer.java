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
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CancellationException;

/**
 * A dialog that allows the user to add a new customer or edit an existing customer.
 *
 * @author patrick
 * @version 1.0
 */
class AddCustomer extends JDialog {
    private static boolean edit = false; //States whether this is an edit or creation of a customer.
    private final Year yearInfo;
    //private final JPanel contentPanel = new JPanel();
    //Editable Field for user to input customer info.
    private JCheckBox Delivered;
    private JCheckBox Paid;
    private JTable ProductTable;
    private JTextField Name;
    private JTextField Address;
    private JTextField ZipCode;
    private JTextField Town;
    private JTextField State;
    private JTextField Phone;
    private JTextField Email;
    private JTextField DonationsT;
    //Variables used to store regularly accessed info.
    private String year = null;
    private BigDecimal totalCostFinal = BigDecimal.ZERO;
    //Variables used to calculate difference of orders when in edit mode.
    private String NameEditCustomer = null;
    private int preEditMulchSales = 0;
    private int preEditLawnProductSales = 0;
    private int preEditLivePlantSales = 0;
    private BigDecimal preEditDonations = BigDecimal.ZERO;
    private BigDecimal preEditOrderCost = BigDecimal.ZERO;
    private AddCustomerWorker addCustWork = null;
    private Customer customerInfo = null;
    private int newCustomer = 0;
    /**
     * Used to open dialog with already existing customer information from year as specified in Customer Report.
     *
     * @param customerName the name of the customer being edited.
     */
    public AddCustomer(String customerName) {
        year = CustomerReport.year;
        yearInfo = new Year(year);
        customerInfo = new Customer(customerName, year);
        edit = true;
        initUI();
        this.setTitle("ABOS - Edit Customer - " + customerName);

        //Set the address
        String[] addr = customerInfo.getCustAddressFrmName();
        String city = addr[0];
        String state = addr[1];
        String zip = addr[2];
        String streetAdd = addr[3];
        //Fill in Customer info fields.
        Address.setText(streetAdd);
        Town.setText(city);
        State.setText(state);
        ZipCode.setText(zip);
        Phone.setText(customerInfo.getPhone());
        Paid.setSelected(Boolean.getBoolean(customerInfo.getPaid()));
        Delivered.setSelected(Boolean.getBoolean(customerInfo.getDelivered()));
        Email.setText(customerInfo.getEmail());
        Name.setText(customerName);
        DonationsT.setText(customerInfo.getDontation().toPlainString());
        preEditDonations = customerInfo.getDontation();
        //Fill the table with their previous order info on record.
        fillOrderedTable();

        NameEditCustomer = customerName;
        edit = true;
        //Add a Event to occur if a cell is changed in the table

        setVisible(true);
    }

    public AddCustomer() {
        newCustomer = 1;

        year = YearWindow.year;
        yearInfo = new Year(year);

        initUI();
        fillTable();

    }

    /*
      Launch the application.
     */
/*    public static void main(String... args) {
        try {
            AddCustomer dialog = new AddCustomer();
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (RuntimeException e) {
            LogToFile.log(e, Severity.SEVERE, );
        }
    }*/

    /**
     * Create the dialog.
     */
    private void initUI() {
        setSize(900, 600);
        getContentPane().setLayout(new BorderLayout());
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ABOS-LOGO.png")));
        this.setTitle("ABOS - Add Customer");
        WrapLayout flow = new WrapLayout();

        //Add Customer info fields and lables
        {
            JPanel North = new JPanel();
            North.setLayout(flow);
            {
                JPanel namePnl = new JPanel(flow);
                namePnl.add(new JLabel("Name"));
                namePnl.add(Name = new JTextField(Config.getProp("CustomerName"), 15));
                North.add(namePnl);
            }
            {
                JPanel streetAddressPnl = new JPanel(flow);
                streetAddressPnl.add(new JLabel("Street Address"));
                streetAddressPnl.add(Address = new JTextField(Config.getProp("CustomerAddress"), 20));
                North.add(streetAddressPnl);
            }
            {
                JPanel zipCodePnl = new JPanel(flow);
                zipCodePnl.add(new JLabel("ZipCode"));
                zipCodePnl.add(ZipCode = new JTextField(Config.getProp("CustomerZipCode"), 5));
                ZipCode.addActionListener(new MyTextActionListener());
                North.add(zipCodePnl);
            }
            {
                JPanel custTownPnl = new JPanel(flow);
                custTownPnl.add(new JLabel("Town"));
                custTownPnl.add(Town = new JTextField(Config.getProp("CustomerTown"), 10));
                North.add(custTownPnl);
            }
            {
                JPanel custStatePnl = new JPanel(flow);
                custStatePnl.add(new JLabel("State"));
                custStatePnl.add(State = new JTextField(Config.getProp("CustomerState"), 15));
                North.add(custStatePnl);
            }
            {
                JPanel custPhonePnl = new JPanel(flow);
                custPhonePnl.add(new JLabel("Phone #"));
                custPhonePnl.add(Phone = new JTextField(Config.getProp("CustomerPhone"), 10));
                North.add(custPhonePnl);
            }
            {
                JPanel custEmailPnl = new JPanel(flow);
                custEmailPnl.add(new JLabel("Email Address"));
                custEmailPnl.add(Email = new JTextField(Config.getProp("CustomerEmail"), 10));
                North.add(custEmailPnl);
            }
            {
                North.add(Paid = new JCheckBox("Paid", Boolean.valueOf(Config.getProp("CustomerPaid"))));
                North.add(Delivered = new JCheckBox("Delivered", Boolean.valueOf(Config.getProp("CustomerDelivered"))));
            }
            {
                JPanel custDonationPnl = new JPanel(flow);
                custDonationPnl.add(new JLabel("Donations"));
                custDonationPnl.add(DonationsT = new JTextField((Config.getProp("CustomerDonation") != null) ? Config.getProp("CustomerDonation") : "0.0", 4));
                North.add(custDonationPnl);
            }
            getContentPane().add(North, BorderLayout.PAGE_START);
        }
        {
            JScrollPane scrollPane = new JScrollPane();
            ProductTable = new JTable();
            ProductTable.setFillsViewportHeight(true);

            scrollPane.setViewportView(ProductTable);
            getContentPane().add(scrollPane, BorderLayout.CENTER);

        }
        //Add button pane to bottom of Window
        {
            JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.PAGE_END);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(e -> {
                    if (infoEntered()) {
                        commitChanges();

                    } else {
                        String message = "<html><head><style>" +
                                "h3 {text-align:center;}" +
                                "h4 {text-align:center;}" +
                                "</style></head>" +
                                "<body><h3>Uh Oh!</h3>" +
                                "<h3>It appears you have not entered any data</h3>" +
                                "<h4>Would you like to re-enter the data?</h4>" +
                                "</body>" +
                                "</html>";
                        int continueUse = JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (continueUse == 1) {
                            dispose();
                            setVisible(false);
                        }
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(e -> dispose());
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Fills the table with quantitys set to 0.
     */
    private void fillTable() {

        Product.formattedProduct[] productArray = yearInfo.getAllProducts();
        Object[][] rows = new Object[productArray.length][6];
        int i = 0;
        for (Product.formattedProduct productOrder : productArray) {
            rows[i][0] = productOrder.productID;
            rows[i][1] = productOrder.productName;
            rows[i][2] = productOrder.productSize;
            rows[i][3] = productOrder.productUnitPrice;
            rows[i][4] = productOrder.orderedQuantity;
            rows[i][5] = productOrder.extendedCost;
            i++;
        }
        ProductTable.setModel(new DefaultTableModel(
                rows,
                new String[]{"ID", "Product Name", "Size", "Price/Item", "Quantity", "Total Cost"}) {
            final boolean[] columnEditables = new boolean[]{
                    false, false, false, false, true, false
            };

            @Override
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        ProductTable.getModel().addTableModelListener(e -> {
            //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
            if ((e.getType() == TableModelEvent.UPDATE) && (e.getColumn() == 4)) {
                int row = e.getFirstRow();
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(null, "You have not entered a number, please enter a number instead.", "", JOptionPane.ERROR_MESSAGE);
                }
                //Removes $ from cost and multiplies to get the total cost for that item
                BigDecimal ItemTotalCost = new BigDecimal(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", "")).multiply(new BigDecimal(quantity));
                ProductTable.getModel().setValueAt(ItemTotalCost, row, 5);
                totalCostFinal = BigDecimal.ZERO;
                for (int rowNo = 0; rowNo < ProductTable.getRowCount(); rowNo++) {
                    totalCostFinal = totalCostFinal.add(new BigDecimal(ProductTable.getModel().getValueAt(rowNo, 5).toString()));//Recalculate Order total
                }
            }
        });
    }

    /**
     * Fills product table with info with quantities set to Amount customer ordered.
     *
     */
    private void fillOrderedTable() {
        Order.orderArray order = new Order().createOrderArray(year, customerInfo.getName(), false);
        Object[][] rows = new Object[order.orderData.length][6];
        int i = 0;
        for (Product.formattedProduct productOrder : order.orderData) {
            rows[i][0] = productOrder.productID;
            rows[i][1] = productOrder.productName;
            rows[i][2] = productOrder.productSize;
            rows[i][3] = productOrder.productUnitPrice;
            rows[i][4] = productOrder.orderedQuantity;
            rows[i][5] = productOrder.extendedCost;
            preEditOrderCost = preEditOrderCost.add(productOrder.extendedCost);
            i++;
        }
        ProductTable.setModel(new MyDefaultTableModel(rows));
        ProductTable.getModel().addTableModelListener(e -> {
            //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
            if ((e.getType() == TableModelEvent.UPDATE) && (e.getColumn() == 4)) {
                int row = e.getFirstRow();
                int quantity = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                //Removes $ from cost and multiplies to get the total cost for that item
                BigDecimal ItemTotalCost = new BigDecimal(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", "")).multiply(new BigDecimal(quantity));
                ProductTable.getModel().setValueAt(ItemTotalCost, row, 5);
                totalCostFinal = BigDecimal.ZERO;
                for (int rowNo = 0; rowNo < ProductTable.getRowCount(); rowNo++) {
                    totalCostFinal = totalCostFinal.add(new BigDecimal(ProductTable.getModel().getValueAt(rowNo, 5).toString()));//Recalculate Order total
                }
            }
        });
        //Fills original totals to calculate new values to insert in TOTALS table
        preEditMulchSales = getNoMulchOrdered();
        preEditLawnProductSales = getNoLawnProductsOrdered();
        preEditLivePlantSales = getNoLivePlantsOrdered();
        for (int rowNo = 0; rowNo < ProductTable.getRowCount(); rowNo++) {
            totalCostFinal = totalCostFinal.add(new BigDecimal(ProductTable.getModel().getValueAt(rowNo, 5).toString()));//Recalculate Order total

        }
    }

    /**
     * Commits table to the Database
     */
    private void commitChanges() {

        ProgressDialog progDial = new ProgressDialog();

        addCustWork = new AddCustomerWorker(Address.getText(),
                Town.getText(),
                State.getText(),
                year,
                edit,
                ProductTable,
                Name.getText(),
                ZipCode.getText(),
                Phone.getText(),
                Email.getText(),
                DonationsT.getText(),
                NameEditCustomer,
                Paid.isSelected(),
                Delivered.isSelected(),
                progDial.statusLbl);
        addCustWork.addPropertyChangeListener(event -> {
            switch (event.getPropertyName()) {
                case "progress":
                    progDial.progressBar.setIndeterminate(false);
                    progDial.progressBar.setValue((Integer) event.getNewValue());
                    break;
                case "state":
                    switch ((SwingWorker.StateValue) event.getNewValue()) {
                        case DONE:
                            try {
                                int success = addCustWork.get();
                                if (success == 1) {
                                    updateTots();
                                    dispose();
                                    setVisible(false);
                                }
                            } catch (CancellationException e) {
                                LogToFile.log(e, Severity.INFO, "The process was cancelled.");
                            } catch (Exception e) {
                                LogToFile.log(e, Severity.WARNING, "The process Failed.");
                            }
                            addCustWork = null;
                            progDial.dispose();
                            break;
                        case STARTED:
                        case PENDING:
                            progDial.progressBar.setVisible(true);
                            progDial.progressBar.setIndeterminate(true);
                            break;
                    }
                    break;
            }
        });
        addCustWork.execute();
    }

    /**
     * Loops through Table to get total amount of Bulk Mulch ordered.
     *
     * @return The amount of Bulk mulch ordered
     */
    private int getNoMulchOrdered() {
        int quantMulchOrdered = 0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if ((ProductTable.getModel().getValueAt(i, 1).toString().contains("Mulch")) && (ProductTable.getModel().getValueAt(i, 1).toString().contains("Bulk"))) {
                quantMulchOrdered += Integer.parseInt(ProductTable.getModel().getValueAt(i, 4).toString());
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
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 0).toString().contains("-P") || ProductTable.getModel().getValueAt(i, 0).toString().contains("-FV")) {
                livePlantsOrdered += Integer.parseInt((ProductTable.getModel().getValueAt(i, 4).toString()));
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
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 0).toString().contains("-L")) {
                lawnProductsOrdered += Integer.parseInt(ProductTable.getModel().getValueAt(i, 4).toString());
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

    /**
     * Anaylizes if any text was entered into both the Address and Name field and if both are empty returns false, else true
     *
     * @return if required info was entered
     */
    private boolean infoEntered() {
        return !((Name.getText().isEmpty()) && (Address.getText().isEmpty()));
    }

    /**
     * Updates the totals tables
     */
    private void updateTots() {
        /*
          get current totals
          add to them
          update
         */
            BigDecimal donationChange = new BigDecimal((Objects.equals(DonationsT.getText(), "")) ? "0" : DonationsT.getText()).subtract(preEditDonations);
            BigDecimal donations = yearInfo.getDonations().add(donationChange);
            int Lg = yearInfo.getLG() + (getNoLawnProductsOrdered() - preEditLawnProductSales);
            int LP = yearInfo.getLP() + (getNoLivePlantsOrdered() - preEditLivePlantSales);
            int Mulch = yearInfo.getMulch() + (getNoMulchOrdered() - preEditMulchSales);
            BigDecimal OT = yearInfo.getOT().add(totalCostFinal.subtract(preEditOrderCost));
            int Customers = (yearInfo.getNoCustomers() + newCustomer);
            BigDecimal GTot = yearInfo.getGTot().add(totalCostFinal.subtract(preEditOrderCost).add(donationChange));
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
    }

    private static class MyDefaultTableModel extends DefaultTableModel {

        final boolean[] columnEditables;

        public MyDefaultTableModel(Object[][] rows) {

            super(rows, new String[]{
                    "ID", "Product Name", "Size", "Price/Item", "Quantity", "Total Cost"
            });
            columnEditables = new boolean[]{
                    false, false, false, false, true, false
            };
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return columnEditables[column];
        }


    }

    private class MyTextActionListener implements ActionListener {
        /**
         * Handle the text field Return.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String zip = ZipCode.getText();
            if (zip.length() > 4) {
                String cityAndState = "";
                try {
                    cityAndState = Geolocation.getCityState(zip);
                } catch (IOException e1) {
                    LogToFile.log(e1, Severity.WARNING, "Couldn't contact geolocation service. Please try again or enter the adress manually and contact suport.");
                }
                String[] StateTown = cityAndState.split("&");
                String state = StateTown[1];
                String town = StateTown[0];
                Town.setText(town);
                State.setText(state);
            }
        }
    }


}
