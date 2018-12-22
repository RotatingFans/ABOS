/**
 * groups.js
 *
 * @description :: The groups table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'groups',
  tableName: 'groups',
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
    groupName: {
      type: 'string',
      required: true,
      columnName: 'group_name'
    },
    yearId: {
      type: 'number',
      required: true,
      columnName: 'year_id'
    }
  },
  migrate: 'safe',
  primaryKey: 'id',
  year: {
    model: 'year'
  }
};