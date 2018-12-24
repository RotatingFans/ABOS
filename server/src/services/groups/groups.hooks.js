module.exports = {
  before: {
    all: [],
    find(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');
      const seqClient = app.get('sequelizeClient');

      const userYear = seqClient.models['userYear'];
      const year = seqClient.models['year'];

      context.params.sequelize = {
        attributes: [['group_name', 'groupName'], 'id', 'year_id'],
        include: [{model: year, attributes: ['id']}, {model: userYear, attributes: ['id']}]
      };

      return context;
    },
    get(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');
      const seqClient = app.get('sequelizeClient');

      const userYear = seqClient.models['userYear'];
      const year = seqClient.models['year'];

      context.params.sequelize = {
        attributes: [['group_name', 'groupName'], 'id', 'year_id'],
        include: [{model: year, attributes: ['id']}, {model: userYear, attributes: ['id']}]
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
