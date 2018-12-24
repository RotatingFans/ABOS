/* eslint-disable no-unused-vars */
class Service {
  constructor(options) {
    this.options = options || {};
  }

  async get(id, params) {
  }

  async create(data, params) {

    let newProducts = data.newProducts;
    let updatedProducts = data.updatedProducts;
    let deletedProducts = data.deletedProducts;
    for (const product of newProducts) {

      if (product.status !== 'DELETE') {
        await products.create({
          human_product_id: product.humanProductId,
          product_name: product.productName,
          unit_size: product.unitSize,
          unit_cost: product.unitCost,
          category_id: product.category,
          year_id: data.year
        });
      }
    }
    for (const product of updatedProducts) {
      let prod = await products.findByPk(product.id);

      prod.human_product_id = product.humanProductId;
      prod.product_name = product.productName;
      prod.unit_size = product.unitSize;
      prod.unit_cost = product.unitCost;
      prod.category_id = product.category;
      await prod.save();

    }
    for (const product of deletedProducts) {

      let prod = await products.findByPk(product.id);
      await prod.destroy();

    }

    return ['success'];


  }

  async update(id, data, params) {
  }

  async patch(id, data, params) {
  }

  async remove(id, params) {
  }
}

module.exports = function (options) {
  return new Service(options);
};

module.exports.Service = Service;
