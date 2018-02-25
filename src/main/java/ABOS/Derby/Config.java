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

package ABOS.Derby;/*
  Created by patrick on 4/16/15.
 */

import java.io.*;
import java.util.Properties;

class Config {

    public static boolean doesConfExist() {
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection") Properties prop = new Properties();
        InputStream input = null;
        boolean loc = false;
        try {

            input = new FileInputStream("./LGconfig.properties");

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

    public static String getDbLoc() {
        Properties prop = new Properties();
        InputStream input = null;
        String loc = "";
        try {

            input = new FileInputStream("./LGconfig.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            loc = prop.getProperty("databaseLocation");


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
    }

// --Commented out by Inspection START (2/1/16 5:28 PM):
//    public static void setDbLoc(String Loc) {
//        Properties prop = new Properties();
//        OutputStream output = null;
//
//        try {
//
//            output = new FileOutputStream("./LGconfig.properties");
//
//            // set the properties value
//            prop.setProperty("databaseLocation", Loc);
//
//
//            // save properties to project root folder
//            prop.store(output, null);
//
//        } catch (IOException io) {
//            io.printStackTrace();
//        } finally {
//            if (output != null) {
//                try {
//                    output.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
// --Commented out by Inspection STOP (2/1/16 5:28 PM)

// --Commented out by Inspection START (1/25/16 10:13 AM):
//    public static void setProp(String property, String setting) {
//        Properties prop = new Properties();
//        OutputStream output = null;
//
//        try {
//
//            output = new FileOutputStream("./LGconfig.properties");
//
//            // set the properties value
//            prop.setProperty(property, setting);
//
//
//            // save properties to project root folder
//            prop.store(output, null);
//
//        } catch (IOException io) {
//            io.printStackTrace();
//        } finally {
//            if (output != null) {
//                try {
//                    output.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
// --Commented out by Inspection STOP (1/25/16 10:13 AM)

    public static String getProp(String property) {
        Properties prop = new Properties();
        InputStream input = null;
        String loc = "";
        try {

            input = new FileInputStream("./LGconfig.properties");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            loc = prop.getProperty(property);

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
    }

    public static void createConfigFile() {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream("./LGconfig.properties");

            //Add DB setting
            prop.setProperty("databaseLocation", "");


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
            //Reports
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
