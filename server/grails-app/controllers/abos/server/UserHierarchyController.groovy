package abos.server

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_USER'])

class UserHierarchyController {
    static responseFormats = ['json', 'xml']

    def index() {
        def q = params.'q'
        def yearS = '0'
        def allSearchParams
        def thisSearchParams = [:]
        q?.split(';').each {
            // split only the first separation token.
            // eg. if the separation token is : and you have time:15:32, this
            // means only the time and 15:32 will be splitted and not
            // the 15 will be separated from 32
            if ((it?.trim())) {

                def idx = [it.indexOf('='), it.indexOf(':')].findAll { it > -1 }.min()
                log.trace "Index of clause separator in ${it} is ${idx}"

                if (idx > -1) {
                    def arr = [it[0..idx - 1], it[idx + 1..it.size() - 1]]
                    if (arr.size() > 1) {
                        if (arr[0] == 'year') {
                            yearS = arr[1]
                        }
                        thisSearchParams[arr[0]] = arr[1]
                    } else
                        thisSearchParams[arr[0]] = true
                } else {
                    thisSearchParams[it] = true
                }
            }
        }
        allSearchParams = thisSearchParams
        def users = User.findAll()
        def year = Year.findById(Long.decode(yearS.toString()))
        def usersList = [:]
        for (u in users) {
            def subUsers = [:]
            UserYear userYear = UserYear.findByUserAndYear(u, year)

            for (su in users) {
                UserYear userYearSub = UserYear.findByUserAndYear(su, year)
                subUsers.put((su.username), [
                        group  : userYearSub?.group?.id,
                        checked: (UserManager.findByManageAndUserAndYear(u, su, year) != null),
                        status : userYearSub?.status
                ])

            }

            usersList.put((u.username),
                    [
                            group   : userYear?.group?.id,
                            subUsers: subUsers,
                            status  : userYear?.status
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
        def users = jsonParams.data
        def year = Year.findById(Long.decode(jsonParams.year.toString()))
        //def usersList = [:]
        for (u in users) {
            def user = User.findByUsername(u.key)
            UserYear userYear = UserYear.findOrCreateByUserAndYear(user, year)
            if (u.value.group != null) {
                userYear.group = Groups.findById(u.value.group)
            }
            userYear.status = u.value.status ?: "DISABLED"
            userYear.save()
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
