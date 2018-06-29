package abos.server

import Utilities.DbInt
import Utilities.Order
import javafx.util.Pair
import org.springframework.beans.factory.annotation.Autowired

import java.text.DateFormat
import java.text.SimpleDateFormat

class convertFrmMySqlToGORM {
    @Autowired
    RoleDataService roleDataService

    @Autowired
    UserRoleDataService userRoleDataService

    @Autowired
    UserDataService userDataService
    @Autowired
    CategoriesDataService categoriesDataService
    @Autowired
    ProductsDataService productsDataService
    @Autowired
    CustomersDataService customersDataService
    @Autowired
    OrdersDataService ordersDataService
    @Autowired
    YearDataService yearDataService
    @Autowired
    OrderedProductsDataService orderedProductsDataService

    static def convert(Pair<String, String> userPass) {

        /*
        login
        Loop through users
            - add users with test as password
            - Add roles
            - Add user_roles
        Loop through years
            - add year/saveID
            - Add preferences
            - Add categories
            - Add products
            - Add Customers
            - Add Orders
            - Add ordered_products
         */

        if (DbInt.verifyLoginAndUser(userPass) && DbInt.testConnection() && DbInt.isAdmin()) {
            Role adminRole = new Role(authority: 'ROLE_ADMIN').save()
            Role userRole = new Role(authority: 'ROLE_USER').save()

            DbInt.getUsers().each { user ->
                User usr = new User(username: user.getUserName(), password: "test").save()
                UserRole.create usr, user.isAdmin() ? adminRole : userRole
                UserRole.withSession {
                    it.flush()
                    it.clear()
                }
            }
            DbInt.getYears().each { yr ->
                Utilities.Year yearObj = new Utilities.Year(yr)
                Year year = new Year(year: yr).save()
                yearObj.getCategories().each { cat ->
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    Date date = format.parse(cat.catDate)
                    new Categories(categoryName: cat.catName, deliveryDate: date, year: year).save()
                }
                yearObj.getAllProducts().each { prod ->
                    new Products(humanProductId: prod.productID, productName: prod.productName, unitSize: prod.productSize, unitCost: prod.productUnitPrice, category: Categories.findByCategoryName(prod.productCategory), year: year).save()
                }
                yearObj.getUsers().each { usr ->
                    User user = User.findByUsername(usr.getUserName())
                    yearObj.getCustomers(usr.getUserName()).each { cust ->
                        Customers customers = new Customers(customerName: cust.getName(), streetAddress: cust.getAddr(), city: cust.getTown(), state: cust.getState(), zipCode: cust.getZip(), phone: cust.getPhone(), custEmail: cust.getEmail(), latitude: cust.getLat(), longitude: cust.getLon(), ordered: false, home: false, interested: false, donation: cust.getDontation(), year: year).save()
                        if (cust.getOrderId > 0) {
                            customers.ordered = true
                            customers.save()
                            int id = cust.getOrderId()
                            Order.orderDetails dets = Order.getOrder(yr, id)
                            Order.orderArray ordArr = cust.getOrderArray()
                            //User user, Customers customers, BigDecimal cost, int quantity, BigDecimal amountPaid, Boolean delivered, Year year, String userName
                            Orders orders = new Orders(user: user, customer: customers, cost: dets.totalCost, quantity: dets.totalQuantity, amountPaid: dets.paid, delivered: dets.delivered, year: year, userName: user.username).save()
                            ordArr.orderData.each { data ->
                                new Ordered_products(user: user, customer: customers, order: orders, products: products.findByHumanProductId(data.productID), quantity: data.orderedQuantity, extendedCost: data.extendedCost, year: year, userName: user.username).save()
                            }
                        }

                    }
                }

            }

        }
    }
}
