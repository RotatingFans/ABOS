package grails.plugins.restsearch

import abos.server.UserService
import grails.orm.PagedResultList
import grails.transaction.Transactional
import org.apache.commons.lang.time.DateUtils

import static grails.gorm.multitenancy.Tenants.currentId
import static grails.gorm.multitenancy.Tenants.withId

@Transactional(readOnly = true)
@groovy.util.logging.Slf4j
class RestSearchService {

    def grailsApplication

    static final DEFAULT_SEARCH_ITEM_SEPARATOR_TOKEN = ','
    static final DEFAULT_SEARCH_RANGE_TOKEN = '|'
    static final DEFAULT_SEARCH_ANY_TOKEN = '*'
    static final DEFAULT_SEARCH_LIKE_TOKEN = '*'
    static final DEFAULT_SEARCH_NEGATE_TOKEN = '!'
    static
    final DEFAULT_DATE_FORMATS = ['yyyy-MM-dd', 'yyyy-MM-DD HH:mm:ss', "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssz", "yyyy-MM-dd'T'HH:mm:ssZ", "yyyy-MM-dd'T'HH:mm:ssX"]

    def searchablePropertyMapName = "restsearch"

    UserService userService


    def search(Class resource, Map params, Map paginationParams) {
        search(resource, [params], paginationParams)
    }


