import java.sql.SQLException;

/**
 * Created by patrick on 1/29/17.
 */
public class CommonErrors {
    public static String returnSqlMessage(SQLException e) {
        String retMsg = "";
        switch (e.getSQLState()) {
            case "S0022":
                retMsg = "Database incompatible";
                break;
            default:
                retMsg = "Error utilizing databse. Please try restarting the software.";
                break;

        }
        return retMsg;
    }
}
