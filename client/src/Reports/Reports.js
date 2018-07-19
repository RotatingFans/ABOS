import React from 'react';
import {SimpleForm, TextInput} from 'react-admin';
import Wizard from './Wizard'

const steps = () => [
    "World", "World2"
];


function save(record, redirect) {
    console.log(record)

}

const stepsContent = () => [
    <TextInput
        source="Name"/>, <TextInput
        source="Name2"/>
];
export const Reports = (props) => (
    <Wizard {...props} steps={steps()} stepContents={stepsContent()} save={save} formName={"Record-form"}/>
);

