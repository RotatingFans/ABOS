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

package abos.server

import grails.core.GrailsApplication
import grails.gorm.DetachedCriteria
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.multitenancy.AllTenantsResolver
import org.grails.datastore.mapping.multitenancy.TenantResolver
import org.grails.datastore.mapping.multitenancy.exceptions.TenantNotFoundException
import org.springframework.beans.factory.annotation.Autowired

@CompileStatic
class CurrentUserTenantResolver implements TenantResolver, AllTenantsResolver {

    @Autowired
    GrailsApplication grailsApplication

    @CompileDynamic
    Iterable<Serializable> resolveTenantIds() {
        new DetachedCriteria(User)
                .distinct('userName')
                .list()
    }

    @Override
    Serializable resolveTenantIdentifier() throws TenantNotFoundException {

        String username = loggedUsername()
        if (username) {
            return username
        }
        throw new TenantNotFoundException("Tenant could not be resolved from Spring Security Principal")
    }

    @CompileDynamic
    String loggedUsername() {
        Object bean = grailsApplication.mainContext.getBean("springSecurityService")
        if (!(bean instanceof SpringSecurityService)) {
            return null
        }
        SpringSecurityService springSecurityService = (SpringSecurityService) bean

        if (springSecurityService.principal instanceof String) {
            return springSecurityService.principal
        }
        if (springSecurityService.principal instanceof GrailsUser) {
            return ((GrailsUser) springSecurityService.principal).username
        }
        null
    }
}
