/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    return sequelize.define('preferences', {
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
        pref_key: {
            type: DataTypes.STRING(45),
            allowNull: false,
            unique: true
        },
        pref_value: {
            type: DataTypes.STRING(100),
            allowNull: false
        },
        year_id: {
            type: DataTypes.BIGINT,
            allowNull: false,
            references: {
                model: 'year',
                key: 'id'
            }
        }
    }, {
        tableName: 'preferences'
    });
};
