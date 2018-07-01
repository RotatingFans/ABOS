package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController
import groovy.util.logging.Slf4j

@Slf4j
@Secured(['ROLE_USER'])
class CategoriesController extends customRestSearchController<Categories> {

    CategoriesController() {
        super(Categories)
    }

    CategoriesController(boolean readOnly) {
        super(Categories, readOnly)
    }
}
