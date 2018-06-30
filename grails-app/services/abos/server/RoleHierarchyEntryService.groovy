package abos.server

import grails.gorm.services.Service

@Service(RoleHierarchyEntry)
interface RoleHierarchyEntryService {

    RoleHierarchyEntry get(Serializable id)

    List<RoleHierarchyEntry> list(Map args)

    Long count()

    void delete(Serializable id)

    RoleHierarchyEntry save(RoleHierarchyEntry roleHierarchyEntry)

}