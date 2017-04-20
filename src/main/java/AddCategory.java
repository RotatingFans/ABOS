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

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

/**
 * Created by patrick on 4/5/16.
 */
class AddCategory extends JDialog implements ActionListener {
    private final JPanel contentPanel = new JPanel();
    private String catName = "";
    private String catDate = "";
    private JDatePickerImpl datePicker;
    private JTextField categoryTextField;
    private String year;
    private JButton okButton;

    public AddCategory(String year) {
        initUI(year);
        this.setModal(true);
        //setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public AddCategory() {
        this.setModal(true);

        initUI("");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    }

    public static void main(String... args) {
        try {
            new AddCategory(args[1]);

        } catch (RuntimeException e) {
            LogToFile.log(e, Severity.WARNING, "Error opening window. Please try again or contact support.");
        }
    }

    public String[] showDialog() {
        setVisible(true);
        return new String[]{catName, catDate};
    }

    private void initUI(String Year) {
        if (!Objects.equals(Year, "")) {
            year = Year;
        }
        setSize(600, 400);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());
        this.setTitle("ABOS - Add Category");
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("ABOS-LOGO.png")));
        FlowLayout flow = new FlowLayout(FlowLayout.LEADING);
        //Main Content
        {

            JPanel north = new JPanel(flow);
            //Category Name
            {
                JPanel name = new JPanel(flow);
                JLabel catLbl = new JLabel("Category Name:");
                categoryTextField = new JTextField(20);
                name.add(catLbl);
                name.add(categoryTextField);
                north.add(name);
            }
            //Category Date
            {
                JPanel catDatePanel = new JPanel(flow);
                JLabel catLbl = new JLabel("Category Date:");
                UtilDateModel model = new UtilDateModel();

                Properties p = new Properties();
                p.setProperty("text.today", "Today");
                p.setProperty("text.month", "Month");
                p.setProperty("text.year", "Year");
                JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
                datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

                catDatePanel.add(catLbl);
                catDatePanel.add(datePicker);
                north.add(catDatePanel);
            }
            contentPanel.add(north);


            //Button Pane
            {
                JPanel buttonPane = new JPanel();
                buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
                getContentPane().add(buttonPane, BorderLayout.SOUTH);
                //OKButton
                {
                    okButton = new JButton("OK");
                    buttonPane.add(okButton);
                    getRootPane().setDefaultButton(okButton);
                }
                //CancelButton
                JButton cancelButton;
                {
                    cancelButton = new JButton("Cancel");

                    buttonPane.add(cancelButton);
                }
                //OKButton Action
                okButton.addActionListener(this);
                okButton.setActionCommand("OK");

                //CancelButton Action
                cancelButton.addActionListener(this);
                cancelButton.setActionCommand("Cancel");
            }
        }
    }

    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();
        if (source == okButton) {
            if (saveData()) {
                catDate = datePicker.getModel().getYear() + "-" + datePicker.getModel().getMonth() + "-" + datePicker.getModel().getDay();
                catName = categoryTextField.getText();
            }
        }
        dispose();
    }
    private boolean saveData() {

        //Add DB setting

        if (!Objects.equals(year, "") && year != null) {


            try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO Categories (Name, Date) VALUES (?,?)")) {
                prep.setString(1, categoryTextField.getText());
                Date selectedDate = (Date) datePicker.getModel().getValue();

                prep.setDate(2, new java.sql.Date(selectedDate.getTime()));
                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
                return false;
            }


        }
        return true;


    }
}