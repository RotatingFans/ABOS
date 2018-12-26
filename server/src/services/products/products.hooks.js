const {productsAttr, yearAttr} = require("../../models/attributes");
const {authenticate} = require('@feathersjs/authentication').hooks;
const checkPermissions = require('../../hooks/check-permissions');


module.exports = {
  before: {
    all: [authenticate('jwt'), checkPermissions(['ROLE_USER'])],
    find(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');
      const seqClient = context.app.get('sequelizeClient');
      const categories = seqClient.models['categories'];


      const year = seqClient.models['year'];
      let yrInc = {model: year, attributes: yearAttr};
      if (context.params.query.year) {
        yrInc.where = {id: context.params.query.year};
        delete context.params.query.year;

      }
      context.params.sequelize = {
        attributes: productsAttr, include: [{model: categories}, yrInc]
      };

      return context;
    },
    get(context) {
      // Get the Sequelize instance. In the generated application via:
      //  const sequelize = context.app.get('sequelizeClient');
      const seqClient = context.app.get('sequelizeClient');
      const categories = seqClient.models['categories'];


      const year = seqClient.models['year'];
      let yrInc = {model: year, attributes: yearAttr};
      if (context.params.query.year) {
        yrInc.where = {id: context.params.query.year};
        delete context.params.query.year;

      }
      context.params.sequelize = {
        attributes: productsAttr, include: [{model: categories}, yrInc]
      };

      return context;
    },
    create: [checkPermissions(['ROLE_ADMIN'])],
    update: [checkPermissions(['ROLE_ADMIN'])],
    patch: [checkPermissions(['ROLE_ADMIN'])],
    remove: [checkPermissions(['ROLE_ADMIN'])]
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
