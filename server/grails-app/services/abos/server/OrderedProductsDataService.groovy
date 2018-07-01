package abos.server

import grails.gorm.services.Service

@Service(Ordered_products)
/*
    static belongsTo = [user: User, customer: customers, order: Orders]
    Products products
    int quantity
    BigDecimal extendedCost
    Year year
    String userName
 */
interface OrderedProductsDataService {


    void delete(Serializable id)

}
