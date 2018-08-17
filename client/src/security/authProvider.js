// in src/authProvider.js
import {AUTH_CHECK, AUTH_ERROR, AUTH_GET_PERMISSIONS, AUTH_LOGIN, AUTH_LOGOUT} from 'react-admin';

export default (type, params) => {


    if (type === AUTH_LOGIN) {
        const {username, password} = params;
        const request = new Request('http://localhost:8080/api/login', {
            method: 'POST',
            body: JSON.stringify({username, password}),
            headers: new Headers({'Content-Type': 'application/json'}),
        });
        return fetch(request)
            .then(response => {
                if (response.status < 200 || response.status >= 300) {
                    throw new Error(response.statusText);
                }
                return response.json();
            })
            .then(({access_token, roles}) => {
                localStorage.setItem('access_token', access_token);
                localStorage.setItem('role', roles[0]);
            });
    }
    if (type === AUTH_LOGOUT) {
        localStorage.removeItem('access_token');
        localStorage.removeItem('role');
        return Promise.resolve();
    }
    if (type === AUTH_ERROR) {
        const status = params.status;
        if (status === 401 || status === 403) {
            localStorage.removeItem('access_token');
            localStorage.removeItem('role');

            return Promise.reject();
        }
        return Promise.resolve();
    }
    if (type === AUTH_CHECK) {
        const token = localStorage.getItem('access_token');

        return localStorage.getItem('access_token') && fetch(
            `http://localhost:8080/api/AuthCheck`,

            {
                headers: {
                    'Accept': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            })
            .then(response => {
                let status = response.status;
                if (status === 401 || status === 403) {
                    localStorage.removeItem('access_token');
                    localStorage.removeItem('role');

                    return false;
                } else {
                    return true;
                }
            })
            ? Promise.resolve() : Promise.reject();
    }
    if (type === AUTH_GET_PERMISSIONS) {
        const role = localStorage.getItem('role');
        return role ? Promise.resolve(role) : Promise.reject();
    }
    return Promise.resolve();
}