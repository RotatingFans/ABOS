package abos.server

import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class CategoriesController extends customRestSearchController<Categories> {

    CategoriesController() {
        super(Categories)
    }

    CategoriesController(boolean readOnly) {
        super(Categories, readOnly)
    }
}
