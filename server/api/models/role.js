/**
 * role.js
 *
 * @description :: The role table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'role',
  tableName: 'role',
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
    authority: {
      type: 'string',
      required: true,
      columnName: 'authority',
      unique: true
    }
  },
  migrate: 'safe',
  primaryKey: 'id'
};