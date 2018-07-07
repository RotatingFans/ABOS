description "Generate RestSearch Controller for the specified DOMAIN CLASS", "grails generate-rest-search-controller [DOMAIN CLASS]"

if (args) {
    def sourceClassName = args[0]
    def sourceClass = source(sourceClassName)

    if (sourceClass) {
        def model = model(sourceClass)
        render template: template('Controller.groovy'),
                destination: file("grails-app/controllers/${model.packagePath}/${model.convention('Controller')}.groovy"),
                model: model

        addStatus "generate-rest-search-controller completed for $sourceClassName"
    } else {
        error "Domain class not found"
    }
} else {
    error "No domain class specified"
}
