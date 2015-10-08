import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by patrick on 4/11/15.
 */

public class FirstStart extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private JTextField DbLoc;
    private JButton okButton;
    private JButton cancelButton;

    public FirstStart() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            new FirstStart();

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
                openFile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser chooser = new JFileChooser();
                        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
                        // under the demo/jfc directory in the JDK.
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        int returnVal = chooser.showOpenDialog(FirstStart.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            DbLoc.setText(chooser.getSelectedFile().getAbsolutePath());
                        }


                    }
                });
            }
            contentPanel.add(north, BorderLayout.CENTER);
        }


        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
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
                    new Config().setDbLoc(DbLoc.getText());
                    DbInt.createDb("Set");
                    DbInt.writeData("Set", "CREATE TABLE Customers(CustomerID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), Address VARChAR(255), Ordered VARChAR(255), NI VARChAR(255), NH VARChAR(255))");
                    DbInt.writeData("Set", "CREATE TABLE YEARS(ID int PRIMARY KEY NOT NULL, YEARS varchar(4))");
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

}
