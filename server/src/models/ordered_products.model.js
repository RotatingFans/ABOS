// See http://docs.sequelizejs.com/en/latest/docs/models-definition/
// for more of what you can do here.
const Sequelize = require('sequelize');
const DataTypes = Sequelize.DataTypes;

module.exports = function (app) {
  const sequelizeClient = app.get('sequelizeClient');
  const schema = sequelizeClient.define('ordered_products', {
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
