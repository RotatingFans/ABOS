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
//@Resource(uri = '/api/orders')
class Orders implements MultiTenant<abos.server.Orders> {
    static belongsTo = [user: User, customer: Customers]
    static hasMany = [orderedProducts: Ordered_products]
    BigDecimal cost
    int quantity
    BigDecimal amountPaid
    Boolean delivered
    Year year
    String userName

    static constraints = {
        cost min: 0.0, scale: 2
        amountPaid min: 0.0, scale: 2
        quantity min: 0
    }
    static mapping = {
        tenantId name: 'userName'
        orderedProducts cascade: 'all-delete-orphan', lazy: false

    }

    def beforeInsert() {
        userName = user.username
    }

    def beforeUpdate() {
        userName = user.username
    }
    static restsearch = [
            id             : true,
            orderedProducts: true,
            cost           : true,
            quantity       : true,
            amountPaid     : true,
            delivered      : true,
            'year'         : [field: 'year.id'],
            'year.id'      : true,
            userName       : true,
            user           : [field: 'user.id'],

    ]
}
