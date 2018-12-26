const checkPermissions = require('../../hooks/check-permissions');
const filterManagedUsers = require('../../hooks/filter-managed-users');
const {authenticate} = require('@feathersjs/authentication').hooks;

module.exports = {
  before: {
    all: [authenticate('jwt'), checkPermissions(['ROLE_USER'])],
    find: [],
    get: [],
    create: [checkPermissions(['ROLE_ADMIN'])],
    update: [checkPermissions(['ROLE_ADMIN'])],
    patch: [checkPermissions(['ROLE_ADMIN'])],
    remove: [checkPermissions(['ROLE_ADMIN'])]
  },

  after: {
    all: [],
    find: [],
    get: [],
    create: [],
    update: [],
    patch: [],
    remove: []
  },

  error: {
    all: [],
    find: [],
    get: [],
    create: [],
    update: [],
    patch: [],
    remove: []
  }
};