    def search(Class resource, List params, Map paginationParams) {
        log.trace "Searching ${resource.name} with params=${params} and pagination=${paginationParams}"

        def max = paginationParams.max
        def offset = paginationParams.offset

        // allows ordering by multi-field separated by ,
        def sortFields = paginationParams.'sort'?.split(',')
        def orderFields = paginationParams.'order'?.split(',')

        PagedResultList results
        if (resource.getInterfaces().contains(grails.gorm.MultiTenant)) {
            def tCount = 0
            userService.listAllManaged().each {

                withId(it.username, { session ->
                    log.debug(currentId().toString())
                    def c = resource.createCriteria()

                    if (results) {
                        //			log.debug(c)

                        def result = c.list([max: max, offset: offset]) {
                            or {
                                log.trace "search.or.begin"
                                params.each { orJoin ->
                                    and {
                                        log.trace "search.and.begin for OR=${orJoin}"
                                        orJoin.each { k, value ->
                                            log.trace "Processing search param ${k} with value ${value}"
                                            def searchProps = resource."${searchablePropertyMapName}"[k]
                                            if (!searchProps)
                                                throw new SearchException(SearchExceptionCode.SEARCH_FIELD_NOT_EXISTS.number, "Search Property (or aka) does not exists: $k")
                                                        .addParam('field', k)
                                                        .addParam('value', value)
                                                        .setSolution('Check the Spelling')

                                            def propName, propType, propFormula, propValue
                                            if (searchProps == true) {
                                                propName = k
                                            } else {
                                                propName = searchProps.field
                                                propType = searchProps.type
                                                propFormula = searchProps.formula
                                                propValue = searchProps.value
                                            }

                                            def artefact = grailsApplication.getArtefact('Domain', resource.name)
                                            def persistentProperty = artefact?.getPersistentProperty(propName)
                                            propType = propType ?: persistentProperty?.getType()

                                            // if (params.containsKey(k) && propType) {
                                            // don't need to check in params because it is iterating over it!!!
                                            if (propType) {
                                                log.trace "propType of ${k} is ${propType}"
                                                if (propFormula) {
                                                    log.trace "A formula is defined to the property $k=$propFormula - evaluating..."
                                                    value = propFormula.call(value)
                                                } else if (propValue) {
                                                    log.trace "A fixed value is defined to the property $k=$propValue"
                                                    if (value && value == 'false') {
                                                        log.trace "value is false. so the fixed value will be negated"
                                                        value = "!${propValue}"
                                                    } else {
                                                        value = propValue
                                                    }

                                                }

                                                log.trace "Current value of param ${k} is ${value}"
                                                if (value) {
                                                    and {
                                                        processSearch(delegate, propName, propType, value)
                                                    }
                                                }
                                            }
                                        } // orJoin.each {k, value
                                        log.trace "search.and.end for OR=${orJoin}"
                                    } // and
                                } // orJoin
                                log.trace "search.or.end"
                            } // or

                            // TODO add pagination
                            // allows ordering by multi-field
                            sortFields.eachWithIndex { sortField, idx ->
                                def orderField
                                if (orderFields?.size() > idx)
                                    orderField = orderFields[idx]
                                else
                                    orderField = 'asc'

                                order(sortField, orderField)
                            }
                        }
                        results.addAll(result)
                        tCount += result.totalCount

                    } else {
                        results = c.list([max: max, offset: offset]) {
                            or {
                                log.trace "search.or.begin"
                                params.each { orJoin ->
                                    and {
                                        log.trace "search.and.begin for OR=${orJoin}"
                                        orJoin.each { k, value ->
                                            log.trace "Processing search param ${k} with value ${value}"
                                            def searchProps = resource."${searchablePropertyMapName}"[k]
                                            if (!searchProps)
                                                throw new SearchException(SearchExceptionCode.SEARCH_FIELD_NOT_EXISTS.number, "Search Property (or aka) does not exists: $k")
                                                        .addParam('field', k)
                                                        .addParam('value', value)
                                                        .setSolution('Check the Spelling')

                                            def propName, propType, propFormula, propValue
                                            if (searchProps == true) {
                                                propName = k
                                            } else {
                                                propName = searchProps.field
                                                propType = searchProps.type
                                                propFormula = searchProps.formula
                                                propValue = searchProps.value
                                            }

                                            def artefact = grailsApplication.getArtefact('Domain', resource.name)
                                            def persistentProperty = artefact?.getPersistentProperty(propName)
                                            propType = propType ?: persistentProperty?.getType()

                                            // if (params.containsKey(k) && propType) {
                                            // don't need to check in params because it is iterating over it!!!
                                            if (propType) {
                                                log.trace "propType of ${k} is ${propType}"
                                                if (propFormula) {
                                                    log.trace "A formula is defined to the property $k=$propFormula - evaluating..."
                                                    value = propFormula.call(value)
                                                } else if (propValue) {
                                                    log.trace "A fixed value is defined to the property $k=$propValue"
                                                    if (value && value == 'false') {
                                                        log.trace "value is false. so the fixed value will be negated"
                                                        value = "!${propValue}"
                                                    } else {
                                                        value = propValue
                                                    }

                                                }

                                                log.trace "Current value of param ${k} is ${value}"
                                                if (value) {
                                                    and {
                                                        processSearch(delegate, propName, propType, value)
                                                    }
                                                }
                                            }
                                        } // orJoin.each {k, value
                                        log.trace "search.and.end for OR=${orJoin}"
                                    } // and
                                } // orJoin
                                log.trace "search.or.end"
                            } // or

                            // TODO add pagination
                            // allows ordering by multi-field
                            sortFields.eachWithIndex { sortField, idx ->
                                def orderField
                                if (orderFields?.size() > idx)
                                    orderField = orderFields[idx]
                                else
                                    orderField = 'asc'

                                order(sortField, orderField)
                            }
                        }
                        tCount += results.totalCount

                    }
                })

            }
            results.setTotalCount(tCount)
        } else {
            def c = resource.createCriteria()
            results = c.list([max: max, offset: offset]) {
                or {
                    log.trace "search.or.begin"
                    params.each { orJoin ->
                        and {
                            log.trace "search.and.begin for OR=${orJoin}"
                            orJoin.each { k, value ->
                                log.trace "Processing search param ${k} with value ${value}"
                                def searchProps = resource."${searchablePropertyMapName}"[k]
                                if (!searchProps)
                                    throw new SearchException(SearchExceptionCode.SEARCH_FIELD_NOT_EXISTS.number, "Search Property (or aka) does not exists: $k")
                                            .addParam('field', k)
                                            .addParam('value', value)
                                            .setSolution('Check the Spelling')

                                def propName, propType, propFormula, propValue
                                if (searchProps == true) {
                                    propName = k
                                } else {
                                    propName = searchProps.field
                                    propType = searchProps.type
                                    propFormula = searchProps.formula
                                    propValue = searchProps.value
                                }

                                def artefact = grailsApplication.getArtefact('Domain', resource.name)
                                def persistentProperty = artefact?.getPersistentProperty(propName)
                                propType = propType ?: persistentProperty?.getType()

                                // if (params.containsKey(k) && propType) {
                                // don't need to check in params because it is iterating over it!!!
                                if (propType) {
                                    log.trace "propType of ${k} is ${propType}"
                                    if (propFormula) {
                                        log.trace "A formula is defined to the property $k=$propFormula - evaluating..."
                                        value = propFormula.call(value)
                                    } else if (propValue) {
                                        log.trace "A fixed value is defined to the property $k=$propValue"
                                        if (value && value == 'false') {
                                            log.trace "value is false. so the fixed value will be negated"
                                            value = "!${propValue}"
                                        } else {
                                            value = propValue
                                        }

                                    }

                                    log.trace "Current value of param ${k} is ${value}"
                                    if (value) {
                                        and {
                                            processSearch(delegate, propName, propType, value)
                                        }
                                    }
                                }
                            } // orJoin.each {k, value
                            log.trace "search.and.end for OR=${orJoin}"
                        } // and
                    } // orJoin
                    log.trace "search.or.end"
                } // or

                // TODO add pagination
                // allows ordering by multi-field
                sortFields.eachWithIndex { sortField, idx ->
                    def orderField
                    if (orderFields?.size() > idx)
                        orderField = orderFields[idx]
                    else
                        orderField = 'asc'

                    order(sortField, orderField)
                }
            }
        }

        return results
    }

