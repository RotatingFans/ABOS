package abos.server

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])

class UserHierarchyController {
    static responseFormats = ['json', 'xml']

    def index() {
        def users = User.findAll()
        def usersList = [:]
        for (u in users) {
            def subUsers = [:]
            for (su in users) {
                subUsers.put((su.username), [
                        group  : 1,
                        checked: (UserManager.findByManageAndUser(u, su) != null)
                ])

            }

            usersList.put((u.username),
                    [
                            group   : 1,
                            subUsers: subUsers
                    ]
            )

        }
        render(usersList as JSON)
        //render status: 200
    }
}
