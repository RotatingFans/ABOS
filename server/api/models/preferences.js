/**
 * preferences.js
 *
 * @description :: The preferences table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'preferences',
  tableName: 'preferences',
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
    prefKey: {
      type: 'string',
      required: true,
      columnName: 'pref_key',
      unique: true
    },
    prefValue: {
      type: 'string',
      required: true,
      columnName: 'pref_value'
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