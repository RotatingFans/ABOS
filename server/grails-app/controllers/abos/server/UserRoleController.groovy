package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class UserRoleController extends customRestSearchController<UserRole> {

    UserRoleController() {
        super(UserRole)
    }

    UserRoleController(boolean readOnly) {
        super(UserRole, readOnly)
    }
}
