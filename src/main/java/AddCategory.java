import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by patrick on 4/5/16.
 */
class AddCategory extends JDialog {
    private final JPanel contentPanel = new JPanel();
    JDatePickerImpl datePicker;
    private JTextField categoryTextField;
    private String year;

    public AddCategory(String year) {
        initUI(year);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String... args) {
        try {
            new AddCategory(args[1]);

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //SetBounds(X,Y,Width,Height)
    private void initUI(String Year) {
        year = Year;
        setSize(600, 400);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout());
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
                JButton okButton;
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
                okButton.addActionListener(e -> {
                    saveData();

                    dispose();
                });
                okButton.setActionCommand("OK");

                //CancelButton Action
                cancelButton.addActionListener(e -> dispose());
                cancelButton.setActionCommand("Cancel");
            }
        }
    }

    private void saveData() {

        //Add DB setting


        try (PreparedStatement prep = DbInt.getPrep(year, "INSERT INTO Categories (Name, Date) VALUES (?,?)")) {
            prep.setString(1, categoryTextField.getText());
            Date selectedDate = (Date) datePicker.getModel().getValue();

            prep.setDate(2, new java.sql.Date(selectedDate.getTime()));
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}
