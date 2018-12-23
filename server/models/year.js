/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    return sequelize.define('year', {
        id: {
            type: DataTypes.BIGINT,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },
        version: {
            type: DataTypes.BIGINT,
            allowNull: false
        },
        year: {
            type: DataTypes.STRING(4),
            allowNull: false,
            unique: true
        }
    }, {
        tableName: 'year', underscored: true,
    });
};
