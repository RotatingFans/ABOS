/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const schema = sequelize.define('user_role', {}, {
        tableName: 'user_role', underscored: true,
    });
    schema.associate = models => {
        schema.belongsTo(models.user);
        schema.belongsTo(models.role);

    };
    return schema;
};