    protected void processSearch(builder, propName, propType, value) {
        log.trace "processSearch(builder, $propName, $propType, $value)"
        def array = value.split(searchItemSeparatorToken)
        builder.or {
            log.trace "processSearch.builder.or start"
            array.each { arrayItem ->
                inListClaus(builder, propName, propType, arrayItem)
            }
            log.trace "processSearch.builder.or end"
        }
    }

    protected void inListClaus(builder, propName, propType, value) {
        log.trace "inListClaus(builder, $propName, $propType, $value)"
        def op = 'and'

        if (value.startsWith(searchNegateToken)) {
            op = 'not'
            value = value.substring(searchNegateToken.size())
        }

        builder."$op" {
            if (value.contains(searchRangeToken)) {
                // range
                rangeClause(builder, propName, propType, value.split("\\${searchRangeToken}"))
            } else if (value == searchAnyToken) {
                // any
                notNull(builder, propName)
            } else if (value.contains(searchLikeToken)) {
                // ilike clause
                ilikeClause(builder, propName, propType, value)
            } else {
                // eq clause
                eqClause(builder, propName, propType, value)
            }
        }
    }

    protected void rangeClause(builder, propName, propType, range) {
        log.trace "rangeClause(builder, $propName, $propType, $range)"
        builder.and {
            if (range[0]) {
                log.trace "range has minimum value of ${range[0]}"
                builder.gt propName, convert(range[0], propType)
            }
            if (range.size() > 1) {
                if (range[1]) {
                    log.trace "range has maximum value of ${range[1]}"
                    builder.lt propName, convert(range[1], propType)
                }
            }
        }
    }

    protected void eqClause(builder, propName, propType, value) {
        log.trace "eqClause(builder, $propName, $propType, $value)"
        builder.eq propName, convert(value, propType)
    }

    protected void ilikeClause(builder, propName, propType, value) {
        log.trace "ilikeClause(builder, $propName, $propType, $value)"
        value = value.replaceAll("\\${searchLikeToken}", '%')
        log.trace "ilikeClause value = $value"
        builder.ilike propName, value
    }

    protected void notNull(builder, propName) {
        log.trace "notNull(builder, $propName)"
        builder.isNotNull propName
    }


    protected def convert(value, type) {
        log.trace "Converting value ${value} as ${type}"
        def v
        try {
            if (type == Date) {
                if (value.isNumber()) {
                    log.trace "Value ${value} is a number. Creating a Date from milliseconds"
                    v = new Date(value.toLong())
                } else if (value =~ /[\+\-]?\d+d/) {
                    log.trace "Value ${value} is days form now. Adding it to current Date"
                    def m = (value =~ /([\+\-]?\d+)d/)
                    def days = m[0][1].toInteger()
                    v = new Date().clearTime() + days
                } else {
                    log.trace "Value ${value} is a string. Trying to parse it as a Date"
                    // JAVA nao aceita o Z como sendo UTC (o iso8601 aceita)
                    String[] formats = grailsApplication.config.grails.databinding.dateFormats ?: DEFAULT_DATE_FORMATS
                    log.trace "Parsing date ${value} with Formats ${formats}"
                    //v = DateUtils.parseDateStrictly(value.replaceAll('Z', 'UTC'), formats)
                    v = DateUtils.parseDateStrictly(value, formats)
                }
            } else { // is not a date
                v = type.valueOf(value)
            }
        } catch (e) {
            log.error "Error converting value ${value} with type ${type}", e
            throw new SearchException(SearchExceptionCode.INVALID_SEARCH_VALUE.number, "Invalid value: $value")
                    .addParam('type', type.name)
                    .addParam('value', value)
                    .setSolution('Check the value')
        }

        log.trace "Converted value of ${value} as ${type} is ${v} of class ${v.getClass()}"
        v
    }

    protected def getSearchItemSeparatorToken() {
        grailsApplication.config.search.searchItemSeparatorToken ?: DEFAULT_SEARCH_ITEM_SEPARATOR_TOKEN
    }

    protected def getSearchRangeToken() {
        grailsApplication.config.search?.searchRangeToken ?: DEFAULT_SEARCH_RANGE_TOKEN
    }

    protected def getSearchAnyToken() {
        grailsApplication.config.search?.searchAnyToken ?: DEFAULT_SEARCH_ANY_TOKEN
    }

    protected def getSearchLikeToken() {
        grailsApplication.config.search?.searchLikeToken ?: DEFAULT_SEARCH_LIKE_TOKEN
    }

    protected def getSearchNegateToken() {
        grailsApplication.config.search?.searchNegateToken ?: DEFAULT_SEARCH_NEGATE_TOKEN
    }
}
