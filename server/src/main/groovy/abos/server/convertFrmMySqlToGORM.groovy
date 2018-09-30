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
            - Add customers
            - Add Orders
            - Add ordered_products
         */

        if (DbInt.verifyLoginAndUser(userPass) && DbInt.testConnection() && DbInt.isAdmin()) {
            Role adminRole = new Role(authority: 'ROLE_ADMIN').save()
            Role userRole = new Role(authority: 'ROLE_USER').save()
            System.out.print("converting...")
            DbInt.getUsers().each { user ->
                //System.out.print("Users")

                User usr = new User(username: user.getUserName(), password: 'test')
                if (!usr.save()) {
                    usr.errors.allErrors.each {
                        println it
                    }
                }
                UserRole.create usr, user.isAdmin() ? adminRole : userRole, true

            }
            DbInt.getYears().each { yr ->
                Utilities.Year yearObj = new Utilities.Year(yr)
                Year year = Year.findOrCreateWhere(year: yr)
                if (!year.save()) {
                    year.errors.allErrors.each {
                        println it
                    }
                }
                yearObj.getCategories().each { cat ->
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    Date date = format.parse(cat.catDate)
                    new Categories(categoryName: cat.catName, deliveryDate: date, year: year).save()
                }
                yearObj.getAllProducts().each { prod ->
                    Products prds = new Products(humanProductId: prod.productID, productName: prod.productName, unitSize: prod.productSize, unitCost: prod.productUnitPrice, category: Categories.findByCategoryName(prod.productCategory), year: year)
                    if (!prds.save()) {
                        prds.errors.allErrors.each {
                            println it
                        }
                    }
                }
                yearObj.getUsers().each { usr ->

                    User user = User.findByUsername(usr.getUserName())
                    //System.out.print(user.toString())

                    yearObj.getCustomers(usr.getUserName()).each { cust ->
                        //System.out.print("Customer")

                        Customers customers = new Customers(customerName: cust.getName(), streetAddress: cust.getAddr(), city: cust.getTown(), state: cust.getState(), zipCode: cust.getZip(), phone: cust.getPhone(), custEmail: cust.getEmail(), latitude: cust.getLat(), longitude: cust.getLon(), ordered: false, home: false, interested: false, donation: cust.getDontation(), year: year, user: user, userName: user.username)
                        //println year
                        if (!customers.save()) {
                            customers.errors.allErrors.each {
                                println it
                            }
                        }
                        println cust.getOrderId()
                        if (cust.getOrderId() > 0) {
                            System.out.print("Order")

                            //customers.ordered = true
                            //customers.save()
                            int id = cust.getOrderId()
                            def dets = Order.getOrder(yr, cust.getId())
                            def ordArr = cust.getOrderArray()
                            //User user, customers customers, BigDecimal cost, int quantity, BigDecimal amountPaid, Boolean delivered, Year year, String userName
                            Orders orders = new Orders(user: user, customer: customers, cost: dets.totalCost, quantity: dets.totalQuantity, amountPaid: dets.paid, delivered: dets.delivered, year: year, userName: user.username)
                            if (!orders.save(flush: true)) {
                                orders.errors.allErrors.each {
                                    println it
                                }
                            }
                            println orders
                            ordArr.orderData.each { data ->
                                /* println data.productID
                                 println Products.findByHumanProductIdAndYear(data.productID, year)
                                 println data.orderedQuantity
                                 println data.extendedCost.toString()
                                 //println customers
                                 println year*/
                                Ordered_products ops = new Ordered_products(user: user, customer: customers, order: orders, products: Products.findByHumanProductIdAndYear(data.productID, year), quantity: data.orderedQuantity, extendedCost: data.extendedCost, year: year, userName: user.username)
                                if (!ops.save()) {
                                    ops.errors.allErrors.each {
                                        println it
                                    }
                                }
                            }
                        }

                    }
                }

            }

        }
    }
}
