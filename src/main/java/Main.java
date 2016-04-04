import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */

class Main extends JFrame {

    private JFrame frame;
    private JPanel panel_1;

    /**
     * Create the application.
     */
    private Main() {
        initialize();
    }

    /**
     * Launch the application.
     *
     * @param args command line arguments
     */
    public static void main(String... args) {
        EventQueue.invokeLater(() -> {
            try {
                Main window = new Main();
                window.frame.setVisible(true);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!Config.doesConfExist()) {
            new Settings();
        }


        frame = new JFrame();
        frame.setBounds(100, 100, 690, 470);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        //JSeparator separator = new JSeparator();
        //separator.setBounds(0, 0, 682, 39);
        //frame.getContentPane().add(separator);

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 682, 44);
        frame.getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout());
        //Settings
        {
            JButton btnNewButton = new JButton("Settings");
            btnNewButton.addActionListener(e -> new Settings());
            //btnNewButton.setBounds(429, 5, 107, 39);
            panel.add(btnNewButton);
        }
        //Reports
        {
            JButton btnNewButton = new JButton("Reports");
            btnNewButton.addActionListener(e -> new Reports());
            //btnNewButton.setBounds(429, 5, 107, 39);
            panel.add(btnNewButton);
        }
        //Add Year
        {
            JButton btnNewButton = new JButton("Add Year");
            btnNewButton.addActionListener(e -> new AddYear());
            //btnNewButton.setBounds(429, 5, 107, 39);
            panel.add(btnNewButton);
        }
        //Add Customer
        {
            JButton AddCustomerB = new JButton("Add Customer");
            AddCustomerB.addActionListener(e -> new AddCustomerNO());
            //AddCustomerB.setBounds(274, 5, 140, 39);
            panel.add(AddCustomerB);
        }
        //View Map
        {
            JButton ViewMap = new JButton("View Map");
            ViewMap.addActionListener(e -> {
                Map window = new Map();
                window.setVisible(true);
                window.map().setDisplayToFitMapElements(true, false, false);

            });
            //	ViewMap.setBounds(165, 5, 107, 39);
            panel.add(ViewMap);
        }
        //Refresh Button
        {
            JButton refresh = new JButton("Refresh");
            refresh.addActionListener(e -> {
                //Refreshes the Window
                panel_1.removeAll();

                addYears();

                frame.invalidate();
                frame.validate();
                frame.repaint();
                panel_1.repaint();

            });
            panel.add(refresh);
        }
        //GridLayoutPanel
        {
            panel_1 = new JPanel();
            panel_1.setBounds(0, 50, 682, 386);
            frame.getContentPane().add(panel_1, BorderLayout.CENTER);
            panel_1.setLayout(new GridLayout(2, 3, 1, 1));
        }
        addYears();

    }

    /**
     * Adds the year buttons to the main panel.
     */
    private void addYears() {
        Collection<String> ret = new ArrayList<String>();
        ///Select all years
        try (PreparedStatement prep = DbInt.getPrep("Set", "SELECT Years.YEARS FROM Years");
             ResultSet rs = prep.executeQuery()
        ) {


            while (rs.next()) {

                ret.add(rs.getString(1));

            }

            rs.close();
            if (DbInt.pCon != null) {
                //DbInt.pCon.close();
                DbInt.pCon = null;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("Error Start");

            System.out.println(e.getErrorCode());
            System.out.println(e.getSQLState());
            System.out.println(e.getLocalizedMessage());
            System.out.println(e.getMessage());
            System.out.println("Error end");


        }
        //Create a button for each year
        for (String aRet : ret) {
            JButton b = new JButton(aRet);
            b.addActionListener(e -> {
                //On button click open Year window
                new Year(((AbstractButton) e.getSource()).getText());

                System.out.print(((AbstractButton) e.getSource()).getText());

            });
            panel_1.add(b);
        }

    }
}
