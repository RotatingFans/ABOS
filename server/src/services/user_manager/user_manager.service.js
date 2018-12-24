// Initializes the `user_manager` service on path `/user-manager`
const createService = require('feathers-sequelize');
const createModel = require('../../models/user_manager.model');
const hooks = require('./user_manager.hooks');

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
  app.use('/userManager', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('userManager');

  service.hooks(hooks);
};
