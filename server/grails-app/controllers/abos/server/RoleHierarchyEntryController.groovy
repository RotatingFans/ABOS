package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class RoleHierarchyEntryController extends customRestSearchController<RoleHierarchyEntry> {

    RoleHierarchyEntryController() {
        super(RoleHierarchyEntry)
    }

    RoleHierarchyEntryController(boolean readOnly) {
        super(RoleHierarchyEntry, readOnly)
    }
}
