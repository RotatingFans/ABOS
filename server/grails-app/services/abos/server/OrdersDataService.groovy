package abos.server

import grails.gorm.services.Service

@Service(Orders)
/*    static belongsTo = [user: User, customer: Customers]
    BigDecimal cost
    int quanity
    BigDecimal amountPaid
    Boolean delivered
    Year year
    String userName
    */
interface OrdersDataService {
    Orders save(User user, Customers customer, BigDecimal cost, int quantity, BigDecimal amountPaid, Boolean delivered, Year year, String userName)

    void delete(Serializable id)
}