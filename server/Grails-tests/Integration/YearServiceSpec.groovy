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

import Rollback
import grails.testing.mixin.integration.Integration
import SessionFactory
import Specification

@Integration
@Rollback
class YearServiceSpec extends Specification {

    YearService yearService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Year(...).save(flush: true, failOnError: true)
        //new Year(...).save(flush: true, failOnError: true)
        //Year year = new Year(...).save(flush: true, failOnError: true)
        //new Year(...).save(flush: true, failOnError: true)
        //new Year(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //year.id
    }

    void "test get"() {
        setupData()

        expect:
        yearService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Year> yearList = yearService.list(max: 2, offset: 2)

        then:
        yearList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        yearService.count() == 5
    }

    void "test delete"() {
        Long yearId = setupData()

        expect:
        yearService.count() == 5

        when:
        yearService.delete(yearId)
        sessionFactory.currentSession.flush()

        then:
        yearService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Year year = new Year()
        yearService.save(year)

        then:
        year.id != null
    }
}
