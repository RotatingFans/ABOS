package abos.server

import grails.gorm.services.Service

@Service(Ordered_products)
/*
    static belongsTo = [user: User, customer: Customers, order: Orders]
    Products products
    int quantity
    BigDecimal extendedCost
    Year year
    String userName
 */
interface OrderedProductsDataService {
    Ordered_products save(User user, Customers customer, Orders order, Products products, int quantity, BigDecimal extendedCost, Year year, String userName)

    void delete(Serializable id)

}
