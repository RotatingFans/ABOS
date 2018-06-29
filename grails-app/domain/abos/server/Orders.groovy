package abos.server

class Orders {
    static belongsTo = [user: User, customer: Customers]
    BigDecimal cost
    int quanity
    BigDecimal amountPaid
    Boolean delivered
    static constraints = {
        cost min: 0, scale: 2
        amountPaid min: 0, scale: 2
        quanity min: 0
    }
}
