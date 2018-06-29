package abos.server

import grails.gorm.services.Service

@Service(Categories)

interface CategoriesDataService {
    Categories save(String categoryName, Date deliveryDate, Year year)

    void delete(Serializable id)

    Categories findByCategoryName(String categoryName)

}
