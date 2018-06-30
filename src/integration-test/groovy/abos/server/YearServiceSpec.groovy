package abos.server

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

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
