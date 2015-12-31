/*
  Created by patrick on 4/16/15.
 */

import java.io.*;
import java.util.Properties;

class Config {


    private Config() {
    }

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


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            if ("LGconfig.properties (No such file or directory)".equals(ex.getMessage())) {
                return false;
            } else {
                ex.printStackTrace();
            }
            //System.out.print(ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                    loc = true;
                } catch (IOException e) {
                    e.printStackTrace();
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


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return loc;
    }

    public static void setDbLoc(String Loc) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream("./LGconfig.properties");

            // set the properties value
            prop.setProperty("databaseLocation", Loc);


            // save properties to project root folder
            prop.store(output, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
