const assert = require('assert');
const app = require('../../src/app');

describe('\'UserHierarchy\' service', () => {
  it('registered the service', () => {
    const service = app.service('UserHierarchy');

    assert.ok(service, 'Registered the service');
  });
});
