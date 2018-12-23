/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const schema = sequelize.define('groups', {
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
        group_name: {
            type: DataTypes.STRING(255),
            allowNull: false
        },

    }, {
        tableName: 'groups', underscored: true,
    });
    schema.associate = models => {
        schema.belongsTo(models.year);
    };
    return schema;
};
