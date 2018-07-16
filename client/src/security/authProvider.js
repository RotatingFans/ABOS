// in src/authProvider.js
import {AUTH_ERROR, AUTH_LOGIN, AUTH_LOGOUT} from 'react-admin';

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
            .then(({access_token}) => {
                localStorage.setItem('access_token', access_token);
            });
    }
    if (type === AUTH_LOGOUT) {
        localStorage.removeItem('access_token');
    }
    if (type === AUTH_ERROR) {
        const status = params.status;
        if (status === 401 || status === 403) {
            localStorage.removeItem('token');
            return Promise.reject();
        }
        return Promise.resolve();
    }
    return Promise.resolve();
}