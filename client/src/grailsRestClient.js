import {CREATE, DELETE, fetchUtils, GET_LIST, GET_MANY, GET_MANY_REFERENCE, GET_ONE, UPDATE,} from 'react-admin';
import hostURL from "./host";

export const GET_PLAIN_MANY = "GET_PLAIN_MANY";
const apiUrl = hostURL + '/api';

export default (httpClient = fetchUtils.fetchJson) => {
    const makeFilters = filters => {
        let filterString = '';
        Object.keys(filters).map(function (key, index) {
            let value = filters[key];
            if (filterString !== '') {
                filterString += ';';
            }
            filterString += `${key}:${value}`;
        });


        return filterString;
    };

    const makeParam = params => {
        const {page, perPage} = params.pagination;
        const {field, order} = params.sort;
        // TODO: handle filter
        return `sort=${field}&order=${order}&max=${perPage}&offset=${(page - 1) *
        perPage}&q=${makeFilters(params.filter)}`;
    };

    /**
     * @param {String} type One of the constants appearing at the top if this file, e.g. 'UPDATE'
     * @param {String} resource Name of the resource to fetch, e.g. 'posts'
     * @param {Object} params The REST request params, depending on the type
     * @returns {Object} { url, options } The HTTP request parameters
     */
    const convertRESTRequestToHTTP = (type, resource, params) => {
        let url = '';
        const options = {};
        switch (type) {
            case GET_LIST: {
                url = `${apiUrl}/${resource}.json?${makeParam(params)}`;
                break;
            }
            case GET_ONE:
                url = `${apiUrl}/${resource}/${params.id}.json`;
                break;
            case GET_MANY: {
                url = `${apiUrl}/${resource}.json`;
                break;
            }
            case GET_PLAIN_MANY: {
                url = `${apiUrl}/${resource}?q=${makeFilters(params.filter)}`;
                break;
            }
            case GET_MANY_REFERENCE: {
                url = `${apiUrl}/${resource}.json?${makeParam(params)}`;
                break;
            }
            case UPDATE:
                url = `${apiUrl}/${resource}/${params.id}.json`;
                options.method = 'PUT';
                options.body = JSON.stringify(params.data);
                break;
            case CREATE:
                url = `${apiUrl}/${resource}.json`;
                options.method = 'POST';
                options.body = JSON.stringify(params.data);
                break;
            case DELETE:
                url = `${apiUrl}/${resource}/${params.id}.json`;
                options.method = 'DELETE';
                break;
            default:
                throw new Error(`Unsupported fetch action type ${type}`);
        }
        return {url, options};
    };

    /**
     * @param {Object} response HTTP response from fetch()
     * @param {String} type One of the constants appearing at the top if this file, e.g. 'UPDATE'
     * @param {String} resource Name of the resource to fetch, e.g. 'posts'
     * @param {Object} params The REST request params, depending on the type
     * @returns {Object} REST response
     */
    const convertHTTPResponseToREST = (response, type, resource, params) => {
        const {headers, json} = response;
        switch (type) {
            case GET_LIST:
            case GET_MANY_REFERENCE:
                /*
                if (!headers.has('content-range')) {
                  throw new Error(
                    'The Content-Range header is missing in the HTTP Response. The simple REST client expects responses for lists of resources to contain this header with the total number of results to build the pagination. If you are using CORS, did you declare Content-Range in the Access-Control-Expose-Headers header?'
                  );
                }
                */
                return {
                    data: json,
                    total: parseInt(headers.get('X-Search-Hit-Count'), 10),
                };
            case CREATE:
                return {data: {json, id: json.id}};
            case DELETE:
                return {data: {...params.data}};
            default:
                return {data: json};
        }
    };

    /**
     * @param {string} type Request type, e.g GET_LIST
     * @param {string} resource Resource name, e.g. "posts"
     * @param {Object} payload Request parameters. Depends on the request type
     * @returns {Promise} the Promise for a REST response
     */
    return (type, resource, params) => {
        const {url, options} = convertRESTRequestToHTTP(type, resource, params);

        return httpClient(url, options).then(response => {
            return convertHTTPResponseToREST(response, type, resource, params);
        });
    };
};
