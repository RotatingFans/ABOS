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

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */

class Main extends JFrame {

    private JFrame frame;
    private JPanel panel_1;

    /**
     * Create the application.
     */
    private Main() {
        initialize();
    }

    /**
     * Launch the application.
     *
     * @param args command line arguments
     */
    public static void main(String... args) {
        EventQueue.invokeLater(() -> {
            try {
                Main window = new Main();
                window.frame.setVisible(true);
            } catch (RuntimeException e) {
                LogToFile.log(e, Severity.SEVERE, "Error starting application. Try reinstalling or contacting support.");
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
/*
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
 */
        //ImageIcon img = new ImageIcon(getClass().getClassLoader().getResource("Report.xsl"));
        // Create the Log To File class
        Boolean addYears = true;
        if (!Config.doesConfExist()) {
            //new Settings();
            addYears = false;
        }


        frame = new JFrame();
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ABOS-LOGO.png")));
        frame.setTitle("ABOS");
        frame.setBounds(100, 100, 690, 470);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        //JSeparator separator = new JSeparator();
        //separator.setBounds(0, 0, 682, 39);
        //frame.getContentPane().add(separator);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 682, 44);
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout());
        //Settings
        {
            JButton btnNewButton = new JButton("Settings");
         //   btnNewButton.addActionListener(e -> new Settings());
            //btnNewButton.setBounds(429, 5, 107, 39);
            panel.add(btnNewButton);
        }
        //Reports
        {
            JButton btnNewButton = new JButton("Reports");
            //btnNewButton.addActionListener(e -> new Reports());
            //btnNewButton.setBounds(429, 5, 107, 39);
            panel.add(btnNewButton);
        }
        //Add Year
        {
            JButton btnNewButton = new JButton("Add Year");
            btnNewButton.addActionListener(e -> new AddYear());
            //btnNewButton.setBounds(429, 5, 107, 39);
            panel.add(btnNewButton);
        }
        //Add Customer
        {
            JButton AddCustomerB = new JButton("Add Customer");
            AddCustomerB.addActionListener(e -> new AddCustomerNO());
            //AddCustomerB.setBounds(274, 5, 140, 39);
            panel.add(AddCustomerB);
        }
        //View Map
        {
            JButton ViewMap = new JButton("View Map");
            ViewMap.addActionListener(e -> {
                Map window = new Map();
                window.setVisible(true);
                window.map().setDisplayToFitMapElements(true, false, false);

            });
            //	ViewMap.setBounds(165, 5, 107, 39);
            panel.add(ViewMap);
        }
        //Refresh Button
        {
            JButton refresh = new JButton("Refresh");
            refresh.addActionListener(e -> {
                //Refreshes the Window
                panel_1.removeAll();

                addYears();

                frame.invalidate();
                frame.validate();
                frame.repaint();
                panel_1.repaint();

            });
            panel.add(refresh);
        }
        //GridLayoutPanel
        {
            panel_1 = new JPanel();
            panel_1.setBounds(0, 50, 682, 386);
            frame.getContentPane().add(panel_1, BorderLayout.CENTER);
            panel_1.setLayout(new GridLayout(2, 3, 1, 1));
        }
        if (addYears) {

            addYears();
        }

    }

    /**
     * Adds the year buttons to the main panel.
     */
    private void addYears() {
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

    }
}