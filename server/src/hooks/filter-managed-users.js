// Use this hook to manipulate incoming or outgoing data.
// For more information on hooks see: http://docs.feathersjs.com/api/hooks.html

// eslint-disable-next-line no-unused-vars
const {Forbidden, BadRequest} = require('@feathersjs/errors');

module.exports = function (options = {}) {
  options = Object.assign({
    field: 'user_id',
    createField: 'user'
  }, options);
  const {field, createField} = options;

  return async context => {
    if (context.path === "user" && !context.params.payload) {
      return context;
    }
    if (!context.params.payload.userId) {
      throw new Forbidden('NOT AUTHENTICATED!')
    }
    const sequelize = context.app.get('sequelizeClient');
    const user_manager = sequelize.models['user_manager'];
    let year = 1;

    if (context.params.query.year) {
      year = context.params.query.year;
    } else if (context.params.query.year_id) {
      year = context.params.query.year_id;
    } else if (context.params.user.enabledYear) {
      year = context.params.user.enabledYear;
    }
    let uM = [];
    if (!context.params.query[field] || context.params.query.includeSub === "true") {
      uM = await user_manager.findAll({
        where: {manage_id: context.params.payload.userId, year_id: year}

      });
    } else {
      uM = await user_manager.findAll({
        where: {manage_id: context.params.payload.userId, user_id: context.params.query[field], year_id: year}

      });
    }
    delete context.params.query[field];
    delete context.params.query.includeSub;

    if (uM) {
      let userIds = [];
      for (const manageEntry of uM) {
        userIds.push(manageEntry.user_id);
      }
      if (context.method === "find" || context.method === "get") {
        context.params.query[field] = {'$in': userIds};

      } else {
        if (!userIds.includes(context.data[createField])) {
          throw new BadRequest('Invalid User ID');
          //        console.log(context);
        }
      }

    }
    return context;


  };
};
