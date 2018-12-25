const attributes = require('./attributes');

module.exports = function (app) {
  const seqClient = app.get('sequelizeClient');
  const categories = seqClient.models['categories'];
  const customers = seqClient.models['customers'];
  const groups = seqClient.models['groups'];
  const orderedProducts = seqClient.models['ordered_products'];
  const orders = seqClient.models['orders'];
  const products = seqClient.models['products'];
  const role = seqClient.models['role'];
  const RoleHierarchyEntry = seqClient.models['role_hierarchy_entry'];
  const user = seqClient.models['user'];
  const userManager = seqClient.models['user_manager'];
  const userRole = seqClient.models['user_role'];
  const userYear = seqClient.models['user_year'];
  const year = seqClient.models['year'];
  return {

    GroupInclude: function () {
      return {}
    },
    categoriesInclude: function () {
      return {model: categories}
    },


    roleInclude: function () {
      return {}
    },
    roleHierarchyEntryInclude: function () {
      return {}
    },
    userInclude: function () {
      return {
        model: user,
        attributes: attributes.userAttr
      }
    },
    userManagerInclude: function () {
      return {}
    },
    userRoleInclude: function () {
      return {}
    },
    userYearInclude: function () {
      return {}
    },

    yearInclude: function () {
      return {model: year, attributes: attributes.yearAttr};
    },

//const  yearInclude = { model: year, attributes: yearAttr };
    productsInclude: function () {
      return {
        model: products,
        attributes: attributes.productsAttr,
        include: [categoriesInclude(), yearInclude()],
        as: 'products'
      }
    },
    orderedProductsInclude: function () {
      return {
        model: orderedProducts,
        attributes: attributes.orderedProductsAttr,
        include: [productsInclude(), yearInclude()],
        as: 'orderedProducts'
      }
    },
    ordersInclude: function () {
      return {
        model: orders,
        attributes: attributes.ordersAttr,
        include: [orderedProductsInclude(), yearInclude()],
        as: 'order'
      }
    },
  }


};





