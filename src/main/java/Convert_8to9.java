import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by patrick on 1/26/16.
 */
public class Convert_8to9 {
    private Convert_8to9() {
        System.out.println("Starting");
        Iterable<String> years = getYears();
        Object[][] productData; //this is generic can use String[] directly
        for (String year : years) {
            int noOfProducts = 1;
            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM Products");
                 ResultSet rs = prep.executeQuery()) {
                while (!rs.isLast()) {
                    rs.next();
                }
                noOfProducts = rs.getRow();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            productData = new Object[5][noOfProducts + 1];
            System.out.println("Starting");
            //ID, PName, Unit, Size, Category

            try (PreparedStatement prep = DbInt.getPrep(year, "SELECT * FROM Products");
                 ResultSet rs = prep.executeQuery()) {

                Object rsArray;

                while (rs.next()) {
                    //ID
                    rsArray = rs.getObject(2);
                    productData[0][rs.getRow()] = rsArray;
                    //PNAME
                    rsArray = rs.getObject(3);
                    productData[1][rs.getRow()] = rsArray;
                    //Unit
                    rsArray = rs.getObject(4);
                    productData[2][rs.getRow()] = rsArray;
                    //Size
                    rsArray = rs.getObject(5);
                    productData[3][rs.getRow()] = rsArray;

                }
                ////DbInt.pCon.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            //puts all the customer data into an array
            for (int i = 1; i < productData[2].length; i++) {
                //Category
                productData[4][i] = "";

            }
            //Store settings data

            //Delete Year Customer table

            try (PreparedStatement addCol = DbInt.getPrep(year, "DROP TABLE \"PRODUCTS\"")) {
                addCol.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Recreate Year Customer table

            try (PreparedStatement addCol = DbInt.getPrep(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255), Category VARCHAR(255))")) {
                addCol.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (PreparedStatement prep = DbInt.getPrep("Set", "CREATE TABLE Categories(ID int PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),Name varchar(255), Date DATE)")) {
                prep.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (PreparedStatement addCol = DbInt.getPrep(year, "CREATE TABLE PRODUCTS(PID INTEGER PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),ID VARCHAR(255), PName VARCHAR(255), Unit VARCHAR(255), Size VARCHAR(255), Category VARCHAR(255))")) {
                addCol.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //Write data back to year
            for (int i = 1; i < productData[2].length; i++) {
                try (PreparedStatement addCol = DbInt.getPrep(year, "INSERT INTO PRODUCTS(ID, PName, Unit, Size, Category) VALUES (?,?,?,?,?)")) {
                    addCol.setString(1, productData[0][i].toString());
                    addCol.setString(2, productData[1][i].toString());

                    addCol.setString(3, productData[2][i].toString());
                    addCol.setString(4, productData[3][i].toString());
                    addCol.setString(5, productData[4][i].toString());

                    addCol.execute();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


        }


        System.out.println("Success");
    }

    public static void main(String... args) {
        EventQueue.invokeLater(() -> {
            try {
                Convert_8to9 window = new Convert_8to9();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
    }

    private static Iterable<String> getYears() {
        Collection<String> ret = new ArrayList<>();
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT YEARS FROM Years");
             ResultSet rs = prep.executeQuery()) {


            while (rs.next()) {

                ret.add(rs.getString("YEARS"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }




}
