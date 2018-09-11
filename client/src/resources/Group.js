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
import GroupIcon from 'material-ui/svg-icons/social/person';

export {GroupIcon};

export const GroupList = props => (
    <List {...props}>
        <Datagrid>
            <TextField source="GroupName"/>
            <DateField source="deliveryDate"/>
            <EditButton basePath="/categories"/>
        </Datagrid>
    </List>
);

const GroupTitle = ({record}) => {
    return <span>Group {record ? `"${record.name}"` : ''}</span>;
};

export const GroupEdit = props => (
    <Edit title={<GroupTitle/>} {...props}>
        <SimpleForm>
            <DisabledInput source="GroupName"/>
            <DateInput source="deliveryDate"/>
        </SimpleForm>
    </Edit>
);

export const GroupCreate = props => (
    <Create title="Create a Group" {...props}>
        <SimpleForm>
            <TextInput source="name"/>
            <DateInput source="deliveryDate"/>
        </SimpleForm>
    </Create>
);