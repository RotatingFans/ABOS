package abos.server

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

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

    @Transactional
    def save() {
        def jsonParams = request.JSON
        log.debug(jsonParams.toString())
        def users = jsonParams
        //def usersList = [:]
        for (u in users) {
            def user = User.findByUsername(u.key)

            //def subUsers = [:]
            for (su in u.value.subUsers) {
                def subUser = User.findByUsername(su.key)
                // UserManager.findOrSaveByManageAndUser(user, subUser)

                if (su.value.checked) {
                    UserManager.findOrSaveByManageAndUser(user, subUser)

                } else {
                    def uM = UserManager.findByManageAndUser(user, subUser)
                    if (uM != null) {
                        uM.delete(flush: true)
                        //uM.save()
                    }
                }


            }


        }
        render([status: "success"] as JSON, status: 200)
    }
}
