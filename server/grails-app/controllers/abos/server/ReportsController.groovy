/*******************************************************************************
 * ABOS
 * Copyright (C) 2018 Patrick Magauran
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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
        def repTitle = ""
        def Splitting = ""
        def fileName = "report.pdf"

        switch (jsonParams.template) {
            case "customers_split":
                repTitle = "Year of " + Year.findById(jsonParams.Year).year
                Splitting = ""
                fileName = Year.findById(jsonParams.Year).year + "_customer_orders_" + Category + ".pdf"
                break

            case "Year Totals":
                repTitle = "Year of " + Year.findById(jsonParams.Year).year
                Splitting = ""
                fileName = Year.findById(jsonParams.Year).year + "_Total_Orders_" + Category + ".pdf"

                break

            case "Customer Year Totals":
                repTitle = customers.get(0).customerName + " " + Year.findById(jsonParams.Year).year + " Order"
                Splitting = ""
                fileName = customers.get(0).customerName + "_" + Year.findById(jsonParams.Year).year + "_Order_" + Category + ".pdf"

                break

            case "Customer All-Time Totals":
                repTitle = "All orders of " + customers.get(0).customerName
                Splitting = "Year:"
                fileName = customers.get(0).customerName + "_historical_orders.pdf"

                break
        }
        ReportGenerator rg = new ReportGenerator(jsonParams.template, jsonParams.Year.toString(), jsonParams.Scout_name, jsonParams.Scout_address, formattedAddress, jsonParams.Scout_Rank, jsonParams.Scout_Phone, jsonParams.LogoLocation.base64, Category, user, customers, repTitle, Splitting, jsonParams.Print_Due_Header, "")
        String fileLoc = rg.generate()
        println fileLoc
        InputStream pdf = new FileInputStream(fileLoc)
        header 'Access-Control-Expose-Headers', 'Content-Disposition'
        render(file: pdf, fileName: fileName, contentType: "application/pdf")
    }
}















