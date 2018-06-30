package abos.server

import grails.gorm.services.Service

@Service(Categories)
interface CategoriesService {

    Categories get(Serializable id)

    List<Categories> list(Map args)

    Long count()

    void delete(Serializable id)

    Categories save(Categories categories)

}