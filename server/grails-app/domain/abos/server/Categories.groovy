package abos.server

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
//@Resource(uri = '/api/Categories')

class Categories {
    String categoryName
    Date deliveryDate
    Year year
    static contstraints = {
        categoryName size: 1..255, unique: true

    }
    static restsearch = [
            categoryName: true,
            id          : true,
            deliveryDate: true,
            year        : true
    ]

}
