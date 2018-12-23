const R = require('ramda');

const path = require('path');
const feathers = require('@feathersjs/feathers');
const express = require('@feathersjs/express');
const socketio = require('@feathersjs/socketio');

const Sequelize = require('sequelize');
const service = require('feathers-sequelize');
const sequelize = new Sequelize('sequelize', 'admin', 'dev123', {
    dialect: 'mysql',
    host: "172.17.0.2",
    port: 3306,
    dialectOptions: {decimalNumbers: true}
});
const categories = sequelize.import(__dirname + "/models/categories");
const customers = sequelize.import(__dirname + "/models/customers");
const groups = sequelize.import(__dirname + "/models/groups");
const orderedProducts = sequelize.import(__dirname + "/models/ordered_products");
const orders = sequelize.import(__dirname + "/models/orders");
const products = sequelize.import(__dirname + "/models/products");
const role = sequelize.import(__dirname + "/models/role");
const RoleHierarchyEntry = sequelize.import(__dirname + "/models/role_hierarchy_entry");
const user = sequelize.import(__dirname + "/models/user");
const userManager = sequelize.import(__dirname + "/models/user_manager");
const userRole = sequelize.import(__dirname + "/models/user_role");
const userYear = sequelize.import(__dirname + "/models/user_year");
const year = sequelize.import(__dirname + "/models/year");

const categoriesAttr = [];

const customerAttr = {
    include: [['user_name', "userName"], ['zip_code', "zipCode"], ['customer_name', "customerName"], ['street_address', "streetAddress"], ['cust_email', 'custEmail']],
    exclude: ['cust_email', 'version', "user_name", 'zip_code', "customer_name", "street_address"]
};
const GroupAttr = [];
const orderedProductsAttr = ['id', 'quantity', ['user_name', 'userName']];
const ordersAttr = ['id', 'cost', 'quantity', ['amount_paid', 'amountPaid'], 'delivered', ['user_name', 'userName'],];
const productsAttr = ['id', ['human_product_id', 'humanProductId'], ['unit_cost', 'unitCost'], ['unit_size', 'unitSize'], ['product_name', 'productName']];
const roleAttr = [];
const roleHierarchyEntryAttr = [];
const userAttr = ['id', ['full_name', 'fullName'], 'username'];
const userManagerAttr = [];
const userRoleAttr = [];
const userYearAttr = [];
const yearAttr = ['id', 'year'];

const GroupInclude = [];
const categoriesInclude = {model: categories};


const roleInclude = [];
const roleHierarchyEntryInclude = [];
const userInclude = {model: user, attributes: userAttr};
const userManagerInclude = [];
const userRoleInclude = [];
const userYearInclude = [];

function yearInclude() {
    return {model: year, attributes: yearAttr};
}

//const  yearInclude = { model: year, attributes: yearAttr };
const productsInclude = {
    model: products,
    attributes: productsAttr,
    include: [categoriesInclude, yearInclude()],
    as: 'products'
};
const orderedProductsInclude = {
    model: orderedProducts,
    attributes: orderedProductsAttr,
    include: [productsInclude, yearInclude()],
    as: 'orderedProducts'
};
const ordersInclude = {
    model: orders,
    attributes: ordersAttr,
    include: [orderedProductsInclude, yearInclude()],
    as: 'order'
};
categories.associate(sequelize.models);
customers.associate(sequelize.models);
groups.associate(sequelize.models);
orderedProducts.associate(sequelize.models);
orders.associate(sequelize.models);
products.associate(sequelize.models);
//role.associate(sequelize.models);
//RoleHierarchyEntry.associate(sequelize.models);
//user.associate(sequelize.models);
userManager.associate(sequelize.models);
userRole.associate(sequelize.models);
userYear.associate(sequelize.models);
//year.associate(sequelize.models);

const Message = sequelize.define('message', {
    text: {
        type: Sequelize.STRING,
        allowNull: false
    }
}, {
    freezeTableName: true
});

// Create an Express compatible Feathers application instance.
const app = express(feathers());
sequelize.sync({});

