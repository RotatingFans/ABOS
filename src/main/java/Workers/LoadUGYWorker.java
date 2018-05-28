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

package Workers;

import Controllers.UsersGroupsAndYearsController;
import Utilities.*;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import javax.swing.*;

/**
 * @author Patrick Magauran
 */
public class LoadUGYWorker extends Task<Triple<TreeItem<TreeItemPair<String, Pair<String, Object>>>, Map<String, Map<String, User>>, Map<String, ArrayList<Group>>>> {
    private final UsersGroupsAndYearsController UGYController;

    /**
     * Creates an instance of the worker
     *
     * @param UGYController the mainController that started the worker
     */
    public LoadUGYWorker(UsersGroupsAndYearsController UGYController) {
        this.UGYController = UGYController;
    }

    private static void failIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Interrupted while loading application");
        }
    }

    private int IntegerLength(int n) {
        if (n < 100000) {
            // 5 or less
            if (n < 100) {
                // 1 or 2
                if (n < 10) { return 1; } else { return 2; }
            } else {
                // 3 or 4 or 5
                if (n < 1000) { return 3; } else {
                    // 4 or 5
                    if (n < 10000) { return 4; } else { return 5; }
                }
            }
        } else {
            // 6 or more
            if (n < 10000000) {
                // 6 or 7
                if (n < 1000000) { return 6; } else { return 7; }
            } else {
                // 8 to 10
                if (n < 100000000) { return 8; } else {
                    // 9 or 10
                    if (n < 1000000000) { return 9; } else { return 10; }
                }
            }
        }

    }

/*    @Override
    protected void process(List<String> chunks) {
        // Updates the messages text area
        chunks.forEach(StatusLbl::setText);
    }*/

    @Override
    protected Triple<TreeItem<TreeItemPair<String, Pair<String, Object>>>, Map<String, Map<String, User>>, Map<String, ArrayList<Group>>> call() {
        long startTime = System.nanoTime();
        Map<String, Map<String, User>> cachedUsers = new HashMap<>();
        //year -> groups
        Map<String, ArrayList<Group>> cachedGroups = new HashMap<>();
        updateMessage("Loading Data");

        Iterable<String> ret = DbInt.getUserYears();
        TreeItem<TreeItemPair<String, Pair<String, Object>>> root = new TreeItem<>(new TreeItemPair("Root Node", new Pair<String, String>("RootNode", "")));
        cachedGroups.clear();
        cachedUsers.clear();

        ///Select all years
        //Create a button for each year
/*        for (String aRet : ret) {
            JButton b = new JButton(aRet);
            b.addActionListener(e -> {
                //On button click open Utilities.Year window
                new YearWindow(((AbstractButton) e.getSource()).getText());

            });
            panel_1.add(b);
        }*/
        for (String curYear : ret) {
            UsersGroupsAndYearsController.contextTreeItem tIYear = UGYController.new contextTreeItem(curYear, "Year");
            Year year = new Year(curYear);
            User curUser = DbInt.getUser(curYear);
            Iterable<Group> groups = Group.getGroups(curYear);
            for (Group group : groups) {
                UsersGroupsAndYearsController.contextTreeItem tiGroup = UGYController.new contextTreeItem(group.getName(), new Pair<>("Group", group));
                cachedGroups.computeIfPresent(curYear, (k, v) -> {
                    if (!v.contains(group)) {

                        v.add(group);
                    }
                    return v;
                });
                cachedGroups.computeIfAbsent(curYear, k -> {
                    ArrayList<Group> v = new ArrayList();
                    v.add(group);
                    return v;
                });
                Iterable<User> users = group.getUsers();

                for (User user : users) {

                    UsersGroupsAndYearsController.contextTreeItem uManTi = UGYController.new contextTreeItem(user.getFullName() + " (" + user.getUserName() + ")", new Pair<>("User", user));
                    tiGroup.getChildren().add(uManTi);
                    //UserName -> year -> UserObject
                    //private Map<String, Map<String, User>> cachedUsers = new HashMap<>();
                    cachedUsers.computeIfPresent(user.getUserName(), (k, v) -> {
                        v.put(curYear, new User(user.getUserName(), curYear, true));
                        return v;
                    });
                    cachedUsers.computeIfAbsent(user.getUserName(), k -> {
                        Map<String, User> v = new HashMap<>();
                        v.put(curYear, new User(user.getUserName(), curYear, true));

                        return v;
                    });
                }
                tIYear.getChildren().add(tiGroup);

            }
            root.getChildren().add(tIYear);


        }
        DbInt.getUsers().forEach(user -> {
            cachedUsers.computeIfAbsent(user.getUserName(), k -> {
                Map<String, User> v = new HashMap<>();
                v.put("DB", user);
                return v;
            });
            cachedUsers.computeIfPresent(user.getUserName(), (k, v) -> {
                v.put("DB", user);
                return v;
            });
        });
        long endTime = System.nanoTime();
        double totalTime = (endTime - startTime) / 1000000;
        //System.out.println("Inital load took " + totalTime + "ms");
        LogToFile.log(null, Severity.FINEST, "Inital load took " + totalTime + "ms");
        return new Triple<TreeItem<TreeItemPair<String, Pair<String, Object>>>, Map<String, Map<String, User>>, Map<String, ArrayList<Group>>>() {
            @Override
            public TreeItem<TreeItemPair<String, Pair<String, Object>>> getLeft() {
                return root;
            }

            @Override
            public Map<String, Map<String, User>> getMiddle() {
                return cachedUsers;
            }

            @Override
            public Map<String, ArrayList<Group>> getRight() {
                return cachedGroups;
            }
        };
    }
}