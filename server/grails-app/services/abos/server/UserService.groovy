package abos.server

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic
class UserService {
    RoleDataService roleDataService

    UserRoleDataService userRoleDataService

    UserDataService userDataService

    SpringSecurityService springSecurityService

    @Transactional
    User save(String username, String password, String authority) {
        Role role = roleDataService.findByAuthority(authority)
        if (!role) {
            role = roleDataService.saveByAuthority(authority)
        }
        User user = userDataService.save(username, password)
        userRoleDataService.save(user, role)
        UserManager userManager = new UserManager(manager: user, user: user)
        userManager.save()
        user
    }

    @CompileDynamic
    List<User> listAllManaged() {
        User currentUser = springSecurityService.isLoggedIn() ?
                springSecurityService.loadCurrentUser() as User :
                null as User
        UserManager.where {
            manage == currentUser
        }.list().user

    }

    @CompileDynamic
    List<User> listAllManaged(Year year) {
        User currentUser = springSecurityService.isLoggedIn() ?
                springSecurityService.loadCurrentUser() as User :
                null as User
        UserManager.where {
            manage == currentUser
            year == year
        }.list().user

    }

    @CompileDynamic
    String getYearAccess(Year year) {
        User currentUser = springSecurityService.isLoggedIn() ?
                springSecurityService.loadCurrentUser() as User :
                null as User

        UserYear.findByUserAndYear(currentUser, year).status

    }

    @CompileDynamic
    Year getLatestYear() {
        User currentUser = springSecurityService.isLoggedIn() ?
                springSecurityService.loadCurrentUser() as User :
                null as User

        UserYear.findAllByUserAndStatus(currentUser, "ENABLED").last().year

    }
}

