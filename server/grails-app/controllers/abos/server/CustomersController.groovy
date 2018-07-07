package abos.server

import Utilities.Geolocation
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

import static grails.gorm.multitenancy.Tenants.withId

@Secured(['ROLE_USER'])
class CustomersController extends customRestSearchController<Customers> {

    CustomersController() {
        super(Customers)
    }

    CustomersController(boolean readOnly) {
        super(Customers, readOnly)
    }

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
