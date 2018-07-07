package grails.plugins.restsearch

class InternalServerErrorController {

    def index() {
        def exception = request.exception.cause.targetException

        def map = [
                error  : 500,
                message: exception?.message
        ]
        if (exception instanceof SearchException) {
            map.number = "${exception.number}"
            map.solution = "${exception.solution}"
            exception.params.each { k, v ->
                map[k] = v
            }
        }

        render map as grails.converters.JSON

    }
}
