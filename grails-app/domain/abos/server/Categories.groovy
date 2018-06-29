package abos.server

class Categories {
    String categoryName
    Date deliveryDate
    static contstraints = {
        categoryName size: 1..255, unique: true
    }
}
