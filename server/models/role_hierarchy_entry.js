/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    return sequelize.define('role_hierarchy_entry', {
        id: {
            type: DataTypes.BIGINT,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },

        entry: {
            type: DataTypes.STRING(255),
            allowNull: false,
            unique: true
        }
    }, {
        tableName: 'role_hierarchy_entry', underscored: true,
    });
};
