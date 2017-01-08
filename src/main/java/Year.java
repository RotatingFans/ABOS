import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by patrick on 7/27/16.
 */
public class Year {
    private final String year;

    public Year(String year) {

        this.year = year;
    }

    public Iterable<String> getCustomerNames() {
        Collection<String> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("NAME"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    public String getTots(String info) {
        String ret = "";

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM TOTALS");
             ResultSet rs = prep.executeQuery()
        ) {

            //prep.setString(1, info);


            while (rs.next()) {

                ret = rs.getString(info);

            }
            //////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Gets the Total Donations Using getTots Function
     *
     * @return The total donation amount
     */
    public String getDonations() {
        return getTots("Donations");
    }

    /**
     * Gets the Total Lawn ANd Garden quantities Using getTots Function
     *
     * @return The total Lawn ANd Garden quantities amount
     */
    public String getLG() {
        return getTots("LG");
    }

    /**
     * Gets the Total Live Plants quantities Using getTots Function
     *
     * @return The total Live Plants quantities amount
     */
    public String getLP() {
        return getTots("LP");
    }

    /**
     * Gets the Total Mulch quantities Using getTots Function
     *
     * @return The total Mulch quantities amount
     */
    public String getMulch() {
        return getTots("MULCH");
    }

    /**
     * Gets the order Total Using getTots Function
     *
     * @return The Order total amount
     */
    public String getOT() {
        return getTots("TOTAL");
    }

    /**
     * Gets the Total Customer Using getTots Function
     *
     * @return The total amount of Customers
     */
    public String getNoCustomers() {
        return getTots("CUSTOMERS");
    }

    /**
     * Gets the Total Commissions Using getTots Function
     *
     * @return The total Commissions amount
     */
    public String getCommis() {
        return getTots("COMMISSIONS");
    }

    /**
     * Gets the Grand Total Using getTots Function
     *
     * @return The Grand total amount
     */
    public String getGTot() {
        return (Objects.equals(getTots("GRANDTOTAL"), "")) ? ("0") : getTots("GRANDTOTAL");
    }

    public Product.formattedProduct[] getAllProducts() {
        //String[] toGet = {"ID", "PNAME", "SIZE", "UNIT"};
        List<Product.formattedProduct> ProductInfoArray = new ArrayList<>(); //Single array to store all data to add to table.
        //Get a prepared statement to retrieve data

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS");
             ResultSet ProductInfoResultSet = prep.executeQuery()) {
            //Run through Data set and add info to ProductInfoArray
            while (ProductInfoResultSet.next()) {

                ProductInfoArray.add(new Product.formattedProduct(ProductInfoResultSet.getString("ID"), ProductInfoResultSet.getString("PNAME"), ProductInfoResultSet.getString("SIZE"), ProductInfoResultSet.getString("UNIT"), ProductInfoResultSet.getString("Category"), 0, 0.0));
            }
            DbInt.pCon.commit();
            ////DbInt.pCon.close();


            //Close prepared statement
            ProductInfoResultSet.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ProductInfoArray.toArray(new Product.formattedProduct[ProductInfoArray.size()]);

    }
}