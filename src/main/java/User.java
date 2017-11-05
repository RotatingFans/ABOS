/*
 * Copyright (c) Patrick Magauran 2017.
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    private String fullName;
    private ArrayList<String> uManage = new ArrayList<>();
    private int groupId;
    private String userName;

    public User(String userName, String fullName, ArrayList<String> uManage, int groupId) {
        this.userName = userName;
        this.fullName = fullName;
        this.uManage = uManage;
        this.groupId = groupId;
    }

    public User(String userName, String fullName, String uManage, int groupId) {
        this.userName = userName;
        this.fullName = fullName;
        List<String> retL = new ArrayList<String>(Arrays.asList(uManage.split("\\s*,\\s*")));
        retL.forEach(uName -> {
            if (!uName.isEmpty()) {
                this.uManage.add(uName);
            }
        });
        this.groupId = groupId;
    }

    public static void createUser(String uName, String password) {
        String createAndGrantCommand = "CREATE USER '" + uName + "'@'%' IDENTIFIED BY '" + password + "'";
        try (PreparedStatement prep = DbInt.getPrep("")) {
            prep.addBatch(createAndGrantCommand);
            prep.addBatch("GRANT SELECT ON `" + DbInt.prefix + "Commons`.* TO '" + uName + "'@'%'");
            prep.executeBatch();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        try (PreparedStatement prep = DbInt.getPrep("Commons", "INSERT INTO Users(userName, Years) Values (?, '')")) {
            prep.setString(1, uName);
            prep.execute();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public ArrayList<String> getuManage() {
        return uManage;
    }

    public int getGroupId() {
        return groupId;
    }

    public void addToYear(String year) {
        String[] createAndGrantCommand = {"GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.customerview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.orderedproductsview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.ordersview TO '" + userName + "'@'%'",
                "GRANT SELECT, INSERT, UPDATE, DELETE ON `" + DbInt.prefix + year + "`.usersview TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.products TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.groups TO '" + userName + "'@'%'",
                "GRANT SELECT ON `" + DbInt.prefix + year + "`.categories TO '" + userName + "'@'%'"};
        try (PreparedStatement prep = DbInt.getPrep("")) {
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
        try (PreparedStatement prep = DbInt.getPrep("Commons", "UPDATE Users SET Years=CONCAT(Years, ',', ?) WHERE userName=?")) {
            prep.setString(1, year);
            prep.setString(2, userName);
            prep.execute();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        Integer CommonsID = 0;
        try (PreparedStatement prep = DbInt.getPrep("Commons", "SELECT idUsers FROM Users where userName=?")) {
            prep.setString(1, userName);
            try (ResultSet rs = prep.executeQuery()) {
                rs.next();
                CommonsID = rs.getInt("idUsers");
            }

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }

        try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO users(userName, fullName, uManage, Admin, commonsID, groupId) VALUES(?,?,?,?,?,?)")) {
            prep.setString(1, userName);
            prep.setString(2, fullName);
            prep.setString(3, arrayToCSV(uManage));
            prep.setInt(4, 0);
            prep.setInt(5, CommonsID);
            prep.setInt(6, groupId);
            prep.execute();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    private String arrayToCSV(ArrayList<String> array) {
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
