// in src/App.js
import React from 'react';
import {Admin, fetchUtils, Resource} from 'react-admin';

import {CategoryCreate, CategoryEdit, CategoryList} from './resources/Categories.js';
import restClient from './grailsRestClient';
import authProvider from './security/authProvider';

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

const App = () => (
    <Admin dataProvider={dataProvider} authProvider={authProvider}>
        <Resource name="Categories" list={CategoryList} edit={CategoryEdit} create={CategoryCreate}/>
    </Admin>
);

export default App;
