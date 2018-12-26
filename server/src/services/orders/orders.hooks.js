const {ordersAttr, customerAttr, orderedProductsAttr, yearAttr, userAttr, productsAttr} = require("../../models/attributes");
// const seqClient = app.get('sequelizeClient');
// const customers = seqClient.models['customers'];
// const products = seqClient.models['products'];
const {authenticate} = require('@feathersjs/authentication').hooks;
const checkPermissions = require('../../hooks/check-permissions');
const filterManagedUsers = require('../../hooks/filter-managed-users');
const {disallow} = require('feathers-hooks-common');


module.exports = {
  before: {
    all: [authenticate('jwt'), checkPermissions(['ROLE_USER']), filterManagedUsers()],
    find(context) {
      // Get the Sequelize instance. In the generated application via:
      const seqClient = context.app.get('sequelizeClient');
      const categories = seqClient.models['categories'];
      const customers = seqClient.models['customers'];
      const orderedProducts = seqClient.models['ordered_products'];
      const products = seqClient.models['products'];

      const year = seqClient.models['year'];
      let yrInc = {model: year, attributes: yearAttr};
      if (context.params.query.year) {
        yrInc.where = {id: context.params.query.year};

      }
      delete context.params.query.year;

      context.params.sequelize = {
        include: [{model: customers, attributes: ['donation']}, {
          model: orderedProducts,
          attributes: orderedProductsAttr,
          include: [{
            model: products,
            attributes: productsAttr,
            include: [{model: categories}, {model: year, attributes: yearAttr}],
            as: 'products'
          }, {model: year, attributes: yearAttr}],
          as: 'orderedProducts'
        }, yrInc],
        attributes: ['id', 'cost', 'quantity', ['amount_paid', 'amountPaid'], 'delivered', ['user_name', 'userName'], 'customer_id', 'year_id'],
      };

      return context;
    },
    get(context) {
      const seqClient = context.app.get('sequelizeClient');
      const categories = seqClient.models['categories'];
      const customers = seqClient.models['customers'];
      const orderedProducts = seqClient.models['ordered_products'];
      const products = seqClient.models['products'];

      const year = seqClient.models['year'];
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');
      let yrInc = {model: year, attributes: yearAttr};
      if (context.params.query.year) {
        yrInc.where = {id: context.params.query.year};
        delete context.params.query.year;

      }
      delete context.params.query.year;

      context.params.sequelize = {
        include: [{model: customers, attributes: ['donation']}, {
          model: orderedProducts,
          attributes: orderedProductsAttr,
          include: [{
            model: products,
            attributes: productsAttr,
            include: [{model: categories}, {model: year, attributes: yearAttr}],
            as: 'products'
          }, {model: year, attributes: yearAttr}],
          as: 'orderedProducts'
        }, yrInc],
        attributes: ['id', 'cost', 'quantity', ['amount_paid', 'amountPaid'], 'delivered', ['user_name', 'userName'], 'customer_id', 'year_id'],
      };

      return context;
    },
    create: [disallow()],
    update: [disallow()],
    patch: [disallow()],
    remove: [disallow()]
  },

  after: {
    all: [],
    find(context) {
      let orders = [];
      context.result.data.forEach(res => {
        let ops = [];
        res.dataValues.orderedProducts.forEach(op => {
          op.dataValues.extendedCost = op.dataValues.quantity * op.products.dataValues.unitCost;
          ops.push(op);
        });
        res.dataValues.orderedProducts = ops;
        orders.push(res);
      });
      context.result.data = orders;
      return context;
    },
    get: [],
    create: [],
    update: [],
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
