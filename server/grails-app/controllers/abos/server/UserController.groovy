package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class UserController extends customRestSearchController<User> {

    UserController() {
        super(User)
    }

    UserController(boolean readOnly) {
        super(User, readOnly)
    }
}
