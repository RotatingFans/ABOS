package abos.server

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class Products {
    String humanProductId
    String productName
    String unitSize
    BigDecimal unitCost
    Categories category
    Year year

    static constraints = {
        humanProductId unique: false, size: 1..255
        productName size: 1..255
        unitSize size: 1..255
        unitCost min: 0.0, scale: 2
        category nullable: true
    }

    static restsearch = [
            humanProductId: true,
            id            : true,
            'year'        : [field: 'year.id'],
            'year.id'     : true,
            productName   : true,
            unitSize      : true,
            unitCost      : true,
            category      : true
    ]
}