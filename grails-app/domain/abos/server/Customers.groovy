package abos.server

class Customers {
    String customerName
    String streetAddress
    String city
    String state
    String zipCode
    String phone
    String custEmail
    double latitude
    double longitude
    boolean ordered
    boolean home
    boolean interested
    BigDecimal donation
    static belongsTo = [user: User]
    static hasOne = [order: Orders]
    static constraints = {
        customerName size: 1..255
        streetAddress size: 1.255
        city size: 1..255
        state size: 1..255
        zipCode size: 5
        phone size: 1..255, blank: true, nulllable: true
        custEmail email: true, size: 1..255, blank: true, nulllable: true
        latitude scale: 11
        longitude scale: 11
        ordered nullable: true
        home nullable: true
        interested nullable: true
        donation scale: 2, nullable: true
        order unique: true
    }

}
