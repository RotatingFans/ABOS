import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by patrick on 4/11/15.
 */

class FirstStart extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private JTextField DbLoc;

    FirstStart() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            new FirstStart();

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
            DbLoc = new JTextField();
            //Address.setBounds(90, 15, 340, 28);
            north.add(DbLoc);
            DbLoc.setColumns(30);

        }
        {
            JButton openFile = new JButton("...");
            north.add(openFile);
            openFile.addActionListener(e -> {
                //Creates a JFileChooser to select a directory to store the Databases
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    DbLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                }


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
        okButton.addActionListener(e -> {
            Config.setDbLoc(DbLoc.getText());
            //Creates Set Database
            //TODO allow to not create Db
            //TODO remove writeData
            DbInt.createDb("Set");
            DbInt.writeData("Set", "CREATE TABLE Customers(CustomerID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), Address VARChAR(255), Ordered VARChAR(255), NI VARChAR(255), NH VARChAR(255))");
            DbInt.writeData("Set", "CREATE TABLE YEARS(ID int PRIMARY KEY NOT NULL, YEARS varchar(4))");
            dispose();
        });
        okButton.setActionCommand("OK");

        cancelButton.addActionListener(e -> dispose());
        cancelButton.setActionCommand("Cancel");
    }

}
