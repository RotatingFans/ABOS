package abos.server

import grails.gorm.services.Service

@Service(Customers)
interface CustomersService {

    Customers get(Serializable id)

    List<Customers> list(Map args)

    Long count()

    void delete(Serializable id)

    Customers save(Customers customers)

}