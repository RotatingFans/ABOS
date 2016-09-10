import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patrick on 7/27/16.
 */
public class Product {
    public final String productID;
    public final String productName;
    public final String productSize;
    public final String productUnitPrice;
    public final String productCategory;

    public Product(String productID, String productName, String productSize, String productUnitPrice, String productCategory) {
        this.productID = productID;
        this.productName = productName;
        this.productSize = productSize;
        this.productUnitPrice = productUnitPrice;
        this.productCategory = productCategory;
    }

    public static List<String> GetProductInfo(String info, String PID, String year) {
        List<String> ret = new ArrayList<>();

        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM PRODUCTS WHERE PID=?")) {


            prep.setString(1, PID);

            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret.add(rs.getString(info));

                }
            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static class formattedProduct {
        public final String productID;
        public final String productName;
        public final String productSize;
        public final String productUnitPrice;
        public final String productCategory;
        public final int orderedQuantity;
        public final Double extendedCost;

        public formattedProduct(String productID, String productName, String productSize, String productUnitPrice, String productCategory, int orderedQuantity, Double extendedCost) {
            this.productID = productID;
            this.productName = productName;
            this.productSize = productSize;
            this.productUnitPrice = productUnitPrice;
            this.productCategory = productCategory;
            this.orderedQuantity = orderedQuantity;
            this.extendedCost = extendedCost;
        }
    }
}
