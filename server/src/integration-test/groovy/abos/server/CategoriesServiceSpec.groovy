package abos.server

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.hibernate.SessionFactory
import spock.lang.Specification

@Integration
@Rollback
class CategoriesServiceSpec extends Specification {

    CategoriesService categoriesService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Categories(...).save(flush: true, failOnError: true)
        //new Categories(...).save(flush: true, failOnError: true)
        //Categories categories = new Categories(...).save(flush: true, failOnError: true)
        //new Categories(...).save(flush: true, failOnError: true)
        //new Categories(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //categories.id
    }

    void "test get"() {
        setupData()

        expect:
        categoriesService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Categories> categoriesList = categoriesService.list(max: 2, offset: 2)

        then:
        categoriesList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        categoriesService.count() == 5
    }

    void "test delete"() {
        Long categoriesId = setupData()

        expect:
        categoriesService.count() == 5

        when:
        categoriesService.delete(categoriesId)
        sessionFactory.currentSession.flush()

        then:
        categoriesService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Categories categories = new Categories()
        categoriesService.save(categories)

        then:
        categories.id != null
    }
}
