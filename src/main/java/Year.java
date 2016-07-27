import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by patrick on 7/27/16.
 */
public class Year {
    public static Iterable<String> getCustomers(String year) {
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

}
