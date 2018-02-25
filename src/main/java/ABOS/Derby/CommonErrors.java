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

package ABOS.Derby;

import java.sql.SQLException;

/**
 * Created by patrick on 1/29/17.
 */
class CommonErrors {
    public static String returnSqlMessage(SQLException e) {
        String retMsg;
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
