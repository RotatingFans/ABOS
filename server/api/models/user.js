/**
 * user.js
 *
 * @description :: The user table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'user',
  tableName: 'user',
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
    passwordExpired: {
      type: 'boolean',
      required: true,
      columnName: 'password_expired'
    },
    accountExpired: {
      type: 'boolean',
      required: true,
      columnName: 'account_expired'
    },
    fullName: {
      type: 'string',
      required: true,
      columnName: 'full_name'
    },
    username: {
      type: 'string',
      required: true,
      columnName: 'username',
      unique: true
    },
    accountLocked: {
      type: 'boolean',
      required: true,
      columnName: 'account_locked'
    },
    password: {
      type: 'string',
      required: true,
      columnName: 'password'
    },
    enabled: {
      type: 'boolean',
      required: true,
      columnName: 'enabled'
    }
  },
  migrate: 'safe',
  primaryKey: 'id'
};