import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
class DbInt {
    public static Connection pCon = null;

    /**
     * Gets Data with specifed command and DB
     *
     * @param Db      THe database to retireve data from
     * @param command The command To execute
     * @return and ArrayList of the resulting Data
     * @deprecated true
     */
    @Deprecated
    public static List<String> getData(String Db, String command) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }

        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", Config.getDbLoc(), Db);
        System.setProperty("derby.system.home",
                Config.getDbLoc());
        List<String> res = new ArrayList<String>();
        command = command.replaceAll("''", "/0027");

        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(command)) {


            while (rs.next()) {

                res.add(rs.getString(1).replaceAll("/0027", "'"));

            }
            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }

        return res;
    }

    /**Gets the specified Customer info
     * @param yearL The year to search
     * @param name The customer name
     * @param info The info to search for
     * @return A string with the resulting data
     */
    public static String getCustInf(String yearL, String name, String info) {
        String ret = "";

        try (PreparedStatement prep = DbInt.getPrep(yearL, "SELECT * FROM CUSTOMERS WHERE NAME=?")) {


            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    ret = rs.getString(info);

                }
            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    /**Creates a Prepared statemtn from provided Parameters.
     * @param Db The database to create the statement for
     * @param Command The Base command for the statement
     * @return the PreparedStatemtn that was created.
     */
    public static PreparedStatement getPrep(String Db, String Command) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }
        Statement st = null;
        ResultSet rs = null;
        pCon = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", Config.getDbLoc(), Db);
        System.setProperty("derby.system.home",
                Config.getDbLoc());
        try {


            pCon = DriverManager.getConnection(url);
            pCon.setAutoCommit(true);

            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
                //return rs;


            return pCon.prepareStatement(Command, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }


            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DbInt.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return null;
    }

    /**Gets # of collumns in a table
     * @param Db The DB the table is in
     * @param Table the Table to get number of columns from
     * @return An integer with number of columns
     */
    public static int getNoCol(String Db, String Table) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }
        int columnsNumber = 0;

        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", Config.getDbLoc(), Db);
        System.setProperty("derby.system.home",
                Config.getDbLoc());
        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(String.format("SELECT * FROM %s", Table))) {



            ResultSetMetaData rsmd = rs.getMetaData();

            columnsNumber = rsmd.getColumnCount();
            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }

        return columnsNumber;
    }

    /**Writes data to A DB
     * @param Db The DB to write to
     * @param command THe command to execute
     * @deprecated true
     */
    @Deprecated
    public static void writeData(String Db, String command) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }

        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", Config.getDbLoc(), Db);
        System.setProperty("derby.system.home",
                Config.getDbLoc());
        command.replaceAll("'", "/0027");

        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement()
        ) {

            st.executeUpdate(command);


            // DriverManager.getConnection("jdbc:derby:;shutdown=true");
            //return rs;
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally");

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }


    }

    /**
     * Creates a database with specified name
     *
     * @param DB The name of the DB to create
     */
    public static void createDb(String DB) {


        String url = String.format("jdbc:derby:%s/%s;create=true", Config.getDbLoc(), DB);//;create=true

        try (Connection con = DriverManager.getConnection(url);
             Statement st = con.createStatement()) {


            //  DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally", ex);

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        }
    }

// --Commented out by Inspection START (1/2/2016 12:01 PM):
//    /**
//     * Closes the database connection.
//     */
//    public void close() {
//        try {
//            DriverManager.getConnection("jdbc:derby:;shutdown=true");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }
// --Commented out by Inspection STOP (1/2/2016 12:01 PM)

}