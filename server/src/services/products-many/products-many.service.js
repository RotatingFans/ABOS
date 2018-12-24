// Initializes the `ProductsMany` service on path `/ProductsMany`
const createService = require('./products-many.class.js');
const hooks = require('./products-many.hooks');

module.exports = function (app) {

  const paginate = app.get('paginate');

  const options = {
    paginate
  };

  // Initialize our service with any options it requires
  app.use('/ProductsMany', createService(options));

  // Get our initialized service so that we can register hooks
  const service = app.service('ProductsMany');

  service.hooks(hooks);
};
