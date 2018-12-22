/**
 * categories.js
 *
 * @description :: The categories table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'categories',
  tableName: 'categories',
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
    categoryName: {
      type: 'string',
      required: true,
      columnName: 'category_name'
    },
    deliveryDate: {
      type: 'ref',
      required: true,
      columnType: 'datetime',
      columnName: 'delivery_date'
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
