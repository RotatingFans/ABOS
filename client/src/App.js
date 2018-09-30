// in src/App.js
import React from 'react';
import {Admin, fetchUtils, Layout, Resource, AppBar, UserMenu, MenuItemLink} from 'react-admin';

import {CategoryCreate, CategoryEdit, CategoryList} from './resources/Categories.js';
import {GroupCreate, GroupEdit, GroupList} from './resources/Group.js';
import {YearCreate, YearEdit, YearList, YearShow} from './resources/Year.js';
import {CustomerCreate, CustomerEdit, CustomerList} from './resources/Customers.js';
import restClient from './grailsRestClient';
import authProvider from './security/authProvider';
import {Dashboard} from './dashboard';
import {Reports} from "./Reports";
import Menu from "./resources/Menu";
import {UGY} from "./UGY";
import {UserList, UserShow} from "./resources/User";
import {Maps} from './maps';
import { Route } from 'react-router-dom';
import About from './resources/About';
import InfoIcon from '@material-ui/icons/Info';

const httpClient = (url, options = {}) => {
    if (!options.headers) {
        options.headers = new Headers({Accept: 'application/json'});
    }
    const token = localStorage.getItem('access_token');
    options.headers.set('Authorization', `Bearer ${token}`);
    return fetchUtils.fetchJson(url, options);
};
const dataProvider = restClient(httpClient);
//const dataProvider = simpleRestProvider('http://192.168.1.3:8080/api', httpClient);
const MyUserMenu = props => (
    <UserMenu {...props}>
        <MenuItemLink
            to="/about"
            primaryText="About"
            leftIcon={<InfoIcon />}
        />
    </UserMenu>
);

const MyAppBar = props => <AppBar {...props} userMenu={<MyUserMenu />} />;
const routes = [
    <Route exact path="/about" component={About} />,
];
const layout = (props) => <Layout {...props} appBar={MyAppBar}/>;
const App = () => (
    <Admin dashboard={Dashboard} dataProvider={dataProvider} authProvider={authProvider} appLayout={layout} customRoutes={routes}>
        {permissions => [
            <Resource name="customers" list={CustomerList} edit={CustomerEdit} create={CustomerCreate}/>,
            <Resource name="Reports" list={Reports}/>,
            <Resource name="Maps" list={Maps}/>,
            //Reports
            // <Resource name="customers"/>,
            <Resource name="User" list={UserList} show={UserShow}/>,

            permissions === 'manager'
                ? <Resource name="User"/>
                : null,
            permissions === 'ROLE_ADMIN'
                ? <Resource name="Categories" list={CategoryList} edit={CategoryEdit} create={CategoryCreate}/>
                //UGY
                : <Resource name="Categories"/>,
            permissions === 'ROLE_ADMIN'
                ? <Resource name="Years" show={YearShow} edit={YearEdit} list={YearList} create={YearCreate}/>
                //UGY
                : <Resource name="Years"/>,
            permissions === 'ROLE_ADMIN'
                ? <Resource name="Group" list={GroupList} edit={GroupEdit} create={GroupCreate}/>
                //UGY
                : <Resource name="Group"/>,
            permissions === 'ROLE_ADMIN'
                ? <Resource name="UsersProducts" options={{label: 'Users and Products'}} list={UGY}/>
                //UGY
                : null,
        ]}

    </Admin>
);

export default App;
