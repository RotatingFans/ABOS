package abos.server

import grails.gorm.services.Service

@Service(Year)
interface YearService {

    Year get(Serializable id)

    List<Year> list(Map args)

    Long count()

    void delete(Serializable id)

    Year save(Year year)

}