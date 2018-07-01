package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class RoleController extends customRestSearchController<Role> {

    RoleController() {
        super(Role)
    }

    RoleController(boolean readOnly) {
        super(Role, readOnly)
    }
}
