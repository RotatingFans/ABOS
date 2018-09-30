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
class RoleServiceSpec extends Specification {

    RoleService roleService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Role(...).save(flush: true, failOnError: true)
        //new Role(...).save(flush: true, failOnError: true)
        //Role role = new Role(...).save(flush: true, failOnError: true)
        //new Role(...).save(flush: true, failOnError: true)
        //new Role(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //role.id
    }

    void "test get"() {
        setupData()

        expect:
        roleService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Role> roleList = roleService.list(max: 2, offset: 2)

        then:
        roleList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        roleService.count() == 5
    }

    void "test delete"() {
        Long roleId = setupData()

        expect:
        roleService.count() == 5

        when:
        roleService.delete(roleId)
        sessionFactory.currentSession.flush()

        then:
        roleService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Role role = new Role()
        roleService.save(role)

        then:
        role.id != null
    }
}
