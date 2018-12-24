// Initializes the `groups` service on path `/groups`
const createService = require('feathers-sequelize');
const createModel = require('../../models/groups.model');
const hooks = require('./groups.hooks');

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
  app.use('/Group', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('Group');

  service.hooks(hooks);
};
