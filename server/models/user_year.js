/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const schema = sequelize.define('user_year', {
        id: {
            type: DataTypes.BIGINT,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },


        status: {
            type: DataTypes.STRING(255),
            allowNull: false
        },

    }, {
        tableName: 'user_year', underscored: true,
    });
    schema.associate = models => {
        schema.belongsTo(models.year);
        schema.belongsTo(models.groups);
        schema.belongsTo(models.user);

    };
    return schema;
};
