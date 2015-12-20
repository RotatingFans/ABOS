import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
@SuppressWarnings("unused")
public class DbInt {
    public static Connection pCon = null;

    /**
     * Gets Data with specifed command and DB
     *
     * @param Db      THe database to retireve data from
     * @param command The command To execute
     * @return and ArrayList of the resulting Data
     * @deprecated true
     */
    public static ArrayList<String> getData(String Db, String command) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", new Config().getDbLoc(), Db);
        System.setProperty("derby.system.home",
                new Config().getDbLoc());
        ArrayList<String> res = new ArrayList<String>();
        try {


            command = command.replaceAll("''", "/0027");
            con = DriverManager.getConnection(url);
            st = con.createStatement();
            rs = st.executeQuery(command);
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

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (st != null) {
                    st.close();
                    st = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DbInt.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
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

        PreparedStatement prep = DbInt.getPrep(yearL, "SELECT * FROM CUSTOMERS WHERE NAME=?");
        try {


            prep.setString(1, name);
            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret = rs.getString(info);

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
        PreparedStatement prep = null;
        Statement st = null;
        ResultSet rs = null;
        pCon = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", new Config().getDbLoc(), Db);
        System.setProperty("derby.system.home",
                new Config().getDbLoc());
        ArrayList<String> res = new ArrayList<String>();
        try {


            pCon = DriverManager.getConnection(url);
            pCon.setAutoCommit(true);
            prep = pCon.prepareStatement(Command, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

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

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (st != null) {
                    st.close();
                    st = null;
                }


            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DbInt.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return prep;
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
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", new Config().getDbLoc(), Db);
        System.setProperty("derby.system.home",
                new Config().getDbLoc());
        ArrayList<String> res = new ArrayList<String>();
        try {


            con = DriverManager.getConnection(url);
            st = con.createStatement();
            rs = st.executeQuery(String.format("SELECT * FROM %s", Table));
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

        } finally {

            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (st != null) {
                    st.close();
                    st = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DbInt.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return columnsNumber;
    }

    /**Writes data to A DB
     * @param Db The DB to write to
     * @param command THe command to execute
     * @deprecated true
     */
    public static void writeData(String Db, String command) {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        //String Db = String.format("L&G%3",year);
        String url = String.format("jdbc:derby:%s/%s", new Config().getDbLoc(), Db);
        System.setProperty("derby.system.home",
                new Config().getDbLoc());
        ArrayList<String> res = new ArrayList<String>();
        try {


            command.replaceAll("'", "/0027");
            con = DriverManager.getConnection(url);
            st = con.createStatement();
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

        } finally {

            try {

                if (st != null) {
                    st.close();
                    st = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DbInt.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }


    }

    /**
     * Creates a database with specified name
     *
     * @param DB The name of the DB to create
     */
    public static void createDb(String DB) {
        Connection con = null;
        Statement st = null;

        String url = String.format("jdbc:derby:%s/%s;create=true", new Config().getDbLoc(), DB);//;create=true

        try {


            con = DriverManager.getConnection(url);
            st = con.createStatement();
            //  DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(DbInt.class.getName());

            if (((ex.getErrorCode() == 50000)
                    && ("XJ015".equals(ex.getSQLState())))) {

                lgr.log(Level.INFO, "Derby shut down normally", ex);

            } else {

                lgr.log(Level.SEVERE, ex.getMessage(), ex);
            }

        } finally {

            try {

                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(DbInt.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}