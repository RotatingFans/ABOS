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

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class AddGroup {
    public static Group addGroup(String year) {
        Optional<Group> returnGroup = Optional.empty();
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add new group");

// Set the button types.
        ButtonType addGrp = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addGrp, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField groupName = new TextField();
        groupName.setPromptText("Group Name");

        grid.add(new Label("Group Name:"), 0, 0);
        grid.add(groupName, 1, 0);


// Enable/Disable login button depending on whether a username was entered.
        javafx.scene.Node addGroupButton = dialog.getDialogPane().lookupButton(addGrp);
        addGroupButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        groupName.textProperty().addListener((observable, oldValue, newValue) -> addGroupButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> groupName.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addGrp) {
                return groupName.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("INSERT INTO groups(Name) VALUES(?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

                prep.setString(1, result.get());

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            returnGroup = Optional.of(new Group(result.get(), year));

        }
        return returnGroup.orElse(null);
    }

    public static Group addGroup(String year, String groupName) {
        Optional<Group> returnGroup = Optional.empty();
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit group " + groupName);

// Set the button types.
        ButtonType addGrp = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addGrp, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField groupNameTextField = new TextField();
        groupNameTextField.setText(groupName);

        grid.add(new Label("Group Name:"), 0, 0);
        grid.add(groupNameTextField, 1, 0);


// Enable/Disable login button depending on whether a username was entered.
        javafx.scene.Node addGroupButton = dialog.getDialogPane().lookupButton(addGrp);
        addGroupButton.setDisable(true);

// Do some validation (using the Java 8 lambda syntax).
        groupNameTextField.textProperty().addListener((observable, oldValue, newValue) -> addGroupButton.setDisable(newValue.trim().isEmpty()));

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> groupNameTextField.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addGrp) {
                return groupNameTextField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try (Connection con = DbInt.getConnection(year);
                 PreparedStatement prep = con.prepareStatement("UPDATE groups SET Name=? WHERE Name=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                prep.setString(1, result.get());
                prep.setString(2, groupName);

                prep.execute();
            } catch (SQLException e) {
                LogToFile.log(e, Severity.SEVERE, CommonErrors.returnSqlMessage(e));
            }
            returnGroup = Optional.of(new Group(result.get(), year));

        }
        return returnGroup.orElse(null);
    }
}
