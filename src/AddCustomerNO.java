import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by patrick on 4/11/15.
 */

public class AddCustomerNO extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private JTextField Address;
    private JCheckBox NI;
    private JCheckBox NH;
    private JCheckBox Donate;
    private JButton okButton;
    private JButton cancelButton;

    public AddCustomerNO() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            new AddCustomerNO();

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
            JPanel north = new JPanel(new FlowLayout());
            {
                JLabel lblNewLabel = new JLabel("Address");
                //   lblNewLabel.setBounds(10, 25, 80, 14);
                north.add(lblNewLabel);
            }
            {
                Address = new JTextField();
                //Address.setBounds(90, 15, 340, 28);
                north.add(Address);
                Address.setColumns(30);

            }
            contentPanel.add(north, BorderLayout.NORTH);
        }


        //Center
        {
            JPanel center = new JPanel(new FlowLayout());
            {
                NI = new JCheckBox("Not Interested");
                // NI.setBounds(10, 70, 150, 23);
                center.add(NI);
            }
            {
                NH = new JCheckBox("Not Home");
                // NH.setBounds(160, 70, 120, 23);
                center.add(NH);
            }
            {
                Donate = new JCheckBox("Gave Donation");
                // Donate.setBounds(280, 70, 150, 23);
                center.add(Donate);
            }
            contentPanel.add(center, BorderLayout.CENTER);
        }

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.AFTER_LAST_LINE);
            {
                okButton = new JButton("OK");

                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addCustomer();
                    dispose();
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

    private void addCustomer() {
        try {
            //Address.getText(),NI.isSelected(),NH.isSelected(),Donate.isSelected())
            PreparedStatement writeCust = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(Address,Ordered, NI, NH) VALUES (?,'False',?,?)");
            writeCust.setString(1, Address.getText());
            writeCust.setString(2, Boolean.toString(NI.isSelected()));
            writeCust.setString(3, Boolean.toString(NH.isSelected()));
            writeCust.execute();
            DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
