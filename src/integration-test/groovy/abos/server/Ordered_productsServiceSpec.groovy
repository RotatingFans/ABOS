package abos.server

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class Ordered_productsServiceSpec extends Specification {

    Ordered_productsService ordered_productsService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Ordered_products(...).save(flush: true, failOnError: true)
        //new Ordered_products(...).save(flush: true, failOnError: true)
        //Ordered_products ordered_products = new Ordered_products(...).save(flush: true, failOnError: true)
        //new Ordered_products(...).save(flush: true, failOnError: true)
        //new Ordered_products(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //ordered_products.id
    }

    void "test get"() {
        setupData()

        expect:
        ordered_productsService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Ordered_products> ordered_productsList = ordered_productsService.list(max: 2, offset: 2)

        then:
        ordered_productsList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        ordered_productsService.count() == 5
    }

    void "test delete"() {
        Long ordered_productsId = setupData()

        expect:
        ordered_productsService.count() == 5

        when:
        ordered_productsService.delete(ordered_productsId)
        sessionFactory.currentSession.flush()

        then:
        ordered_productsService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Ordered_products ordered_products = new Ordered_products()
        ordered_productsService.save(ordered_products)

        then:
        ordered_products.id != null
    }
}
