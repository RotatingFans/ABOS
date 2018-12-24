// Initializes the `year` service on path `/year`
const createService = require('feathers-sequelize');
const createModel = require('../../models/year.model');
const hooks = require('./year.hooks');

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
  app.use('/Years', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('Years');

  service.hooks(hooks);
};
