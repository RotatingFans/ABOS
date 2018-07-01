package abos.server


import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.restsearch.customRestSearchController

@Secured(['ROLE_USER'])
class PreferencesController extends customRestSearchController<Preferences> {

    PreferencesController() {
        super(Preferences)
    }

    PreferencesController(boolean readOnly) {
        super(Preferences, readOnly)
    }
}
