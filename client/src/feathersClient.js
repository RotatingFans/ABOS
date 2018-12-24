const feathers = require('@feathersjs/feathers');
const rest = require('@feathersjs/rest-client');

const app = feathers();
const restClient = rest('http://localhost:3030');
app.configure(restClient.fetch(window.fetch));

// Connect to the same as the browser URL (only in the browser)

// Connect to a different URL
export default app;