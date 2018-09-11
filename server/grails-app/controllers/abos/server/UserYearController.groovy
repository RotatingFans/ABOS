package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class UserYearController extends customRestSearchController<UserYear> {

    UserYearController() {
        super(UserYear)
    }

    UserYearController(boolean readOnly) {
        super(UserYear, readOnly)
    }
}
