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

package Utilities;

import javafx.util.Pair;

public class TreeItemPair<K, V> extends Pair<K, V> {

    public TreeItemPair(K key, V value) {
        super(key, value);
    }

    public TreeItemPair(K key) {
        super(key, null);
    }

    @Override
    public String toString() {
        return this.getKey().toString();
    }
}
