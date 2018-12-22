/**
 * customers.js
 *
 * @description :: The customers table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'customers',
  tableName: 'customers',
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
    phone: {
      type: 'string',
      required: false,
      allowNull: true,
      columnName: 'phone'
    },
    custEmail: {
      type: 'string',
      required: false,
      allowNull: true,
      columnName: 'cust_email'
    },
    home: {
      type: 'boolean',
      required: false,
      allowNull: true,
      columnName: 'home'
    },
    interested: {
      type: 'boolean',
      required: false,
      allowNull: true,
      columnName: 'interested'
    },
    ordered: {
      type: 'boolean',
      required: false,
      allowNull: true,
      columnName: 'ordered'
    },
    donation: {
      type: 'number',
      required: false,
      allowNull: true,
      columnType: 'decimal(19,2)',
      columnName: 'donation'
    },
    userName: {
      type: 'string',
      required: true,
      columnName: 'user_name'
    },
    zipCode: {
      type: 'string',
      required: false,
      allowNull: true,
      columnName: 'zip_code'
    },
    customerName: {
      type: 'string',
      required: true,
      columnName: 'customer_name'
    },
    streetAddress: {
      type: 'string',
      required: true,
      columnName: 'street_address'
    },
    state: {
      type: 'string',
      required: false,
      allowNull: true,
      columnName: 'state'
    },
    latitude: {
      type: 'number',
      required: true,
      columnType: 'double',
      columnName: 'latitude'
    },
    longitude: {
      type: 'number',
      required: true,
      columnType: 'double',
      columnName: 'longitude'
    },
    city: {
      type: 'string',
      required: false,
      allowNull: true,
      columnName: 'city'
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
  user: {
    model: 'user'
  },
  year: {
    model: 'year'
  }
};