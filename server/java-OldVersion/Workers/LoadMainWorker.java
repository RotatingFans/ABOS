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

package Workers;

import Controllers.MainController;
import Utilities.*;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.util.Pair;

import java.util.Objects;

//import javax.swing.*;

/**
 * @author Patrick Magauran
 */
public class LoadMainWorker extends Task<TreeItem<TreeItemPair<String, Pair<String, Object>>>> {
    private final MainController mainController;

    /**
     * Creates an instance of the worker
     *
     * @param mainController the mainController that started the worker
     */
    public LoadMainWorker(MainController mainController) {
        this.mainController = mainController;
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
                if (n < 10) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                // 3 or 4 or 5
                if (n < 1000) {
                    return 3;
                } else {
                    // 4 or 5
                    if (n < 10000) {
                        return 4;
                    } else {
                        return 5;
                    }
                }
            }
        } else {
            // 6 or more
            if (n < 10000000) {
                // 6 or 7
                if (n < 1000000) {
                    return 6;
                } else {
                    return 7;
                }
            } else {
                // 8 to 10
                if (n < 100000000) {
                    return 8;
                } else {
                    // 9 or 10
                    if (n < 1000000000) {
                        return 9;
                    } else {
                        return 10;
                    }
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
    protected TreeItem<TreeItemPair<String, Pair<String, Object>>> call() {
        long startTime = System.nanoTime();

        updateMessage("Loading Data");

        Iterable<String> ret = DbInt.getUserYears();
        TreeItem<TreeItemPair<String, Pair<String, Object>>> root = new TreeItem<>(new TreeItemPair("Root Node", new Pair<String, String>("RootNode", "")));
        MainController.contextTreeItem userRoot = mainController.new contextTreeItem("Groups/Users", new Pair<String, String>("RootNode", ""));
        root.getChildren().add(mainController.new contextTreeItem("Reports", "Window"));
        root.getChildren().add(mainController.new contextTreeItem("View Map", "Window"));
        root.getChildren().add(mainController.new contextTreeItem("Preferences", "Window"));
        if (DbInt.isAdmin()) {
            root.getChildren().add(mainController.new contextTreeItem("Users Groups & Years", "Window"));
        }


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
            MainController.contextTreeItem tIYear = mainController.new contextTreeItem(curYear, "Year");
            Year year = new Year(curYear);
            User curUser = DbInt.getUser(curYear);
            Iterable<String> uManage = curUser.getuManage();
            for (String uMan : uManage) {
                MainController.contextTreeItem uManTi = mainController.new contextTreeItem(uMan, new Pair<>("UserCustomerView", curYear));

                Iterable<Customer> customers = year.getCustomers(uMan);
                for (Customer customer : customers) {
                    uManTi.getChildren().add(mainController.new contextTreeItem(customer.getName(), new Pair<String, Customer>("Customer", customer)));
                }
                uManTi.getChildren().add(mainController.new contextTreeItem("Add Customer", new Pair<String, Pair<String, String>>("Window", new Pair<String, String>(curYear, uMan))));
                if (Objects.equals(uMan, curUser.getUserName())) {
                    tIYear.getChildren().addAll(uManTi.getChildren());
                } else {
                    tIYear.getChildren().add(uManTi);
                }
            }
            root.getChildren().add(tIYear);


        }
        long endTime = System.nanoTime();
        double totalTime = (endTime - startTime) / 1000000;
        //System.out.println("Inital load took " + totalTime + "ms");
        LogToFile.log(null, Severity.FINEST, "Inital load took " + totalTime + "ms");
        return root;
    }
}