import React from 'react';
import {
    BooleanField,
    BooleanInput,
    CheckboxGroupInput,
    classes,
    Create,
    CREATE,
    Datagrid,
    DateField,
    DateInput,
    DisabledInput,
    Edit,
    EditButton,
    fetchUtils,
    Field,
    Filter,
    FormDataConsumer,
    GET_LIST,
    Link,
    List,
    NumberField,
    NumberInput,
    ReferenceArrayInput,
    ReferenceInput,
    SelectArrayInput,
    SelectInput,
    SimpleForm,
    TextField,
    TextInput,
    UPDATE
} from 'react-admin';
import ProductsGrid from './ProductsGrid'
import restClient from '../grailsRestClient';
import CustomerLinkField from "./CustomerRecordLink";
import {withStyles} from '@material-ui/core/styles';


const httpClient = (url, options = {}) => {
    if (!options.headers) {
        options.headers = new Headers({Accept: 'application/json'});
    }
    const token = localStorage.getItem('access_token');
    options.headers.set('Authorization', `Bearer ${token}`);
    return fetchUtils.fetchJson(url, options);
};
const dataProvider = restClient(httpClient);
const styles = {
    flex: {display: 'flex'},
    flexColumn: {display: 'flex', flexDirection: 'column'},
    leftCol: {flex: 1, marginRight: '1em'},
    rightCol: {flex: 1, marginLeft: '1em'},
    singleCol: {marginTop: '2em', marginBottom: '2em'},
    inlineBlock: {display: 'inline-flex', marginRight: '1rem'},
    block: {display: 'block'},

};
//import ErrorBoundary from '../ErrorBoundary';
/*import CustomerIcon from '';
export { CustomerIcon };*/
/*
[

  {
    "id": 92,
    "phone": "",
    "home": false,
    "interested": false,
    "ordered": false,
    "donation": 0,
    "userName": "me",
    "order": {
      "id": 92,
      "cost": 46,
      "delivered": false,
      "paid": 0
    },
    "zipCode": null,
    "customerName": "",
    "streetAddress": "",
    "state": null,
    "latitude": ,
    "longitude": ,
    "year": {
      "year": "2018"
    }
  }
]

 */
const CustomerFilter = (props) => (
    <Filter className="form" {...props}>
        <TextInput label="Search" source="customerName" alwaysOn/>
        {/*        <ReferenceArrayInput
            source="year"
            reference="Years"
            sort={{field: 'id', order: 'ASC'}}
            label="Year"
        >
            <SelectArrayInput optionText="year" source="year" label="Year"/>
        </ReferenceArrayInput>*/}
    </Filter>
);

export const CustomerList = (props) => (
    <List {...props} filters={<CustomerFilter/>}>
        <Datagrid>
            <CustomerLinkField/>
            <TextField source="streetAddress"/>
            <TextField source="city"/>
            <TextField source="state"/>
            <NumberField label="Order Cost" source="order.cost" options={{style: 'currency', currency: 'USD'}}/>
            <NumberField label="Amount Paid" source="order.amountPaid" options={{style: 'currency', currency: 'USD'}}/>
            <BooleanField label="Delivered?" source="order.delivered"/>
            <EditButton basePath="/customers"/>
        </Datagrid>

    </List>

);

const CustomerTitle = ({record}) => {
    return <span>Customer {record ? `"${record.name}"` : ''}</span>;
};

