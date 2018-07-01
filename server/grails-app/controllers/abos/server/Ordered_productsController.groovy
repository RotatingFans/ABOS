package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class Ordered_productsController extends customRestSearchController<Ordered_products> {

    Ordered_productsController() {
        super(Ordered_products)
    }

    Ordered_productsController(boolean readOnly) {
        super(Ordered_products, readOnly)
    }
}
