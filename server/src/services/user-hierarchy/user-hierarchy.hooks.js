const {authenticate} = require('@feathersjs/authentication').hooks;
const checkPermissions = require('../../hooks/check-permissions');
const filterManagedUsers = require('../../hooks/filter-managed-users');
const {disallow} = require('feathers-hooks-common');

module.exports = {
  before: {
    all: [authenticate('jwt'), checkPermissions(['ROLE_ADMIN'])],
    find: [],
    get: [disallow()],
    create: [disallow()],
    update: [disallow()],
    patch: [disallow()],
    remove: [disallow()]
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
