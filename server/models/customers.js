/* jshint indent: 2 */

module.exports = function (sequelize, DataTypes) {
    const schema = sequelize.define('customers', {
        id: {
            type: DataTypes.BIGINT,
            allowNull: false,
            primaryKey: true,
            autoIncrement: true
        },

        phone: {
            type: DataTypes.STRING(255),
            allowNull: true
        },
        cust_email: {
            type: DataTypes.STRING(255),
            allowNull: true
        },
        home: {
            type: DataTypes.BOOLEAN,
            allowNull: true
        },
        interested: {
            type: DataTypes.BOOLEAN,
            allowNull: true
        },
        ordered: {
            type: DataTypes.BOOLEAN,
            allowNull: true
        },
        donation: {
            type: DataTypes.DECIMAL,
            allowNull: true
        },
        user_name: {
            type: DataTypes.STRING(255),
            allowNull: false
        },
        zip_code: {
            type: DataTypes.STRING(5),
            allowNull: true
        },
        customer_name: {
            type: DataTypes.STRING(255),
            allowNull: false
        },
        street_address: {
            type: DataTypes.STRING(255),
            allowNull: false
        },
        state: {
            type: DataTypes.STRING(255),
            allowNull: true
        },
        latitude: {
            type: "DOUBLE",
            allowNull: false
        },
        longitude: {
            type: "DOUBLE",
            allowNull: false
        },
        city: {
            type: DataTypes.STRING(255),
            allowNull: true
        },

    }, {
        tableName: 'customers', underscored: true,
    });
    schema.associate = models => {
        schema.belongsTo(models.year);
        schema.belongsTo(models.user);
        schema.hasOne(models.orders, {onDelete: 'cascade'});
    };
    return schema;
};
