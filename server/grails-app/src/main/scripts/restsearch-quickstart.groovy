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

description "Add basic configuration to RestSearch plugin", "grails restsearch-wuickstart"

file('grails-app/conf/spring/resources.groovy').withWriterAppend { BufferedWriter writer ->
    writer.newLine()
    writer.newLine()
    writer.writeLine '// Added by the Rest Search plugin:'
    writer.writeLine 'beans = {'
    writer.writeLine '	"jsonEnumRenderer"(grails.rest.render.json.JsonRenderer, Enumeration)'
    writer.writeLine '	"xmlEnumRenderer"(grails.rest.render.xml.XmlRenderer, Enumeration)'
    writer.writeLine '	"halEnumRenderer"(grails.rest.render.hal.HalJsonRenderer, Enumeration)'
    writer.writeLine '	"jsonEnumCollectionRenderer"(grails.rest.render.json.JsonCollectionRenderer, Enumeration)'
    writer.writeLine '	"xmlEnumCollectionRenderer"(grails.rest.render.xml.XmlCollectionRenderer, Enumeration)'
    writer.writeLine '	"halEnumCollectionRenderer"(grails.rest.render.hal.HalJsonCollectionRenderer, Enumeration)'
    writer.writeLine '	'
    writer.writeLine '	for (domainClass in grailsApplication.domainClasses) {'
    writer.writeLine '		int domainClassRnd = new Random().nextInt(100000);'
    writer.writeLine '		def className = "${domainClass.shortName}_${domainClassRnd}"'
    writer.writeLine '		log.debug "Defining converters for class ${className}"'


    writer.writeLine '		 "json\${className}Renderer"(grails.rest.render.json.JsonRenderer, domainClass.clazz) {'
    writer.writeLine '			 excludes = ["class"]'
    writer.writeLine '		 }'
    writer.writeLine '		 "xml\${className}Renderer"(grails.rest.render.xml.XmlRenderer, domainClass.clazz) {'
    writer.writeLine '			 excludes = ["class"]'
    writer.writeLine '		 }'
    writer.writeLine '		 "hal\${className}Renderer"(grails.rest.render.hal.HalJsonRenderer, domainClass.clazz) {'
    writer.writeLine '			 excludes = ["class"]'
    writer.writeLine '		 }'
    writer.writeLine '		 "json\${className}CollectionRenderer"(grails.rest.render.json.JsonCollectionRenderer, domainClass.clazz) {'
    writer.writeLine '			 excludes = ["class"]'
    writer.writeLine '		 }'
    writer.writeLine '		 "xml\${className}CollectionRenderer"(grails.rest.render.xml.XmlCollectionRenderer, domainClass.clazz) {'
    writer.writeLine '			 excludes = ["class"]'
    writer.writeLine '		 }'
    writer.writeLine '		 "hal\${className}CollectionRenderer"(grails.rest.render.hal.HalJsonCollectionRenderer, domainClass.clazz) {'
    writer.writeLine '			 excludes = ["class"]'
    writer.writeLine '		 }'
    writer.writeLine '	'
    writer.writeLine '	}'
    writer.writeLine '}'


}
