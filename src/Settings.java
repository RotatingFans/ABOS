import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by patrick on 12/24/15.
 */
class Settings extends JDialog {
    private final JPanel contentPanel = new JPanel();

    private Settings() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            new Settings();

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
        JPanel north = new JPanel(new FlowLayout());
        {
            JLabel lblNewLabel = new JLabel("Location to Store the Database:");
            //   lblNewLabel.setBounds(10, 25, 80, 14);
            north.add(lblNewLabel);
        }
        {
            JTextField dbLoc = new JTextField();
            //Address.setBounds(90, 15, 340, 28);
            north.add(dbLoc);
            dbLoc.setColumns(30);

        }
        {
            JButton openFile = new JButton("...");
            north.add(openFile);
            openFile.addActionListener(e -> {


            });
        }
        contentPanel.add(north, BorderLayout.CENTER);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = new JButton("OK");

        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");

        buttonPane.add(cancelButton);
        okButton.addActionListener(e -> dispose());
        okButton.setActionCommand("OK");

        cancelButton.addActionListener(e -> dispose());
        cancelButton.setActionCommand("Cancel");
    }
}
