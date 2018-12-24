module.exports = {

  categoriesAttr: [],

  customerAttr: {
    include: [['user_name', "userName"], ['zip_code', "zipCode"], ['customer_name', "customerName"], ['street_address', "streetAddress"], ['cust_email', 'custEmail']],
    exclude: ['cust_email', 'version', "user_name", 'zip_code', "customer_name", "street_address"]
  },
  GroupAttr: [],
  orderedProductsAttr: ['id', 'quantity', ['user_name', 'userName']],
  ordersAttr: ['id', 'cost', 'quantity', ['amount_paid', 'amountPaid'], 'delivered', ['user_name', 'userName'],],
  productsAttr: ['id', ['human_product_id', 'humanProductId'], ['unit_cost', 'unitCost'], ['unit_size', 'unitSize'], ['product_name', 'productName']],
  roleAttr: [],
  roleHierarchyEntryAttr: [],
  userAttr: ['id', ['full_name', 'fullName'], 'username'],
  userManagerAttr: [],
  userRoleAttr: [],
  userYearAttr: [],
  yearAttr: ['id', 'year'],
};
