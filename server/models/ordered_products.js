/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const schema = sequelize.define('ordered_products', {
        id: {
            type: DataTypes.BIGINT,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },

        user_name: {
            type: DataTypes.STRING(255),
            allowNull: false
        },


        extended_cost: {
            type: DataTypes.DECIMAL(19, 2),
            allowNull: false
        },

        quantity: {
            type: DataTypes.INTEGER(11),
            allowNull: false
        },


    }, {
        tableName: 'ordered_products', underscored: true,
    });
    schema.associate = models => {
        schema.belongsTo(models.year);
        schema.belongsTo(models.user);

        schema.belongsTo(models.products, {as: 'products'});
        schema.belongsTo(models.customers, {onDelete: 'cascade'});
        schema.belongsTo(models.orders);

    };
    return schema;
};
