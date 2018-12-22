/**
 * products.js
 *
 * @description :: The products table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'products',
  tableName: 'products',
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
    humanProductId: {
      type: 'string',
      required: true,
      columnName: 'human_product_id'
    },
    productName: {
      type: 'string',
      required: true,
      columnName: 'product_name'
    },
    unitCost: {
      type: 'number',
      required: true,
      columnType: 'decimal(19,2)',
      columnName: 'unit_cost'
    },
    categoryId: {
      type: 'number',
      required: false,
      allowNull: true,
      columnName: 'category_id'
    },
    yearId: {
      type: 'number',
      required: true,
      columnName: 'year_id'
    },
    unitSize: {
      type: 'string',
      required: true,
      columnName: 'unit_size'
    }
  },
  migrate: 'safe',
  primaryKey: 'id',
  categories: {
    model: 'categories'
  },
  year: {
    model: 'year'
  }
};