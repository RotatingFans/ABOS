// Initializes the `role` service on path `/role`
const createService = require('feathers-sequelize');
const createModel = require('../../models/role.model');
const hooks = require('./role.hooks');

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
  app.use('/role', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('role');

  service.hooks(hooks);
};
