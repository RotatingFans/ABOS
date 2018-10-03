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

class UrlMappings {

    static mappings = {
/*        delete "/$controller/$id(.$format)?"(action: "delete")
        get "/$controller(.$format)?"(action: "index")
        get "/$controller/$id(.$format)?"(action: "show")
        post "/$controller(.$format)?"(action: "save")
        put "/$controller/$id(.$format)?"(action: "update")
        patch "/$controller/$id(.$format)?"(action: "patch")*/
        "/api/Categories"(resources: 'Categories')
        "/api/customers"(resources: 'Customers')
        "/api/Ordered_products"(resources: 'Ordered_products')
        "/api/Orders"(resources: 'Orders')
        "/api/Preferences"(resources: 'Preferences')
        "/api/Products"(resources: 'Products')
        "/api/Role"(resources: 'Role')
        "/api/RoleHierarchyEntry"(resources: 'RoleHierarchyEntry')
        "/api/User"(resources: 'User')
        "/api/UserRole"(resources: 'UserRole')
        "/api/Years"(resources: 'Year')
        "/api/Group"(resources: 'Groups')

        "/api/Reports"(controller: "Reports", action: "index")
        // "/api/convert"(controller: "Convert", action: "index")
        "/api/ProductsMany"(controller: "ProductsTable", action: "index")
        get "/api/UserHierarchy"(controller: "UserHierarchy", action: "index")
        post "/api/UserHierarchy"(controller: "UserHierarchy", action: "save")
        "/api/AuthCheck"(controller: "AuthCheck", action: "index")
        "/api/currentUser"(controller: "User", action: "currentUser")
        "/"(controller: 'application', action: 'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
