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

import java.sql.SQLException;

/**
 * Created by patrick on 1/29/17.
 */
public class CommonErrors {
    public static String returnSqlMessage(SQLException e) {
        String retMsg = "";
        switch (e.getSQLState()) {
            case "S0022":
                retMsg = "Database incompatible";
                break;
            default:
                retMsg = "Error utilizing databse. Please try restarting the software.";
                break;

        }
        return retMsg;
    }
}
