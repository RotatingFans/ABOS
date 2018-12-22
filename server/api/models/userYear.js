/**
 * userYear.js
 *
 * @description :: The userYear table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'useryear',
  tableName: 'user_year',
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
    groupId: {
      type: 'number',
      required: true,
      columnName: 'group_id'
    },
    userId: {
      type: 'number',
      required: true,
      columnName: 'user_id'
    },
    status: {
      type: 'string',
      required: true,
      columnName: 'status'
    },
    yearId: {
      type: 'number',
      required: true,
      columnName: 'year_id'
    }
  },
  migrate: 'safe',
  primaryKey: 'id',
  groups: {
    model: 'groups'
  },
  user: {
    model: 'user'
  },
  year: {
    model: 'year'
  }
};