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

package ABOS.Derby;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * Created by patrick on 12/24/15.
 */
class Settings extends Window {
    //private final JPanel contentPanel = new JPanel();
    //private JTabbedPane north;

    public Settings(Window owner) {
        Stage stage = new Stage();
        FXMLLoader loader;

        Scene root;
        try {
            loader = new FXMLLoader(getClass().getResource("/UI/Settings.fxml"));
            root = new Scene(loader.load());
            SettingsController settingsController = loader.getController();
            settingsController.initUI(this);
            stage.setScene(root);
            stage.setTitle("Preferences");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(owner);
            stage.showAndWait();
        } catch (IOException e) {
            LogToFile.log(e, Severity.SEVERE, "Error loading window. Please retry then reinstall application. If error persists, contact the developers.");
        }

    }

}
