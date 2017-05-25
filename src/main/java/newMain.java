/*
 * Copyright (c) Patrick Magauran 2017.
 * Licensed under the AGPLv3. All conditions of said license apply.
 *     This file is part of LawnAndGarden.
 *
 *     LawnAndGarden is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     LawnAndGarden is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with LawnAndGarden.  If not, see <http://www.gnu.org/licenses/>.
 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Sample application to demonstrate programming an FXML interface.
 */
public class newMain extends Application {
    @FXML
    private TreeView<String> selectNav;

    // main method is only for legacy support - java 8 won't call it for a javafx application.
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(final Stage stage) throws Exception {
        // load the scene fxml UI.
        // grabs the UI scenegraph view from the loader.
        // grabs the UI controller for the view from the loader.
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("UI/Main.fxml"));
        final Parent root = loader.load();
        final MyControllerClass controller = loader.getController();

        // continuously refresh the TreeItems.
        // demonstrates using controller methods to manipulate the controlled UI.
       /* final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(3),
                        new TreeLoadingEventHandler(controller)
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();*/

        // close the app if the user clicks on anywhere on the window.
        // just provides a simple way to kill the demo app.
       /* root.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent t) {
                stage.hide();
            }
        });*/

        // initialize the stage.
        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.UNIFIED);
//        stage.getIcons().add(new Image(getClass().getResourceAsStream("myIcon.png")));
        TreeItem<String> rootItem = new TreeItem<String>("Inbox");
        rootItem.setExpanded(true);
        for (int i = 1; i < 6; i++) {
            TreeItem<String> item = new TreeItem<String>("Message" + i);
            rootItem.getChildren().add(item);
        }


        stage.setMaximized(true);

        stage.show();
    }

    private void updateSelectedItem(Object newValue) {
        System.out.println(newValue);
    }

    /**
     * small helper class for handling tree loading events.
     */
    private class TreeLoadingEventHandler implements EventHandler<ActionEvent> {
        private MyControllerClass controller;
        private int idx = 0;

        TreeLoadingEventHandler(MyControllerClass controller) {
            this.controller = controller;
        }

        @Override
        public void handle(ActionEvent t) {
            controller.loadTreeItems("Loaded " + idx, "Loaded " + (idx + 1), "Loaded " + (idx + 2));
            idx += 3;
        }
    }


    /**
     * Adds the year buttons to the main panel.
     */
/*    private void addYears() {
        Collection<String> ret = new ArrayList<>();
        ///Select all years
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT Years.YEARS FROM Years");
             ResultSet rs = prep.executeQuery()
        ) {


            while (rs.next()) {

                ret.add(rs.getString(1));

            }

            rs.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (Exception e) {
            LogToFile.log(e, Severity.SEVERE, "Error while Selecting years from Database");

            //e.printStackTrace();
            // System.out.println("Error Start");

            // System.out.println(e.getErrorCode());
            // System.out.println(e.getSQLState());
            // System.out.println(e.getLocalizedMessage());
            // System.out.println(e.getMessage());
            // System.out.println("Error end");


        }
        //Create a button for each year
        for (String aRet : ret) {
            JButton b = new JButton(aRet);
            b.addActionListener(e -> {
                //On button click open Year window
                new YearWindow(((AbstractButton) e.getSource()).getText());

            });
            panel_1.add(b);
        }

    }*/
}
