const jsreport = require('jsreport-core')({
  logger: {
    silent: false // when true, it will silence all transports defined in logger
  },
  // options for templating engines and other scripts execution
  // see the https://github.com/pofider/node-script-manager for more information
  templatingEngines: {
    numberOfWorkers: 2,
    strategy: 'in-process',
    host: '127.0.0.1',
    portLeftBoundary: 1338,
    portRightBoundary: 1350,
    templateCache: {
      max: 100, //LRU cache with max 100 entries, see npm lru-cache for other options
      enabled: true //disable cache
    }
  },
});


module.exports = function (app) {
  jsreport.init().then(() => {
    app.set('jsreport', jsreport);
  }).catch((e) => {
    console.error(e);
  });


};
