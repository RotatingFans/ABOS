// Initializes the `reports` service on path `/reports`
const createService = require('./reports.class.js');
const hooks = require('./reports.hooks');
const auth = require('@feathersjs/authentication');

module.exports = function (app) {

  const paginate = app.get('paginate');

  const options = {
    paginate
  };

  // Initialize our service with any options it requires
  app.use('/reports', auth.express.authenticate('jwt'), async function (req, res, next) {

    const jsreport = app.get('jsreport');
    await jsreport.render({
      template: {
        content: '<h1>Hello WORLD</h1>',
        engine: 'jsrender',
        recipe: 'chrome-pdf'
      }
    }).then((resp) => {
      // prints pdf with headline Hello world
      // console.log(resp.content.toString());
      res.writeHead(200, {
        'Content-Type': 'application/pdf',
        'Content-Disposition': 'attachment; filename=some_file.pdf',
        'Content-Length': resp.content.length,
        'Access-Control-Expose-Headers': 'Content-Disposition'
      });
      res.end(resp.content);
      return '';
      //  return {data: resp.content.toString('base64')};
    }).catch(e => {
      console.error(e);
    });
    next();
    // console.log(req);
  });

  // Get our initialized service so that we can register hooks
  // const service = app.service('reports');

  //service.hooks(hooks);
};
