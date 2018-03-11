/*
 * Copyright (c) Patrick Magauran 2018.
 *   Licensed under the AGPLv3. All conditions of said license apply.
 *       This file is part of ABOS.
 *
 *       ABOS is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Affero General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       ABOS is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Affero General Public License for more details.
 *
 *       You should have received a copy of the GNU Affero General Public License
 *       along with ABOS.  If not, see <http://www.gnu.org/licenses/>.
 */

package Utilities;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by patrick on 5/27/17.
 */
public class ProgressForm {
    final Label label = new Label();
    private final Stage dialogStage;
    private final ProgressBar pb = new ProgressBar();
    //  private final ProgressIndicator pin = new ProgressIndicator();

    public ProgressForm() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        // PROGRESS BAR
        label.setText("alerto");

        pb.setProgress(-1F);

        final VBox vb = new VBox();
        vb.setSpacing(5);
        vb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(label, pb);

        Scene scene = new Scene(vb);
        dialogStage.setScene(scene);
    }

    public void activateProgressBar(final Task<?> task) {
        pb.progressProperty().bind(task.progressProperty());
        label.textProperty().bind(task.messageProperty());

        dialogStage.show();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }
}
