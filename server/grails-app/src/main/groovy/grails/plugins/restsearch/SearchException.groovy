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

package grails.plugins.restsearch

class SearchException extends Exception {

    Integer number
    Map params
    String solution

    SearchException(int number) {
        this(number, '')
    }

    SearchException(int number, String message) {
        this(number, message, '', [:])
    }

    SearchException(int number, String message, String solution, Map params) {
        super(message)
        this.number = number
        this.solution = solution
        this.params = params
    }

    def addParam(String key, String value) {
        params[key] = value
        return this
    }

    def setSolution(solution) {
        this.solution = solution
        return this
    }
}
