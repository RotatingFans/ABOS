import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by patrick on 12/24/15.
 */
public class Reports extends JDialog {
    private final JPanel contentPanel = new JPanel();
    JTabbedPane SteptabbedPane;
    private JTextField DbLoc;
    private JButton okButton;
    private JButton nextButton;
    private JPanel ReportInfo;
    private JButton cancelButton;
    private JComboBox cmbxReportType;
    private JComboBox cmbxYears = new JComboBox(new DefaultComboBoxModel<>());
    private JComboBox cmbxCustomers = new JComboBox(new DefaultComboBoxModel<>());

    public Reports() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            new Reports();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //SetBounds(X,Y,Width,Height)
    private void initUI() {
        setSize(450, 150);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());
        //North
        {
            SteptabbedPane = new JTabbedPane();
            //Report Type
            {
                JPanel ReportType = new JPanel(new FlowLayout());
                {
                    cmbxReportType = new JComboBox(new DefaultComboBoxModel<>());
                    cmbxReportType.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JComboBox comboBox = (JComboBox) actionEvent.getSource();

                            Object selected = comboBox.getSelectedItem();
                            if (cmbxReportType.getSelectedItem() != "") {
                                nextButton.setEnabled(true);
                            }

                        }
                    });
                    cmbxReportType.addItem("");
                    cmbxReportType.addItem("Year Totals");
                    cmbxReportType.addItem("Customer Year Totals");
                    cmbxReportType.addItem("Customer All-time Totals");

                    ReportType.add(cmbxReportType);
                }
                SteptabbedPane.addTab("Report Type", ReportType);

            }
            //Report Info
            {
                ReportInfo = new JPanel(new FlowLayout());
                {
                    cmbxYears.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JComboBox comboBox = (JComboBox) actionEvent.getSource();

                            Object selected = comboBox.getSelectedItem();
                            if (cmbxReportType.getSelectedIndex() == 1) {
                                if (cmbxYears.getSelectedItem() != "") {
                                    ArrayList<String> customersY = getCustomers(cmbxYears.getSelectedItem().toString());
                                    cmbxCustomers.removeAllItems();
                                    cmbxCustomers.addItem("");
                                    cmbxCustomers.setSelectedItem("");
                                    for (int i = 0; i < customersY.size(); i++) {
                                        cmbxCustomers.addItem(customersY.get(i));
                                    }
                                    cmbxCustomers.setEnabled(true);
                                }
                            } else if (cmbxYears.getSelectedItem() != "") {
                                nextButton.setEnabled(true);
                            }

                        }
                    });

                    cmbxCustomers.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JComboBox comboBox = (JComboBox) actionEvent.getSource();

                            Object selected = comboBox.getSelectedItem();
                            if (cmbxCustomers.getSelectedItem() != "") {
                                nextButton.setEnabled(true);
                            }

                        }
                    });
                    ReportInfo.add(cmbxYears);
                    ReportInfo.add(cmbxCustomers);

                }
                SteptabbedPane.addTab("Report Info", ReportInfo);

            }
            //Report Preview
            {
                JPanel ReportPreview = new JPanel(new FlowLayout());
                {

                }
                SteptabbedPane.addTab("Report Type", ReportPreview);

            }

            contentPanel.add(SteptabbedPane, BorderLayout.CENTER);
        }


        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                nextButton = new JButton("Next -->");
                nextButton.setEnabled(false);
                buttonPane.add(nextButton);
                getRootPane().setDefaultButton(nextButton);
            }
            {
                okButton = new JButton("OK");
                okButton.setEnabled(false);
                buttonPane.add(okButton);
            }
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    dispose();
                }
            });
            nextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SteptabbedPane.setSelectedIndex(SteptabbedPane.getSelectedIndex() + 1);
                    switch (SteptabbedPane.getSelectedIndex()) {
                        case 0:
                            updateCombos();
                            break;
                        case 1:
                            break;
                        case 2:
                            break;


                    }

                }
            });
            okButton.setActionCommand("OK");

            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            cancelButton.setActionCommand("Cancel");
        }
    }

    private void updateCombos() {
        ArrayList<String> years = getYears();

        switch (cmbxReportType.getSelectedItem().toString()) {

            case "Year Totals":
                ReportInfo.removeAll();
                ReportInfo.add(cmbxYears);
                cmbxYears.removeAllItems();
                cmbxYears.addItem("");
                cmbxYears.setSelectedItem("");
                for (int i = 0; i < years.size(); i++) {

                    cmbxYears.addItem(years.get(i));
                }

                break;
            case "Customer Year Totals":
                ReportInfo.removeAll();
                ReportInfo.add(cmbxYears);
                ReportInfo.add(cmbxCustomers);
                cmbxCustomers.setEnabled(false);
                cmbxYears.removeAllItems();
                cmbxYears.addItem("");
                cmbxYears.setSelectedItem("");
                for (int i = 0; i < years.size(); i++) {
                    cmbxYears.addItem(years.get(i));
                }

                break;
            case "Customer All-time Totals":
                ReportInfo.removeAll();
                ReportInfo.add(cmbxCustomers);
                cmbxCustomers.removeAllItems();
                cmbxCustomers.addItem("");
                cmbxCustomers.setSelectedItem("");
                ArrayList<String> customers = getCustomers();
                for (int i = 0; i < customers.size(); i++) {
                    cmbxCustomers.addItem(customers.get(i));
                }
                break;
        }
    }

    private ArrayList<String> getYears() {
        ArrayList<String> ret = new ArrayList<>();
        PreparedStatement prep = DbInt.getPrep("Set", "SELECT YEARS FROM Years");
        try {


            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret.add(rs.getString("YEARS"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    private ArrayList<String> getCustomers(String year) {
        ArrayList<String> ret = new ArrayList<>();
        PreparedStatement prep = DbInt.getPrep(year, "SELECT NAME FROM Customers");
        try {


            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret.add(rs.getString("NAME"));

            }
            ////DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return ret;
    }

    private ArrayList<String> getCustomers() {
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<String> years = getYears();
        for (int i = 0; i < years.size(); i++) {
            PreparedStatement prep = DbInt.getPrep(years.get(i), "SELECT NAME FROM Customers");
            try {


                ResultSet rs = prep.executeQuery();

                while (rs.next()) {
                    String name = rs.getString("NAME");
                    if (ret.contains(name)) {
                        ret.set(ret.indexOf(name), ret.get(ret.indexOf(name)) + years.get(i).toString());
                    } else {
                        ret.add(name + years.get(i).toString());
                    }

                }
                ////DbInt.pCon.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return ret;
    }
}
