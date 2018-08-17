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
    required,
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

const requiredValidate = required();

const formValidate = required();

class reportsWizard extends React.Component {
    //users: {}, years: {}, customers: {}
    state = {update: false};

    constructor(props) {
        super(props);

    }


    save = (record, redirect) => {
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
                    }).then(response => {
                        let filename = "report.pdf";
                        const disposition = response.headers.get("content-disposition");
                        if (disposition && disposition.indexOf('attachment') !== -1) {
                            const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                            let matches = filenameRegex.exec(disposition);
                            if (matches != null && matches[1]) {
                                filename = matches[1].replace(/['"]/g, '');
                            }
                        }
                        response.blob().then(blob => {
                            download(blob, filename, "application/pdf")
                        })
                    })
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
            }).then(response => {
                let filename = "report.pdf";
                const disposition = response.headers.get("content-disposition");
                if (disposition && disposition.indexOf('attachment') !== -1) {
                    const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    let matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) {
                        filename = matches[1].replace(/['"]/g, '');
                    }
                }
                response.blob().then(blob => {
                    download(blob, filename, "application/pdf")
                })
            })
        }


        //console.log(fetchUtils.fetchJson(url, options));

    };

    getCustomersWithYearAndUser(Year, User) {

        dataProvider(GET_LIST, 'customers', {
            filter: {year: Year, user: User},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            this.setState({customers: response.data})
        })

    }

    getCustomersWithUser(User) {

        dataProvider(GET_LIST, 'customers', {
            filter: {user: User},
            sort: {field: 'id', order: 'DESC'},
            pagination: {page: 1, perPage: 1000},
        }).then(response => {
            response.data.reduceRight((acc, obj, i) => {
                acc[obj.customerName] ? response.data.splice(i, 1) : acc[obj.customerName] = true;
                return acc;
            }, Object.create(null));

            //response.data.sort((a, b) => b.customerName - a.customerName);
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
            response.data.unshift({id: 'All', categoryName: 'All'});
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

    updateReportType(ReportType) {
        switch (ReportType) {
            case 'customers_split':
                this.setState({
                    reportType: ReportType,
                    yearReq: true,
                    userReq: true,
                    custReq: false,
                    catReq: true,
                    dueReq: true
                });

                break;
            case 'Year Totals':
                this.setState({
                    reportType: ReportType,
                    yearReq: true,
                    userReq: true,
                    custReq: false,
                    catReq: true,
                    dueReq: true
                });

                break;
            case 'Customer Year Totals':
                this.setState({
                    reportType: ReportType,
                    yearReq: true,
                    userReq: true,
                    custReq: true,
                    catReq: true,
                    dueReq: true
                });

                break;
            case 'Customer All-Time Totals':
                this.setState({
                    reportType: ReportType,
                    yearReq: false,
                    userReq: true,
                    custReq: true,
                    catReq: false,
                    dueReq: false
                });

                break;
        }

        // this.updateChoices();
    }

    updateChoices() {
        if (this.state.update) {
            const year = this.state.year;
            const user = this.state.user;
            if ((year && user) > -1) {
                this.getCustomersWithYearAndUser(year, user);

            }
            if (year) {
                this.getCategoriesForYear(year);

            }
            if (user && this.state.reportType === 'Customer All-Time Totals') {
                this.getCustomersWithUser(user);

            }
            this.setState({update: false})

        }
    }
    stepsContent() {

        this.setState({
            stepsContent: [<CustomSelectInput
                source="template" choices={[{id: 'customers_split', name: 'Year; Split by Customer'}, {
                    id: 'Year Totals',
                    name: 'Year Totals'
                }, {id: 'Customer Year Totals', name: 'Customer Year Totals'}, {
                    id: 'Customer All-Time Totals',
                    name: 'Customer All-Time Totals'
            }]} validate={requiredValidate} onChangeCustomHandler={(key) => this.updateReportType(key)}/>,
                    [
                        <TextInput
                            source="Scout_name" validate={requiredValidate}/>,
                        <TextInput
                            source="Scout_address" validate={requiredValidate}/>,
                        <TextInput
                            source="Scout_Zip" validate={requiredValidate}/>,
                        <TextInput
                            source="Scout_Town" validate={requiredValidate}/>,
                        <TextInput
                            source="Scout_State" validate={requiredValidate}/>,
                        <TextInput
                            source="Scout_Phone" validate={requiredValidate}/>,
                        <TextInput
                            source="Scout_Rank" validate={requiredValidate}/>,
                        <ImageInput
                            source="LogoLocation" accept="image/*">
                            <ImageField source="src" title="title"/>
                        </ImageInput>,

                        <FormDataConsumer>
                            {({formData, ...rest}) => {
                                if (this.state.yearReq) {
                                    return (
                                        <CustomSelectInput source={"Year"} label="Year" optionText="year"
                                                           optionValue="id" choices={this.state.years}
                                                           onChangeCustomHandler={(key) => this.updateYear(key)}
                                                           validate={requiredValidate}  {...rest}/>
                                    )
                                }
                            }}
                        </FormDataConsumer>,
                        <FormDataConsumer>
                            {({formData, ...rest}) => {
                                if (this.state.userReq) {

                                    return <CustomSelectInput label="User" source="User" optionText={"userName"}
                                                              optionValue={"id"}
                                                              choices={this.state.users}  {...rest}
                                                              onChangeCustomHandler={(key) => this.updateUser(key)}
                                                              validate={requiredValidate}/>
                                }
                            }
                            }
                        </FormDataConsumer>,



                        <FormDataConsumer>
                            {({formData, ...rest}) => {
                                if (this.state.year && this.state.catReq) {
                                    //console.log(this.state.year);

                                    return <SelectInput source="Category" optionText={"categoryName"}
                                                        optionValue={"categoryName"}
                                                        choices={this.state.categories} {...rest}
                                                        validate={requiredValidate}/>


                                }

                            }
                            }
                        </FormDataConsumer>,

                        <FormDataConsumer>
                            {({formData, ...rest}) => {

                                if ((this.state.year && this.state.user && this.state.custReq) || (this.state.reportType === 'Customer All-Time Totals' && this.state.user && this.state.custReq)) {
                                    return <SelectArrayInput source="Customer" optionText={"customerName"}
                                                             optionValue={"id"} choices={
                                        this.state.customers} {...rest} validate={requiredValidate}/>


                                }
                            }
                            }
                        </FormDataConsumer>,

                        <FormDataConsumer>
                            {({formData, ...rest}) => {
                                if (this.state.dueReq) {
                                    return <BooleanInput
                                        source="Print_Due_Header"/>
                                }
                            }}
                        </FormDataConsumer>

                    ]]
            }
        )
    }

    componentWillReceiveProps() {
        this.getUsers();
        this.getYears();

    }

    componentWillMount() {
        this.stepsContent();

    }

    render() {

        this.updateChoices();
        return (
            <Wizard {...this.props} steps={steps()} stepContents={this.state.stepsContent} save={this.save}
                    formName={"record-form"}/>
        )
    }
}

export default reportsWizard;

