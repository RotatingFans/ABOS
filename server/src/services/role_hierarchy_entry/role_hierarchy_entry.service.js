// Initializes the `role_hierarchy_entry` service on path `/role-hierarchy-entry`
const createService = require('feathers-sequelize');
const createModel = require('../../models/role_hierarchy_entry.model');
const hooks = require('./role_hierarchy_entry.hooks');

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
  app.use('/RoleHierarchyEntry', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('RoleHierarchyEntry');

  service.hooks(hooks);
};
