/**
 * year.js
 *
 * @description :: The year table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'year',
  tableName: 'year',
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
    year: {
      type: 'string',
      required: true,
      columnName: 'year',
      unique: true
    }
  },
  migrate: 'safe',
  primaryKey: 'id'
};