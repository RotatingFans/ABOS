package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class ProductsController extends customRestSearchController<Products> {

    ProductsController() {
        super(Products)
    }

    ProductsController(boolean readOnly) {
        super(Products, readOnly)
    }
}
