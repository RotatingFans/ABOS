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

import grails.gorm.services.Query
import grails.gorm.services.Service
import groovy.transform.CompileStatic

@Service(UserRole)
@CompileStatic
interface UserRoleDataService {

    @Query("""select $user.username from ${UserRole userRole}
    inner join ${User user = userRole.user}
    inner join ${Role role = userRole.role}
    where $role.authority = $authority""")
    List<String> findAllUsernameByAuthority(String authority)

    UserRole save(User user, Role role)

    void delete(User user, Role role)

    void deleteByUser(User user)
}
