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

package Utilities;/*
  Created by patrick on 4/16/15.
 */

import Launchers.Settings;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class Config {
    private static String ConfigLocation = "./ABOSConfig.properties";

    /**
     * @return the configurationFile location
     */
    public static String getConfigLocation() {
        return ConfigLocation;
    }

    /**
     * @param configLocation The location of the configuration file
     */
    public static void setConfigLocation(String configLocation) {
        ConfigLocation = configLocation;
    }

    /**
     * @return If the configuration file specified exists
     */
    public static boolean doesConfExist() {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") Properties prop = new Properties();
        InputStream input = null;
        boolean loc = false;
        try {

            input = new FileInputStream(ConfigLocation);

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            //loc = prop.getProperty("databaseLocation");


        } catch (FileNotFoundException ignored) {
            return false;
        } catch (IOException ex) {
            if ("LGconfig.properties (No such file or directory)".equals(ex.getMessage())) {
                return false;
            } else {
                LogToFile.log(ex, Severity.SEVERE, "Error reading config file state, ensure the software has access to the directory.");
            }
            //System.out.print(ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                    loc = true;
                } catch (IOException e) {
                    LogToFile.log(e, Severity.WARNING, "");
                }

            }
            if (input == null) {
                loc = false;
            }
        }


        return loc;
        //return true;
    }

    /**
     * @return The database URL
     */
    public static String getDbLoc() {
        return getProp("databaseLocation");
    }

    /**
     * @return The prefix for the database
     */
    public static String getPrefix() {
        return getProp("databasePrefix");
    }

    /**
     * @return The SSL choice
     */
    public static String getSSL() {
        return getProp("SSL");
    }

    /**
     * @return The program version
     */
    public static Version getProgramVersion() {
        final Properties properties = new Properties();
        try {
            properties.load(Config.class.getClass().getResourceAsStream("/CompileProps.properties"));

            // read each line and write to System.out
            return new Version(properties.getProperty("Version"));


        } catch (IOException e) {
            LogToFile.log(e, Severity.SEVERE, "Unable to determine program version. Please attempt a re-install of the software.");
            return new Version("");
        }
    }

    /**
     * @param property The propertiy to retrieve
     * @return The value of Property
     */
    @Nonnull
    public static String getProp(String property) {
        if (doesConfExist()) {
            Properties prop = new Properties();
            InputStream input = null;
            String loc = "";
            try {

                input = new FileInputStream(ConfigLocation);

                // load a properties file
                prop.load(input);

                // get the property value and print it out
                loc = prop.getProperty(property, "");

            } catch (IOException ex) {
                LogToFile.log(ex, Severity.SEVERE, "Error reading config file, ensure the software has access to the directory.");

                //System.out.print(ex.getMessage());
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        LogToFile.log(e, Severity.WARNING, "");

                    }
                }
            }


            return loc;
        } else {
            createConfigFile();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Set Configuration?");
            alert.setHeaderText("Software needs to be configured.");
            alert.setContentText("A configuration file has been created. Would you like to edit it?");

            ButtonType buttonTypeOne = new ButtonType("Edit");
            ButtonType buttonTypeTwo = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == buttonTypeOne) {
                new Settings();
                return getProp(property);

            } else {
                return "";
            }
        }
    }

    /**
     * Creates a default configuration file
     */
    public static void createConfigFile() {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream(ConfigLocation);

            //Add DB setting
            prop.setProperty("databaseLocation", "");
            prop.setProperty("SSL", "TRUE");
            prop.setProperty("databasePrefix", "ABOS-Test-");


            //AddCustomer
            {
                prop.setProperty("CustomerName", "");
                prop.setProperty("CustomerAddress", "");
                prop.setProperty("CustomerZipCode", "");
                prop.setProperty("CustomerTown", "");
                prop.setProperty("CustomerState", "");
                prop.setProperty("CustomerPhone", "");
                prop.setProperty("CustomerEmail", "");
                prop.setProperty("CustomerPaid", "");
                prop.setProperty("CustomerDelivered", "");
                prop.setProperty("CustomerDonation", "");
            }
            //Maps
            //Launchers.Reports
            {
                prop.setProperty("ReportType", "");
                prop.setProperty("ScoutName", "");
                prop.setProperty("ScoutAddress", "");
                prop.setProperty("ScoutZip", "");
                prop.setProperty("ScoutTown", "");
                prop.setProperty("ScoutState", "");
                prop.setProperty("ScoutPhone", "");

                prop.setProperty("ScoutRank", "");
                prop.setProperty("logoLoc", "");
                prop.setProperty("pdfLoc", "");

            }
            prop.store(output, null);

        } catch (IOException io) {
            LogToFile.log(io, Severity.SEVERE, "Error writing settings file. Please try again.");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    LogToFile.log(e, Severity.SEVERE, "Error closing settings file. Please try again.");
                }
            }

        }
    }
}
