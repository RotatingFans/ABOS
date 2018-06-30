package abos.server

class Year {
    String year
    static hasMany = [orderedProducts: Ordered_products]

    static constraints = {
        year size: 4..4, unique: true, blank: false
    }
}
