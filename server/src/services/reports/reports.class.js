/* eslint-disable no-unused-vars */
class Service {
  constructor(options, app, req, res, next) {
    const jsreport = this.app.get('jsreport');
    return jsreport.render({
      template: {
        content: '<h1>Hello WORLD</h1>',
        engine: 'jsrender',
        recipe: 'phantom-pdf'
      }
    }).then((resp) => {
      // prints pdf with headline Hello world
      console.log(resp.content.toString());
      return {data: resp.content.toString('base64')};
    }).catch(e => {
      console.error(e);
    });

    this.options = options || {};
    this.app = app;
  }

  async find(params) {
    return [];
  }

  get(id, params) {
    const jsreport = this.app.get('jsreport');
    return jsreport.render({
      template: {
        content: '<h1>Hello WORLD</h1>',
        engine: 'jsrender',
        recipe: 'phantom-pdf'
      }
    }).then((resp) => {
      // prints pdf with headline Hello world
      console.log(resp.content.toString());
      return {data: resp.content.toString('base64')};
    }).catch(e => {
      console.error(e);
    });
  }

  async create(data, params) {
    if (Array.isArray(data)) {
      return Promise.all(data.map(current => this.create(current, params)));
    }

    return data;
  }

  async update(id, data, params) {
    return data;
  }

  async patch(id, data, params) {
    return data;
  }

  async remove(id, params) {
    return {id};
  }
}

module.exports = function (options, app, req, res, next) {
  return new Service(options, app, req, res, next);
};
module.exports.Service = Service;
