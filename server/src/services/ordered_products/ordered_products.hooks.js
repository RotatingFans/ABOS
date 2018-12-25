const {ordersAttr, customerAttr, orderedProductsAttr, yearAttr, userAttr, productsAttr} = require("../../models/attributes");


module.exports = {
  before: {
    all: [],
    find(context) {
      // Get the Sequelize instance. In the generated application via:
      const sequelize = context.app.get('sequelizeClient');
      const categories = sequelize.models['categories'];

      const products = sequelize.models['products'];


      const year = sequelize.models['year'];
      let yrInc = {model: year, attributes: yearAttr};
      if (context.params.query.year) {
        yrInc.where = {id: context.params.query.year};
        delete context.params.query.year;

      }
      context.params.sequelize = {
        attributes: ['id', 'quantity', [sequelize.literal(`\`quantity\` * \`products\`.\`unit_cost\``), 'extendedCost'], ['user_name', 'userName']],
        include: [{
          model: products,
          attributes: productsAttr,
          include: [{model: categories}, {model: year, attributes: yearAttr}],
          as: 'products'
        }, yrInc],
      };
      return context;
    },
    get(context) {
      // Get the Sequelize instance. In the generated application via:
      const sequelize = context.app.get('sequelizeClient');
      const categories = sequelize.models['categories'];

      const products = sequelize.models['products'];
      let yrInc = {model: sequelize.models['year'], attributes: yearAttr};
      if (context.params.query.year) {
        yrInc.where = {id: context.params.query.year};
        delete context.params.query.year;

      }
      context.params.sequelize = {
        attributes: ['id', 'quantity', [sequelize.literal(`\`quantity\` * \`products\`.\`unit_cost\``), 'extendedCost'], ['user_name', 'userName']],
        include: [{
          model: products,
          attributes: productsAttr,
          include: [{model: categories}, {model: year, attributes: yearAttr}],
          as: 'products'
        }, yrInc],
      };
      return context;
    },
    create: [],
    update: [],
    patch: [],
    remove: []
  },

  after: {
    all: [],
    find(context) {
      let ops = [];


      context.result.data.forEach(op => {


        op.dataValues.extendedCost = op.dataValues.quantity * op.products.dataValues.unitCost;
        ops.push(op);


      });
      context.result.data = ops;
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
