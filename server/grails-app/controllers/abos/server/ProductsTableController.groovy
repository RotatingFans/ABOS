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
