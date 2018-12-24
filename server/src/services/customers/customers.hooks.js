const {ordersAttr, customerAttr, orderedProductsAttr, yearAttr, userAttr, productsAttr} = require("../../models/attributes");

// const seqClient = app.get('sequelizeClient');
// const orders = seqClient.models['orders'];
// const orderedProducts = seqClient.models['ordered_products'];

module.exports = {
  before: {
    all: [],
    find(context) {
      // Get the Sequelize instance. In the generated application via:
      const sequelize = context.app.get('sequelizeClient');
      const orderedProducts = sequelize.models['ordered_products'];

      let yrInc = {model: sequelize.models['year'], attributes: yearAttr};
      if (context.params.query.year) {
        yrInc.where = {id: context.params.query.year};
        delete context.params.query.year;

      }

      context.params.sequelize = {
        include: [yrInc, {
          model: sequelize.models.user,
          attributes: userAttr
        }, {
          model: sequelize.models.orders,
          attributes: ordersAttr,
          include: [{
            model: orderedProducts,
            include: [{
              model: sequelize.models.products,
              attributes: productsAttr,
              include: [{model: sequelize.models.categories}, {model: sequelize.models.year, attributes: yearAttr}],
              as: 'products'
            }, {model: sequelize.models.year, attributes: yearAttr}],
            as: 'orderedProducts'
          }, {model: sequelize.models.year, attributes: yearAttr}],
          as: 'order'
        }],
        attributes: customerAttr,
      };


      return context;
    },
    get(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');

      const sequelize = context.app.get('sequelizeClient');

      context.params.sequelize = {
        include: [{model: sequelize.models.year, attributes: yearAttr}, {
          model: sequelize.models.user,
          attributes: userAttr
        }, {
          model: sequelize.models.orders,
          attributes: ordersAttr,
          include: [{
            model: sequelize.models.orderedProducts,
            attributes: orderedProductsAttr,
            include: [{
              model: sequelize.models.products,
              attributes: productsAttr,
              include: [{model: sequelize.models.categories}, {model: sequelize.models.year, attributes: yearAttr}],
              as: 'products'
            }, {model: sequelize.models.year, attributes: yearAttr}],
            as: 'orderedProducts'
          }, {model: sequelize.models.year, attributes: yearAttr}],
          as: 'order'
        }],
        attributes: customerAttr,
      };

      return context;
    },
    async create(context) {
      const sequelize = context.app.get('sequelizeClient');
      // const seqClient = context.app.get('sequelizeClient');
      //   const orders = seqClient.models['orders'];
      const orderedProducts = sequelize.models['ordered_products'];

      const user = sequelize.models['user'];

      //BadRequest: notNull Violation: customers.version cannot be null,
      // notNull Violation: customers.user_name cannot be null,
      // notNull Violation: customers.customer_name cannot be null,
      // notNull Violation: customers.street_address cannot be null,
      // notNull Violation: customers.latitude cannot be null,
      // notNull Violation: customers.longitude cannot be null
      let customer = context.data;
      let usr = await user.findByPk(customer.user);
      customer.user_id = customer.user;
      customer.year_id = customer.year;
      customer.user_name = usr.username;
      customer.customer_name = customer.customerName;
      customer.street_address = customer.streetAddress;
      customer.latitude = 0;
      customer.longitude = 0;
      let ops = [];
      customer.order.orderedProducts.forEach(op => {
        op.extended_cost = op.extendedCost;
        op.user_name = usr.username;
        op.user_id = customer.user;
        op.year_id = customer.year;
        op.products_id = op.products.id;
        ops.push(op);
      });
      customer.order.orderedProducts = ops;
      customer.order.user_name = usr.username;
      customer.order.user_id = customer.user;
      customer.order.year_id = customer.year;
      customer.order.amount_paid = customer.order.amountPaid;
      context.data = customer;
      context.params.sequelize = {
        include: [{
          model: sequelize.models.orders,
          as: 'order',
          include: [{
            model: sequelize.models.orderedProducts,
            as: 'orderedProducts'
          }],

        }],
      };
      return context;
    },
    async update(context) {
      const sequelize = context.app.get('sequelizeClient');
// const seqClient = app.get('sequelizeClient');
      const orderedProducts = sequelize.models['ordered_products'];


      const user = sequelize.models['user'];

      let customer = context.data;
      let usr = await user.findByPk(customer.user.id);
      customer.user_name = usr.username;
      customer.customer_name = customer.customerName;
      customer.street_address = customer.streetAddress;
      customer.latitude = 0;
      customer.longitude = 0;
      let ops = [];
      customer.order.orderedProducts.forEach(op => {
        op.extended_cost = op.extendedCost;
        op.user_name = usr.username;
        op.user = usr;
        ops.push(op);
      });
      customer.order.orderedProducts = ops;
      customer.order.user_name = usr.username;
      customer.order.user = usr;
      customer.order.amount_paid = customer.order.amountPaid;
      context.data = customer;
      context.params.sequelize = {
        include: [{
          model: sequelize.models.orders,
          as: 'order',
          include: [{
            model: sequelize.models.orderedProducts,
            as: 'orderedProducts'
          }],

        }],
      };
      return context;
    },
    patch: [],
    remove: []
  },

  after: {
    all: [],
    find(context) {
      let customers = [];
      const seqClient = context.app.get('sequelizeClient');
      const orderedProducts = seqClient.models['ordered_products'];

      context.result.data.forEach(cust => {

        let ops = [];
        cust.dataValues.order.dataValues.orderedProducts.forEach(op => {
          op.dataValues.extendedCost = op.dataValues.quantity * op.products.dataValues.unitCost;
          ops.push(op);
        });
        cust.dataValues.order.dataValues.orderedProducts = ops;
        customers.push(cust);
      });
      context.result.data = customers;
      return context;
    },
    get(context) {


      let ops = [];
      context.result.dataValues.order.dataValues.orderedProducts.forEach(op => {
        op.dataValues.extendedCost = op.dataValues.quantity * op.products.dataValues.unitCost;
        ops.push(op);
      });
      context.result.dataValues.order.dataValues.orderedProducts = ops;
      return context;

    },
    async update(context) {
      const seqClient = app.get('sequelizeClient');
      const customers = seqClient.models['customers'];
      const orderedProducts = seqClient.models['ordered_products'];
      const orders = seqClient.models['orders'];
      const products = seqClient.models['products'];

      let order;
      if (context.data.order.id) {
        order = await orders.findByPk(context.data.order.id);
        order.set(context.data.order);
      } else {
        order = orders.build(context.data.order);
      }
      let customer = await customers.findByPk(context.data.id);
      let user = await customer.getUser();
      let year = await customer.getYear();
      let ops = [];
      order.user_name = context.data.user_name;

      for (const op of context.data.order.orderedProducts) {

        /*}
        await context.data.order.orderedProducts.forEach(async op => {*/
        let opM;
        if (op.id) {
          opM = await orderedProducts.findByPk(op.id);
          opM.set(op);
        } else {
          opM = orderedProducts.build(op);
        }
        let product = await products.findByPk(op.products.id);
        opM.user_name = context.data.user_name;

        opM.setUser(user, {save: false});
        opM.setYear(year, {save: false});
        opM.setCustomer(customer, {save: false});
        opM.setOrder(order, {save: false});
        opM.setProducts(product, {save: false});
        // console.log(opM.toJSON());
        let response5 = await opM.save();
        ops.push(response5);
      }
      await order.setOrderedProducts(ops, {save: false});
      //
      await order.save();
      return context;
    },
    async create(context) {
      const seqClient = app.get('sequelizeClient');
      const customers = seqClient.models['customers'];
      const orderedProducts = seqClient.models['ordered_products'];
      const orders = seqClient.models['orders'];
      const products = seqClient.models['products'];

      let order;
      if (context.result.order.id) {
        order = await orders.findByPk(context.result.order.id);
        order.set(context.result.order);
      } else {
        order = orders.build(context.result.order);
      }
      let customer = await customers.findByPk(context.result.id);
      let user = await customer.getUser();
      let year = await customer.getYear();
      let ops = [];
      order.user_name = context.result.user_name;

      for (const op of context.result.order.orderedProducts) {

        /*}
        await context.result.order.orderedProducts.forEach(async op => {*/
        let opM;
        if (op.id) {
          opM = await orderedProducts.findByPk(op.id);
          opM.set(op);
        } else {
          opM = orderedProducts.build(op);
        }
        let product = await products.findByPk(op.products_id);
        opM.user_name = context.result.user_name;

        opM.setUser(user, {save: false});
        opM.setYear(year, {save: false});
        opM.setCustomer(customer, {save: false});
        opM.setOrder(order, {save: false});
        opM.setProducts(product, {save: false});
        // console.log(opM.toJSON());
        let response5 = await opM.save();
        ops.push(response5);
      }
      await order.setOrderedProducts(ops, {save: false});
      //
      await order.save();
      return context;
    },
    patch: [],
    remove: []
  },

  error: {
    all: [],
    find: [],
    get: [],
    create: [],
    update: [],
    patch: [],
    remove: []
  }
};
