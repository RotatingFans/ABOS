const assert = require('assert');
const app = require('../../src/app');

describe('\'user_manager\' service', () => {
  it('registered the service', () => {
    const service = app.service('user-manager');

    assert.ok(service, 'Registered the service');
  });
});
