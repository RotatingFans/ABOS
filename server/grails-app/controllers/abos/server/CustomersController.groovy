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

import Utilities.Geolocation
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController
import grails.transaction.Transactional

import static grails.gorm.multitenancy.Tenants.withId

@Secured(['ROLE_USER'])
class CustomersController extends customRestSearchController<Customers> {

    CustomersController() {
        super(Customers)
    }

    CustomersController(boolean readOnly) {
        super(Customers, readOnly)
    }

    @Transactional
    def delete() {
        try {

            def customer = Customers.get(params.get("id"))
            def user = customer.getUser()
            withId(user.getUsername(), { sessions ->
                customer.orderedProducts.removeAll()
                customer.order = null

                customer.delete(flush: true)
            })
            render('')
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    @Transactional

    def save() {
        try {

            def customer = new Customers(request.JSON)
            def user = customer.getUser()
            withId(user.getUsername(), { sessions ->
                def year = customer.getYear()
                def coords = Geolocation.GetCoords(customer.getStreetAddress() + " " + customer.getCity() + ", " + customer.getState() + " " + customer.getZipCode())
                customer.setLatitude(coords.getLat())
                customer.setLongitude(coords.getLon())
                customer.setUserName(user.getUsername())
                customer.order.orderedProducts.each { op ->
                    customer.addToOrderedProducts(op)
                    year.addToOrderedProducts(op)
                    op.setUserName(customer.getUserName())
                    customer.order.addToOrderedProducts(op)
                    user.addToOrderedProducts(op)
                }
                customer.order.setYear(year)
                customer.order.setUserName(customer.getUserName())
                user.addToOrders(customer.order)

                //    year.save(flush: true)

                // log.debug(customer.customerName)
                customer.save(flush: true)
            })
            render(template: "customers", model: ['customers': customer])
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }


}
