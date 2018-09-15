package abos.server

import grails.gorm.MultiTenant
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
class Ordered_products implements MultiTenant<abos.server.Ordered_products> {
    static belongsTo = [user: User, customer: Customers, order: Orders, products: Products]
    Products products
    int quantity
    BigDecimal extendedCost
    Year year
    String userName

    static constraints = {
        //quantity min: 0
        //extendedCost min: 0.0, scale: 2
    }
    static mapping = {
        tenantId name: 'userName'

    }

    def beforeInsert() {
        userName = user.username
    }

    def beforeUpdate() {
        userName = user.username
    }

    static restsearch = [
            id          : true,
            extendedCost: true,
            quantity    : true,
            'year'      : [field: 'year.id'],
            'year.id'   : true,
            userName    : true,
            user        : [field: 'user.id'],


    ]
}
