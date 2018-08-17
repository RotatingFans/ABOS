package abos.server

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class AuthCheckController {
    static responseFormats = ['json', 'xml']

    def index() {

        if (!isLoggedIn()) {
            render status: 401
        } else {
            render status: 200
        }
    }
}
