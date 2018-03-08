/*
 * Copyright (c) Patrick Magauran 2018.
 *   Licensed under the AGPLv3. All conditions of said license apply.
 *       This file is part of ABOS.
 *
 *       ABOS is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Affero General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       ABOS is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Affero General Public License for more details.
 *
 *       You should have received a copy of the GNU Affero General Public License
 *       along with ABOS.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class User {
    private String fullName;
    private ArrayList<String> uManage = new ArrayList<>();
    private int groupId = 1;
    private String userName;
    private Boolean Admin = false;
    private Set<String> years = new HashSet<>();
    private int ACL = 1;

    public User(String year) {
        this(DbInt.getUserName(), year);

    }

    public User(String userName, String year) {
        this(userName, year, false);
    }

    public User(String userName, String year, Boolean admin) {
        this.userName = userName;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM " + (admin ? "users" : "usersview") + " where userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, userName);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    this.fullName = rs.getString("fullName");
                    List<String> retL = new ArrayList<String>(Arrays.asList(rs.getString("uManage").split("\\s*,\\s*")));
                    retL.forEach(uName -> {
                        if (!uName.isEmpty()) {
                            this.uManage.add(uName);
                        }
                    });
                    this.groupId = rs.getInt("groupId");
                    this.Admin = rs.getInt("Admin") > 0;
                }
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        String csvRet = "";
        Collection<String> ret = new ArrayList<>();

        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("SELECT YEARS FROM Users where userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, userName);
            try (ResultSet rs = prep.executeQuery()) {

                while (rs.next()) {

                    csvRet = (rs.getString("YEARS"));

                }
            }
            ////DbInt.pCon.close()

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        List<String> yearsL = new ArrayList<String>(Arrays.asList(csvRet.split("\\s*,\\s*")));
        yearsL.forEach(uName -> {
            if (!uName.isEmpty()) {
                this.years.add(uName);
            }
        });

    }

    public User(String userName, String fullName, ArrayList<String> uManage, int groupId) {
        this(userName, fullName, uManage, null, groupId);
    }

    public User(String userName, String fullName, ArrayList<String> uManage, Set<String> years, int groupId) {
        this(userName, fullName, uManage, null, false, groupId);
    }

    public User(String userName, String fullName, ArrayList<String> uManage, Set<String> years, Boolean admin, int groupId) {
        this.userName = userName;
        this.fullName = fullName;
        this.uManage = uManage;
        this.groupId = groupId;
        this.years = years;
        this.Admin = admin;
    }

    public User(String userName, String fullName, String uManage, int groupId) {
        this(userName, fullName, uManage, "", groupId);

    }

    public User(String userName, String fullName, String years, boolean admin) {
        this(userName, fullName, "", years, admin, 1);

    }

    public User(String userName, String fullName, String uManage, String years, int groupId) {
        this(userName, fullName, uManage, years, false, groupId);
    }

    public User(String userName, String fullName, String uManage, String years, boolean admin, int groupId) {
        this.userName = userName;
        this.fullName = fullName;
        List<String> retL = new ArrayList<String>(Arrays.asList(uManage.split("\\s*,\\s*")));
        retL.forEach(uName -> {
            if (!uName.isEmpty()) {
                this.uManage.add(uName);
            }
        });
        List<String> yearsL = new ArrayList<String>(Arrays.asList(years.split("\\s*,\\s*")));
        yearsL.forEach(uName -> {
            if (!uName.isEmpty()) {
                this.years.add(uName);
            }
        });
        this.groupId = groupId;
        this.Admin = admin;
    }

    public static User createUser(String uName, String password, String fullName, Boolean admin) {
        String createAndGrantCommand = "CREATE USER '" + uName + "'@'%' IDENTIFIED BY '" + password + "'";
        try (Connection con = DbInt.getConnection();
             PreparedStatement prep = con.prepareStatement("", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            prep.addBatch(createAndGrantCommand);
            prep.addBatch("GRANT SELECT ON `" + DbInt.prefix + "Commons`.* TO '" + uName + "'@'%'");
            if (admin) {
                prep.addBatch("GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, RELOAD, INDEX, ALTER, SHOW DATABASES, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, CREATE USER, TRIGGER, SUPER ON *.* TO '" + uName + "'@'%' WITH GRANT OPTION");
            }
            prep.executeBatch();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("INSERT INTO Users(userName, fullName, Admin,  Years) Values (?,?,?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, uName);
            prep.setString(2, fullName);
            prep.setInt(3, admin ? 1 : 0);
            prep.setString(4, "");

            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return new User(uName, fullName, "", admin);

    }

    public static void updateUser(String uName, String password) {
        //TODO mysql version
        // String createAndGrantCommand = "ALTER USER '" + uName + "'@'%' IDENTIFIED BY '" + password + "'";
        String createAndGrantCommand = "SET PASSWORD FOR '" + uName + "'@'%' = PASSWORD('" + password + "')";

        try (Connection con = DbInt.getConnection();
             PreparedStatement prep = con.prepareStatement("", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.addBatch(createAndGrantCommand);
            prep.executeBatch();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }


    }

    public void addToYear(String year) {
        String[] createAndGrantCommand = {"GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.customerview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.orderedproductsview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.ordersview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.usersview TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.products TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.groups TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.categories TO '" + userName + "'@'%'"};
        try (Connection con = DbInt.getConnection();
             PreparedStatement prep = con.prepareStatement("", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            prep.addBatch(createAndGrantCommand[0]);
            prep.addBatch(createAndGrantCommand[1]);
            prep.addBatch(createAndGrantCommand[2]);
            prep.addBatch(createAndGrantCommand[3]);
            prep.addBatch(createAndGrantCommand[4]);
            prep.addBatch(createAndGrantCommand[5]);
            prep.addBatch(createAndGrantCommand[6]);

            prep.executeBatch();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("UPDATE Users SET Years=CONCAT(Years, ',', ?) WHERE userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, year);
            prep.setString(2, userName);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        Integer CommonsID = 0;
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("SELECT idUsers FROM Users where userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, userName);
            try (ResultSet rs = prep.executeQuery()) {
                rs.next();
                CommonsID = rs.getInt("idUsers");
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("INSERT INTO users(userName, fullName, uManage, Admin, commonsID, groupId) VALUES(?,?,?,?,?,?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, userName);
            prep.setString(2, fullName);
            prep.setString(3, arrayToCSV(uManage));
            prep.setInt(4, Admin ? 1 : 0);
            prep.setInt(5, CommonsID);
            prep.setInt(6, groupId);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public void updateYear(String year) {
        String[] createAndGrantCommand = {"GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.customerview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.orderedproductsview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.ordersview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.usersview TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.products TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.groups TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.categories TO '" + userName + "'@'%'"};
        try (Connection con = DbInt.getConnection();
             PreparedStatement prep = con.prepareStatement("", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            prep.addBatch(createAndGrantCommand[0]);
            prep.addBatch(createAndGrantCommand[1]);
            prep.addBatch(createAndGrantCommand[2]);
            prep.addBatch(createAndGrantCommand[3]);
            prep.addBatch(createAndGrantCommand[4]);
            prep.addBatch(createAndGrantCommand[5]);
            prep.addBatch(createAndGrantCommand[6]);

            prep.executeBatch();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("UPDATE Users SET Years=?, fullName=?, Admin=? WHERE userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, arrayToCSV(years));
            prep.setString(2, fullName);
            prep.setInt(3, Admin ? 1 : 0);
            prep.setString(4, userName);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        Integer CommonsID = 0;
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("SELECT idUsers FROM Users where userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, userName);
            try (ResultSet rs = prep.executeQuery()) {
                rs.next();
                CommonsID = rs.getInt("idUsers");
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        String oldUName = userName;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT userName FROM users where commonsID=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, CommonsID);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    oldUName = rs.getString("userName");
                }
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("INSERT INTO users(userName, fullName, uManage, Admin, commonsID, groupId, ACL) VALUES(?,?,?,?,?,?, 1) " +
                     "ON DUPLICATE KEY UPDATE userName=?, fullName=?, uManage=?, Admin=?, commonsID=?, groupId=?, ACL=1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, userName);
            prep.setString(2, fullName);
            prep.setString(3, arrayToCSV(uManage));
            prep.setInt(4, Admin ? 1 : 0);
            prep.setInt(5, CommonsID);
            prep.setInt(6, groupId);
            prep.setString(7, userName);
            prep.setString(8, fullName);
            prep.setString(9, arrayToCSV(uManage));
            prep.setInt(10, Admin ? 1 : 0);
            prep.setInt(11, CommonsID);
            prep.setInt(12, groupId);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("UPDATE users SET uManage = REPLACE (uManage, ?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, oldUName);
            prep.setString(2, userName);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public void removeFromYear(String year) {
        if (years.remove(year) && !isAdmin()) {
            String[] createAndGrantCommand = {"REVOKE SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.customerview FROM '" + userName + "'@'%'",
                    "REVOKE SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.orderedproductsview FROM '" + userName + "'@'%'",
                    "REVOKE SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.ordersview FROM '" + userName + "'@'%'",
                    "REVOKE SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.usersview FROM '" + userName + "'@'%'",
                    "REVOKE SELECT ON `" + DbInt.prefix + year + "`.products FROM '" + userName + "'@'%'",
                    "REVOKE SELECT ON `" + DbInt.prefix + year + "`.groups FROM '" + userName + "'@'%'",
                    "REVOKE SELECT ON `" + DbInt.prefix + year + "`.categories FROM '" + userName + "'@'%'"};
            try (Connection con = DbInt.getConnection();
                 PreparedStatement prep = con.prepareStatement("", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                prep.addBatch(createAndGrantCommand[0]);
                prep.addBatch(createAndGrantCommand[1]);
                prep.addBatch(createAndGrantCommand[2]);
                prep.addBatch(createAndGrantCommand[3]);
                prep.addBatch(createAndGrantCommand[4]);
                prep.addBatch(createAndGrantCommand[5]);
                prep.addBatch(createAndGrantCommand[6]);

                prep.executeBatch();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        }
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("UPDATE Users SET Years=? WHERE userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, arrayToCSV(years));
            prep.setString(2, userName);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        Integer CommonsID = 0;
        try (Connection con = DbInt.getConnection("Commons");
             PreparedStatement prep = con.prepareStatement("SELECT idUsers FROM Users where userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, userName);
            try (ResultSet rs = prep.executeQuery()) {
                rs.next();
                CommonsID = rs.getInt("idUsers");
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        String oldUName = userName;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT userName FROM users where commonsID=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, CommonsID);
            try (ResultSet rs = prep.executeQuery()) {
                if (rs.next()) {
                    oldUName = rs.getString("userName");
                }
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("UPDATE users set userName=?, fullName=?, uManage=?, Admin=?, commonsID=?, groupId=?, ACL=0 WHERE userName=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, userName);
            prep.setString(2, fullName);
            prep.setString(3, arrayToCSV(uManage));
            prep.setInt(4, Admin ? 1 : 0);
            prep.setInt(5, CommonsID);
            prep.setInt(6, groupId);
            prep.setString(7, oldUName);


            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("UPDATE users SET uManage = REPLACE (uManage, ?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, oldUName);
            prep.setString(2, userName);
            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public String toString() {
        return fullName + " (" + userName + ")";
    }

    public boolean isAdmin() {
        return Admin;
    }

    public void setAdmin(Boolean admin) {
        Admin = admin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public ArrayList<String> getuManage() {
        return uManage;
    }

    public void setuManage(ArrayList<String> uManage) {
        this.uManage = uManage;
    }

    public Set<String> getYears() {
        return years;
    }

    public void setYears(Set<String> years) {
        this.years = years;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    private String arrayToCSV(Collection<String> array) {
        final String[] ret = {""};
        array.forEach(value -> {
            if (!ret[0].isEmpty()) {
                ret[0] = ret[0] + "," + value;
            } else {
                ret[0] = value;
            }
        });
        return ret[0];
    }


}
