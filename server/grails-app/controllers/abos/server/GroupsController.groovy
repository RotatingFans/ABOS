package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class GroupsController extends customRestSearchController<Groups> {
    GroupsController() {
        super(Groups)
    }

    GroupsController(boolean readOnly) {
        super(Groups, readOnly)
    }
}
