/*******************************************************************************
 * ABOS
 * Copyright (C) 2018 Patrick Magauran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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

    //private int id;
    private Utilities.Settable<Integer> id = new Utilities.Settable(-1, -1);
    private Utilities.Settable<ArrayList<User>> groupUsers = new Utilities.Settable(null, null);

    public Group(String name, String year) {
        this.name = name;
        this.year = year;
    }

    public Group(String name, String year, int id, ArrayList<User> uMan) {
        this.name = name;
        this.year = year;
        this.id.set(id);
        this.groupUsers.set(uMan);
    }

    public Group(int id, String year) {
        this.id.orElseGetAndSet(() -> {
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT * FROM groups WHERE ID=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, id);
                try (ResultSet rs = prep.executeQuery()) {


                    while (rs.next()) {

                        this.name = rs.getString("Name");
                    }
                }
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            return id;
        });
        groupUsers.orElseGetAndSet(() -> {
            ArrayList<User> users = new ArrayList<>();
            if (Objects.equals(name, "Ungrouped")) {
                try (Connection con = DbInt.getConnection(year);
                     PreparedStatement prep = con.prepareStatement("SELECT * FROM users WHERE groupId IS NULL", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    try (ResultSet rs = prep.executeQuery()) {


                        while (rs.next()) {

                            users.add(new User(rs.getString("userName"), year));
                            ////Utilities.DbInt.pCon.close();
                        }
                    }
                } catch (SQLException e) {
                    LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                }
            }
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("SELECT * FROM users WHERE groupId=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setInt(1, id);

                try (ResultSet rs = prep.executeQuery()) {


                    while (rs.next()) {

                        users.add(new User(rs.getString("userName"), year));
                        ////Utilities.DbInt.pCon.close();
                    }
                }
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            } catch (GroupNotFoundException e) {
                LogToFile.log(e, Severity.WARNING, "Group not found. Please retry the action.");
            }
            return users;
        });
        this.year = year;
    }

    public static Iterable<Group> getGroups(String year) {
        ArrayList<Group> groups = new ArrayList<>();
        String name;

        //private int id;
        Integer id;
        ArrayList<User> groupUsers = new ArrayList<>();
        try (Connection con2 = DbInt.getConnection(year);
             PreparedStatement prep2 = con2.prepareStatement("SELECT * FROM groups", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            try (ResultSet rs2 = prep2.executeQuery()) {


                while (rs2.next()) {

                    name = rs2.getString("Name");
                    id = rs2.getInt("ID");
                    ArrayList<User> users = new ArrayList<>();
                    if (Objects.equals(name, "Ungrouped")) {
                        try (Connection con = DbInt.getConnection(year);
                             PreparedStatement prep = con.prepareStatement("SELECT * FROM users WHERE groupId IS NULL", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                            try (ResultSet rs = prep.executeQuery()) {


                                while (rs.next()) {

                                    users.add(new User(rs.getString("userName"), year));
                                    ////Utilities.DbInt.pCon.close();
                                }
                            }
                        } catch (SQLException e) {
                            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                        }
                    }
                    try (Connection con = DbInt.getConnection(year);
                         PreparedStatement prep = con.prepareStatement("SELECT * FROM users WHERE groupId=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                        prep.setInt(1, id);

                        try (ResultSet rs = prep.executeQuery()) {


                            while (rs.next()) {

                                users.add(new User(rs.getString("userName"), year));
                                ////Utilities.DbInt.pCon.close();
                            }
                        }
                    } catch (SQLException e) {
                        LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                    } catch (GroupNotFoundException e) {
                        LogToFile.log(e, Severity.WARNING, "Group not found. Please retry the action.");
                    }

                    groups.add(new Group(name, year, id, users));
                }
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
        return groupUsers.orElseGetAndSet(() -> {
            ArrayList<User> users = new ArrayList<>();
            if (Objects.equals(name, "Ungrouped")) {
                try (Connection con = DbInt.getConnection(year);
                     PreparedStatement prep = con.prepareStatement("SELECT * FROM users WHERE groupId IS NULL", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                    try (ResultSet rs = prep.executeQuery()) {


                        while (rs.next()) {

                            users.add(new User(rs.getString("userName"), year));
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

                        users.add(new User(rs.getString("userName"), year));
                        ////Utilities.DbInt.pCon.close();
                    }
                }
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            } catch (GroupNotFoundException e) {
                LogToFile.log(e, Severity.WARNING, "Group not found. Please retry the action.");
            }
            return users;
        });

    }

    public void removeGroup() {
        getUsers().forEach(user -> {
            user.setGroupId(1);
            user.updateYear(year);
        });
        try (Connection con = DbInt.getConnection(year);
             PreparedStatement prep = con.prepareStatement("DELETE FROM groups WHERE ID=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            prep.setInt(1, getID());

            prep.execute();
        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public int getID() throws GroupNotFoundException {
        return id.orElseGetAndSet(() -> {
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
        });

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
