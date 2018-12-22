/**
 * userManager.js
 *
 * @description :: The userManager table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'usermanager',
  tableName: 'user_manager',
  schema: true,
  attributes: {
    id: {
      type: 'number',
      required: true,
      columnName: 'id',
      autoIncrement: true
    },
    version: {
      type: 'number',
      required: true,
      columnName: 'version'
    },
    userId: {
      type: 'number',
      required: true,
      columnName: 'user_id'
    },
    yearId: {
      type: 'number',
      required: true,
      columnName: 'year_id'
    },
    manageId: {
      type: 'number',
      required: true,
      columnName: 'manage_id'
    }
  },
  migrate: 'safe',
  primaryKey: 'id',
  user: {
    model: 'user'
  },
  year: {
    model: 'year'
  }
};