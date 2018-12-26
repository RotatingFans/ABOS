const {authenticate} = require('@feathersjs/authentication').hooks;

module.exports = {
  before: {
    all: [],
    find: [authenticate('jwt')],
    get: [],
    create: [authenticate('jwt')],
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
