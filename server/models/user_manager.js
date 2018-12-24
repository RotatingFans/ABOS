/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const schema = sequelize.define('user_manager', {
        id: {
            type: DataTypes.BIGINT,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },


    }, {
        tableName: 'user_manager',
        underscored: true,
    });
    schema.associate = models => {
        schema.belongsTo(models.year);
        schema.belongsTo(models.user);
        schema.belongsTo(models.user, {as: "manage"});

    };
    return schema;
};
