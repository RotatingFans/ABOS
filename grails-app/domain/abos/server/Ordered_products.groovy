package abos.server

class Ordered_products {
    static belongsTo = [user: User, customer: Customers, order: Orders]
    Products products
    int quantity
    BigDecimal extendedCost
    static constraints = {
        quantity min: 0
        extendedCost min: 0, scale: 2
    }
}
