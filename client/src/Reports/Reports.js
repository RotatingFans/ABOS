import React from 'react';
import {SimpleForm, TextInput} from 'react-admin';
import Wizard from './Wizard'

const steps = () => [
    "World", "World2"
];

function handleSubmit(record) {

}

function save(record) {

}

const stepsContent = () => [
    <SimpleForm record={{}} handleSubmit={handleSubmit} save={save} saving={false} form={"report-form"}><TextInput
        source="Name"/></SimpleForm>, <div>Hello World2</div>
];
export const Reports = (props) => (
    <Wizard {...props} steps={steps()} stepContents={stepsContent()}/>
);

