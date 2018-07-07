package grails.plugins.restsearch

abstract class customRestSearchController<T> extends RestSearchController {




    customRestSearchController(Class<T> resource) {
        this(resource, false)
    }

    customRestSearchController(Class<T> resource, boolean readOnly) {
        super(resource, readOnly)
    }

    /*def index() {
        def result = search(params)

        // headers for response
        header 'X-Total-Count', result.totalCount
        header 'X-Current-Offset', params.offset
        header 'X-Search-Sort', params.'sort'
        header 'X-Search-Order', params.'order'
        header 'X-Search-Q', params.'q'

        if (params.headerOnly) {
            render ''
        } else {
            respond result, [includes: includeFields]
        }
        return
    }

    protected def getFilterParams() {
        // returns filter params like ParentProperty Value or UserName, etc...
    }

    protected def search(Map params) {
        resource.log.trace "search($params)"
        this.CustomRestSearchService.search(resource, getSearchParams(params), getPaginationParams(params))
    }

    protected def getPaginationParams(Map params) {
        return [
                'sort'  : params.sort ?: 'id',
                'order' : params.order ?: 'desc',
                'max'   : params.max ?: 10,
                'offset': params.offset ?: 0
        ]
    }

    protected def getSearchParams(params) {
        resource.log.trace "getSearchParams($params)"
        def searchParams = [:]

        params.q?.split(';').each {
            // split only the first separation token.
            // eg. if the separation token is : and you have time:15:32, this
            // means only the time and 15:32 will be splitted and not
            // the 15 will be separated from 32
            if ((it?.trim())) {


                def idx = [it.indexOf('='), it.indexOf(':')].findAll { it > -1 }.min()
                resource.log.trace "Index of clause separator in ${it} is ${idx}"
                if (idx > -1) {
                    def arr = [it[0..idx - 1], it[idx + 1..it.size() - 1]]
                    if (arr.size() > 1)
                        searchParams[arr[0]] = arr[1]
                    else
                        searchParams[arr[0]] = true
                } else {
                    searchParams[it] = true
                }
            }
        }
        resource.log.trace "getSearchParams returns: ${searchParams}"
        def filters = filterParams
        if (filters)
            searchParams += filters
        searchParams
    }

    @Override
    protected T queryForResource(Serializable id) {
        resource.log.trace "queryForResource($id)"
        def p = [q: "id:${id}"]
        def result = search(p)
        if (result.size() > 0)
            return result[0]
        return null
    }

    protected def getIncludeFields() {
        resource.log.trace "getting fields to include from params.fields=${params.fields}"
        params.fields?.tokenize(',')*.trim()
    }
*/
}
