package abos.server

import grails.gorm.services.Service

@Service(Customers)
/*
    String customerName
    String streetAddress
    String city
    String state
    String zipCode
    String phone
    String custEmail
    double latitude
    double longitude
    boolean ordered
    boolean home
    boolean interested
    BigDecimal donation
    Year year
 */
interface CustomersDataService {
    Customers save(String customerName, String streetAddress, String city, String state, String zipCode, String phone, String custEmail, double latitude, double longitude, boolean ordered, boolean home, boolean interested, BigDecimal donation, Year year)

    void delete(Serializable id)

    Customers findByCustomerName(String customerName)

}
