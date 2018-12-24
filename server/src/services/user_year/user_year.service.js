// Initializes the `user_year` service on path `/user-year`
const createService = require('feathers-sequelize');
const createModel = require('../../models/user_year.model');
const hooks = require('./user_year.hooks');

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
  app.use('/userYear', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('userYear');

  service.hooks(hooks);
};
