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


export const YearList = props => (
    <List {...props}>
        <Datagrid>
            <TextField source="year"/>
            <EditButton basePath="/year"/>
        </Datagrid>
    </List>
);

const YearTitle = ({record}) => {
    return <span>Year {record ? `"${record.year}"` : ''}</span>;
};

export const YearEdit = props => (
    <Edit title={<YearTitle/>} {...props}>
        <SimpleForm>
            <DisabledInput source="year"/>

        </SimpleForm>
    </Edit>
);

export const YearCreate = props => (
    <Create title="Create a Year" {...props}>
        <SimpleForm>
            <TextInput source="year"/>
        </SimpleForm>
    </Create>
);