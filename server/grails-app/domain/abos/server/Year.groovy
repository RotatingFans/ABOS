package abos.server

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
//@Resource(uri = '/api/Years')
class Year {
    String year
    static hasMany = [orderedProducts: Ordered_products]

    static constraints = {
        year size: 4..4, unique: true, blank: false
    }
}
