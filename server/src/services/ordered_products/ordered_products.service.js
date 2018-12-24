// Initializes the `ordered_products` service on path `/orderedProducts`
const createService = require('feathers-sequelize');
const createModel = require('../../models/ordered_products.model');
const hooks = require('./ordered_products.hooks');

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
  app.use('/orderedProducts', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('orderedProducts');

  service.hooks(hooks);
};
