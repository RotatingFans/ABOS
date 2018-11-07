/*******************************************************************************
 * ABOS
 * Copyright (C) 2018 Patrick Magauran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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

        UserYear.findAllByUserAndStatus(currentUser, "ENABLED")?.last()?.year

    }
}

