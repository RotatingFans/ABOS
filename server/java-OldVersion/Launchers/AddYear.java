/*******************************************************************************
 * ABOS
 * Copyright (C) 2018 Patrick Magauran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package Launchers;/*
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

import Controllers.AddYearController;
import Utilities.LogToFile;
import Utilities.Severity;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Created by patrick on 5/28/17.
 */
public class AddYear extends Window {
    public AddYear(Window owner) {
        Stage stage = new Stage();
        FXMLLoader loader;

        Scene root;
        try {
            loader = new FXMLLoader(getClass().getResource("/UI/AddYear.fxml"));
            root = new Scene(loader.load());
            AddYearController addYearController = loader.getController();
            addYearController.initAddYear(this);
            stage.setScene(root);
            stage.setTitle("Reports");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            stage.showAndWait();
        } catch (IOException e) {
            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
        }

    }

    public AddYear(String year, Window owner) {
        Stage stage = new Stage();
        FXMLLoader loader;

        Scene root;
        try {
            loader = new FXMLLoader(getClass().getResource("/UI/AddYear.fxml"));
            root = new Scene(loader.load());
            AddYearController addYearController = loader.getController();
            addYearController.initAddYear(year, this);
            stage.setScene(root);
            stage.setTitle("Reports");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            stage.showAndWait();
        } catch (IOException e) {
            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
        }

    }
}
