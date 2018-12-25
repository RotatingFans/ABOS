const {authenticate} = require('@feathersjs/authentication').hooks;

const {
  hashPassword, protect
} = require('@feathersjs/authentication-local').hooks;

module.exports = {
  before: {

    create: [hashPassword()],
    update: [hashPassword(), authenticate('jwt')],
    patch: [hashPassword(), authenticate('jwt')],
    remove: [authenticate('jwt')],
    all: [],
    find: [function (context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');


      context.params.sequelize = {
        attributes: [['full_name', 'fullName'], 'username', 'id', 'password']
      };

      return context;
    },
      authenticate('jwt')],
    get: [function (context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');


      context.params.sequelize = {
        attributes: [['full_name', 'fullName'], 'username', 'id']
      };

      return context;
    },
      authenticate('jwt')],

  },

  after: {
    all: [
      // Make sure the password field is never sent to the client
      // Always must be the last hook
      protect('password')
    ],
    find: [],
    get: [async function (context) {
      const sequelize = context.app.get('sequelizeClient');
      const user_year = sequelize.models['user_year'];
      return user_year.findOne({where: {user_id: context.result.id, status: "ENABLED"}}).then(uY => {
        if (uY) {
          const enYear = uY.year_id;
          if (!enYear) {
            context.result.enabledYear = -1;
          } else {
            context.result.enabledYear = enYear;

          }
        } else {
          context.result.enabledYear = -1;

        }
        return context;
      });

    }],
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
