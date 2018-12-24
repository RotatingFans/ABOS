const assert = require('assert');
const app = require('../../src/app');

describe('\'ordered_products\' service', () => {
  it('registered the service', () => {
    const service = app.service('orderedProducts');

    assert.ok(service, 'Registered the service');
  });
});
