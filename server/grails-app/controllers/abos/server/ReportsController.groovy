package abos.server

import grails.plugin.springsecurity.annotation.Secured
import org.grails.web.json.JSONArray

import static grails.gorm.multitenancy.Tenants.withId

@Secured(['ROLE_USER'])

class ReportsController {

    def index() {
        log.debug('returning report')
        log.debug(params.toString())
        log.debug(request.JSON.toString())
        def jsonParams = request.JSON
        //String reportType, String selectedYear, String scoutName, String scoutStAddr, String addrFormat, String scoutRank, String scoutPhone, String logoLoc, String category, String user, ArrayList<Customers> customers, String repTitle, String splitting, Boolean includeHeader, String pdfLoc1
        def formattedAddress = jsonParams.Scout_Town + ", " + jsonParams.Scout_State + " " + jsonParams.Scout_Zip
        def customers = new ArrayList<Customers>()

        def user = User.findById(jsonParams.User).getUsername()
        if (jsonParams.Customer instanceof JSONArray) {
            withId(user, {
                jsonParams.Customer.each {
                    customers.add(Customers.findById(it))
                }
            })
        }
        def Category = jsonParams.Category ?: "All"
        ReportGenerator rg = new ReportGenerator(jsonParams.template, jsonParams.Year.toString(), jsonParams.Scout_name, jsonParams.Scout_address, formattedAddress, jsonParams.Scout_Rank, jsonParams.Scout_Phone, jsonParams.LogoLocation.base64, Category, user, customers, "Test", "Test1", jsonParams.Print_Due_Header, "")
        String fileLoc = rg.generate()
        println fileLoc
        InputStream pdf = new FileInputStream(fileLoc)
        render(file: pdf, fileName: 'report.pdf', contentType: "application/pdf")
    }
}















