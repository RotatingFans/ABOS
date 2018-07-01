package abos.server

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import grails.validation.ValidationException
import spock.lang.Specification

class PreferencesControllerSpec extends Specification implements ControllerUnitTest<PreferencesController>, DomainUnitTest<Preferences> {

    def populateValidParams(params) {
        assert params != null

        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
        assert false, "TODO: Provide a populateValidParams() implementation for this generated test suite"
    }

    void "Test the index action returns the correct model"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * list(_) >> []
            1 * count() >> 0
        }

        when: "The index action is executed"
        controller.index()

        then: "The model is correct"
        !model.preferencesList
        model.preferencesCount == 0
    }

    void "Test the create action returns the correct model"() {
        when: "The create action is executed"
        controller.create()

        then: "The model is correctly created"
        model.preferences != null
    }

    void "Test the save action with a null instance"() {
        when: "Save is called for a domain instance that doesn't exist"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        controller.save(null)

        then: "A 404 error is returned"
        response.redirectedUrl == '/preferences/index'
        flash.message != null
    }

    void "Test the save action correctly persists"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * save(_ as Preferences)
        }

        when: "The save action is executed with a valid instance"
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        populateValidParams(params)
        def preferences = new Preferences(params)
        preferences.id = 1

        controller.save(preferences)

        then: "A redirect is issued to the show action"
        response.redirectedUrl == '/preferences/show/1'
        controller.flash.message != null
    }

    void "Test the save action with an invalid instance"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * save(_ as Preferences) >> { Preferences preferences ->
                throw new ValidationException("Invalid instance", preferences.errors)
            }
        }

        when: "The save action is executed with an invalid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        def preferences = new Preferences()
        controller.save(preferences)

        then: "The create view is rendered again with the correct model"
        model.preferences != null
        view == 'create'
    }

    void "Test the show action with a null id"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * get(null) >> null
        }

        when: "The show action is executed with a null domain"
        controller.show(null)

        then: "A 404 error is returned"
        response.status == 404
    }

    void "Test the show action with a valid id"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * get(2) >> new Preferences()
        }

        when: "A domain instance is passed to the show action"
        controller.show(2)

        then: "A model is populated containing the domain instance"
        model.preferences instanceof Preferences
    }

    void "Test the edit action with a null id"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * get(null) >> null
        }

        when: "The show action is executed with a null domain"
        controller.edit(null)

        then: "A 404 error is returned"
        response.status == 404
    }

    void "Test the edit action with a valid id"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * get(2) >> new Preferences()
        }

        when: "A domain instance is passed to the show action"
        controller.edit(2)

        then: "A model is populated containing the domain instance"
        model.preferences instanceof Preferences
    }


    void "Test the update action with a null instance"() {
        when: "Save is called for a domain instance that doesn't exist"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        controller.update(null)

        then: "A 404 error is returned"
        response.redirectedUrl == '/preferences/index'
        flash.message != null
    }

    void "Test the update action correctly persists"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * save(_ as Preferences)
        }

        when: "The save action is executed with a valid instance"
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        populateValidParams(params)
        def preferences = new Preferences(params)
        preferences.id = 1

        controller.update(preferences)

        then: "A redirect is issued to the show action"
        response.redirectedUrl == '/preferences/show/1'
        controller.flash.message != null
    }

    void "Test the update action with an invalid instance"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * save(_ as Preferences) >> { Preferences preferences ->
                throw new ValidationException("Invalid instance", preferences.errors)
            }
        }

        when: "The save action is executed with an invalid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        controller.update(new Preferences())

        then: "The edit view is rendered again with the correct model"
        model.preferences != null
        view == 'edit'
    }

    void "Test the delete action with a null instance"() {
        when: "The delete action is called for a null instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(null)

        then: "A 404 is returned"
        response.redirectedUrl == '/preferences/index'
        flash.message != null
    }

    void "Test the delete action with an instance"() {
        given:
        controller.preferencesService = Mock(PreferencesService) {
            1 * delete(2)
        }

        when: "The domain instance is passed to the delete action"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(2)

        then: "The user is redirected to index"
        response.redirectedUrl == '/preferences/index'
        flash.message != null
    }
}






