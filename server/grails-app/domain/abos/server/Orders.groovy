package abos.server

import grails.gorm.MultiTenant
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])
//@Resource(uri = '/api/orders')
class Orders implements MultiTenant<abos.server.Orders> {
    static belongsTo = [user: User, customer: Customers]
    static hasMany = [orderedProducts: Ordered_products]
    BigDecimal cost
    int quantity
    BigDecimal amountPaid
    Boolean delivered
    Year year
    String userName

    static constraints = {
        cost min: 0.0, scale: 2
        amountPaid min: 0.0, scale: 2
        quantity min: 0
    }
    static mapping = {
        tenantId name: 'userName'
        children cascade: 'all-delete-orphan', lazy: false

    }

    def beforeInsert() {
        userName = user.username
    }

    def beforeUpdate() {
        userName = user.username
    }
    static restsearch = [
            id             : true,
            orderedProducts: true,
            cost           : true,
            quantity       : true,
            amountPaid     : true,
            delivered      : true,
            'year'         : [field: 'year.id'],
            'year.id'      : true,
            userName       : true,
    ]
}
