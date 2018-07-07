package grails.plugins.restsearch

class NotFoundController {

    def index() {

        def map = [
                error  : 404,
                message: 'Not Found'
        ]
        render map as grails.converters.JSON
    }
}
