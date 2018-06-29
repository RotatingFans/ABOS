package abos.server

import grails.gorm.services.Service

@Service(Year)

interface YearDataService {
    Year save(String year)

    void delete(Serializable id)

    Year findByyear(String year)

}
