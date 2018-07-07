package grails.plugins.restsearch

@groovy.util.logging.Slf4j
abstract class NestedRestSearchController<T, Z> extends RestSearchController<T> {

    def responseFormats = ['json', 'xml', 'hal']

    String parentPropName
    Class<Z> parentClass

    NestedRestSearchController(Class<T> resource, String parentPropName, Class<Z> parentClass) {
        super(resource, false)
        this.parentPropName = parentPropName
        this.parentClass = parentClass
    }

    NestedRestSearchController(Class<T> resource, parentPropName, Class<Z> parentClass, boolean readOnly) {
        super(resource, readOnly)
        this.parentPropName = parentPropName
        this.parentClass = parentClass
    }

    protected def getFilterParams() {
        // returns filter params like ParentProperty Value or UserName, etc...
        // always force parent property to match the parent id
        def parentId = params["${parentPropName}Id"]
        log.trace "Filtering by ${parentPropName}. ${parentPropName}Id == params[${parentPropName}Id]"
        return ["${parentPropName}Id": params["${parentPropName}Id"]]
    }

    protected T createResource(Map params) {
        def newParams = (params + [parentPropName: ["id": params["${parentPropName}Id"]]])
        super.createResource(newParams)
    }

    protected T createResource() {
        T instance = resource.newInstance()
        bindData instance, getObjectToBind()
        def parentInstance = restSearchService.search(parentClass, ["id": params["${parentPropName}Id"]], [max: 1])[0]
        instance[parentPropName] = parentInstance
        instance
    }
}
