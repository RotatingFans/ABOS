import React from 'react';
import {
    Create,
    Datagrid,
    DateField,
    DateInput,
    DisabledInput,
    Edit,
    EditButton,
    List,
    SimpleForm,
    TextField,
    TextInput,
} from 'react-admin';
//import ErrorBoundary from '../ErrorBoundary';
import YearIcon from 'material-ui/svg-icons/social/person';

export {YearIcon};

export const YearList = props => (
    <List {...props}>
        <Datagrid>
            <TextField source="YearName"/>
            <DateField source="deliveryDate"/>
            <EditButton basePath="/categories"/>
        </Datagrid>
    </List>
);

const YearTitle = ({record}) => {
    return <span>Year {record ? `"${record.name}"` : ''}</span>;
};

export const YearEdit = props => (
    <Edit title={<YearTitle/>} {...props}>
        <SimpleForm>
            <DisabledInput source="YearName"/>
            <DateInput source="deliveryDate"/>
        </SimpleForm>
    </Edit>
);

export const YearCreate = props => (
    <Create title="Create a Year" {...props}>
        <SimpleForm>
            <TextInput source="name"/>
            <DateInput source="deliveryDate"/>
        </SimpleForm>
    </Create>
);