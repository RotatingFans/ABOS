import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

//TODO Add comments and inline documentation
//TODO Improve color pallete and design
//TODO Remove private info
//TODO Add saved window
public class Main extends JFrame {

    private JFrame frame;
    private JPanel panel_1;

    /**
     * Create the application.
     */
    public Main() {
        initialize();
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        if (!new Config().doesConfExist()) {
            new FirstStart();
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

        JButton btnNewButton = new JButton("Add Year");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AddYear();
            }
        });
        //btnNewButton.setBounds(429, 5, 107, 39);
        panel.add(btnNewButton);

        JButton AddCustomerB = new JButton("Add Customer");
        AddCustomerB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AddCustomerNO();
            }
        });
        //AddCustomerB.setBounds(274, 5, 140, 39);
        panel.add(AddCustomerB);

        JButton ViewMap = new JButton("View Map");
        ViewMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Map window = new Map();
                window.setVisible(true);
            }
        });
        //	ViewMap.setBounds(165, 5, 107, 39);
        panel.add(ViewMap);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                panel_1.removeAll();
                //			panel_1 = new JPanel();
                //			panel_1.setBounds(0, 50, 682, 386);
                //			frame.getContentPane().remove(panel_1);
                //		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
                //		panel_1.setLayout(new GridLayout(2, 3, 1, 1));
                addYears();
//				revalidate();
//				validate();
//				panel_1.repaint();
//				repaint();
                //	initialize();
                //SwingUtilities.updateComponentTreeUI(frame);
                frame.invalidate();
                frame.validate();
                frame.repaint();
                panel_1.repaint();
                //window.setVisible(true);
            }
        });
        panel.add(refresh);
        panel_1 = new JPanel();
        panel_1.setBounds(0, 50, 682, 386);
        frame.getContentPane().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new GridLayout(2, 3, 1, 1));
        addYears();


    }

    private void addYears() {
        ArrayList<String> ret = new ArrayList<String>();

        PreparedStatement prep = DbInt.getPrep("Set", "SELECT Years.YEARS FROM Years");
        try {


            ResultSet rs = prep.executeQuery();

            while (rs.next()) {

                ret.add(rs.getString(1));

            }
            DbInt.pCon.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<String> res = ret;

        for (int i = 0; i < res.size(); i++) {
            JButton b = new JButton(res.get(i));
            b.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new Year(((AbstractButton) e.getSource()).getText());

                    System.out.print(((AbstractButton) e.getSource()).getText());

                }
            });
            panel_1.add(b);
        }

    }
}
