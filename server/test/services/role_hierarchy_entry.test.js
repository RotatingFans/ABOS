const assert = require('assert');
const app = require('../../src/app');

describe('\'role_hierarchy_entry\' service', () => {
  it('registered the service', () => {
    const service = app.service('role-hierarchy-entry');

    assert.ok(service, 'Registered the service');
  });
});
