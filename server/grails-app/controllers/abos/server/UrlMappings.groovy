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

        "/api/Reports"(controller: "Reports", action: "index")
        "/api/AuthCheck"(controller: "AuthCheck", action: "index")
        "/"(controller: 'application', action: 'index')
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
