const assert = require('assert');
const app = require('../../src/app');

describe('\'user_year\' service', () => {
  it('registered the service', () => {
    const service = app.service('user-year');

    assert.ok(service, 'Registered the service');
  });
});
