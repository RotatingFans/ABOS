/*
 * Copyright (c) Patrick Magauran 2017.
 * Licensed under the AGPLv3. All conditions of said license apply.
 *     This file is part of LawnAndGarden.
 *
 *     LawnAndGarden is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     LawnAndGarden is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with LawnAndGarden.  If not, see <http://www.gnu.org/licenses/>.
 */

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
/*

    public static void main(String... args) {
        try {
            new ProgressDialog();

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
*/

    //SetBounds(X,Y,Width,Height)
    private void initUI() {
        setSize(300, 100);
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
