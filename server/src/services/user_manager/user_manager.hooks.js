const checkPermissions = require('../../hooks/check-permissions');
const {authenticate} = require('@feathersjs/authentication').hooks;
const {disallow} = require('feathers-hooks-common');

const filterManagedUsers = require('../../hooks/filter-managed-users');
module.exports = {
  before: {
    all: [disallow()],
    find: [],
    get: [],
    create: [],
    update: [],
    patch: [],
    remove: []
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
