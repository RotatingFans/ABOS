package abos.server

import grails.gorm.services.Service

@Service(Products)
interface ProductsService {

    Products get(Serializable id)

    List<Products> list(Map args)

    Long count()

    void delete(Serializable id)

    Products save(Products products)

}