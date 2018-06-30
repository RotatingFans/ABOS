package abos.server

import grails.gorm.services.Service

@Service(Orders)
interface OrdersService {

    Orders get(Serializable id)

    List<Orders> list(Map args)

    Long count()

    void delete(Serializable id)

    Orders save(Orders orders)

}