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

package Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class Group {
    private String name;
    private String year;
    private int id;
    public Group(String name, String year) {
        this.name = name;
        this.year = year;
    }

    public Group(int id, String year) {
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM groups WHERE ID=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {


                while (rs.next()) {

                    this.name = rs.getString("Name");
                }
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        this.id = id;
        this.year = year;
    }

    public static Iterable<Group> getGroups(String year) {
        ArrayList<Group> groups = new ArrayList<>();
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM groups", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {

                groups.add(new Group(rs.getString("Name"), year));
                ////Utilities.DbInt.pCon.close();
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return groups;

    }

    public static ArrayList<Group> getGroupCollection(String year) {
        ArrayList<Group> groups = new ArrayList<>();
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM groups", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = prep.executeQuery()) {
            while (rs.next()) {

                groups.add(new Group(rs.getString("Name"), year));
                ////Utilities.DbInt.pCon.close();
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        return groups;

    }
    public Iterable<User> getUsers() {
        ArrayList<User> groups = new ArrayList<>();
        if (Objects.equals(name, "Ungrouped")) {
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT * FROM users WHERE groupId IS NULL", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                try (ResultSet rs = prep.executeQuery()) {


                    while (rs.next()) {

                        groups.add(new User(rs.getString("userName"), year));
                        ////Utilities.DbInt.pCon.close();
                    }
                }
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
        }
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM users WHERE groupId=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setInt(1, getID());

            try (ResultSet rs = prep.executeQuery()) {


                while (rs.next()) {

                    groups.add(new User(rs.getString("userName"), year, true));
                    ////Utilities.DbInt.pCon.close();
                }
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        } catch (GroupNotFoundException e) {
            LogToFile.log(e, Severity.WARNING, "Group not found. Please retry the action.");
        }
        return groups;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public int getID() throws GroupNotFoundException {
        int gID = -1;
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("SELECT * FROM groups WHERE Name=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            prep.setString(1, name);
            try (ResultSet rs = prep.executeQuery()) {


                while (rs.next()) {

                    gID = rs.getInt("ID");
                    ////Utilities.DbInt.pCon.close();
                }
            }
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        if (gID < 0) {
            throw new GroupNotFoundException();
        }
        return gID;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        Group other = (Group) obj;
        return (this.getID() == (other.getID())) && (this.getName().equals(other.getName()));
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public String toString() {
        return name;
    }

    public class GroupNotFoundException extends RuntimeException {
        public GroupNotFoundException() {
            super();
        }
    }
}
