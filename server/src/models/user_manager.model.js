// See http://docs.sequelizejs.com/en/latest/docs/models-definition/
// for more of what you can do here.
const Sequelize = require('sequelize');
const DataTypes = Sequelize.DataTypes;

module.exports = function (app) {
  const sequelizeClient = app.get('sequelizeClient');
  const schema = sequelizeClient.define('user_manager', {
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
    schema.belongsTo(models.user, {as: 'manage'});

  };
  return schema;
};
