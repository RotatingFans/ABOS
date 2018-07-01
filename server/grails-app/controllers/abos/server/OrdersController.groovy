package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class OrdersController extends customRestSearchController<Orders> {

    OrdersController() {
        super(Orders)
    }

    OrdersController(boolean readOnly) {
        super(Orders, readOnly)
    }
}
