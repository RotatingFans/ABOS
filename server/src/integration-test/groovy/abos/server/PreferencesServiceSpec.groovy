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
class PreferencesServiceSpec extends Specification {

    PreferencesService preferencesService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Preferences(...).save(flush: true, failOnError: true)
        //new Preferences(...).save(flush: true, failOnError: true)
        //Preferences preferences = new Preferences(...).save(flush: true, failOnError: true)
        //new Preferences(...).save(flush: true, failOnError: true)
        //new Preferences(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //preferences.id
    }

    void "test get"() {
        setupData()

        expect:
        preferencesService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Preferences> preferencesList = preferencesService.list(max: 2, offset: 2)

        then:
        preferencesList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        preferencesService.count() == 5
    }

    void "test delete"() {
        Long preferencesId = setupData()

        expect:
        preferencesService.count() == 5

        when:
        preferencesService.delete(preferencesId)
        sessionFactory.currentSession.flush()

        then:
        preferencesService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Preferences preferences = new Preferences()
        preferencesService.save(preferences)

        then:
        preferences.id != null
    }
}
