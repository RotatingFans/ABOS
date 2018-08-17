// in src/App.js
import React from 'react';
import {Admin, fetchUtils, Layout, Resource} from 'react-admin';

import {CategoryCreate, CategoryEdit, CategoryList} from './resources/Categories.js';
import {CustomerCreate, CustomerEdit, CustomerList} from './resources/Customers.js';
import restClient from './grailsRestClient';
import authProvider from './security/authProvider';
import {Dashboard} from './dashboard';
import {Reports} from "./Reports";
import Menu from "./resources/Menu";

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
const layout = (props) => <Layout {...props} menu={Menu}/>;
const App = () => (
    <Admin dashboard={Dashboard} dataProvider={dataProvider} authProvider={authProvider}>
        {permissions => [
            <Resource name="customers" list={CustomerList} edit={CustomerEdit} create={CustomerCreate}/>,
            <Resource name="Reports" list={Reports}/>,
            //Reports
            // <Resource name="customers"/>,
            <Resource name="Years"/>,
            <Resource name="User"/>,
            <Resource name="Categories"/>,
            permissions === 'manager'
                ? <Resource name="User"/>
                : null,
            permissions === 'ROLE_ADMIN'
                ? <Resource name="Categories" list={CategoryList} edit={CategoryEdit} create={CategoryCreate}/>
                //UGY
                : null,
        ]}

    </Admin>
);

export default App;
