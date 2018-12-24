/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const schema = sequelize.define('orders', {
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

        amount_paid: {
            type: DataTypes.DECIMAL,
            allowNull: false
        },
        delivered: {
            type: DataTypes.BOOLEAN,
            allowNull: false
        },
        quantity: {
            type: DataTypes.INTEGER(11),
            allowNull: false
        },

        cost: {
            type: DataTypes.DECIMAL,
            allowNull: false
        },

    }, {
        tableName: 'orders', underscored: true,
    });
    schema.associate = models => {
        schema.belongsTo(models.year);
        schema.belongsTo(models.user);

        schema.belongsTo(models.customers);
        schema.hasMany(models.ordered_products, {as: 'orderedProducts', onDelete: 'cascade'});

    };
    return schema;
};
