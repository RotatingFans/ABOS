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

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN'])

class ProductsTableController {
    static responseFormats = ['json', 'xml']

    @Transactional
    def index() {
        def jsonParams = request.JSON
        def newProducts = jsonParams.newProducts
        def updatedProducts = jsonParams.updatedProducts
        def deletedProducts = jsonParams.deletedProducts

        newProducts.each { product ->
            if (product.status != 'DELETE') {
                def prod = new Products(humanProductId: product.humanProductId, productName: product.productName, unitSize: product.unitSize, unitCost: product.unitCost, category: Categories.findById(product.category), year: Year.findById(5))

                if (!prod.save()) {
                    prod.errors.allErrors.each {
                        println it
                    }
                }
            }
        }
        updatedProducts.each { product ->
            def prod = Products.findById(product.id)

            prod.humanProductId = product.humanProductId
            prod.productName = product.productName
            prod.unitSize = product.unitSize
            prod.unitCost = product.unitCost
            prod.category = Categories.findById(product.category)
            prod.save()
            prod.errors.allErrors.each {
                println it
            }
        }
        deletedProducts.each { product ->
            def prod = Products.findById(product.id)
            prod.delete()
            prod.errors.allErrors.each {
                println it
            }
        }
        render([status: "success"] as JSON, status: 200)
    }
}
