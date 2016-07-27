import javax.swing.*;
import java.awt.*;

/**
 * Created by patrick on 7/26/16.
 */
public class ProgressDialog extends JDialog {
    public JProgressBar progressBar;
    public JLabel statusLbl;

    public ProgressDialog() {
        initUI();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public static void main(String... args) {
        try {
            new ProgressDialog();

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //SetBounds(X,Y,Width,Height)
    private void initUI() {
        setSize(600, 400);
        getContentPane().setLayout(new BorderLayout());

        //Main Content
        {

            //Category Name
            {
                statusLbl = new JLabel("");
                getContentPane().add(statusLbl, BorderLayout.NORTH);

            }

            //Category Date
            {
                progressBar = new JProgressBar();
                getContentPane().add(progressBar, BorderLayout.CENTER);
            }


            //Button Pane
            {
                JPanel buttonPane = new JPanel();
                buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
                getContentPane().add(buttonPane, BorderLayout.SOUTH);

                //CancelButton
                JButton cancelButton;
                {
                    cancelButton = new JButton("Cancel");

                    buttonPane.add(cancelButton);
                }


                //CancelButton Action
                cancelButton.addActionListener(e -> dispose());
                cancelButton.setActionCommand("Cancel");
            }
        }
    }


}
