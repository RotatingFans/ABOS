module.exports = {
  before: {
    all: [],
    find(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');


      context.params.sequelize = {
        attributes: [['full_name', 'fullName'], 'username', 'id']
      };

      return context;
    },
    get(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');


      context.params.sequelize = {
        attributes: [['full_name', 'fullName'], 'username', 'id']
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
