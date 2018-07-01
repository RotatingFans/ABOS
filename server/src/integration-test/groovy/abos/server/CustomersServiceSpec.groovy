package abos.server

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.hibernate.SessionFactory
import spock.lang.Specification

@Integration
@Rollback
class CustomersServiceSpec extends Specification {

    CustomersService customersService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new customers(...).save(flush: true, failOnError: true)
        //new customers(...).save(flush: true, failOnError: true)
        //customers customers = new customers(...).save(flush: true, failOnError: true)
        //new customers(...).save(flush: true, failOnError: true)
        //new customers(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //customers.id
    }

    void "test get"() {
        setupData()

        expect:
        customersService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Customers> customersList = customersService.list(max: 2, offset: 2)

        then:
        customersList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        customersService.count() == 5
    }

    void "test delete"() {
        Long customersId = setupData()

        expect:
        customersService.count() == 5

        when:
        customersService.delete(customersId)
        sessionFactory.currentSession.flush()

        then:
        customersService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Customers customers = new Customers()
        customersService.save(customers)

        then:
        customers.id != null
    }
}
