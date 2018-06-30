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

import Utilities.Customer;
import Utilities.DbInt;
import Utilities.Year;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

//import javax.swing.*;

//import com.itextpdf.text.Document;

/**
 * Searches the text files under the given directory and counts the number of instances a given word is found
 * in these file.
 *
 * @author Albert Attard
 */
public class orderHistoryReportWorker extends Task<Integer> {
    private final String csvLocation;
    private Double prog = 0.0;

    /**
     * Creates an instance of the worker
     *
     * @param csvLocation Location to save the CSV
     */
    public orderHistoryReportWorker(String csvLocation) {
        this.csvLocation = csvLocation;

    }

// --Commented out by Inspection START (7/27/16 3:02 PM):
//    private static void failIfInterrupted() throws InterruptedException {
//        if (Thread.currentThread().isInterrupted()) {
//            throw new InterruptedException("Interrupted while searching files");
//        }
//    }
// --Commented out by Inspection STOP (7/27/16 3:02 PM)

    @Override
    protected Integer call() throws Exception {
        Path newFile = Paths.get(csvLocation);
        BufferedWriter writer = Files.newBufferedWriter(newFile);
        try {
            HashMap<String, ArrayList<String>> addressYears = new HashMap<>();

            updateMessage("Generating Report");

            ArrayList<String> years = DbInt.getUserYears();
            writer.write("\"Address\"");
            for (String year : years) {
                writer.write(",\"" + year + "\"");
            }
            for (String yearString : years) {
                Year year = new Year(yearString);
                for (Customer customer : year.getCustomers()) {
                    String address = customer.getFormattedAddress();
                    if (!addressYears.containsKey(address)) {

                        ArrayList<String> customs = new ArrayList();
                        customs.add(yearString);
                        addressYears.put(address, customs);

                    } else {
                        addressYears.get(address).add(yearString);
                    }
                }
            }
            addressYears.forEach((address, addrYears) -> {
                try {
                    writer.newLine();
                    writer.write("\"" + address + "\"");
                    for (String year : years) {
                        if (addrYears.contains(year)) {
                            writer.write(",\"TRUE\"");
                        } else {
                            writer.write(",\"FALSE\"");
                        }
                    }

                } catch (IOException e) {
                    updateMessage("Error writing CSV file. Please try again.");
                }

            });
        } catch (IOException e) {
            updateMessage("Error writing CSV file. Please try again.");
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception ex) {
                updateMessage("Error writing CSV file. Please try again.");
            }
        }
        updateMessage("Done");


        // Return the number of matches found
        return 1;
    }

    private double getProg() {
        return prog;
    }

    private void setProgress(double progress) {
        prog += progress;
        updateProgress(progress, 100.0);
    }
}