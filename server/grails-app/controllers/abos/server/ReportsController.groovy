package abos.server

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_USER'])

class ReportsController {

    def index() {
        log.debug('returning report')
        log.debug(params.toString())
        log.debug(request.JSON.toString())
        InputStream pdf = this.class.classLoader.getResourceAsStream('2018-Mar11-LG.pdf')
        render(file: pdf, fileName: 'report.pdf', contentType: "application/pdf")
    }
}
