package abos.server

class Products {
    String humanProductId
    String productName
    String unitSize
    BigDecimal unitCost
    Categories category
    Year year

    static constraints = {
        humanProductId unique: true, size: 1..255
        productName size: 1..255
        unitSize size: 1..255
        unitCost min: 0.0, scale: 2
    }
}
