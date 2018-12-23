/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const category = sequelize.define('categories', {
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
        category_name: {
            type: DataTypes.STRING(255),
            allowNull: false
        },
        delivery_date: {
            type: DataTypes.DATE,
            allowNull: false
        },

    }, {
        tableName: 'categories', underscored: true,
    });
    category.associate = models => {
        category.belongsTo(models.year);
    };
    return category;
};
