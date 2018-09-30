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

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.hibernate.SessionFactory
import spock.lang.Specification

@Integration
@Rollback
class UserRoleServiceSpec extends Specification {

    UserRoleService userRoleService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new UserRole(...).save(flush: true, failOnError: true)
        //new UserRole(...).save(flush: true, failOnError: true)
        //UserRole userRole = new UserRole(...).save(flush: true, failOnError: true)
        //new UserRole(...).save(flush: true, failOnError: true)
        //new UserRole(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //userRole.id
    }

    void "test get"() {
        setupData()

        expect:
        userRoleService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<UserRole> userRoleList = userRoleService.list(max: 2, offset: 2)

        then:
        userRoleList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        userRoleService.count() == 5
    }

    void "test delete"() {
        Long userRoleId = setupData()

        expect:
        userRoleService.count() == 5

        when:
        userRoleService.delete(userRoleId)
        sessionFactory.currentSession.flush()

        then:
        userRoleService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        UserRole userRole = new UserRole()
        userRoleService.save(userRole)

        then:
        userRole.id != null
    }
}
