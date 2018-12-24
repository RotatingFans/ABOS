// Initializes the `Categories` service on path `/categories`
const createService = require('feathers-sequelize');
const createModel = require('../../models/categories.model');
const hooks = require('./categories.hooks');

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
  app.use('/Categories', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('Categories');

  service.hooks(hooks);
};
