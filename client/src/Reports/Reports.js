import React from 'react';
import {
    BooleanInput,
    fetchUtils,
    FormDataConsumer,
    GET_LIST,
    ImageField,
    ImageInput,
    ReferenceArrayInput,
    ReferenceInput,
    SelectArrayInput,
    SelectInput,
    SimpleForm,
    TextInput
} from 'react-admin';
import Wizard from './Wizard'
import download from 'downloadjs';
import restClient from "../grailsRestClient";
import {formValueSelector} from 'redux-form';

const httpClient = (url, options = {}) => {
    if (!options.headers) {
        options.headers = new Headers({Accept: 'application/json'});
    }
    const token = localStorage.getItem('access_token');
    options.headers.set('Authorization', `Bearer ${token}`);
    return fetchUtils.fetchJson(url, options);
};
const dataProvider = restClient(httpClient);

const CustomSelectInput = ({onChangeCustomHandler, ...rest}) => (
    <SelectInput onChange={(event, key, payload) => {
        onChangeCustomHandler(key)
    }}
                 {...rest}
    />
);

const steps = () => [
    "Pick Report Template", "Fill In Details"
];
const convertFileToBase64 = file => new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file.rawFile);

    reader.onload = () => resolve(reader.result);
    reader.onerror = reject;
});

const save = (record, redirect) => {
    console.log(record);
    let options = {};
    let url = 'http://localhost:8080/api/Reports';
    if (!options.headers) {
        options.headers = new Headers({Accept: 'application/pdf'});
    }
    const token = localStorage.getItem('access_token');
    options.headers.set('Authorization', `Bearer ${token}`);
    if (record.LogoLocation) {
        convertFileToBase64(record.LogoLocation).then(b64 => {
                record.LogoLocation.base64 = b64;
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
                }).then(response => response.blob())
                    .then(blob => download(blob, "report.pdf", "application/pdf"))
            }
        )
    } else {
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
        }).then(response => response.blob())
            .then(blob => download(blob, "report.pdf", "application/pdf"))
    }


    //console.log(fetchUtils.fetchJson(url, options));

};

class reportsWizard extends React.Component {
    //users: {}, years: {}, customers: {}
    state = {users: [{id: 'test', userName: 'test'}]};

    constructor(props) {
        super(props);

    }

    getCustomersWithYearAndUser(Year, User) {

        dataProvider(GET_LIST, 'customers', {
            filter: {year: Year, User: User},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            this.setState({customers: response.data})
        })

    }

    getCategoriesForYear(Year) {
        // this.setState({year: Year});

        dataProvider(GET_LIST, 'Categories', {
            filter: {year: Year},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            this.setState({categories: response.data})
        })


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

    updateYear(year) {
        this.setState({year: year});
        this.updateChoices();

    }

    updateUser(user) {
        this.setState({user: user});
        this.updateChoices();
    }

    updateChoices() {
        const year = this.state.year;
        const user = this.state.user;
        if (year && user) {
            this.getCustomersWithYearAndUser(year, user);

        }
        if (year) {
            this.getCategoriesForYear(year);

        }
    }

    stepsContent() {

        this.setState({
                stepsContent: [<SelectInput
                    source="template" choices={[{id: 'customers_split', name: 'Year; Split by Customer'}]}/>,
                    [
                        <TextInput
                            source="Scout_name"/>,
                        <TextInput
                            source="Scout_address"/>,
                        <TextInput
                            source="Scout_Zip"/>,
                        <TextInput
                            source="Scout_Town"/>,
                        <TextInput
                            source="Scout_State"/>,
                        <TextInput
                            source="Scout_Phone"/>,
                        <TextInput
                            source="Scout_Rank"/>,
                        <ImageInput
                            source="LogoLocation" accept="image/*">
                            <ImageField source="src" title="title"/>
                        </ImageInput>,
                        <ReferenceInput label="Year" source="Year" reference="Years"
                                        onChange={(event, key, val) => this.updateYear(key)}>
                            <SelectInput optionText="year" optionValue="id"/>
                        </ReferenceInput>,

                        <CustomSelectInput label="User" source="User" optionText={"userName"} optionValue={"userName"}
                                           choices={this.state.users}
                                           onChangeCustomHandler={(key) => this.updateUser(key)}/>,


                        <FormDataConsumer>
                            {({formData, ...rest}) => {
                                if (this.state.year) {
                                    //console.log(this.state.year);

                                    return <SelectInput source="Category" optionText={"categoryName"}
                                                        optionValue={"categoryName"}
                                                        choices={this.state.categories} {...rest}
                                                        allowEmpty/>


                                }
                                if (this.state.year && this.state.user) {
                                    return <SelectArrayInput source="Customer" choices={
                                        this.state.customers} {...rest} allowEmpty/>


                                }
                            }
                            }
                        </FormDataConsumer>,

                        <FormDataConsumer>
                            {({formData, ...rest}) => {


                            }
                            }
                        </FormDataConsumer>,


                        <BooleanInput
                            source="Print_Due_Header"/>,

                    ]]
            }
        )
    }

    componentWillReceiveProps() {
        this.getUsers();
    }

    componentWillMount() {
        this.stepsContent();
    }

    render() {
        return (
            <Wizard {...this.props} steps={steps()} stepContents={this.state.stepsContent} save={save}
                    formName={"record-form"}/>
        )
    }
}

export default reportsWizard;

