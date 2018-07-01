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
    <Filter {...props}>
        <TextInput label="Search" source="q" alwaysOn/>
        <ReferenceArrayInput
            source="year"
            reference="Years"
            sort={{field: 'id', order: 'ASC'}}
        >
            <SelectArrayInput optionText="year" source="yea.year" label="Year"/>
        </ReferenceArrayInput>
    </Filter>
);

export const CustomerList = (props) => (
    <List {...props} filters={<CustomerFilter/>}>
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
            {/*<DisabledInput source="CustomerName"/>
            <DateInput source="deliveryDate"/>*/}
        </SimpleForm>
    </Edit>
);

export const CustomerCreate = props => (
    <Create title="Create a Customer" {...props}>
        <SimpleForm>
            {/*            <TextInput source="CustomerName"/>
            <DateInput source="deliveryDate"/>*/}
        </SimpleForm>
    </Create>
);
