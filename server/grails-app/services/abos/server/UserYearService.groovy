package abos.server

import grails.gorm.services.Service

@Service(UserYear)
interface UserYearService {

    UserYear get(Serializable id)

    List<UserYear> list(Map args)

    Long count()

    void delete(Serializable id)

    UserYear save(UserYear userYear)

}