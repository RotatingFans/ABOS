import React from 'react';
import restClient from "../grailsRestClient";


const httpClient = (url, options = {}) => {
    if (!options.headers) {
        options.headers = new Headers({Accept: 'application/json'});
    }
    const token = localStorage.getItem('access_token');
    options.headers.set('Authorization', `Bearer ${token}`);
    return fetchUtils.fetchJson(url, options);
};
const dataProvider = restClient(httpClient);


class UGYEditor extends React.Component {
    //users: {}, years: {}, customers: {}
    state = {update: false};
    save = (record, redirect) => {
        console.log(record);
        let options = {};
        let url = 'http://localhost:8080/api/Reports';
        if (!options.headers) {
            options.headers = new Headers({Accept: 'application/pdf'});
        }
        const token = localStorage.getItem('access_token');
        options.headers.set('Authorization', `Bearer ${token}`);

        fetch(url, {
            method: "POST",
            mode: "cors",
            cache: "no-cache",
            credentials: "same-origin", // include, same-origin, *omit
            headers: {
                "Content-Type": "application/json; charset=utf-8",
                'Authorization': `Bearer ${token}`
                // "Content-Type": "application/x-www-form-urlencoded",
            },
            redirect: "follow", // manual, *follow, error
            referrer: "no-referrer", // no-referrer, *client
            body: JSON.stringify(record),
        }).then(response => {

        })


        //console.log(fetchUtils.fetchJson(url, options));

    };

    constructor(props) {
        super(props);

    }

    getUsers() {
        dataProvider(GET_LIST, 'User', {
            filter: {},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            this.setState({users: response.data})
        })


    }

    getYears() {
        dataProvider(GET_LIST, 'Years', {
            filter: {},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            this.setState({years: response.data})
        })


    }

    updateYear(year) {
        this.setState({year: year, update: true});
        // this.updateChoices();

    }

    updateUser(user) {
        this.setState({user: user, update: true});
        // this.updateChoices();
    }


    componentWillReceiveProps() {
        this.getUsers();
        this.getYears();

    }

    componentWillMount() {

    }

    render() {
        /*
        *                         | Tab Pane
        *                         |    Users | Groups | Products
        *                         |
        *                         |     U Menu Bar - Add Element (Dropdown for bulk or simple) Multi Action Menu
        *                         |     S   Expansion Panels( Enabled, Disabled, Archived)
        *                         |     E     Selectable Expansion Panels with Delete/Edit buttons on end
        *                         |     R       Group Selection
        *                         |     S       Management Selection - Use Selectable Nested List
        *                         |
        *      Nested List        |     G Menu Bar - Add Element (Dropdown for bulk or simple) Multi Action Menu
        * See List on Material UI |     R   Expansion Panels with Delete/Edit buttons on end E
        *                         |     O     Edit Button Has option to remove all selected group members from group
        *                         |     U   List of Group Members(Selectable)
        *                         |     P
        *                         |
        *                         |     P Mimic Add Customer, but Some Changes
        *                         |     R   Top Pane
        *                         |     O     Different Import/Export function buttons
        *                         |     D   Add Product inputs/Button
        *                         |     U   Table
        *                         |     C   No Quantity/Extended Cost
        *                         |     T   Add Category Selection
        *                         |     S     Include Add Category Button - Should open a modal dialog
        *                         |
        *                         |------------------------------------------------------------------- Save | Cancel ---
         */
        return (<div>Test</div>

        )
    }
}

export default UGYEditor;

