const assert = require('assert');
const app = require('../../src/app');

describe('\'ProductsMany\' service', () => {
  it('registered the service', () => {
    const service = app.service('ProductsMany');

    assert.ok(service, 'Registered the service');
  });
});
