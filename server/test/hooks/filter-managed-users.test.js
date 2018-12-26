const assert = require('assert');
const feathers = require('@feathersjs/feathers');
const filterManagedUsers = require('../../src/hooks/filter-managed-users');

describe('\'filterManagedUsers\' hook', () => {
  let app;

  beforeEach(() => {
    app = feathers();

    app.use('/dummy', {
      async get(id) {
        return {id};
      }
    });

    app.service('dummy').hooks({});
  });

  it('runs the hook', async () => {
    const result = await app.service('dummy').get('test');

    assert.deepEqual(result, {id: 'test'});
  });
});
