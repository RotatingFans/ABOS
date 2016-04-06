import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by patrick on 4/5/16.
 */
class AddCategory extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private JTextField catTxt;
    private JComboBox catCmbx;
    private boolean addToProducts = false;

    public AddCategory() {
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
                catTxt = new JTextField();
                name.add(catLbl);
                name.add(catTxt);
                north.add(name);
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


        try (PreparedStatement prep = DbInt.getPrep("Set", "INSERT INTO Categories (Name) VALUES (?)")) {
            prep.setString(1, catTxt.getText());
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


}
