/*
  Created by patrick on 4/16/15.
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class Config {


    public static boolean doesConfExist() {
        Properties prop = new Properties();
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
}
