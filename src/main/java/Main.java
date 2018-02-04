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

import com.mchange.v2.log.slf4j.Slf4jMLog;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Sample application to demonstrate programming an FXML interface.
 */
public class Main extends Application {
    @FXML
    private TreeView<String> selectNav;
    private Stage masterStage;
    // main method is only for legacy support - java 8 won't call it for a javafx application.
    public static void main(String[] args) { launch(args); }

    public Boolean checkUpdates() {
// Make a URL to the web page
        try {
            URL url = new URL("https://roatingfans.gitlab.io/ABOS/version-2.html");

            // Get the input stream through URL Connection
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();

            // Once you have the Input Stream, it's just plain old Java IO stuff.

            // For this case, since you are interested in getting plain-text web page
            // I'll use a reader and output the text content to System.out.

            // For binary content, it's better to directly read the bytes from stream and write
            // to the target file.


            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            final Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("CompileProps.properties"));
            // read each line and write to System.out
            while ((line = br.readLine()) != null) {
                if (!Objects.equals(properties.getProperty("Version"), line)) {
                    return true;
                }
            }
        } catch (IOException e) {
            LogToFile.log(e, Severity.WARNING, "Error checking for updates. If error persists, reinstall and contact developer.");
        }


        return false;
    }

    @Override
    public void start(final Stage stage) throws Exception {
        // load the scene fxml UI.
        // grabs the UI scenegraph view from the loader.
        // grabs the UI controller for the view from the loader.
        Slf4jMLog.config("");
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("UI/Main.fxml"));
        final Parent root = loader.load();
        final MainController controller = loader.getController();
        if (checkUpdates()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Update Available");
            alert.setHeaderText("An Update is Available");
            alert.setContentText("If you choose to download, download the latest tag's java Artifacts");

            ButtonType buttonTypeOne = new ButtonType("Download");
            ButtonType buttonTypeTwo = new ButtonType("Remind Me Later", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                if (Desktop.isDesktopSupported()) {
                    new Thread(() -> {
                        try {
                            Desktop.getDesktop().browse(new URI("https://gitlab.com/RoatingFans/ABOS/tags"));

                        } catch (URISyntaxException | IOException e) {
                            LogToFile.log(e, Severity.WARNING, "Error opening download window. Please try navigating to https://gitlab.com/RoatingFans/ABOS/tags");
                        }
                    }).start();

                }
            }
        }
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
        stage.getScene().getStylesheets().add("UI/Main.css");
        stage.initStyle(StageStyle.UNIFIED);
        stage.setOnCloseRequest(windowEvent -> {
            if (windowEvent.getSource() != stage.getOwner()) {
                stage.close();
            } else {
                windowEvent.consume();
            }
        });
        stage.setMaximized(true);
        masterStage = stage;
        controller.initialize(stage);

        stage.show();
    }

    private void updateSelectedItem(Object newValue) {
    }




    /*
      Adds the year buttons to the main panel.
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
