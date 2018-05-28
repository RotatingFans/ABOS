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

import Exceptions.AccessException;
import Utilities.DbInt;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Map;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
public class addFlyWay extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(final Stage stage) {
        Map<String, String> params = getParameters().getNamed();
        if (params.containsKey("username") && params.containsKey("password")) {
            if (!DbInt.verifyLoginAndUser(new Pair<>(params.get("username"), params.get("password")))) {
                System.out.println("Error Logging in");
                System.exit(0);

            }

        }
        DbInt.getDatabses().forEach((db) -> {
            try {
                DbInt.baselineDatabse(db);
            } catch (AccessException ignored) {
                System.out.println("You must be admin");
                System.exit(0);
            }
        });
        System.exit(0);

    }
}