export const CustomerEdit = withStyles(styles)(({classes, ...props}) => (
    <Edit {...props}>

        <SimpleForm>
            <TextInput label="Customer Name" source="customerName" formClassName={classes.inlineBlock}/>
            <TextInput source="phone" formClassName={classes.inlineBlock}/>
            <TextInput source="custEmail" formClassName={classes.inlineBlock}/>
            <span/>

            <TextInput source="streetAddress" formClassName={classes.inlineBlock}/>
            <TextInput source="city" formClassName={classes.inlineBlock}/>
            <TextInput source="state" formClassName={classes.inlineBlock}/>
            <TextInput source="zipCode" formClassName={classes.inlineBlock}/>
            <span/>


            <TextInput label="Donation" source="donation" formClassName={classes.inlineBlock}/>
            <TextInput label="Amount Paid" source="order.amountPaid" formClassName={classes.inlineBlock}/>
            <BooleanInput label="Delivered?" source="order.delivered" formClassName={classes.inlineBlock}/>
            <span/>
            {/*            <ReferenceInput label="Year to add to" source="year" reference="Years">
                <SelectInput optionText="year" />
            </ReferenceInput>

            <ReferenceInput label="User to add to" source="user" reference="User" >
                <SelectInput optionText="username"  />
            </ReferenceInput>*/}

            <ProductsGrid source="order"  {...props}/>
        </SimpleForm>
    </Edit>
    /*<Edit {...props} filters={<CustomerFilter/>}>
    <ProductsGrid>
        <TextField label="Customer Name" source="customerName"/>
        <TextField source="streetAddress"/>
        <TextField source="city"/>
        <TextField source="state"/>
        <NumberField label="Order Cost" source="order.cost" options={{style: 'currency', currency: 'USD'}}/>
        <NumberField label="Amount Paid" source="order.paid" options={{style: 'currency', currency: 'USD'}}/>
        <BooleanField label="Delivered?" source="order.delivered"/>
        <EditButton basePath="/customers"/>
    </ProductsGrid>
</Edit>*/
));
const reverseGeocode = (address) => {
    return {lat: 0, long: 0};
};

const saveCreation = (record, redirect) => {

    let {lat, long} = reverseGeocode(record.streetAddress + " " + record.city + ", " + record.state + " " + record.zipCode);
    dataProvider(CREATE, 'Customers', {
        data: {
            customerName: record.customerName,
            streetAddress: record.streetAddress,
            city: record.city,
            state: record.state,
            zipCode: record.zipCode,
            phone: record.phone,
            custEmail: record.custEmail,
            latitude: lat,
            longitude: long,
            ordered: false,
            home: true,
            interested: true,
            donation: record.donation,
            year: {id: record.year},

            user: record.user,

            userName: record.user.userName,
            order: {}

        }
    }).then(response => {
        let customer = {id: response.id};
        let order = record.order;
        order.customer = customer;
        let newOrderedProducts = [];
        order.orderedProducts.forEach((orderedProduct) => {
            orderedProduct.customer = customer;
            newOrderedProducts.push(orderedProduct);
        });
        order.orderedProducts = newOrderedProducts;
        dataProvider(UPDATE, 'Customers', {

            data: {
                customerName: record.customerName,
                streetAddress: record.streetAddress,
                city: record.city,
                state: record.state,
                zipCode: record.zipCode,
                phone: record.phone,
                custEmail: record.custEmail,
                latitude: lat,
                longitude: long,
                ordered: false,
                home: true,
                interested: true,
                donation: record.donation,
                year: {id: record.year},

                user: record.user,

                userName: record.user.userName,
                order: order


            }

        });
    });


};
export const CustomerCreate = withStyles(styles)(({classes, ...props}) => (
    <Create title="Create a Customer" {...props}>
        <SimpleForm save={saveCreation}>
            <TextInput label="Customer Name" source="customerName" formClassName={classes.inlineBlock}/>
            <TextInput source="phone" formClassName={classes.inlineBlock}/>
            <TextInput source="custEmail" formClassName={classes.inlineBlock}/>
            <span/>

            <TextInput source="streetAddress" formClassName={classes.inlineBlock}/>
            <TextInput source="city" formClassName={classes.inlineBlock}/>
            <TextInput source="state" formClassName={classes.inlineBlock}/>
            <TextInput source="zipCode" formClassName={classes.inlineBlock}/>
            <span/>


            <TextInput label="Donation" source="donation" formClassName={classes.inlineBlock}/>
            <TextInput label="Amount Paid" source="order.amountPaid" formClassName={classes.inlineBlock}/>
            <BooleanInput label="Delivered?" source="order.delivered" formClassName={classes.inlineBlock}/>
            <span/>

            <ReferenceInput label="Year to add to" source="year" reference="Years" formClassName={classes.inlineBlock}>
                <SelectInput optionText="year"/>
            </ReferenceInput>

            <ReferenceInput label="User to add to" source="user" reference="User" formClassName={classes.inlineBlock}>
                <SelectInput optionText="username"/>
            </ReferenceInput>

            <ProductsGrid source="order"  {...props}/>


        </SimpleForm>
    </Create>
));
