package grails.plugins.restsearch

import grails.rest.RestfulController

import static grails.gorm.multitenancy.Tenants.withCurrent
import static grails.gorm.multitenancy.Tenants.withId

@groovy.util.logging.Slf4j
abstract class RestSearchController<T> extends RestfulController<T> {

    def responseFormats = ['json', 'xml', 'hal']

    def restSearchService

    RestSearchController(Class<T> resource) {
        this(resource, false)
    }

    RestSearchController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }


    def index() {
        def result = search(params)

        // headers for response
        header 'X-Search-Hit-Count', result.totalCount ?: 0
        header 'X-Current-Offset', params.offset ?: 0
        header 'X-Search-Sort', params.'sort'
        header 'X-Search-Order', params.'order'
        header 'X-Search-Q', params.'q'
        header 'X-Search-Fields', params.'fields'
        header 'Access-Control-Expose-Headers', 'X-Search-Hit-Count, X-Current-Offset, X-Search-Sort, X-Search-Order, X-Search-Q, X-Search-Fields'
        if (params.headerOnly || request.method == 'HEAD') {
            render ''
        } else {
            def results = []
            result.each({
//				log.trace(it.properties)
                if (it.hasProperty("userName")) {
                    withId(it.userName, { session ->

                        results.push(resource.read(it.id))
                    })
                } else {
                    withCurrent({ session ->

                        results.push(resource.read(it.id))
                    })
                }

            })
            respond results, [includes: includeFields]
        }
        return
    }

    def show() {
        def result = queryForResource(params.id)
        log.trace "show.result=${result}"
        if (result)
            respond result, [includes: includeFields]
        else
            notFound()
    }


    protected def getFilterParams() {
        // returns filter params like ParentProperty Value or UserName, etc...
        []
    }

    protected def search(allSearchTokens, paginationParams) {
        log.trace "search($allSearchTokens, $paginationParams)"
        restSearchService.search(resource, allSearchTokens, paginationParams)
    }

    protected def search(Map params) {
        log.trace "search($params)"
        def tokenizedParams = tokenizeAllSearchParams(params.list('q')) ?: filterParams
        search(tokenizedParams, getPaginationParams(params))
    }

    protected def getPaginationParams(Map params) {
        return [
                'sort'  : params.sort ?: 'id',
                'order' : params.order ?: 'desc',
                'max'   : params.max ?: 10,
                'offset': params.offset ?: 0
        ]
    }

    protected def tokenizeAllSearchParams(List q) {
        log.trace "tokenizeAllSearchParams($q)"
        def allSearchParams = q.collect { tokenizeSearchParams(it) }
        log.trace "tokenizeAllSearchParams($q) == ${allSearchParams}"
        allSearchParams
    }

    protected def tokenizeSearchParams(String q) {
        log.trace "tokenizeSearchParams($q)"
        def filters = getFilterParams()
        log.trace "filters to add in tokenizeSearchParams=${filters}"
        def allSearchParams
        def thisSearchParams = [:]

        log.trace "q=${q}"
        q?.split(';').each {
            // split only the first separation token.
            // eg. if the separation token is : and you have time:15:32, this
            // means only the time and 15:32 will be splitted and not
            // the 15 will be separated from 32
            if ((it?.trim())) {

                def idx = [it.indexOf('='), it.indexOf(':')].findAll { it > -1 }.min()
                log.trace "Index of clause separator in ${it} is ${idx}"

                if (idx > -1) {
                    def arr = [it[0..idx - 1], it[idx + 1..it.size() - 1]]
                    if (arr.size() > 1)
                        thisSearchParams[arr[0]] = arr[1]
                    else
                        thisSearchParams[arr[0]] = true
                } else {
                    thisSearchParams[it] = true
                }
            }
        }
        allSearchParams = thisSearchParams

        if (filters)
            allSearchParams += filters

        log.trace "tokenizeSearchParams returns: ${allSearchParams}"
        allSearchParams
    }

    @Override
    protected T queryForResource(Serializable id) {
        log.trace "queryForResource($id)"
        def p = ["id:${id}"]
        def tokenizedParams = tokenizeAllSearchParams(p)
        log.trace "queryForResource.tokenizedParams == ${tokenizedParams}"
        def result = search(tokenizedParams, [max: 1])
        log.trace "queryForResult found ${result.size()} result(s)"
        if (result.size() == 1) {
            return result[0]
        }
        return null
    }

    protected def getIncludeFields() {
        log.trace "getting field list to filter: params.fields=${params.fields}"
        def fieldsToInclude = params.fields?.tokenize(',')*.trim()
        log.trace "field list to filter is ${fieldsToInclude}"
        fieldsToInclude
    }

}
