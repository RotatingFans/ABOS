package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class CustomersController extends customRestSearchController<Customers> {

    CustomersController() {
        super(Customers)
    }

    CustomersController(boolean readOnly) {
        super(Customers, readOnly)
    }
}
