/**
 * orders.js
 *
 * @description :: The orders table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'orders',
  tableName: 'orders',
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

    amountPaid: {
      type: 'number',
      required: true,
      columnType: 'decimal(19,2)',
      columnName: 'amount_paid'
    },
    delivered: {
      type: 'boolean',
      required: true,
      columnName: 'delivered'
    },
    quantity: {
      type: 'number',
      required: true,
      columnName: 'quantity'
    },

    cost: {
      type: 'number',
      required: true,
      columnType: 'decimal(19,2)',
      columnName: 'cost'
    },

    customers: {
      model: 'customers',
      required: true,
      columnName: 'customer_id'
    },
    user: {
      model: 'user',
      required: true,
      columnName: 'user_id'
    },
    year: {
      model: 'year',
      required: true,
      columnName: 'year_id'
    }
  },
  migrate: 'safe',
  primaryKey: 'id',
  customToJSON: function () {
    return {
      orderedProducts: this.orderedProducts,


      id: this.id,

      cost: this.cost,

      quantity: this.quantity,

      amountPaid: this.amountPaid,

      delivered: this.delivered,

      year: {
        id: this.year.id,

        year: this.year.year,
      },

      userName: this.userName,

      donation: this.customers.donation,

    }
  }
};
