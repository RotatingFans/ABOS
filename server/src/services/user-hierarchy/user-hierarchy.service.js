// Initializes the `UserHierarchy` service on path `/UserHierarchy`
const createService = require('./user-hierarchy.class.js');
const hooks = require('./user-hierarchy.hooks');

module.exports = function (app) {

  const paginate = app.get('paginate');

  const options = {
    paginate
  };

  // Initialize our service with any options it requires
  app.use('/UserHierarchy', createService(options, app));

  // Get our initialized service so that we can register hooks
  const service = app.service('UserHierarchy');

  service.hooks(hooks);
};