// Turn on JSON parser for REST services
app.use(express.json());
// Turn on URL-encoded parser for REST services
app.use(express.urlencoded({extended: true}));
// Enable REST services
app.configure(express.rest());
// Enable Socket.io services
app.configure(socketio());
// Create an in-memory Feathers service with a default page size of 2 items
// and a maximum size of 4
app.use(function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
    next();
});
app.use('/messages', service({
    Model: Message,
    paginate: {
        default: 2,
        max: 4
    }
}));
app.use('/categories', service({
    Model: categories,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));

app.use('/customers', service({
    Model: customers,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.service('/customers').hooks({

    before: {
        find(context) {
            // Get the Sequelize instance. In the generated application via:
            //  const sequelize = context.app.get('sequelizeClient');


            context.params.sequelize = {
                include: [yearInclude(), userInclude, {
                    model: orders,
                    attributes: ordersAttr,
                    include: [{
                        model: orderedProducts,
                        attributes: orderedProductsAttr,
                        include: [productsInclude, yearInclude()],
                        as: 'orderedProducts'
                    }, yearInclude()],
                    as: 'order'
                }],
                attributes: customerAttr,
            };


            return context;
        },
        get(context) {
            // Get the Sequelize instance. In the generated application via:
            //  const sequelize = context.app.get('sequelizeClient');


            context.params.sequelize = {
                include: [yearInclude(), userInclude, {
                    model: orders,
                    attributes: ordersAttr,
                    include: [{
                        model: orderedProducts,
                        attributes: orderedProductsAttr,
                        include: [productsInclude, yearInclude()],
                        as: 'orderedProducts'
                    }, yearInclude()],
                    as: 'order'
                }],
                attributes: customerAttr,
            };

            return context;
        }
    },
    after: {
        find(context) {
            let customers = [];


            context.result.data.forEach(cust => {

                let ops = [];
                cust.dataValues.order.dataValues.orderedProducts.forEach(op => {
                    op.dataValues.extendedCost = op.dataValues.quantity * op.products.dataValues.unitCost;
                    ops.push(op);
                });
                cust.dataValues.order.dataValues.orderedProducts = ops;
                customers.push(cust);
            });
            context.result.data = customers;
            return context;
        },
        get(context) {


            let ops = [];
            context.result.dataValues.order.dataValues.orderedProducts.forEach(op => {
                op.dataValues.extendedCost = op.dataValues.quantity * op.products.dataValues.unitCost;
                ops.push(op);
            });
            context.result.dataValues.order.dataValues.orderedProducts = ops;
            return context;

        }
    }
});
app.use('/groups', service({
    Model: groups,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.use('/orderedProducts', service({
    Model: orderedProducts,
    raw: false,
    paginate: {
        default: 10,
        max: 500
    }

}));
app.service('/orderedProducts').hooks({

    before: {
        find(context) {
            // Get the Sequelize instance. In the generated application via:
            //  const sequelize = context.app.get('sequelizeClient');

            let yrInc = yearInclude();
            if (context.params.query.year) {
                yrInc.where = {id: context.params.query.year};
                delete context.params.query.year;

            }
            context.params.sequelize = {
                attributes: ['id', 'quantity', [sequelize.literal(`\`quantity\` * \`products\`.\`unit_cost\``), 'extendedCost'], ['user_name', 'userName']],
                include: [productsInclude, yrInc],
            };
            return context;
        },
        get(context) {
            // Get the Sequelize instance. In the generated application via:
            //  const sequelize = context.app.get('sequelizeClient');

            let yrInc = yearInclude();
            if (context.params.query.year) {
                yrInc.where = {id: context.params.query.year};
                delete context.params.query.year;

            }
            context.params.sequelize = {
                attributes: ['id', 'quantity', [sequelize.literal(`\`quantity\` * \`products\`.\`unit_cost\``), 'extendedCost'], ['user_name', 'userName']],
                include: [productsInclude, yrInc],
            };
            return context;
        }
    },
    after: {
        find(context) {
            let ops = [];


            context.result.data.forEach(op => {


                op.dataValues.extendedCost = op.dataValues.quantity * op.products.dataValues.unitCost;
                ops.push(op);


            });
            context.result.data = ops;
            return context;
        },
    }
});
app.use('/orders', service({
    Model: orders,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.service('/orders').hooks({

    before: {
        find(context) {
            // Get the Sequelize instance. In the generated application via:
            //  const sequelize = context.app.get('sequelizeClient');
            let yrInc = yearInclude();
            if (context.params.query.year) {
                yrInc.where = {id: context.params.query.year};
                delete context.params.query.year;

            }

            context.params.sequelize = {
                include: [{model: customers, attributes: ['donation']}, {
                    model: orderedProducts,
                    attributes: orderedProductsAttr,
                    include: [{
                        model: products,
                        attributes: productsAttr,
                        include: [categoriesInclude, yearInclude()],
                        as: 'products'
                    }, yearInclude()],
                    as: 'orderedProducts'
                }, yrInc],
                attributes: ['id', 'cost', 'quantity', ['amount_paid', 'amountPaid'], 'delivered', ['user_name', 'userName'], 'customer_id'],
            };

            return context;
        },
        get(context) {
            // Get the Sequelize instance. In the generated application via:
            //  const sequelize = context.app.get('sequelizeClient');
            let yrInc = yearInclude();
            if (context.params.query.year) {
                yrInc.where = {id: context.params.query.year};
                delete context.params.query.year;

            }

            context.params.sequelize = {
                include: [{model: customers, attributes: ['donation']}, {
                    model: orderedProducts,
                    attributes: orderedProductsAttr,
                    include: [{
                        model: products,
                        attributes: productsAttr,
                        include: [categoriesInclude, yearInclude()],
                        as: 'products'
                    }, yearInclude()],
                    as: 'orderedProducts'
                }, yrInc],
                attributes: ['id', 'cost', 'quantity', ['amount_paid', 'amountPaid'], 'delivered', ['user_name', 'userName'], 'customer_id'],
            };

            return context;
        }
    },
    after: {
        find(context) {
            let orders = [];
            context.result.data.forEach(res => {
                let ops = [];
                res.dataValues.orderedProducts.forEach(op => {
                    op.dataValues.extendedCost = op.dataValues.quantity * op.products.dataValues.unitCost;
                    ops.push(op);
                });
                res.dataValues.orderedProducts = ops;
                orders.push(res);
            });
            context.result.data = orders;
            return context;
        }
    }
});
app.use('/products', service({
    Model: products,
    raw: false,
    paginate: {
        default: 10,
        max: 1000
    }

}));
app.service('/products').hooks({

    before: {
        find(context) {
            // Get the Sequelize instance. In the generated application via:
            //  const sequelize = context.app.get('sequelizeClient');

            let yrInc = yearInclude();
            if (context.params.query.year) {
                yrInc.where = {id: context.params.query.year};
                delete context.params.query.year;

            }
            context.params.sequelize = {
                attributes: productsAttr, include: [categoriesInclude, yrInc]
            };

            return context;
        },
        get(context) {
            // Get the Sequelize instance. In the generated application via:
            //  const sequelize = context.app.get('sequelizeClient');

            let yrInc = yearInclude();
            if (context.params.query.year) {
                yrInc.where = {id: context.params.query.year};
                delete context.params.query.year;

            }
            context.params.sequelize = {
                attributes: productsAttr, include: [categoriesInclude, yrInc]
            };

            return context;
        }
    }
});
app.use('/role', service({
    Model: role,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.use('/RoleHierarchyEntry', service({
    Model: RoleHierarchyEntry,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.use('/user', service({
    Model: user,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.use('/userManager', service({
    Model: userManager,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.use('/userYear', service({
    Model: userYear,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.use('/userRole', service({
    Model: userRole,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
app.use('/year', service({
    Model: year,
    raw: false,
    paginate: {
        default: 10,
        max: 100
    }

}));
//app.use(express.errorHandler());

Message.sync({force: true}).then(() => {
    // Create a dummy Message
    app.service('messages').create({
        text: 'Message created on server'
    }).then(message => console.log('Created message', message));
});

// Start the server
const port = 3030;

app.listen(port, () => {
    console.log(`Feathers server listening on port ${port}`);
});