import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by patrick on 7/27/16.
 */
public class Order {
    Product product = new Product();
    private double totL = 0.0;
    private double QuantL = 0.0;

    public orderArray createOrderArray(String year, String name) {

        //Variables for inserting info into table
        String[] toGet = {"ID", "PNAME", "SIZE", "UNIT", "Category"};
        List<ArrayList<String>> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray

            for (int i = 0; i < 5; i++) {
                ProductInfoArray.add(new ArrayList<>());
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
        Object[][] rows = new Object[ProductInfoArray.get(2).size()][7];


        String OrderID = DbInt.getCustInf(year, name, "ORDERID");
        //Defines Arraylist of order quanitities
        int noVRows = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity
        for (int i = 0; i < ProductInfoArray.get(2).size(); i++) {

            int quant = 0;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ORDERS WHERE ORDERID=?")) {

                //prep.setString(1, Integer.toString(i));
                prep.setString(1, OrderID);
                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {
                        quant = Integer.parseInt(rs.getString(String.valueOf(i)));
                        //DbInt.pCon.close();
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Fills row array for table with info


            if (quant > 0) {

                rows[noVRows][0] = ProductInfoArray.get(0).get(i);
                rows[noVRows][1] = ProductInfoArray.get(1).get(i);
                rows[noVRows][2] = ProductInfoArray.get(2).get(i);
                rows[noVRows][3] = ProductInfoArray.get(3).get(i);
                rows[noVRows][4] = quant;
                rows[noVRows][5] = (double) quant * Double.parseDouble(ProductInfoArray.get(3).get(i).replaceAll("\\$", ""));
                totL += ((double) quant * Double.parseDouble(ProductInfoArray.get(3).get(i).replaceAll("\\$", "")));
                QuantL += (double) quant;
                rows[noVRows][6] = ProductInfoArray.get(4).get(i);

                noVRows++;

            }
        }
        //Re create rows to remove blank rows
        Object[][] rowDataF = new Object[noVRows][7];

        for (int i = 0; i <= (noVRows - 1); i++) {
            rowDataF[i][0] = rows[i][0];//Product ID
            rowDataF[i][1] = rows[i][1];//Product Name
            rowDataF[i][2] = rows[i][2];//Unit Size
            rowDataF[i][3] = rows[i][3];//Unit Cost
            rowDataF[i][4] = rows[i][4];//Quantity
            rowDataF[i][5] = rows[i][5]; //cost
            rowDataF[i][6] = rows[i][6]; //cost

        }
        return new orderArray(rowDataF, totL, QuantL);


    }

    public orderArray createOrderArray(String year) {
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
        Object[][] rowData = new Object[colO][7];

        int noRows = 0;
        int productsNamed = 0;
        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet Order = st.executeQuery("SELECT * FROM ORDERS")
        ) {


            ResultSetMetaData rsmd = Order.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
            while (Order.next()) {
                if (productsNamed == 0) {
                    //loop through columns
                    for (int c = 3; c <= columnsNumber; c++) {
                        //Get ID of product
                        List<String> IDL = Product.GetProductInfo("ID", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String ID = IDL.get(IDL.size() - 1);
                        //Get Name of product
                        List<String> productL = Product.GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String productName = productL.get(productL.size() - 1);
                        //Get Name of product
                        List<String> sizeL = Product.GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String size = sizeL.get(sizeL.size() - 1);
                        //Get unit cost of product
                        List<String> UnitL = Product.GetProductInfo("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String Unit = UnitL.get(productL.size() - 1);
                        List<String> CategoryL = Product.GetProductInfo("Category", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String Category = CategoryL.get(productL.size() - 1);
                        //Get Quantity ordered
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);
                        //Calculate total price and overall Total
                        double TPrice = UnitD * quantityD;
                        totL += TPrice;
                        QuantL += quantityD;

                        rowData[noRows][0] = ID;
                        rowData[noRows][1] = productName;
                        rowData[noRows][2] = size;
                        rowData[noRows][3] = Unit;
                        rowData[noRows][4] = quantity;
                        rowData[noRows][5] = TPrice;
                        rowData[noRows][6] = Category;

                        noRows += 1;


                    }
                    productsNamed += 1;
                } else {
                    noRows = 0;
                    for (int c = 3; c <= columnsNumber; c++) {

                        //Get Name of product
                        List<String> productL = Product.GetProductInfo("PNAME", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        //Get unit cost of product
                        List<String> UnitL = Product.GetProductInfo("Unit", Integer.toString(Integer.parseInt(rsmd.getColumnName(c)) + 1), year);
                        String Unit = UnitL.get(productL.size() - 1);
                        //Get Quantity ordered
                        String quantity = Order.getString(c);
                        double UnitD = Double.parseDouble(Unit.replaceAll("\\$", ""));
                        double quantityD = Double.parseDouble(quantity);
                        //Calculate total price and overall Total
                        double TPrice = UnitD * quantityD;
                        totL += TPrice;
                        QuantL += quantityD;


                        rowData[noRows][4] = Double.parseDouble(rowData[noRows][4].toString()) + quantityD;
                        rowData[noRows][5] = Double.parseDouble(rowData[noRows][5].toString()) + TPrice;
                        noRows += 1;

                    }
                }
            }
            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(Reports.class.getName());

            if ((ex.getErrorCode() == 50000)
                    && "XJ015".equals(ex.getSQLState())) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }

        //Limit array to only rows that have ordered stuff
        Object[][] rowDataExclude0 = new Object[noRows][7];
        int NumNonEmptyRows = 0;
        for (int i = 0; i <= (noRows - 1); i++) {
            if (Double.parseDouble(rowData[i][4].toString()) > 0.0) {
                rowDataExclude0[NumNonEmptyRows][0] = rowData[i][0];
                rowDataExclude0[NumNonEmptyRows][1] = rowData[i][1];
                rowDataExclude0[NumNonEmptyRows][2] = rowData[i][2];
                rowDataExclude0[NumNonEmptyRows][3] = rowData[i][3];
                rowDataExclude0[NumNonEmptyRows][4] = rowData[i][4];
                rowDataExclude0[NumNonEmptyRows][5] = rowData[i][5];
                rowDataExclude0[NumNonEmptyRows][6] = rowData[i][6];

                NumNonEmptyRows++;
            }
        }
        //Only show non whitespace rows
        Object[][] rowDataF = new Object[NumNonEmptyRows][7];
        for (int i = 0; i <= (NumNonEmptyRows - 1); i++) {

            rowDataF[i][0] = rowDataExclude0[i][0];//ID
            rowDataF[i][1] = rowDataExclude0[i][1];//Name
            rowDataF[i][2] = rowDataExclude0[i][2];//UnitSize
            rowDataF[i][3] = rowDataExclude0[i][3];//UnitCost
            rowDataF[i][4] = rowDataExclude0[i][4];//Quantity
            rowDataF[i][5] = rowDataExclude0[i][5];//Tcost
            rowDataF[i][6] = rowDataExclude0[i][6];//Tcost


        }
        return new orderArray(rowDataF, totL, QuantL);
    }

    public class orderArray {
        public final Object[][] orderData;
        public double totalCost;
        public double totalQuantity;

        public orderArray(Object[][] orderData, double totalCost, double totalQuantity) {
            this.orderData = orderData;
            this.totalCost = totalCost;
            this.totalQuantity = totalQuantity;
        }
    }
}
