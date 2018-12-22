/**
 * roleHierarchyEntry.js
 *
 * @description :: The roleHierarchyEntry table
 * @docs        :: http://sailsjs.org/#!documentation/models
 */

module.exports = {
  identity: 'rolehierarchyentry',
  tableName: 'role_hierarchy_entry',
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
    entry: {
      type: 'string',
      required: true,
      columnName: 'entry',
      unique: true
    }
  },
  migrate: 'safe',
  primaryKey: 'id'
};