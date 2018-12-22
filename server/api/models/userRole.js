/**
 * userRole.js
 *
 * @description :: The userRole table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'userrole',
  tableName: 'user_role',
  schema: true,
  attributes: {
    id: false,
    userId: {
      type: 'number',
      required: true,
      columnName: 'user_id'
    },
    roleId: {
      type: 'number',
      required: true,
      columnName: 'role_id'
    }
  },
  migrate: 'safe',
  primaryKey: 'userId',
  user: {
    model: 'user'
  },
  role: {
    model: 'role'
  }
};
