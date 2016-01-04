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
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String... args) {
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
        {
            JTabbedPane north = new JTabbedPane();
            JPanel general = new JPanel(new FlowLayout());
            {
                //Choose DB location
                //Export DB

            }
            north.addTab("General", general);

            JPanel AddCustomer = new JPanel(new FlowLayout());
            {
                //Set default options for add customer form


            }
            north.addTab("Add Customer", AddCustomer);

            JPanel MapOptions = new JPanel(new FlowLayout());
            {
                //Area to Display
                //Default zoom

            }
            north.addTab("Map", MapOptions);

            JPanel Report = new JPanel(new FlowLayout());
            {
                //Default Options for Form


            }
            north.addTab("Reports", Report);

            JPanel Licesnce = new JPanel(new FlowLayout());
            {
                //Display Liscence info


            }
            north.addTab("Licence", Licesnce);
            contentPanel.add(north, BorderLayout.CENTER);
        }


        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            JButton okButton;
            {
                okButton = new JButton("OK");

                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            JButton cancelButton;
            {
                cancelButton = new JButton("Cancel");

                buttonPane.add(cancelButton);
            }
            okButton.addActionListener(e -> dispose());
            okButton.setActionCommand("OK");

            cancelButton.addActionListener(e -> dispose());
            cancelButton.setActionCommand("Cancel");
        }
    }
}
