import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by patrick on 4/11/15.
 */
class AddCustomerNO extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private LogToFile MyLogger = new LogToFile();
    private JTextField Address;
    private JCheckBox notInterestedChckBx;
    private JCheckBox notHomeChckBx;

    public AddCustomerNO() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String... args) {
        try {
            new AddCustomerNO();

        } catch (RuntimeException e) {
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
            JPanel addressPnl = new JPanel(new FlowLayout());
            addressPnl.add(new JLabel("Address"));
            addressPnl.add(Address = new JTextField(30));
            contentPanel.add(addressPnl, BorderLayout.NORTH);
        }
        //Center
        {
            JPanel center = new JPanel(new FlowLayout());
            center.add(notInterestedChckBx = new JCheckBox("Not Interested"));
            center.add(notHomeChckBx = new JCheckBox("Not Home"));
            contentPanel.add(center, BorderLayout.CENTER);
        }

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.AFTER_LAST_LINE);
            JButton okButton;
            buttonPane.add(okButton = new JButton("OK"));
            getRootPane().setDefaultButton(okButton);
            okButton.addActionListener(e -> {
                addCustomer();
                dispose();
            });
            okButton.setActionCommand("OK");
            JButton cancelButton;
            buttonPane.add(cancelButton = new JButton("Cancel"));
            cancelButton.addActionListener(e -> dispose());
            cancelButton.setActionCommand("Cancel");
        }
    }

    private void addCustomer() {
        try (PreparedStatement writeCust = DbInt.getPrep("Set", "INSERT INTO CUSTOMERS(Address,Ordered, notInterestedChckBx, notHomeChckBx) VALUES (?,'False',?,?)")) {
            writeCust.setString(1, Address.getText());
            writeCust.setString(2, Boolean.toString(notInterestedChckBx.isSelected()));
            writeCust.setString(3, Boolean.toString(notHomeChckBx.isSelected()));
            writeCust.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
