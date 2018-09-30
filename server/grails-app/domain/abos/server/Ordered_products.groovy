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

package abos.server

import grails.gorm.MultiTenant
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class Ordered_products implements MultiTenant<abos.server.Ordered_products> {
    static belongsTo = [user: User, customer: Customers, order: Orders, products: Products]
    Products products
    int quantity
    BigDecimal extendedCost
    Year year
    String userName

    static constraints = {
        //quantity min: 0
        //extendedCost min: 0.0, scale: 2
    }
    static mapping = {
        tenantId name: 'userName'

    }

    def beforeInsert() {
        userName = user.username
    }

    def beforeUpdate() {
        userName = user.username
    }

    static restsearch = [
            id          : true,
            extendedCost: true,
            quantity    : true,
            'year'      : [field: 'year.id'],
            'year.id'   : true,
            userName    : true,
            user        : [field: 'user.id'],


    ]
}
