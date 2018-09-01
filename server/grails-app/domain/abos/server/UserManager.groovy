package abos.server

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])

class UserManager {
    User manage
    User user
    static constraints = {
/*        manager nullable: false
        user nullable: false*/
    }

    static mappedBy = [manage: "none", user: "none"]
}
