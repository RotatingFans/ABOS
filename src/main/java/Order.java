import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 7/27/16.
 */
public class Order {
    // --Commented out by Inspection (7/27/16 3:02 PM):Product product = new Product();
    private double totL = 0.0;
    private double QuantL = 0.0;

    public orderArray createOrderArray(String year, String name, Boolean excludeZeroOrders) {

        List<Product> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {
                ProductInfoArray.add(new Product(ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("PNAME"), ProductInfoResultSet.getString("SIZE"), ProductInfoResultSet.getString("UNIT"), ProductInfoResultSet.getString("Category")));
                DbInt.pCon.commit();

            }
            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        //Table rows array
        Product.formattedProduct[] allProducts = new Product.formattedProduct[ProductInfoArray.size()];
        String OrderID = DbInt.getCustInf(year, name, "ORDERID");
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity
        for (int i = 0; i < ProductInfoArray.size(); i++) {

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
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Fills row array for table with info


            if (((quant > 0) && excludeZeroOrders) || !excludeZeroOrders) {
                Double unitCost = Double.parseDouble(ProductInfoArray.get(i).productUnitPrice.replaceAll("\\$", ""));
                allProducts[noProductsOrdered] = new Product.formattedProduct(ProductInfoArray.get(i).productID, ProductInfoArray.get(i).productName, ProductInfoArray.get(i).productSize, ProductInfoArray.get(i).productUnitPrice, ProductInfoArray.get(i).productCategory, quant, quant * unitCost);
                totL += ((double) quant * unitCost);
                QuantL += (double) quant;
                noProductsOrdered++;

            }
        }
        //Re create rows to remove blank rows
        Product.formattedProduct[] orderedProducts = new Product.formattedProduct[noProductsOrdered];

        System.arraycopy(allProducts, 0, orderedProducts, 0, noProductsOrdered);
        return new orderArray(orderedProducts, totL, QuantL);


    }

    public orderArray createOrderArray(String year) {

        List<Product> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {
                ProductInfoArray.add(new Product(ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("PNAME"), ProductInfoResultSet.getString("SIZE"), ProductInfoResultSet.getString("UNIT"), ProductInfoResultSet.getString("Category")));
                DbInt.pCon.commit();

            }
            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        //Table rows array
        Product.formattedProduct[] allProducts = new Product.formattedProduct[ProductInfoArray.size()];
        //Defines Arraylist of order quanitities
        int noProductsOrdered = 0;
        //Fills OrderQuantities Array
        //For Each product get quantity
        for (int i = 0; i < ProductInfoArray.size(); i++) {

            int quant = 0;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM ORDERS")) {

                //prep.setString(1, Integer.toString(i));
                try (ResultSet rs = prep.executeQuery()) {

                    while (rs.next()) {
                        quant = Integer.parseInt(rs.getString(String.valueOf(i)));
                        //DbInt.pCon.close();
                    }
                }

            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            //Fills row array for table with info


            if (quant > 0) {
                Double unitCost = Double.parseDouble(ProductInfoArray.get(i).productUnitPrice.replaceAll("\\$", ""));
                allProducts[noProductsOrdered] = new Product.formattedProduct(ProductInfoArray.get(i).productID, ProductInfoArray.get(i).productName, ProductInfoArray.get(i).productSize, ProductInfoArray.get(i).productUnitPrice, ProductInfoArray.get(i).productCategory, quant, quant * unitCost);
                totL += ((double) quant * unitCost);
                QuantL += (double) quant;
                noProductsOrdered++;

            }
        }
        //Re create rows to remove blank rows
        Product.formattedProduct[] orderedProducts = new Product.formattedProduct[noProductsOrdered];

        System.arraycopy(allProducts, 0, orderedProducts, 0, noProductsOrdered);
        return new orderArray(orderedProducts, totL, QuantL);
    }

    public static class orderArray {
        public final Product.formattedProduct[] orderData;
        public double totalCost;
        public double totalQuantity;

        public orderArray(Product.formattedProduct[] orderData, double totalCost, double totalQuantity) {
            this.orderData = orderData;
            this.totalCost = totalCost;
            this.totalQuantity = totalQuantity;
        }
    }
}
