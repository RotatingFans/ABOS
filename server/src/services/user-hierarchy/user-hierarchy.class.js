/* eslint-disable no-unused-vars */
class Service {
  constructor(options) {
    this.options = options || {};
  }

  async find(params) {
    console.log(params);
    let users = await user.findAll({attributes: ['id', 'full_name', 'username']});
    let yr = await year.findByPk(params.query.year);
    let usersList = {};


    for (const u of users) {
      let subUsers = {};
      let userYr = await userYear.findOne({where: {user_id: u.id, year_id: yr.id}});
      if (!userYr) {
        userYr = {};
        userYr.group_id = 1;
        userYr.status = "DISABLED"
      }
      for (const su of users) {
        let userYearSub = await userYear.findOne({
          where: {user_id: u.id, year_id: yr.id},
          attributes: ['group_id', 'status']
        });
        if (!userYearSub) {
          userYearSub = {};
          userYearSub.group_id = 1;
          userYearSub.status = "DISABLED"
        }
        subUsers[su.username] = {
          group: userYearSub.group_id,
          checked: (await userManager.findOne({
            where: {
              manage_id: u.id,
              user_id: su.id,
              year_id: yr.id
            }, attributes: ['id']
          }) != null),
          status: userYearSub.status
        };


      }
      let enabledYear = await userYear.findOne({
        where: {user_id: u.id, status: "ENABLED"},
        attributes: ['year_id']
      });
      if (!enabledYear) {
        enabledYear = -1;
      } else {
        enabledYear = enabledYear.year_id;
      }
      usersList[u.username] = {
        id: u.id,
        fullName: u.full_name,
        group: userYr.group_id,
        subUsers: subUsers,
        status: userYr.status,
        enabledYear: enabledYear
      };


    }
    // console.log(usersList);
    return {data: [usersList]};
  }

  async get(id, params) {
  }

  async create(data, params) {

    //   log.debug(jsonParams.toString());
    let users = data.data;
    let yr = await year.findByPk(data.year);
    //let usersList = [:]
    for (const uK of Object.keys(users)) {
      const u = users[uK];
      let usr = await user.findOne({where: {username: uK}});
      let [userYr, cr] = await userYear.findOrBuild({where: {user_id: usr.id, year_id: yr.id}});
      if (u.group != null) {
        userYr.group_id = u.group
      }
      if (u.enabledYear !== -1) {
        if (yr.id !== u.enabledYear) {
          if (u.status === "ENABLED") {
            u.status = "ARCHIVED"
          }
        } else {

          let enabledUsers = await userYear.findAll({where: {user_id: usr.id, status: "ENABLED"}});
          for (let enabledU of enabledUsers) {
            enabledU.status = "ARCHIVED";
            await enabledU.save();
            /*if (cr) {
                // enabledU.errors.allErrors.each {
                //     println it
                // }
            }*/
            //enabledU.save()
          }
          u.status = "ENABLED"


        }
      } else {
        if (u.status === "ENABLED") {
          u.status = "ARCHIVED"
        }
      }
      userYr.status = u.status || "DISABLED";
      await userYr.save();
      //let subUsers = [:]
      for (const suK of Object.keys(u.subUsers)) {
        const su = u.subUsers[suK];
        let subUser = await user.findOne({where: {username: suK}});
        // UserManager.findOrSaveByManageAndUser(user, subUser)

        if (su.checked) {
          await userManager.findOrCreate({where: {manage_id: usr.id, user_id: subUser.id, year_id: yr.id}});

        } else {
          let uM = await userManager.findOne({
            where: {
              manage_id: usr.id,
              user_id: subUser.id,
              year_id: yr.id
            }
          });
          if (uM != null) {
            uM.destroy();
            //uM.save()
          }
        }


      }


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
