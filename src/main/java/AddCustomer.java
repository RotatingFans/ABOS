import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
    private double totalCostFinal = (double) 0.0;
    //Variables used to calculate difference of orders when in edit mode.
    private String NameEditCustomer = null;
    private double preEditMulchSales = 0.0;
    private double preEditLawnProductSales = 0.0;
    private double preEditLivePlantSales = 0.0;
    private double preEditDonations = 0.0;
    private double preEditOrderCost = 0.0;

    private AddCustomerWorker addCustWork = null;
    private Year yearInfo;
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
        DonationsT.setText(customerInfo.getDontation());
        preEditDonations = Double.parseDouble(DonationsT.getText());
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
            boolean[] columnEditables = new boolean[]{
                    false, false, false, false, true, false
            };

            @Override
            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });
        ProductTable.getModel().addTableModelListener(e -> {
            //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
            if ((e.getType() == 0) && (e.getColumn() == 4)) {
                int row = e.getFirstRow();
                int quantity = 0;
                try {
                    quantity = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(null, "You have not entered a number, please enter a number instead.", "", JOptionPane.ERROR_MESSAGE);
                }
                //Removes $ from cost and multiplies to get the total cost for that item
                double ItemTotalCost = quantity * Double.parseDouble(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));
                ProductTable.getModel().setValueAt(ItemTotalCost, row, 5);
                totalCostFinal = 0.0;
                for (int rowNo = 0; rowNo < ProductTable.getRowCount(); rowNo++) {
                    totalCostFinal += Double.parseDouble(ProductTable.getModel().getValueAt(rowNo, 5).toString());//Recalculate Order total
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
            preEditOrderCost += productOrder.extendedCost;
            i++;
        }
        ProductTable.setModel(new MyDefaultTableModel(rows));
        ProductTable.getModel().addTableModelListener(e -> {
            //If A cell in column 5, Quantity column, Then get the row, multiply the quantity by unit and add it to the total cost.
            if ((e.getType() == 0) && (e.getColumn() == 4)) {
                int row = e.getFirstRow();
                int quantity = Integer.parseInt(ProductTable.getModel().getValueAt(row, 4).toString());
                //Removes $ from cost and multiplies to get the total cost for that item
                double ItemTotalCost = quantity * Double.parseDouble(ProductTable.getModel().getValueAt(row, 3).toString().replaceAll("\\$", ""));
                ProductTable.getModel().setValueAt(ItemTotalCost, row, 5);
                totalCostFinal = 0.0;
                for (int rowNo = 0; rowNo < ProductTable.getRowCount(); rowNo++) {
                    totalCostFinal += Double.parseDouble(ProductTable.getModel().getValueAt(rowNo, 5).toString());//Recalculate Order total
                }
            }
        });
        //Fills original totals to calculate new values to insert in TOTALS table
        preEditMulchSales = getNoMulchOrdered();
        preEditLawnProductSales = getNoLawnProductsOrdered();
        preEditLivePlantSales = getNoLivePlantsOrdered();
        for (int rowNo = 0; rowNo < ProductTable.getRowCount(); rowNo++) {
            totalCostFinal += Double.parseDouble(ProductTable.getModel().getValueAt(rowNo, 5).toString());//Recalculate Order total
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
    private double getNoMulchOrdered() {
        double quantMulchOrdered = 0.0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if ((ProductTable.getModel().getValueAt(i, 1).toString().contains("Mulch")) && (ProductTable.getModel().getValueAt(i, 1).toString().contains("Bulk"))) {
                quantMulchOrdered += Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());
            }
        }
        return quantMulchOrdered;
    }

    /**
     * Loops through Table to get total amount of Lawn and Garden Products ordered.
     *
     * @return The amount of Lawn and Garden Products ordered
     */
    private double getNoLivePlantsOrdered() {
        double livePlantsOrdered = 0.0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 0).toString().contains("-P") || ProductTable.getModel().getValueAt(i, 0).toString().contains("-FV")) {
                livePlantsOrdered += Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());
            }
        }
        return livePlantsOrdered;
    }

    /**
     * Loops through Table to get total amount of Lawn Products ordered.
     *
     * @return The amount of Live Plants ordered
     */
    private double getNoLawnProductsOrdered() {
        double lawnProductsOrdered = 0.0;
        for (int i = 0; i < ProductTable.getRowCount(); i++) {
            if (ProductTable.getModel().getValueAt(i, 0).toString().contains("-L")) {
                lawnProductsOrdered += Double.parseDouble(ProductTable.getModel().getValueAt(i, 4).toString());
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
    private double getCommission(double totalCost) {
        double commision = 0.0;
        if ((totalCost > 299.99) && (totalCost < 500.01)) {
            commision = totalCost * 0.05;
        } else if ((totalCost > 500.01) && (totalCost < 1000.99)) {
            commision = totalCost * 0.1;
        } else if (totalCost >= 1001.0) {
            commision = totalCost * 0.15;
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
        try {
            Double donationChange = Double.parseDouble((Objects.equals(DonationsT.getText(), "")) ? "0" : DonationsT.getText()) - preEditDonations;
            Double donations = Double.parseDouble(yearInfo.getDonations()) + donationChange;
            Double Lg = Double.parseDouble(yearInfo.getLG()) + (getNoLawnProductsOrdered() - preEditLawnProductSales);
            Double LP = Double.parseDouble(yearInfo.getLP()) + (getNoLivePlantsOrdered() - preEditLivePlantSales);
            Double Mulch = Double.parseDouble(yearInfo.getMulch()) + (getNoMulchOrdered() - preEditMulchSales);
            Double OT = Double.parseDouble(yearInfo.getOT()) + (totalCostFinal - preEditOrderCost);
            Integer Customers = (yearInfo.getNoCustomers() + newCustomer);
            Double GTot = Double.parseDouble(yearInfo.getGTot()) + (totalCostFinal - preEditOrderCost) + donationChange;
            Double Commis = getCommission(GTot);
            try (PreparedStatement totalInsertString = DbInt.getPrep(year, "INSERT INTO TOTALS(DONATIONS,LG,LP,MULCH,TOTAL,CUSTOMERS,COMMISSIONS,GRANDTOTAL) VALUES(?,?,?,?,?,?,?,?)")) {
                totalInsertString.setString(1, Double.toString(donations));
                totalInsertString.setString(2, Double.toString(Lg));
                totalInsertString.setString(3, Double.toString(LP));
                totalInsertString.setString(4, Double.toString(Mulch));
                totalInsertString.setString(5, Double.toString(OT));
                totalInsertString.setString(6, Integer.toString(Customers));
                totalInsertString.setString(7, Double.toString(Commis));
                totalInsertString.setString(8, Double.toString(GTot));
                totalInsertString.execute();
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, "Could not update year totals. Please delete and recreate the order.");
        }
    }

    private static class MyDefaultTableModel extends DefaultTableModel {

        boolean[] columnEditables;

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
