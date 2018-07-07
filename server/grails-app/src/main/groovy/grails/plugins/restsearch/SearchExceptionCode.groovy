package grails.plugins.restsearch

enum SearchExceptionCode {
    SEARCH_FIELD_NOT_EXISTS(101),
    INVALID_SEARCH_VALUE(102)

    private final int number

    SearchExceptionCode(int number) {
        this.number = number
    }

    def getNumber() {
        number
    }
}
