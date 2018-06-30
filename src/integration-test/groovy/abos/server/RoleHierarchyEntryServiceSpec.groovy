package abos.server

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class RoleHierarchyEntryServiceSpec extends Specification {

    RoleHierarchyEntryService roleHierarchyEntryService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new RoleHierarchyEntry(...).save(flush: true, failOnError: true)
        //new RoleHierarchyEntry(...).save(flush: true, failOnError: true)
        //RoleHierarchyEntry roleHierarchyEntry = new RoleHierarchyEntry(...).save(flush: true, failOnError: true)
        //new RoleHierarchyEntry(...).save(flush: true, failOnError: true)
        //new RoleHierarchyEntry(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //roleHierarchyEntry.id
    }

    void "test get"() {
        setupData()

        expect:
        roleHierarchyEntryService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<RoleHierarchyEntry> roleHierarchyEntryList = roleHierarchyEntryService.list(max: 2, offset: 2)

        then:
        roleHierarchyEntryList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        roleHierarchyEntryService.count() == 5
    }

    void "test delete"() {
        Long roleHierarchyEntryId = setupData()

        expect:
        roleHierarchyEntryService.count() == 5

        when:
        roleHierarchyEntryService.delete(roleHierarchyEntryId)
        sessionFactory.currentSession.flush()

        then:
        roleHierarchyEntryService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        RoleHierarchyEntry roleHierarchyEntry = new RoleHierarchyEntry()
        roleHierarchyEntryService.save(roleHierarchyEntry)

        then:
        roleHierarchyEntry.id != null
    }
}
