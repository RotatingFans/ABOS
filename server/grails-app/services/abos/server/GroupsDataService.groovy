package abos.server

import grails.gorm.services.Service

@Service(Groups)

interface GroupsDataService {
    Groups save(String groupName, Year year)

    void delete(Serializable id)

    Groups findByGroupName(String groupName)

}
