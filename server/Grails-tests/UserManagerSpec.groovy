package abos.server

import TestFor
import Specification

/**
 * See the API for {@link DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(UserManager)
class UserManagerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect: "fix me"
        true == false
    }
}
