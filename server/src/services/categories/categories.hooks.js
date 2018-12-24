module.exports = {
  before: {
    all: [],
    find(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');
      if (context.params.query.year) {
        context.params.query.year_id = context.params.query.year;
        delete context.params.query.year;

      }
      context.params.sequelize = {
        attributes: ['id', ['category_name', 'categoryName'], ['delivery_date', 'deliveryDate']],
      };

      return context;
    },
    get: [],
    create: [],
    update: [],
    patch: [],
    remove: []
  },

  after: {
    all: [],
    find: [],
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
