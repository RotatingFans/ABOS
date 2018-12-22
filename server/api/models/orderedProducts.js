/**
 * orderedProducts.js
 *
 * @description :: The orderedProducts table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'orderedproducts',
  tableName: 'ordered_products',
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
    userName: {
      type: 'string',
      required: true,
      columnName: 'user_name'
    },
    customerId: {
      type: 'number',
      required: true,
      columnName: 'customer_id'
    },
    orderId: {
      type: 'number',
      required: true,
      columnName: 'order_id'
    },
    extendedCost: {
      type: 'number',
      required: true,
      columnType: 'decimal(19,2)',
      columnName: 'extended_cost'
    },
    productsId: {
      type: 'number',
      required: true,
      columnName: 'products_id'
    },
    quantity: {
      type: 'number',
      required: true,
      columnName: 'quantity'
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
    }
  },
  migrate: 'safe',
  primaryKey: 'id',
  customers: {
    model: 'customers'
  },
  orders: {
    model: 'orders'
  },
  products: {
    model: 'products'
  },
  user: {
    model: 'user'
  },
  year: {
    model: 'year'
  }
};