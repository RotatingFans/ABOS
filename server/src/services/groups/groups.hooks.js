module.exports = {
  before: {
    all: [],
    find(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');
      const seqClient = context.app.get('sequelizeClient');

      const userYear = seqClient.models['user_year'];
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
      const seqClient = context.app.get('sequelizeClient');

      const userYear = seqClient.models['user_year'];
      const year = seqClient.models['year'];

      context.params.sequelize = {
        attributes: [['group_name', 'groupName'], 'id', 'year_id'],
        include: [{model: year, attributes: ['id']}, {model: userYear, attributes: ['id']}]
      };

      return context;
    },
    create: [(context) => {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');

      context.data.group_name = context.data.GroupName;
      context.data.year_id = context.data.year;
      return context;
    }],
    update: [(context) => {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');

      context.data.group_name = context.data.GroupName;
      context.data.year_id = context.data.year;
      return context;
    }],
    patch: [(context) => {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');

      context.data.group_name = context.data.GroupName;
      context.data.year_id = context.data.year;
      return context;
    }],
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
