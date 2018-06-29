package abos.server

import grails.gorm.services.Service

@Service(Products)
interface ProductsDataService {
/*
    String humanProductId
    String productName
    String unitSize
    BigDecimal unitCost
    Categories category
    Year year

 */

    Products save(String humanProductId, String productName, String unitSize, BigDecimal unitCost, Categories category, Year year)

    void delete(Serializable id)

    Products findByhumanProductId(String humanProductId)
}