package abos.server

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])

class Groups {
    String groupName
    Year year
    static hasMany = [userYears: UserYear]
    static contstraints = {
        groupName size: 1..255, unique: true

    }
    static mapping = {
        year lazy: false
    }
    static restsearch = [
            categoryName: true,
            id          : true,
            'year'      : [field: 'year.id'],
            'year.id'   : true
    ]
}
