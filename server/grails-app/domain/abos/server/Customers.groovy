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
//@Resource(uri = '/api/customers')

class Customers implements MultiTenant<abos.server.Customers> {
    String customerName
    String streetAddress
    String city
    String state
    String zipCode
    String phone
    String custEmail
    double latitude
    double longitude
    boolean ordered
    boolean home
    boolean interested
    BigDecimal donation
    Year year
    static belongsTo = [user: User]
    String userName
    static hasOne = [order: Orders]
    static hasMany = [orderedProducts: Ordered_products]
    static constraints = {
        customerName size: 1..255
        streetAddress size: 1..255
        city size: 1..255, nullable: true
        state size: 1..255, nullable: true
        zipCode size: 1..5, nullable: true
        phone nullable: true
        custEmail nullable: true
        latitude scale: 11
        longitude scale: 11
        ordered nullable: true
        home nullable: true
        interested nullable: true
        donation scale: 2, nullable: true
        order unique: true, nullable: true
    }
    static mapping = {
        tenantId name: 'userName'
        order cascade: 'all-delete-orphan'
        orderedProducts cascade: 'refresh'

    }

    def beforeInsert() {
        userName = user.username
    }

    def beforeUpdate() {
        userName = user.username

    }

    static restsearch = [
            customerName: [field: 'customerName', formula: { val -> "*${val}*" }],
            user        : [field: 'user.id'],
            id          : true,
            'year'      : [field: 'year.id'],
            'year.id'   : true,
            userName    : true
    ]

}
