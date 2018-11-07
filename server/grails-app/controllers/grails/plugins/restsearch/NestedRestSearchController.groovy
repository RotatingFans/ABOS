/*******************************************************************************
 * ABOS
 * Copyright (C) 2018 Patrick Magauran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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
