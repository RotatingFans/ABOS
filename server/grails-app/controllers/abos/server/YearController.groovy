package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class YearController extends customRestSearchController<Year> {

    YearController() {
        super(Year)
    }

    YearController(boolean readOnly) {
        super(Year, readOnly)
    }
}
