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
    }
}
