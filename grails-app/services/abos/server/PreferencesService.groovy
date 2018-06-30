package abos.server

import grails.gorm.services.Service

@Service(Preferences)
interface PreferencesService {

    Preferences get(Serializable id)

    List<Preferences> list(Map args)

    Long count()

    void delete(Serializable id)

    Preferences save(Preferences preferences)

}