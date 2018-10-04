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

import grails.testing.mixin.integration.Integration
import TenantNotFoundException
import Autowired
import UsernamePasswordAuthenticationToken
import GrantedAuthority
import AuthorityUtils
import SecurityContextHolder
import Specification

@Integration
class CurrentUserTenantResolverSpec extends Specification {

    UserDataService userDataService
    RoleDataService roleDataService
    UserRoleDataService userRoleDataService

    @Autowired
    CurrentUserTenantResolver currentUserTenantResolver

    void "Test Current User throws a TenantNotFoundException if not logged in"() {
        when:
        currentUserTenantResolver.resolveTenantIdentifier()

        then:
        def e = thrown(TenantNotFoundException)
        e.message == "Tenant could not be resolved from Spring Security Principal"
    }

    void "Test current logged in user is resolved "() {
        given:
        Role role = roleDataService.saveByAuthority('ROLE_USER')
        User user = userDataService.save('admin', 'admin')
        userRoleDataService.save(user, role)

        when:
        loginAs('admin', 'ROLE_USER')
        Serializable username = currentUserTenantResolver.resolveTenantIdentifier()

        then:
        username == user.username

        cleanup:
        userRoleDataService.delete(user, role)
        roleDataService.delete(role)
        userDataService.delete(user.id)
    }


    void loginAs(String username, String authority) {
        User user = userDataService.findByUsername(username)
        if (user) {
            // have to be authenticated as an admin to create ACLs
            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(authority)
            SecurityContextHolder.context.authentication = new UsernamePasswordAuthenticationToken(user.username,
                    user.password,
                    authorityList)
        }
    }


}