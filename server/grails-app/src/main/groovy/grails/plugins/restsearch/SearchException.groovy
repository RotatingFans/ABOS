package grails.plugins.restsearch

class SearchException extends Exception {

    Integer number
    Map params
    String solution

    SearchException(int number) {
        this(number, '')
    }

    SearchException(int number, String message) {
        this(number, message, '', [:])
    }

    SearchException(int number, String message, String solution, Map params) {
        super(message)
        this.number = number
        this.solution = solution
        this.params = params
    }

    def addParam(String key, String value) {
        params[key] = value
        return this
    }

    def setSolution(solution) {
        this.solution = solution
        return this
    }
}
