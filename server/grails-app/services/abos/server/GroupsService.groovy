package abos.server

import grails.gorm.services.Service

@Service(Groups)
interface GroupsService {
    Groups get(Serializable id)

    List<Groups> list(Map args)

    Long count()

    void delete(Serializable id)

    Groups save(Groups groups)
}
