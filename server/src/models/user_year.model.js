// See http://docs.sequelizejs.com/en/latest/docs/models-definition/
// for more of what you can do here.
const Sequelize = require('sequelize');
const DataTypes = Sequelize.DataTypes;

module.exports = function (app) {
  const sequelizeClient = app.get('sequelizeClient');
  const schema = sequelizeClient.define('user_year', {
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
