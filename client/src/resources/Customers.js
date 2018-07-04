import React from 'react';
import {
    BooleanField,
    CheckboxGroupInput,
    Create,
    Datagrid,
    DateField,
    DateInput,
    DisabledInput,
    Edit,
    EditButton,
    Filter,
    List,
    NumberField,
    ReferenceArrayInput,
    SelectArrayInput,
    SimpleForm,
    TextField,
    TextInput
} from 'react-admin';
import ProductsGrid from './ProductsGrid'

const styles = {
    flex: {display: 'flex'},
    flexColumn: {display: 'flex', flexDirection: 'column'},
    leftCol: {flex: 1, marginRight: '1em'},
    rightCol: {flex: 1, marginLeft: '1em'},
    singleCol: {marginTop: '2em', marginBottom: '2em'},
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
        <ReferenceArrayInput
            source="year"
            reference="Years"
            sort={{field: 'id', order: 'ASC'}}
            label="Year"
        >
            <SelectArrayInput optionText="year" source="year" label="Year"/>
        </ReferenceArrayInput>
    </Filter>
);

export const CustomerList = (props) => (
    <List {...props}>
        <Datagrid>

            <TextField label="Customer Name" source="customerName"/>
            <TextField source="streetAddress"/>
            <TextField source="city"/>
            <TextField source="state"/>
            <NumberField label="Order Cost" source="order.cost" options={{style: 'currency', currency: 'USD'}}/>
            <NumberField label="Amount Paid" source="order.paid" options={{style: 'currency', currency: 'USD'}}/>
            <BooleanField label="Delivered?" source="order.delivered"/>
            <EditButton basePath="/customers"/>
        </Datagrid>

    </List>

);

const CustomerTitle = ({record}) => {
    return <span>Customer {record ? `"${record.name}"` : ''}</span>;
};

export const CustomerEdit = props => (
    <Edit {...props}>

        <SimpleForm>

            <ProductsGrid>

            </ProductsGrid>
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
);

export const CustomerCreate = props => (
    <Create title="Create a Customer" {...props}>
        <SimpleForm>
            {/*            <TextInput source="CustomerName"/>
            <DateInput source="deliveryDate"/>*/}
        </SimpleForm>
    </Create>
);
