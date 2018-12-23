/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const schema = sequelize.define('products', {
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
        human_product_id: {
            type: DataTypes.STRING(255),
            allowNull: false
        },
        product_name: {
            type: DataTypes.STRING(255),
            allowNull: false
        },
        unit_cost: {
            type: DataTypes.DECIMAL,
            allowNull: false
        },
        unit_size: {
            type: DataTypes.STRING(255),
            allowNull: false
        }
    }, {
        tableName: 'products', underscored: true,

    });
    schema.associate = models => {
        schema.belongsTo(models.year);
        schema.belongsTo(models.categories);

    };
    return schema;
};
