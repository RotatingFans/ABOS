package abos.server

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class UserController extends customRestSearchController<User> {
    def springSecurityService

    UserController() {
        super(User)
    }

    UserController(boolean readOnly) {
        super(User, readOnly)
    }

    def currentUser() {
        User user = springSecurityService.currentUser

        render([userName: user.username, fullName: 'Test Person'] as JSON, status: 200)
    }
}
