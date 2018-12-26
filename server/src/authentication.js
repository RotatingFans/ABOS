const authentication = require('@feathersjs/authentication');
const jwt = require('@feathersjs/authentication-jwt');
const local = require('@feathersjs/authentication-local');


module.exports = function (app) {
  const config = app.get('authentication');

  // Set up authentication with the secret
  app.configure(authentication(config));
  app.configure(jwt());
  app.configure(local());

  // The `authentication` service is used to create a JWT.
  // The before `create` hook registers strategies that can be used
  // to create a new valid JWT (e.g. local or oauth2)
  app.service('authentication').hooks({
    before: {
      create: [
        authentication.hooks.authenticate(config.strategies),
        async function (hook) {
          const seqClient = app.get('sequelizeClient');

          const role = seqClient.models['role'];
          const userRole = seqClient.models['user_role'];

          // make sure params.payload exists
          hook.params.payload = hook.params.payload || {};
          if (hook.params.payload.userId) {
            // merge in a `test` property
            const userRl = await userRole.findOne({
              where: {user_id: hook.params.payload.userId},
              include: {model: role}
            });
            if (userRl.role.authority) {
              Object.assign(hook.params.payload, {role: userRl.role.authority})
            }
          }
        }
      ],
      remove: [
        authentication.hooks.authenticate('jwt')
      ]
    }
  });
};
