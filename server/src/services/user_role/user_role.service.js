// Initializes the `user_role` service on path `/user-role`
const createService = require('feathers-sequelize');
const createModel = require('../../models/user_role.model');
const hooks = require('./user_role.hooks');

module.exports = function (app) {
  const Model = createModel(app);
  const paginate = app.get('paginate');

  const serviceOptions = app.get('serviceOptions');

  const options = {
    Model,
    paginate,
    ...serviceOptions
  };

  // Initialize our service with any options it requires
  app.use('userRole', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('userRole');

  service.hooks(hooks);
};
