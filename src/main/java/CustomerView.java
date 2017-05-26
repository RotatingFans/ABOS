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

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


class CustomerView extends JDialog {

    private static String year = null;
    private JFrame frame;
    private JPanel[] panel;
    // --Commented out by Inspection (1/2/2016 12:01 PM):private JTextField textField;
    private JTextField textField_1;
    private List<String> CustomerNames;
    private JLabel pageL;
    private int currPage = 0;
    private int pages = 0;

    /**
     * Create the application.
     */

    public CustomerView(String Year) {
        year = Year;
        initialize();
        frame.setVisible(true);
    }

    /**
     * Launch the application.
     */
    public static void main(String... args) {
        EventQueue.invokeLater(() -> {
            try {
                CustomerView window = new CustomerView(args[1]);
                window.frame.setVisible(true);
            } catch (RuntimeException e) {
                LogToFile.log(e, Severity.SEVERE, "Error opening window. Please try again.");
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 741, 494);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ABOS-LOGO.png")));
        frame.setTitle("ABOS - Customers - " + year);
        {
            JPanel North = new JPanel(new FlowLayout());


            JButton backBtn = new JButton("<-");
            //btnNewButton_1.setBounds(0, 7, 51, 36);
            backBtn.addActionListener(e -> {
                if (currPage > 0) {
                    frame.getContentPane().remove(panel[currPage]);
                    frame.getContentPane().repaint();
                    frame.getContentPane().add(panel[currPage - 1], BorderLayout.CENTER);
                    frame.getContentPane().repaint();
                    currPage -= 1;
                    pageL.setText(String.format("%s / %s", currPage + 1, pages));
                }
            });
            North.add(backBtn);

            pageL = new JLabel("");
            //pageL.setBounds(61, 18, 46, 14);
            North.add(pageL);

            JButton forbtn = new JButton("->");
            forbtn.addActionListener(e -> {
                if (currPage < (pages - 1)) {
                    frame.getContentPane().remove(panel[currPage]);
                    frame.getContentPane().repaint();
                    frame.getContentPane().add(panel[currPage + 1], BorderLayout.CENTER);
                    frame.getContentPane().repaint();
                    currPage += 1;
                    pageL.setText(String.format("%s / %s", currPage + 1, pages));
                }
            });
            //button.setBounds(117, 7, 51, 36);
            North.add(forbtn);

            JButton btnNewButton_2 = new JButton("Add Customer");
            btnNewButton_2.addActionListener(e -> new AddCustomer(year));
            //btnNewButton_2.setBounds(582, 14, 133, 36);
            North.add(btnNewButton_2);
            textField_1 = new JTextField();
            //textField_1.setBounds(254, 0, 200, 50);
            North.add(textField_1);
            textField_1.setColumns(10);

            JButton btnNewButton = new JButton("Search");
            btnNewButton.addActionListener(e -> {
                //loop through all buttons and look for typed name and turn to page
                for (int i = 0; i < CustomerNames.size(); i++) {
                    if (CustomerNames.get(i).contains(textField_1.getText())) {
                        double pgs = (double) (i + 1) / 6.00000000;
                        int page = (int) Math.ceil(pgs) - 1;
                        frame.getContentPane().remove(panel[currPage]);
                        frame.getContentPane().repaint();
                        frame.getContentPane().add(panel[page], BorderLayout.CENTER);
                        frame.getContentPane().repaint();
                        currPage = page + 1;
                        pageL.setText(String.format("%s / %s", page + 1, pages));
//							frame.getRootPane().setDefaultButton((JButton) panel[page].getComponent(i - ((page + 1) * 6)));
//
//							try {
//								wait(25);
//							} catch (InterruptedException e1) {
//								e1.printStackTrace();
//							}

                    }
                }
            });
            //btnNewButton.setBounds(464, 0, 89, 50);
            North.add(btnNewButton);

            JButton btnRefresh = new JButton("Refresh");
            btnRefresh.addActionListener(e -> {
                frame.setVisible(false);
                initialize();
                frame.setVisible(true);

            });
            //btnRefresh.setBounds(181, 7, 51, 36);
            North.add(btnRefresh);

            frame.getContentPane().add(North, BorderLayout.NORTH);
        }
        addCustomers();

    }

    private void addCustomers() {
        List<String> ret = new ArrayList<>();
        //get all customer names
        try (PreparedStatement prep = DbInt.getPrep(year, "SELECT Customers.Name FROM Customers");
             ResultSet rs = prep.executeQuery()
        ) {


            while (rs.next()) {

                ret.add(rs.getString(1));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
        }
        CustomerNames = ret;
        //Create a button for each name
        panel = new JPanel[CustomerNames.size()];
        for (int i = 0; i < CustomerNames.size(); i++) {
            int pg = (int) Math.ceil((double) (i / 6));
            panel[pg] = new JPanel(new GridLayout(2, 3, 1, 1));

            for (int i1 = 0; i1 < 6; i1++) {
                if (((i1 + i) < CustomerNames.size())) {
                    JButton b = new JButton(CustomerNames.get(i1 + i));
                    b.addActionListener(e -> {
                        //Open Customer Report on button click
                        new CustomerReport(((AbstractButton) e.getSource()).getText(), year);

                        //System.out.print(((AbstractButton) e.getSource()).getText());

                    });

                    panel[pg].add(b);
                }

            }


            //panel[pg].setVisible(false);
            i += 5;
        }

        //	panel[0].setVisible(true);
        if (!CustomerNames.isEmpty()) {
            frame.getContentPane().add(panel[0], BorderLayout.CENTER);
        }

        double s = (double) CustomerNames.size();
        double pagesD = (s / 6.00000);
        pages = ((int) Math.ceil(pagesD));
        pageL.setText(String.format("1 / %s", pages));
        //panel = new JPanel();
        //panel.setBounds(0, 61, 725, 394);


    }
}