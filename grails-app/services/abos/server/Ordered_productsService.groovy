package abos.server

import grails.gorm.services.Service

@Service(Ordered_products)
interface Ordered_productsService {

    Ordered_products get(Serializable id)

    List<Ordered_products> list(Map args)

    Long count()

    void delete(Serializable id)

    Ordered_products save(Ordered_products ordered_products)

}