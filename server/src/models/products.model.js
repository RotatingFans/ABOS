// See http://docs.sequelizejs.com/en/latest/docs/models-definition/
// for more of what you can do here.
const Sequelize = require('sequelize');
const DataTypes = Sequelize.DataTypes;

module.exports = function (app) {
  const sequelizeClient = app.get('sequelizeClient');
  const schema = sequelizeClient.define('products', {
    id: {
      type: DataTypes.BIGINT,
      allowNull: false,
      primaryKey: true,
      autoIncrement: true
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
      type: DataTypes.DECIMAL(19, 2),
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
