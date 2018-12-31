// Initializes the `reports` service on path `/reports`
const createService = require('./reports.class.js');
const hooks = require('./reports.hooks');
const auth = require('@feathersjs/authentication');
const template = require('./Reports.js');
const testData = require('./test.json');
module.exports = function (app) {

  const paginate = app.get('paginate');

  const options = {
    paginate
  };

  async function returnManagedUserFilter(user, year) {
    const sequelize = app.get('sequelizeClient');
    const user_manager = sequelize.models['user_manager'];

    const uM = await user_manager.findAll({
      where: {manage_id: user, year_id: year}

    });
    if (uM) {
      let userIds = [];
      for (const manageEntry of uM) {
        userIds.push(manageEntry.user_id);
      }
      return {'$in': userIds};


    }
    return user;

  }

  async function generateJSON(inputs) {
    const seqClient = app.get('sequelizeClient');
    const categories = seqClient.models['categories'];
    const customersModel = seqClient.models['customers'];
    const groups = seqClient.models['groups'];
    const orderedProducts = seqClient.models['ordered_products'];
    const orders = seqClient.models['orders'];
    const products = seqClient.models['products'];
    const role = seqClient.models['role'];
    const RoleHierarchyEntry = seqClient.models['role_hierarchy_entry'];
    const userModel = seqClient.models['user'];
    const userManager = seqClient.models['user_manager'];
    const userRole = seqClient.models['user_role'];
    const userYear = seqClient.models['user_year'];
    const year = seqClient.models['year'];

    const {
      reportType,
      selectedYear,
      scoutName,
      scoutStAddr,
      scoutCityLine,
      scoutRank,
      scoutPhone,
      logoLoc,
      category,
      user,
      includeSubUsers,
      repTitle,
      splitting,
      includeHeader,
    } = inputs;

    let data = {
      "info": {
        "reportTitle": "",
        "logo": logoLoc,
        "name": scoutName,
        "streetAddress": scoutStAddr,
        "city": scoutCityLine,
        "PhoneNumber": scoutPhone,
        "rank": scoutRank,
        "TotalCost": "0",
        "TotalQuantity": "0"
      },
      "splitting": "",
      "column": [
        {
          "name": "ID"
        },
        {
          "name": "Name"
        },
        {
          "name": "Unit Size"
        },
        {
          "name": "Unit Cost"
        },
        {
          "name": "Quantity"
        },
        {
          "name": "Extended Price"
        }
      ],

    };
    if (reportType === "customers_split") {
      const splitObject = await generateCustomerSplit(inputs);
      data.customerYear = splitObject.customerYear;
      data.info.TotalCost = splitObject.totalCost;
      data.info.TotalQuantity = splitObject.totalQuantity;
    }


    return data;
  }

  async function generateCustomerSplit(inputs) {
    const seqClient = app.get('sequelizeClient');
    const categories = seqClient.models['categories'];
    const customersModel = seqClient.models['customers'];
    const groups = seqClient.models['groups'];
    const orderedProducts = seqClient.models['ordered_products'];
    const orders = seqClient.models['orders'];
    const products = seqClient.models['products'];
    const role = seqClient.models['role'];
    const RoleHierarchyEntry = seqClient.models['role_hierarchy_entry'];
    const userModel = seqClient.models['user'];
    const userManager = seqClient.models['user_manager'];
    const userRole = seqClient.models['user_role'];
    const userYear = seqClient.models['user_year'];
    const year = seqClient.models['year'];

    const {
      reportType,
      selectedYear,
      scoutName,
      scoutStAddr,
      scoutCityLine,
      scoutRank,
      scoutPhone,
      logoLoc,
      category,
      user,
      includeSubUsers,

      repTitle,
      splitting,
      includeHeader,
    } = inputs;

    let data = {

      "customerYear": []
    };
    try {
      let where = {year_id: selectedYear, user_id: user};
      let cat = await categories.findOne({
        where: {
          category_name: category,
          year_id: selectedYear
        }
      });
      if (includeSubUsers) {
        where = {year_id: selectedYear, user_id: await returnManagedUserFilter(user, selectedYear)};

      }
      let catWhere = {};
      if (category !== "All" && cat) {
        catWhere = {category_id: cat.id}
      }
      let customers = await customersModel.findAll({
        where: where,
        include: [{
          model: orders,
          include: {
            model: orderedProducts,
            as: "orderedProducts",
            include: {model: products, as: "products", where: catWhere, include: {model: categories}}
          }
        }, {model: year}]
      });
      let tCostT = 0.0;
      let quantityT = 0;

      for (const cust of customers) {
        let tCost = 0.0;
        let donation = 0.0;
        let custYr = {
          "header": false,
          "title": "",
          "custAddr": false,
          "name": "",
          "streetAddress": "",
          "city": "",
          "prodTable": false,
          "Product": [],
          "totalCost": "",
          "includeDonation": false,
          "Donation": "",
          "GrandTotal": "",
          "DonationThanks": [],
          "specialInfo": []
        };
        let orderArray = cust.order.orderedProducts;
        const totalQuantity = orderArray.reduce((a, b) => {
            return a + b.quantity
          }
          , 0);
        if (totalQuantity > 0) {
          custYr.custAddr = true;
          custYr.name = cust.customer_name;
          custYr.streetAddress = cust.street_address;
          custYr.city = cust.city + ' ' + cust.state + ", " + cust.zip_code;
          custYr.header = true;
          custYr.title = cust.customer_name + ' ' + cust.year.year + ' Order';

          if (includeHeader && category !== "All") {

            custYr.specialInfo.push({text: "*Notice: These products will be delivered to your house on " + cat.delivery_date.toLocaleDateString() + ('. Total paid to date: $' + cust.order.amount_paid)});

          }
          custYr.prodTable = true;
          for (const op of orderArray) {
            if ((op.products.category && op.products.category.category_name === category) || category === "All") {
              let product = {
                "ID": op.products.human_product_id,
                "Name": op.products.product_name,
                "Size": op.products.unit_size,
                "UnitCost": op.products.unit_cost,
                "Quantity": op.quantity,
                "TotalCost": op.extended_cost
              };
              tCost += op.extended_cost;
              quantityT += op.quantity;
              custYr.Product.push(product);
            }
          }
          custYr.totalCost = tCost;
          donation = cust.donation;
          if (donation > 0) {
            custYr.DonationThanks.push({text: 'Thank you for your $' + donation + " donation "});
            custYr.includeDonation = true;
            custYr.Donation = donation;
            custYr.GrandTotal = tCost + donation;
          }

          data.customerYear.push(custYr);

        }
        tCostT += tCost + donation;

      }
      data.totalCost = tCostT;
      data.totalQuantity = quantityT;
    } catch (e) {
      console.error(e);
    }

    return data;
  }

  // Initialize our service with any options it requires
  app.use('/reports', auth.express.authenticate('jwt'), async function (req, res, next) {
    const seqClient = app.get('sequelizeClient');

    const year = seqClient.models['year'];

    let jsonParams = req.body;
    let formattedAddress = jsonParams.Scout_Town + ", " + jsonParams.Scout_State + " " + jsonParams.Scout_Zip;
    let customers = [];

    /*    let user = User.findById(jsonParams.User).getUsername();
        if (jsonParams.Customer instanceof JSONArray) {
          withId(user, {
            jsonParams.Customer.each {
            customers.add(Customers.findById(it))
          }
        })
        }*/
    let user = jsonParams.User;
    let Category = jsonParams.Category || "All";
    let repTitle = "";
    let Splitting = "";
    let fileName = "report.pdf";
    let yearObj = await year.findByPk(jsonParams.Year);
    let yearText = yearObj.year;
    switch (jsonParams.template) {
      case "customers_split":
        repTitle = "Year of " + yearText;
        Splitting = "";
        fileName = yearText + "_customer_orders_" + Category + ".pdf";
        break;

      case "Year Totals":
        repTitle = "Year of " + yearText;
        Splitting = "";
        fileName = yearText + "_Total_Orders_" + Category + ".pdf";

        break;

      case "Customer Year Totals":
        repTitle = customers.get(0).customerName + " " + yearText + " Order";
        Splitting = "";
        fileName = customers.get(0).customerName + "_" + yearText + "_Order_" + Category + ".pdf";

        break;

      case "Customer All-Time Totals":
        repTitle = "All orders of " + customers.get(0).customerName;
        Splitting = "Year:";
        fileName = customers.get(0).customerName + "_historical_orders.pdf";

        break
    }

    const jsreport = app.get('jsreport');
    await jsreport.render({
      template: {
        content: template,
        engine: 'handlebars',
        recipe: 'chrome-pdf',
        chrome: {
          marginTop: "1in",
          marginBottom: "1in",
          marginLeft: "0.5in",
          marginRight: "0.5in",
        }
      },
      data: await generateJSON({
        reportType: jsonParams.template,
        selectedYear: jsonParams.Year,
        scoutName: jsonParams.Scout_name,
        scoutStAddr: jsonParams.Scout_address,
        scoutCityLine: formattedAddress,
        scoutRank: jsonParams.Scout_Rank,
        scoutPhone: jsonParams.Scout_Phone,
        logoLoc: jsonParams.LogoLocation.base64,
        category: Category,
        user: user,
        includeSubUsers: jsonParams.Include_Sub_Users,
        repTitle: repTitle,
        splitting: Splitting,
        includeHeader: jsonParams.Print_Due_Header,
      })
    }).then((resp) => {
      // prints pdf with headline Hello world
      // console.log(resp.content.toString());
      res.writeHead(200, {
        'Content-Type': 'application/pdf',
        'Content-Disposition': 'attachment; filename=' + fileName,
        'Content-Length': resp.content.length,
        'Access-Control-Expose-Headers': 'Content-Disposition'
      });
      res.end(resp.content);
      return '';
      //  return {data: resp.content.toString('base64')};
    }).catch(e => {
      console.error(e);
    });
    next();
    // console.log(req);
  });

  // Get our initialized service so that we can register hooks
  // const service = app.service('reports');

  //service.hooks(hooks);
};
